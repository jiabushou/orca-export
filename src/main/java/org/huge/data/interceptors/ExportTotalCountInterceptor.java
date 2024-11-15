package org.huge.data.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.huge.data.config.OrcaExportVariableHolder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

/**
 * 导出总数Mybatis拦截器
 */
@Intercepts({
        @Signature(
                type = StatementHandler.class,
                method = "prepare",
                args = {Connection.class, Integer.class}
        ),
        @Signature(
                type = ResultSetHandler.class,
                method = "handleResultSets",
                args = {Statement.class}
        )
})
@Slf4j
public class ExportTotalCountInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 判断当前拦截的是哪个方法,执行对应的拦截逻辑
        if (invocation.getTarget() instanceof StatementHandler &&
                !Objects.isNull(OrcaExportVariableHolder.exportCountHolder.get()) &&
                OrcaExportVariableHolder.exportCountHolder.get() == -1) {
            return interceptStatementHandler(invocation);
        } else if (invocation.getTarget() instanceof ResultSetHandler &&
                !Objects.isNull(OrcaExportVariableHolder.exportCountHolder.get()) &&
                OrcaExportVariableHolder.exportCountHolder.get() == -1) {
            return interceptResultSetHandler(invocation);
        }
        return invocation.proceed();
    }

    /**
     * 拦截ResultSetHandler的handleResultSets方法,获取查询总数
     * @param invocation
     * @return
     * @throws SQLException
     */
    private Object interceptResultSetHandler(Invocation invocation) throws SQLException {
        // 1. 获取入参Statement
        Statement statement = (Statement) invocation.getArgs()[0];
        // 2. 获取Statement的ResultSet
        ResultSet resultSet = statement.getResultSet();
        // 3. 获取第一个结果,其为int类型,即为查询总数
        int count = 0;
        try {
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        // 4. 将总数设置到ThreadLocal中
        OrcaExportVariableHolder.exportCountHolder.set(count);
        // 注:不调用invocation.proceed()方法,而是返回空集合,保证整个流程执行完毕
        return new ArrayList<>();
    }

    /**
     * 拦截StatementHandler的prepare方法,修改sql为查询count的sql
     * @description
     * 不宜采用 select * from xxx 改为 select count(*) from xxx 的方式,
     * 考虑如下场景: select * from xxx group by xxx, 则会查询出来的总数是每组的总数,而不是总数
     * @param invocation
     * @return
     * @throws NoSuchFieldException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private Object interceptStatementHandler(Invocation invocation) throws NoSuchFieldException, InvocationTargetException, IllegalAccessException {
        // 1. 获取StatementHandler
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        // 2. 生成查询count的sql
        BoundSql boundSql = statementHandler.getBoundSql();
        String countSql = "select count(*) from (" + boundSql.getSql() + ") t";
        // 3. 利用反射修改BoundSql的sql
        Field sqlField = BoundSql.class.getDeclaredField("sql");
        sqlField.setAccessible(true);
        sqlField.set(boundSql, countSql);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Interceptor.super.plugin(target);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }
}
