package org.huge.data.service.handler;


import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.huge.data.config.OrcaExportFactoryBean;
import org.huge.data.config.OrcaExportProperties;
import org.huge.data.config.OrcaExportVariableHolder;
import org.huge.data.dao.OrcaExportDao;
import org.huge.data.domain.EasyExportTask;
import org.huge.data.enums.ExportTaskStatusEnum;
import org.huge.data.enums.OrcaExportResultCodeEnum;
import org.huge.data.enums.TriggerSourceEnum;
import org.huge.data.exception.OrcaExportException;
import org.huge.data.interfaces.DBBatchProcessFunction;
import org.huge.data.interfaces.DBProcessFunction;
import org.huge.data.interfaces.DBQueryFunction;
import org.huge.data.service.asynHelper.AsyncUploadS3;
import org.huge.data.service.asynHelper.DownloadUrlProcessor;
import org.huge.data.service.subtask.ProcessSubTask;
import org.huge.data.service.subtask.WriteSubTask;
import org.huge.data.service.trigger.EventBasedTrigger;
import org.huge.data.util.OrcaExportJsonUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.ResolvableType;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 异步导出任务处理器
 */
@Getter
@Slf4j
public class AsyncExportTask implements Runnable{

    private final Long asyncTaskId;

    private final OrcaExportDao orcaExportDao;

    private final SqlSessionFactory sqlSessionFactory;

    private final RedissonClient redissonClient;

    private final OrcaExportProperties orcaExportProperties;

    private final EventBasedTrigger eventBasedTrigger;

    // 构造函数
    public AsyncExportTask(Long asyncTaskId,
                           OrcaExportDao orcaExportDao,
                           SqlSessionFactory sqlSessionFactory,
                           RedissonClient redissonClient,
                           OrcaExportProperties orcaExportProperties,
                           EventBasedTrigger eventBasedTrigger) {
        this.asyncTaskId = asyncTaskId;
        this.orcaExportDao = orcaExportDao;
        this.sqlSessionFactory = sqlSessionFactory;
        this.redissonClient = redissonClient;
        this.orcaExportProperties = orcaExportProperties;
        this.eventBasedTrigger = eventBasedTrigger;
    }

