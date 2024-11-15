package org.huge.data.interceptors;


import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.huge.data.config.OrcaExportVariableHolder;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

@Intercepts({
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}
        ),
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
@Slf4j
public class PureQueryParamsStoreInterceptor implements Interceptor {


    @Override
    public Object intercept(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        // 判断needInterceptPureQueryHolder是否为true, 如果为true, 则表示此次查询需要拦截
        if (!Objects.isNull(OrcaExportVariableHolder.needInterceptPureQueryHolder.get()) && OrcaExportVariableHolder.needInterceptPureQueryHolder.get()) {
            // 获取原始的参数
            Object[] args = invocation.getArgs();
            // 获取原始的MappedStatement
            MappedStatement mappedStatement = (MappedStatement) args[0];
            String statementId = mappedStatement.getId();

            // 将三个参数设置到ThreadLocal中
            OrcaExportVariableHolder.statementIdHolder.set(statementId);
            OrcaExportVariableHolder.parameterHolder.set(args[1]);
            OrcaExportVariableHolder.rowBoundsHolder.set((RowBounds) args[2]);

            // 直接返回空的结果集,不再继续执行下去
            return new ArrayList<>();
        }
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
