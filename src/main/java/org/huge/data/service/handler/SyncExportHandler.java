package org.huge.data.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.huge.data.config.OrcaExportProperties;
import org.huge.data.config.OrcaExportVariableHolder;
import org.huge.data.enums.OrcaExportResultCodeEnum;
import org.huge.data.exception.OrcaExportException;
import org.huge.data.interfaces.DBBatchProcessFunction;
import org.huge.data.interfaces.DBProcessFunction;
import org.huge.data.interfaces.DBQueryFunction;
import org.huge.data.service.asynHelper.DownloadUrlProcessor;
import org.huge.data.service.trigger.AsyncExportTriggerHelper;
import org.huge.data.util.OrcaExcelUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * 同步导出处理器
 */
@Slf4j
public class SyncExportHandler {

    @Resource
    private AsyncExportTriggerHelper asyncExportTriggerHelper;

    @Resource
    private OrcaExportProperties orcaExportProperties;

    /**
     * 触发同步导出任务处理器
     * @param dbQueryFunction 数据库查询类对象, 该类必须是个spring bean
     * @param dbProcessFunction 数据库查询结果加工类对象, 该类必须是个spring bean
     * @param queryParams 数据库查询类方法入参
     * @param fileName 导出文件名
     * @param response http响应对象,同步导出时使用
     * @return true: 同步导出处理成功 false: 当前导出任务不符合同步导出要求
     * @param <L>
     * @param <T>
     * @param <R>
     */
    public <L,T,R,P> boolean triggerSyncExportTaskHandle(DBQueryFunction<L, T> dbQueryFunction,
                                                         DBProcessFunction<T, R> dbProcessFunction,
                                                         DBBatchProcessFunction<List<T>, List<R>> dbBatchProcessFunction,
                                                         L queryParams,
                                                         String fileName,
                                                         DownloadUrlProcessor downloadUrlProcessor,
                                                         Map<String, String> manageDownloadUrlParams,
                                                         HttpServletResponse response) {
        // 1. 利用Mybatis拦截器获取导出的总数量
        // 1.1 ThreadLocal变量设置为-1,表示需要查询导出总数量
        OrcaExportVariableHolder.exportCountHolder.set(-1);
        // 1.2 执行查询,该查询会被Mybatis拦截器拦截,并设置导出总数量到ThreadLocal变量中
        dbQueryFunction.query(queryParams);
        // 1.3 从ThreadLocal变量中获取导出总数量
        Integer exportCount = OrcaExportVariableHolder.exportCountHolder.get();
        // 1.4 如果导出数量大于1000,返回false,否则继续执行同步导出任务
        if (Objects.isNull(exportCount) || exportCount < 0) {
            throw new OrcaExportException(OrcaExportResultCodeEnum.ERROR_INNER_COMPONENT, "导出总数量获取失败");
        }

        log.info("OrcaExport,本次导出数量为:{}", exportCount);
        if (exportCount > orcaExportProperties.getBoundOfSyncAndAsync()) {
            log.info("DBQueryFunction:{},DBProcessFunction:{},导出总数量为:{},大于:{},不执行同步导出任务,",
                    dbQueryFunction.getClass().getSimpleName(),
                    (Objects.isNull(dbProcessFunction) ?
                            (Objects.isNull(dbBatchProcessFunction)) ? "" : dbBatchProcessFunction.getClass().getSimpleName() :
                            dbProcessFunction.getClass().getSimpleName()),
                    orcaExportProperties.getBoundOfSyncAndAsync(),
                    exportCount);
            return false;
        }

        // 2. 设置同步导出数量-1,如果成功,则执行导出任务
        // 2.1 同步导出数量-1,如果失败,则返回false
        if (!OrcaExportVariableHolder.SYNC_EXPORT_MAX_COUNT.tryAcquire()){
            log.info("DBQueryFunction:{},DBProcessFunction:{},同步导出数量超过最大限制,不执行同步导出任务",
                    dbQueryFunction.getClass().getSimpleName(),
                    (Objects.isNull(dbProcessFunction) ?
                            (Objects.isNull(dbBatchProcessFunction)) ? "" : dbBatchProcessFunction.getClass().getSimpleName() :
                            dbProcessFunction.getClass().getSimpleName()));
            return false;
        }
        try{
            log.info("DBQueryFunction:{},DBProcessFunction:{},同步导出任务开始执行",
                    dbQueryFunction.getClass().getSimpleName(),
                    Objects.isNull(dbProcessFunction) ? "" : dbProcessFunction.getClass().getSimpleName());
            // 2.2 ThreadLocal变量设置为null,表示不需要查询导出数据量
            OrcaExportVariableHolder.exportCountHolder.set(null);
            // 2.3 执行数据库查询,此步骤不会被Mybatis拦截器拦截
            List<T> dbResult = dbQueryFunction.query(queryParams);

            // 2.4 执行数据加工
            if (!Objects.isNull(dbProcessFunction) || !Objects.isNull(dbBatchProcessFunction)){
                List<R> exportResult = new ArrayList<>();
                // 逐条加工
                if (!Objects.isNull(dbProcessFunction)) {
                    for (T t : dbResult) {
                        exportResult.add(dbProcessFunction.process(t));
                    }
                }
                // 批量加工
                if (!Objects.isNull(dbBatchProcessFunction)) {
                    exportResult = dbBatchProcessFunction.process(dbResult);
                }

                // 2.5 执行同步导出
                try{
                    OrcaExcelUtil.download(response, fileName, exportResult.get(0).getClass(), exportResult);
                    return true;
                }catch (Exception e){
                    throw new OrcaExportException(OrcaExportResultCodeEnum.ERROR_UNKNOWN, "同步导出任务执行失败", e);
                }
            }

            // 2.5 执行同步导出
            try{
                OrcaExcelUtil.download(response, fileName, dbResult.get(0).getClass(), dbResult);
            }catch (Exception e){
                throw new OrcaExportException(OrcaExportResultCodeEnum.ERROR_UNKNOWN, "同步导出任务执行失败", e);
            }
        } catch (Exception e){
            // 说明: 此处不返回false,而是抛出异常,终止整个导出任务,这种一般是代码bug,所以需要持久化,方便开发人员排查,
            // 2.6 如果导出失败,则将任务持久化到数据库中,并标记为失败
            Long taskId = asyncExportTriggerHelper.persistenceExportTask(dbQueryFunction,
                    dbProcessFunction,
                    dbBatchProcessFunction,
                    queryParams,
                    fileName,
                    downloadUrlProcessor,
                    manageDownloadUrlParams);
            log.error("DBQueryFunction:{},DBProcessFunction:{},同步导出任务执行失败,任务已持久化到数据库中,主键:{}",
                    dbQueryFunction.getClass().getSimpleName(),
                    (Objects.isNull(dbProcessFunction) ?
                            (Objects.isNull(dbBatchProcessFunction)) ? "" : dbBatchProcessFunction.getClass().getSimpleName() :
                            dbProcessFunction.getClass().getSimpleName()),
                    taskId);
            throw new OrcaExportException(OrcaExportResultCodeEnum.ERROR_INNER_COMPONENT, "同步导出任务执行失败", e);
        } finally {
            // 2.7 释放同步导出数量
            OrcaExportVariableHolder.SYNC_EXPORT_MAX_COUNT.release();
        }

        return true;
    }
}
