package org.huge.data.config;

import lombok.extern.slf4j.Slf4j;
import org.huge.data.service.handler.AsyncExportTask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步导出任务常驻线程池
 */
@Slf4j
public class AsyncExportThreadPool {

    // 用于处理异步任务的线程池
    public static final ThreadPoolExecutor asyncTaskManageThreadPoolExecutor;

    // 用于定时触发异步任务处理器的延迟任务
    public static final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;


    /**
     * 1. 不设置非核心线程池,因为判断最大线程数,需要获取全局锁
     * 2. 设置核心线程池为1,可根据资源调整
     * 3. 设置容量为10的阻塞队列,避免无限制的任务堆积
     * 4. 自定义拒绝策略,将任务id打印日志,方便排查
     */
    static {
        int corePoolSize = 1;
        int maximumPoolSize = 1;
        long keepAliveTime = 0;
        TimeUnit unit = TimeUnit.MILLISECONDS;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(10);
        // 自定义拒绝策略 当拒绝时不要抛出异常
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();;
        asyncTaskManageThreadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                handler);
    }

    static {
        // 创建定时任务线程池 核心线程数为1
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
    }
}
