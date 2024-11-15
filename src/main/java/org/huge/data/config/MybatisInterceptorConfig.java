package org.huge.data.config;

import com.ke.utopia.ocra.interceptors.ExportTotalCountInterceptor;
import com.ke.utopia.ocra.interceptors.PureQueryParamsStoreInterceptor;
import com.ke.utopia.ocra.interceptors.StreamQueryCloseStatementInterceptor;
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
            //configuration.addInterceptor(new StreamQueryParamsChangeInterceptor());
            configuration.addInterceptor(new StreamQueryCloseStatementInterceptor());
        };
    }
}
