package org.huge.data.config;

import org.huge.data.interceptors.ExportTotalCountInterceptor;
import org.huge.data.interceptors.PureQueryParamsStoreInterceptor;
import org.huge.data.interceptors.StreamQueryCloseStatementInterceptor;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 配置Mybatis拦截器
 */
@Configuration
public class MybatisInterceptorConfig {

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            configuration.addInterceptor(new PureQueryParamsStoreInterceptor());
            configuration.addInterceptor(new ExportTotalCountInterceptor());
            configuration.addInterceptor(new StreamQueryCloseStatementInterceptor());
        };
    }
}
