package org.huge.data.service.trigger;


import lombok.extern.slf4j.Slf4j;
import org.huge.data.config.AsyncExportThreadPool;
import org.huge.data.config.OrcaExportProperties;
import org.huge.data.enums.TriggerSourceEnum;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务扫描未完成的导出任务
 *
 *
 */
@Slf4j
public class TimeBasedTrigger implements Runnable {

    @Resource
    private EventBasedTrigger eventBasedTrigger;

    @Resource
    private OrcaExportProperties orcaExportProperties;

    @Override
    public void run() {
        if (orcaExportProperties.getScheduledTrigger()){
            log.info(">>>定时任务 触发 异步导出任务管理器<<<");
            eventBasedTrigger.triggerAsyncExportTaskHandle(TriggerSourceEnum.TIME_BASED);
            // 随机等待一段时间,避免多个服务同时触发
            try {
                TimeUnit.SECONDS.sleep((long) (Math.random() * 10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    // bean创建并初始化后,将当前任务提交给定时任务线程池处理
    @PostConstruct
    public void init() {
        // 将当前任务提交给定时任务线程池处理, 每次任务执行完后,等待固定时间间隔后再执行一次
        // 产生0到60的随机数,作为延迟时间,避免多个服务同时触发
        long initDelay = (long) (Math.random() * 60);
        AsyncExportThreadPool.scheduledThreadPoolExecutor.scheduleWithFixedDelay(this, initDelay, 600, TimeUnit.SECONDS);
    }
}
