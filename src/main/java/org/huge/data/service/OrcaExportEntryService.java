package org.huge.data.service;


import lombok.extern.slf4j.Slf4j;
import org.huge.data.config.OrcaExportProperties;
import org.huge.data.enums.OrcaExportResultCodeEnum;
import org.huge.data.enums.TriggerSourceEnum;
import org.huge.data.exception.OrcaExportException;
import org.huge.data.interfaces.CriticalHitComposeKey;
import org.huge.data.interfaces.DBBatchProcessFunction;
import org.huge.data.interfaces.DBProcessFunction;
import org.huge.data.interfaces.DBQueryFunction;
import org.huge.data.service.asynHelper.AsyncUploadS3;
import org.huge.data.service.asynHelper.DownloadUrlProcessor;
import org.huge.data.service.handler.SyncExportHandler;
import org.huge.data.service.trigger.AsyncExportTriggerHelper;
import org.huge.data.service.trigger.EventBasedTrigger;
import org.huge.data.util.OrcaExportJsonUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * 导出服务 入口
 */
@Slf4j
public class OrcaExportEntryService {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private EventBasedTrigger eventBasedTrigger;

    @Resource
    private SyncExportHandler syncExportHandler;

    @Resource
    private AsyncExportTriggerHelper asyncExportTriggerHelper;

    @Resource
    private OrcaExportProperties orcaExportProperties;

    /**
     * 参数含义参考下面的submitExportTask同名方法
     * @param dbQueryFunction
     * @param dbProcessFunction
     * @param queryParams
     * @param fileName
     * @param criticalHitComposeKey
     * @param criticalHitComposeKeyParam
     * @param asyncUploadS3
     * @param downloadUrlProcessor
     * @param manageDownloadUrlParams
     * @param response
     * @param <L>
     * @param <T>
     * @param <R>
     * @param <J>
     */
    public <L,T,R,J> void submitExportTask(@NotNull DBQueryFunction<L, T> dbQueryFunction,
                                           DBProcessFunction<T, R> dbProcessFunction,
                                           L queryParams,
                                           @NotNull String fileName,
                                           CriticalHitComposeKey<J> criticalHitComposeKey,
                                           J criticalHitComposeKeyParam,
                                           AsyncUploadS3 asyncUploadS3,
                                           @NotNull DownloadUrlProcessor downloadUrlProcessor,
                                           Map<String, String> manageDownloadUrlParams,
                                           @NotNull HttpServletResponse response){
        submitExportTask(
                dbQueryFunction,
                dbProcessFunction,
                null,
                queryParams,
                fileName,
                criticalHitComposeKey,
                criticalHitComposeKeyParam,
                asyncUploadS3,
                downloadUrlProcessor,
                manageDownloadUrlParams,
                response);
    }

