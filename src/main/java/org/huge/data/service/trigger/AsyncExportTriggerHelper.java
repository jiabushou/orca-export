package org.huge.data.service.trigger;


import org.huge.data.dao.OrcaExportDao;
import org.huge.data.domain.EasyExportTask;
import org.huge.data.enums.ExportTaskStatusEnum;
import org.huge.data.interfaces.DBBatchProcessFunction;
import org.huge.data.interfaces.DBProcessFunction;
import org.huge.data.interfaces.DBQueryFunction;
import org.huge.data.service.asynHelper.AsyncUploadS3;
import org.huge.data.service.asynHelper.DownloadUrlProcessor;
import org.huge.data.util.OrcaExportJsonUtil;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AsyncExportTriggerHelper {

    @Resource
    private OrcaExportDao orcaExportDao;

    @Transactional
    public List<Long> getAsyncExportTaskForHandle(Integer handleCount) {
        // 1. 从数据库中取出待处理任务集合 用for update,添加行锁 开启事务
        List<EasyExportTask> asyncTasksForHandle = orcaExportDao.selectExportTasksByStatusWithPageForUpdate(
                Collections.singletonList(ExportTaskStatusEnum.WAITING),
                0,
                handleCount);

        // 2. 将取出来的任务标记为处理中 修改数据,提交事务
        asyncTasksForHandle.forEach(asyncTaskForHandle -> {
            orcaExportDao.updateExportTaskById(EasyExportTask.builder()
                    .id(asyncTaskForHandle.getId())
                    .status(ExportTaskStatusEnum.RUNNING.getStatus())
                    .build());
        });

        return asyncTasksForHandle.stream().map(EasyExportTask::getId).collect(Collectors.toList());
    }


    public <L,T,R> Long persistenceExportTask(@NotNull DBQueryFunction<L, T> dbQueryFunction,
                                              DBProcessFunction<T, R> dbProcessFunction,
                                              DBBatchProcessFunction<List<T>, List<R>> dbBatchProcessFunction,
                                              L queryParams,
                                              @NotNull String fileName,
                                              DownloadUrlProcessor downloadUrlProcessor,
                                              Map<String, String> manageDownloadUrlParams) {
        // 获取查询类的bean名称
        String dbQueryFunctionBeanName = dbQueryFunction.getClass().getSimpleName();
        dbQueryFunctionBeanName = dbQueryFunctionBeanName.substring(0, 1).toLowerCase() + dbQueryFunctionBeanName.substring(1);
        // 获取加工类的bean名称
        String dbProcessFunctionBeanName = "";
        if (!Objects.isNull(dbProcessFunction)){
            dbProcessFunctionBeanName = dbProcessFunction.getClass().getSimpleName();
            dbProcessFunctionBeanName = dbProcessFunctionBeanName.substring(0, 1).toLowerCase() + dbProcessFunctionBeanName.substring(1);
        }
        if (!Objects.isNull(dbBatchProcessFunction)){
            dbProcessFunctionBeanName = dbBatchProcessFunction.getClass().getSimpleName();
            dbProcessFunctionBeanName = dbProcessFunctionBeanName.substring(0, 1).toLowerCase() + dbProcessFunctionBeanName.substring(1);
        }
        // 获取文件下载Url处理类的bean名称
        String manageDownloadUrlBeanName = downloadUrlProcessor.getClass().getSimpleName();
        manageDownloadUrlBeanName = manageDownloadUrlBeanName.substring(0, 1).toLowerCase() + manageDownloadUrlBeanName.substring(1);
        EasyExportTask easyExportTask = EasyExportTask.builder()
                .status(ExportTaskStatusEnum.WAITING.getStatus())
                .dbFunctionMethod(dbQueryFunctionBeanName)
                .dbFunctionParams(OrcaExportJsonUtil.obj2JsonStr(queryParams))
                .dbResultsProcess(dbProcessFunctionBeanName)
                .fileName(fileName)
                .downloadUrlProcessorClassName(manageDownloadUrlBeanName)
                .downloadUrlProcessorParams(OrcaExportJsonUtil.obj2JsonStr(manageDownloadUrlParams))
                .build();
        int insertCount = orcaExportDao.insertExportTask(easyExportTask);
        if (insertCount != 1){
            throw new RuntimeException("EasyExport,持久化导出任务失败");
        }
        return easyExportTask.getId();
    }
}
