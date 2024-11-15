package org.huge.data.interfaces;

import java.util.List;

/**
 * @description: 数据库查询函数接口
 *
 * @param <T> 查询参数类型
 * @param <R> 返回值类型
 */
@FunctionalInterface
public interface DBQueryFunction <T, R>{

    List<R> query(T t);
}