    @Override
    public void run() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 1. 利用分布式锁限制只有一个线程能够处理该任务
        String redisKey = "EasyExport:Lock:" + asyncTaskId;
        RLock lock = redissonClient.getLock(redisKey);
        SqlSession sqlSession = null;
        ThreadPoolExecutor tempThreadPoolExecutor = null;
        try {
            // todo:这个分布锁是不是没什么用?因为在取锁前通过行锁将任务状态改为处理中,其他线程就不会再去处理了
            // 此处假设一个异步导出任务最多执行20分钟,若有需要,可以调整
            if (lock.tryLock(0L, orcaExportProperties.getAsyncExportTaskExpireTime(), TimeUnit.SECONDS)){
                // 2. 检查该任务当前状态是否为待处理状态,若不是,不处理,并报错
                EasyExportTask easyExportTask = orcaExportDao.selectExportTaskById(asyncTaskId);
                if (Objects.isNull(easyExportTask) || !ExportTaskStatusEnum.RUNNING.getStatus().equals(easyExportTask.getStatus())){
                    log.error("任务ID:{}, 任务不存在或状态不是处理中, easyExportTask:{}", asyncTaskId, easyExportTask);
                    return;
                }

                // 3. 更新触发时间
                orcaExportDao.updateExportTaskById(EasyExportTask.builder()
                        .id(asyncTaskId)
                        .triggerExecutedTime(new Date())
                        .build());

                // 3. 解析导出任务各项参数
                // 3.a 数据库查询操作方法
                Object dbQueryFunctionObject = OrcaExportFactoryBean.getBean(easyExportTask.getDbFunctionMethod());
                @SuppressWarnings("unchecked")
                DBQueryFunction<Object,Object> dbQueryFunction = (DBQueryFunction<Object,Object>) dbQueryFunctionObject;
                ResolvableType resolvableTypeForDBQueryFunction = ResolvableType.forType(dbQueryFunctionObject.getClass().getGenericInterfaces()[0]);

                // 3.b 数据库查询操作方法入参
                Class<?> dbQueryFunctionInputClass = resolvableTypeForDBQueryFunction.getGeneric(0).resolve();
                Object dbQueryFunctionInputValue = OrcaExportJsonUtil.jsonStr2Obj(easyExportTask.getDbFunctionParams(), dbQueryFunctionInputClass);

                // 3.c 数据库加工操作方法
                Object dbProcessFunctionNotKnownObject = OrcaExportFactoryBean.getBean(easyExportTask.getDbResultsProcess());
                ResolvableType resolvableTypeForDBProcessFunction = ResolvableType.forType(dbProcessFunctionNotKnownObject.getClass().getGenericInterfaces()[0]);
                // 判断dbProcessFunctionObject是DBProcessFunction还是DBBatchProcessFunction两个接口的实现类
                DBProcessFunction<Object, Object> dbProcessFunction = null;
                DBBatchProcessFunction<Object, Object> dbBatchProcessFunction = null;
                if (Objects.equals(resolvableTypeForDBProcessFunction.getRawClass(), DBProcessFunction.class)){
                    dbProcessFunction = (DBProcessFunction<Object, Object>) dbProcessFunctionNotKnownObject;
                } else if (Objects.equals(resolvableTypeForDBProcessFunction.getRawClass(), DBBatchProcessFunction.class)){
                    dbBatchProcessFunction = (DBBatchProcessFunction<Object, Object>) dbProcessFunctionNotKnownObject;
                } else {
                    throw new OrcaExportException(OrcaExportResultCodeEnum.ERROR_PARAM_ILLEGAL, "不支持的数据库加工操作方法:" + dbProcessFunctionNotKnownObject);
                }

                // 3.d 数据库加工操作方法返参
                Class<?> dbProcessFunctionOutputClass = null;
                // 如果是DBProcessFunction,则泛型的第二个参数就是返回值类型
                if (Objects.equals(resolvableTypeForDBProcessFunction.getRawClass(), DBProcessFunction.class)){
                    dbProcessFunctionOutputClass = resolvableTypeForDBProcessFunction.getGeneric(1).resolve();
                // 如果是DBBatchProcessFunction,则泛型的第二个参数是一个List,List中的泛型才是返回值类型
                } else if (Objects.equals(resolvableTypeForDBProcessFunction.getRawClass(), DBBatchProcessFunction.class)){
                    dbProcessFunctionOutputClass = resolvableTypeForDBProcessFunction.getGeneric(1).getGeneric(0).resolve();
                }

                // 3.e 上传至文件服务器方法
                Object uploadFunctionObject = OrcaExportFactoryBean.getBean(easyExportTask.getUploadClassName());
                AsyncUploadS3 uploadFunction = (AsyncUploadS3) uploadFunctionObject;

                // 3.f 数据库异步下载URL处理方法
                Object downloadUrlProcessorObject = OrcaExportFactoryBean.getBean(easyExportTask.getDownloadUrlProcessorClassName());
                @SuppressWarnings("unchecked")
                DownloadUrlProcessor downloadUrlProcessorFunction = (DownloadUrlProcessor) downloadUrlProcessorObject;

                // 3.g 数据库异步下载URL处理方法入参
                String manageDownloadUrlParamsString = easyExportTask.getDownloadUrlProcessorParams();
                Map<String, String> manageDownloadUrlParams = OrcaExportJsonUtil.jsonStr2Obj(manageDownloadUrlParamsString, new TypeReference<Map<String, String>>(){});

                // 3.h 文件名
                String fileName = easyExportTask.getFileName();

                // 4. 定义临时线程池,其中一个线程用于执行写任务,剩余线程用于执行加工任务 加工任务结果放入FIFO的阻塞队列
                // 因为写任务无法并发执行,所以只有一个线程用于执行写任务
                // 自定义拒绝策略 当任务拒绝时调用队列的阻塞方法,阻塞等待队列直到有空闲位置
                RejectedExecutionHandler handler = (r, executor) -> {
                    // 该步骤会阻塞,直到队列有空闲位置
                    try {
                        executor.getQueue().put(r);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                };
                int totalThreadCount = 1 + orcaExportProperties.getProcessThreadCount();
                tempThreadPoolExecutor = new ThreadPoolExecutor(
                        totalThreadCount,
                        totalThreadCount,
                        0,
                        TimeUnit.SECONDS,
                        new ArrayBlockingQueue<>(5),
                        handler);


                // 5. 定义阻塞队列 加工子任务结果放入队列,写子任务从队列中取出结果
                // 存储加工后结果的阻塞队列 跟 线程池里的阻塞队列 在放数据时,应该采用阻塞的方法,否则,它们的数量大小就要精确设置,否则会有溢出风险
                BlockingQueue<Future<List<Object>>> processAfterFutureResultQueue = new ArrayBlockingQueue<>(10);

                // 6. 生成写子任务
                Future<String> writeFutureResult = tempThreadPoolExecutor.submit(new WriteSubTask(
                        processAfterFutureResultQueue,
                        asyncTaskId,
                        fileName,
                        dbProcessFunctionOutputClass,
                        orcaExportProperties,
                        uploadFunction));

                // 7. 执行普通查询,该查询会被mybatis拦截器拦截,返回空,用于设置ThreadLocal变量的值
                // 通过ThreadLocal设置标记,拦截接下来的普通查询
                OrcaExportVariableHolder.needInterceptPureQueryHolder.set(true);
                dbQueryFunction.query(dbQueryFunctionInputValue);
                OrcaExportVariableHolder.needInterceptPureQueryHolder.set(false);

                // 8. 执行流式查询
                sqlSession = sqlSessionFactory.openSession();
                if (Objects.isNull(OrcaExportVariableHolder.statementIdHolder.get()) ||
                        Objects.isNull(OrcaExportVariableHolder.parameterHolder.get()) ||
                        Objects.isNull(OrcaExportVariableHolder.rowBoundsHolder.get())){
                    log.error("任务ID:{}, ThreadLocal为空,无法进行流式查询", asyncTaskId);
                    return;
                }
                // 通过ThreadLocal设置标记, 拦截接下来的流式查询
                OrcaExportVariableHolder.needInterceptStreamQueryHolder.set(true);
                Cursor<?> dbResultCursor = sqlSession.selectCursor(
                        OrcaExportVariableHolder.statementIdHolder.get(),
                        OrcaExportVariableHolder.parameterHolder.get(),
                        OrcaExportVariableHolder.rowBoundsHolder.get());
                OrcaExportVariableHolder.needInterceptStreamQueryHolder.set(false);
                List<Object> processBeforeResultList = new CopyOnWriteArrayList<>();

                // 9. 生成加工子任务
                int processNumberCountPerThread = orcaExportProperties.getProcessNumberCountPerThread();
                for (Object dbResult : dbResultCursor) {
                    // 每存够100条数据,就提交一个加工子任务
                    processBeforeResultList.add(dbResult);
                    if (processBeforeResultList.size() == processNumberCountPerThread){
                        submitProcessSubTask(tempThreadPoolExecutor, processAfterFutureResultQueue, processBeforeResultList, dbProcessFunction, dbBatchProcessFunction);
                    }
                }
                // 处理剩余的数据
                if (processBeforeResultList.size() > 0){
                    submitProcessSubTask(tempThreadPoolExecutor, processAfterFutureResultQueue, processBeforeResultList, dbProcessFunction, dbBatchProcessFunction);
                }
                // 通知写子任务不会再有新的任务提交
                processAfterFutureResultQueue.put(OrcaExportVariableHolder.PROCESS_SUB_TASK_END);

                // 10. 获取写子任务结果
                String downloadUrl = null;
                try {
                    downloadUrl = writeFutureResult.get();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }

                // 11. 执行用户自定义处理下载Url的方法
                downloadUrlProcessorFunction.manageDownUrl(downloadUrl, manageDownloadUrlParams);

                // 12. 更新任务状态为已完成
                orcaExportDao.updateExportTaskById(EasyExportTask.builder()
                        .id(asyncTaskId)
                        .status(ExportTaskStatusEnum.SUCCESS.getStatus())
                        .build());
            }else {
                log.info("任务ID:{}, 未获取到分布式锁,不进行处理", asyncTaskId);
            }
        } catch (Exception e) {
            // 12. 更新任务状态为失败
            orcaExportDao.updateExportTaskById(EasyExportTask.builder()
                    .id(asyncTaskId)
                    .status(ExportTaskStatusEnum.FAILED.getStatus())
                    .build());
            log.error("任务ID:{}, 执行异步导出任务失败", asyncTaskId, e);
        } finally {
            // 更新完成时间
            orcaExportDao.updateExportTaskById(EasyExportTask.builder()
                    .id(asyncTaskId)
                    .finishedTime(new Date())
                    .build());
            // 清空ThreadLocal
            OrcaExportVariableHolder.exportCountHolder.remove();
            OrcaExportVariableHolder.needInterceptPureQueryHolder.remove();
            OrcaExportVariableHolder.needInterceptStreamQueryHolder.remove();
            OrcaExportVariableHolder.statementIdHolder.remove();
            OrcaExportVariableHolder.parameterHolder.remove();
            OrcaExportVariableHolder.rowBoundsHolder.remove();
            // 释放分布式锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()){
                lock.unlock();
            }
            // 关闭sqlSession
            if (!Objects.isNull(sqlSession)){
                sqlSession.close();
            }
            // 关闭线程池
            if (!Objects.isNull(tempThreadPoolExecutor)){
                tempThreadPoolExecutor.shutdown();
            }
            stopWatch.stop();
            log.info("任务ID:{}, 执行异步导出任务结束,耗时:{}ms", asyncTaskId, stopWatch.getTime());
        }
        log.info("任务ID:{}, 执行异步导出任务结束,触发获取下一个异步任务", asyncTaskId);
        eventBasedTrigger.triggerAsyncExportTaskHandle(TriggerSourceEnum.EVENT_BASED);
    }

    private void submitProcessSubTask(ThreadPoolExecutor tempThreadPoolExecutor,
                                      BlockingQueue<Future<List<Object>>> processAfterFutureResultQueue,
                                      List<Object> processBeforeResultList,
                                      DBProcessFunction<Object, Object> dbProcessFunction,
                                      DBBatchProcessFunction<Object, Object> dbBatchProcessFunction) {
        // 提交加工子任务 如果被拒绝,等待队列会阻塞直至有空闲位置
        Future<List<Object>> processFutureResult = tempThreadPoolExecutor.submit(
                new ProcessSubTask(processBeforeResultList, dbProcessFunction, dbBatchProcessFunction));
        // 清空等待加工的数据
        processBeforeResultList.clear();
        // 将加工任务结果放入阻塞队列 该步骤会阻塞,直到队列有空闲位置
        try {
            processAfterFutureResultQueue.put(processFutureResult);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
