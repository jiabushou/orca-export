package org.huge.data.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.BaseExecutor;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.jdbc.ConnectionLogger;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;
import org.huge.data.config.OrcaExportVariableHolder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Properties;

import static org.apache.ibatis.mapping.ResultSetType.FORWARD_ONLY;


@Intercepts({
        @Signature(
                type = Executor.class,
                method = "queryCursor",
                args = {MappedStatement.class, Object.class, RowBounds.class}
        )
})
@Slf4j
public class StreamQueryCloseStatementInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 根据isStreamQueryHolder判断是否要拦截
        if (!Objects.isNull(OrcaExportVariableHolder.needInterceptStreamQueryHolder.get()) &&
                OrcaExportVariableHolder.needInterceptStreamQueryHolder.get()) {
            // 获取原始的参数
            Object[] args = invocation.getArgs();
            // 获取原始的MappedStatement
            MappedStatement mappedStatement = (MappedStatement) args[0];
            // 获取原始的Object
            Object parameter = args[1];
            // 获取BoundSql
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            // 获取原始的RowBounds
            RowBounds rowBounds = (RowBounds) args[2];

            // 通过反射设置fetchSize和resultSetType
            Field fetchSizeField = MappedStatement.class.getDeclaredField("fetchSize");
            Field resultSetTypeField = MappedStatement.class.getDeclaredField("resultSetType");

            resultSetTypeField.setAccessible(true);
            resultSetTypeField.set(mappedStatement, FORWARD_ONLY);

            fetchSizeField.setAccessible(true);
            fetchSizeField.set(mappedStatement, Integer.MIN_VALUE);

            // 基于4个参数代替执行doQueryCursor方法
            return doQueryCursor(invocation, mappedStatement, parameter, rowBounds, boundSql);
        }
        return null;
    }

    // 此步操作相当于覆写原方法的逻辑,唯一不同点就是没有调用closeOnCompletion
    private <E> Object doQueryCursor(Invocation invocation, MappedStatement mappedStatement, Object parameter, RowBounds rowBounds, BoundSql boundSql) throws SQLException, NoSuchFieldException, IllegalAccessException {
        // 获取相关成员变量
        //Class<?> targetClass = getSourceTarget(invocation.getTarget());
        BaseExecutor baseExecutor = getSourceTarget(invocation.getTarget());
        Class<?> targetClass = BaseExecutor.class;

        Field wrapperField = targetClass.getDeclaredField("wrapper");
        wrapperField.setAccessible(true);
        Executor wrapper = (Executor) wrapperField.get(baseExecutor);

        Field transactionField = targetClass.getDeclaredField("transaction");
        transactionField.setAccessible(true);
        Transaction transaction = (Transaction) transactionField.get(baseExecutor);

        Field queryStackField = targetClass.getDeclaredField("queryStack");
        queryStackField.setAccessible(true);
        int queryStack = (int) queryStackField.get(baseExecutor);


        // 代替调用invocation.target的doQueryCursor方法
        Configuration configuration = mappedStatement.getConfiguration();
        StatementHandler handler = configuration.newStatementHandler(wrapper, mappedStatement, parameter, rowBounds, null, boundSql);
        Statement stmt = prepareStatement(handler, mappedStatement.getStatementLog(), transaction, queryStack);
        // 不调用该方法，因为sharding-jdbc不支持, 主程序中会关掉sqlSession,不调用该方法理论上不会有问题
        //stmt.closeOnCompletion();
        return handler.<E>queryCursor(stmt);
    }

    private BaseExecutor getSourceTarget(Object target) throws NoSuchFieldException, IllegalAccessException {
        Field hField = Proxy.class.getDeclaredField("h");
        hField.setAccessible(true);

        Field targetField = Plugin.class.getDeclaredField("target");
        targetField.setAccessible(true);

        // 链路 Proxy-->InvocationHandler-->Plugin-->target(Proxy)
        for (;;){
            if (target instanceof Proxy){
                InvocationHandler invocationHandler = (InvocationHandler) hField.get(target);
                if (invocationHandler instanceof Plugin){
                    target = targetField.get(invocationHandler);
                }
            }else {
                // 应用可能开启缓存
                if (target instanceof BaseExecutor){
                    return (BaseExecutor) target;
                }else if (target instanceof CachingExecutor){
                    Field delegateField = CachingExecutor.class.getDeclaredField("delegate");
                    delegateField.setAccessible(true);
                    return (BaseExecutor) delegateField.get(target);
                }
            }
        }
    }

    private Statement prepareStatement(StatementHandler handler, Log statementLog, Transaction transaction, int queryStack) throws SQLException {
        Statement stmt;
        Connection connection = getConnection(statementLog, transaction, queryStack);
        stmt = handler.prepare(connection, transaction.getTimeout());
        handler.parameterize(stmt);
        return stmt;
    }

    protected Connection getConnection(Log statementLog, Transaction transaction, int queryStack) throws SQLException {
        Connection connection = transaction.getConnection();
        if (statementLog.isDebugEnabled()) {
            return ConnectionLogger.newInstance(connection, statementLog, queryStack);
        } else {
            return connection;
        }
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
