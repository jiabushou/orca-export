package org.huge.data.config;

import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

/**
 * 用于存储EasyExport组件的一些常驻变量
 */
public class OrcaExportVariableHolder {

    // -1: 表示此次查询需要获取导出总数 null: 表示此次查询不需要获取导出总数
    public static ThreadLocal<Integer> exportCountHolder = new ThreadLocal<>();

    // 组件执行流式查询的标识,避免拦截到用户自己实现的流式查询 true: 表示此次查询需要执行流式查询 false: 表示此次查询不需要执行流式查询
    public static ThreadLocal<Boolean> needInterceptStreamQueryHolder = new ThreadLocal<>();

    // 组件执行普通查询的标记,用于避免拦截到用户自己实现的普通查询 true: 表示此次执行的普通查询需要拦截 false: 表示此次执行的普通查询不需要拦截
    public static ThreadLocal<Boolean> needInterceptPureQueryHolder = new ThreadLocal<>();

    // 存储拦截普通查询后的参数, 用于后续作为调用流式查询的参数
    public static ThreadLocal<String> statementIdHolder = new ThreadLocal<>();

    public static ThreadLocal<Object> parameterHolder = new ThreadLocal<>();

    public static ThreadLocal<RowBounds> rowBoundsHolder = new ThreadLocal<>();

    // 同一时刻执行同步导出的最大数量
    public static final Semaphore SYNC_EXPORT_MAX_COUNT = new Semaphore(10);

    // 加工子任务结束标志
    public static final Future<List<Object>> PROCESS_SUB_TASK_END = CompletableFuture.completedFuture(null);

}