    /**
     * 提交导出任务主入口
     * @param dbQueryFunction                 必传,数据库查询类对象实例, 该实例对应的类必须是个spring bean
     * @param dbProcessFunction               与dbBatchProcessFunction二选一,数据库查询结果加工类对象实例, 该实例对应的类必须是个spring bean
     * @param dbBatchProcessFunction          与dbBatchProcessFunction二选一,用于批量加工数据, 该实例对应的类必须是个spring bean
     *                                        入参的List的数量小于等于OrcaExportProperties.processNumberCountPerThread,默认值100
     *                                        例如：共510条数据,则会分5次处理,每次处理100条,最后一次处理10条
     * @param queryParams                     非必传,数据库查询对象实例方法入参
     * @param fileName                        必传,导出文件名
     * @param criticalHitComposeKey           非必传,防暴击key生成器 不传则使用默认的防暴击key生成器
     * @param criticalHitComposeKeyParam      非必传,防暴击key生成器入参值
     * @param asyncUploadS3                   必传,文件上传至文件服务器方法
     * @param downloadUrlProcessor            必传,异步下载地址处理器
     * @param manageDownloadUrlParams         非必传,异步下载地址处理器入参
     * @param response                        必传,同步导出时, 用于写入的web流
     * @param <L>
     * @param <T>
     * @param <R>
     * @param <J>
     * @Throws EasyExportException 如果请求过于频繁,则抛出异常,其中的message为"请求过于频繁,请稍后重试"
     */
    public <L,T,R,J> void submitExportTask(@NotNull DBQueryFunction<L, T> dbQueryFunction,
                                           DBProcessFunction<T, R> dbProcessFunction,
                                           DBBatchProcessFunction<List<T>, List<R>> dbBatchProcessFunction,
                                           L queryParams,
                                           @NotNull String fileName,
                                           CriticalHitComposeKey<J> criticalHitComposeKey,
                                           J criticalHitComposeKeyParam,
                                           @NotNull AsyncUploadS3 asyncUploadS3,
                                           @NotNull DownloadUrlProcessor downloadUrlProcessor,
                                           Map<String, String> manageDownloadUrlParams,
                                           @NotNull HttpServletResponse response){
        // 参数校验 dbProcessFunction和dbBatchProcessFunction只能传一个
        if (Objects.nonNull(dbProcessFunction) && Objects.nonNull(dbBatchProcessFunction)){
            throw new OrcaExportException(OrcaExportResultCodeEnum.ERROR_PARAM_ILLEGAL, "dbProcessFunction和dbBatchProcessFunction只能传一个");
        }
        if (Objects.isNull(dbProcessFunction) && Objects.isNull(dbBatchProcessFunction)){
            throw new OrcaExportException(OrcaExportResultCodeEnum.ERROR_PARAM_ILLEGAL, "dbProcessFunction和dbBatchProcessFunction必须传一个");
        }
        // 1. 利用分布式锁限制暴击 如果用户传入了暴击key生成器,则使用用户的暴击key生成器; 否则,使用默认的暴击key生成器
        String lockKey;
        if (!Objects.isNull(criticalHitComposeKey)){
            // 默认防暴击的key=查询类名+":"+查询参数+":"+加工类名
            lockKey = criticalHitComposeKey.getCriticalHitComposeKey(criticalHitComposeKeyParam);
        }else{
            lockKey = defaultCriticalHitComposeKey(dbQueryFunction, dbProcessFunction, dbBatchProcessFunction, queryParams);
        }
        log.info("OrcaExport,防暴击key:{}", lockKey);
        RLock lock = redissonClient.getLock(lockKey);

        try{
            if(lock.tryLock(0L, 5L, TimeUnit.SECONDS)){
                log.info("OrcaExport,数据库查询类:{},数据库查询参数:{},数据库加工参数:{},文件名:{}提交成功,开始处理",
                        dbQueryFunction.getClass().getSimpleName(),
                        OrcaExportJsonUtil.obj2JsonStr(queryParams),
                        Objects.isNull(dbProcessFunction) ?
                                dbBatchProcessFunction.getClass().getSimpleName() :
                                dbProcessFunction.getClass().getSimpleName(),
                        fileName);

                // 同步导出允许存在,则先执行同步导出逻辑
                if (orcaExportProperties.getAbandonSyncExport() == 1){
                    // 2. 将任务以同步导出的方式执行
                    boolean syncExportResult = syncExportHandler.triggerSyncExportTaskHandle(
                            dbQueryFunction,
                            dbProcessFunction,
                            dbBatchProcessFunction,
                            queryParams,
                            fileName,
                            downloadUrlProcessor,
                            manageDownloadUrlParams,
                            response);
                    // 3. 如果同步导出成功,返回; 否则, 以异步导出的方式执行
                    if (syncExportResult){
                        return;
                    }
                }

                // 4. 将任务持久化到数据库中
                asyncExportTriggerHelper.persistenceExportTask(dbQueryFunction,
                        dbProcessFunction,
                        dbBatchProcessFunction,
                        queryParams,
                        fileName,
                        asyncUploadS3,
                        downloadUrlProcessor,
                        manageDownloadUrlParams);
                // 5. 将任务以异步导出的方式执行 触发异步任务导出处理器
                eventBasedTrigger.triggerAsyncExportTaskHandle(TriggerSourceEnum.EVENT_BASED);
            }else {
                throw new OrcaExportException(OrcaExportResultCodeEnum.ERROR_INNER_COMPONENT, "请求过于频繁,请稍后重试");
            }
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }

        // 不要释放锁,而是等待锁自动释放,因为其中的逻辑是异步的,执行很快
        //finally {
        //    if (lock.isLocked() && lock.isHeldByCurrentThread()){
        //        lock.unlock();
        //    }
        //}
    }

    /**
     * 默认暴击key生成器
     * @param dbQueryFunction
     * @param dbProcessFunction
     * @param queryParams
     * @return
     * @param <L>
     * @param <T>
     * @param <R>
     */
    private <L,T,R> String defaultCriticalHitComposeKey(DBQueryFunction<L, T> dbQueryFunction,
                                                        DBProcessFunction<T, R> dbProcessFunction,
                                                        DBBatchProcessFunction<List<T>, List<R>> dbBatchProcessFunction,
                                                        L queryParams){
        return "OrcaExport:" + dbQueryFunction.getClass().getSimpleName() + ":" +
                (Objects.isNull(queryParams) ? "" : OrcaExportJsonUtil.obj2JsonStr(queryParams)) + ":" +
                (Objects.isNull(dbProcessFunction) ?
                        (Objects.isNull(dbBatchProcessFunction)) ? "" : dbBatchProcessFunction.getClass().getSimpleName() :
                        dbProcessFunction.getClass().getSimpleName());
    }
}
