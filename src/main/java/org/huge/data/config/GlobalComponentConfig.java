package org.huge.data.config;



import org.huge.data.controller.OrcaExportController;
import org.huge.data.dao.OrcaExportDao;
import org.huge.data.dao.OrcaExportDaoImpl;
import org.huge.data.service.OrcaExportEntryService;
import org.huge.data.service.asynHelper.AsyncUploadS3;
import org.huge.data.service.handler.SyncExportHandler;
import org.huge.data.service.trigger.AsyncExportTriggerHelper;
import org.huge.data.service.trigger.EventBasedTrigger;
import org.huge.data.service.trigger.TimeBasedTrigger;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 全局Bean配置
 */
@Configuration
@EnableConfigurationProperties(OrcaExportProperties.class)
public class GlobalComponentConfig {

    @Bean
    public SyncExportHandler syncExportHandler() {
        return new SyncExportHandler();
    }

    @Bean
    public AsyncExportTriggerHelper asyncExportTriggerHelper() {
        return new AsyncExportTriggerHelper();
    }

    @Bean
    public TimeBasedTrigger timeBasedTrigger() {
        return new TimeBasedTrigger();
    }

    @Bean
    public EventBasedTrigger eventBasedTrigger() {
        return new EventBasedTrigger();
    }


    @Bean
    public OrcaExportDao easyExportDao() {
        return new OrcaExportDaoImpl();
    }

    @Bean
    public OrcaExportEntryService easyExportEntryService() {
        return new OrcaExportEntryService();
    }


    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setBasePackage("org.huge.data.mapper"); // 设置你的Mapper接口所在的包
        return configurer;
    }

    @Bean
    public OrcaExportFactoryBean easyExportFactoryBean() {
        return new OrcaExportFactoryBean();
    }

    @Bean
    public OrcaExportController easyExportController() {
        return new OrcaExportController();
    }
}
