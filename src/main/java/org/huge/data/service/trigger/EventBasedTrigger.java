package org.huge.data.service.trigger;


import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.huge.data.config.AsyncExportThreadPool;
import org.huge.data.config.OrcaExportProperties;
import org.huge.data.dao.OrcaExportDao;
import org.huge.data.enums.OrcaExportResultCodeEnum;
import org.huge.data.enums.TriggerSourceEnum;
import org.huge.data.exception.OrcaExportException;
import org.huge.data.service.asynHelper.AsyncUploadS3;
import org.huge.data.service.handler.AsyncExportTask;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Slf4j
public class EventBasedTrigger {

    @Resource
    private OrcaExportDao orcaExportDao;

    @Resource
    private SqlSessionFactory sqlSessionFactory;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private AsyncExportTriggerHelper asyncExportTriggerHelper;

    @Resource
    private OrcaExportProperties orcaExportProperties;

    /**
     * 触发异步导出任务管理器
     * @description 有两个触发来源,基于事件触发,基于时间触发
     */
    public void triggerAsyncExportTaskHandle(TriggerSourceEnum triggerSourceEnum){
        // 1. 根据是事件触发/时间触发决定取多少条,避免对太多行加行锁
        // 参数判空
        if (Objects.isNull(triggerSourceEnum)){
            throw new OrcaExportException(OrcaExportResultCodeEnum.ERROR_INNER_COMPONENT, "触发异步导出任务管理器失败,触发来源为空");
        }
        // 如果是事件触发的,则只取一条;如果是时间触发的,则取5条;否则报错
        int handleCount = triggerSourceEnum.equals(TriggerSourceEnum.EVENT_BASED) ? 1 :
                triggerSourceEnum.equals(TriggerSourceEnum.TIME_BASED) ? 5 : -1;
        if (handleCount == -1){
            throw new OrcaExportException(OrcaExportResultCodeEnum.ERROR_INNER_COMPONENT, "触发异步导出任务管理器失败,触发来源非法" + triggerSourceEnum);
        }

        List<Long> asyncTaskIdsForHandle = asyncExportTriggerHelper.getAsyncExportTaskForHandle(handleCount);

        // 3. 将待处理任务集合提交到线程池中
        asyncTaskIdsForHandle.forEach(asyncTaskIdForHandle ->
                AsyncExportThreadPool.asyncTaskManageThreadPoolExecutor.submit(
                        new AsyncExportTask(
                                asyncTaskIdForHandle,
                                orcaExportDao,
                                sqlSessionFactory,
                                redissonClient,
                                orcaExportProperties,
                                this)));
    }
}
