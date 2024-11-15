package org.huge.data.interfaces;

/**
 * 数据库查询结果加工函数接口
 * @param <T> 被加工的数据类型
 * @param <R> 加工后的数据类型
 */
@FunctionalInterface
public interface DBProcessFunction<T, R> {

    R process(T t);
}
