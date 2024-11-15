package org.huge.data.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("orca-export")
@Setter
@Getter
public class OrcaExportProperties {

    // 是否开启定时任务触发器
    private Boolean scheduledTrigger = false;

    // 临时excel文件在内存中的常驻行数
    private Integer sxssfRowAccessWindowSize = 500;

    // 最大加工线程数(当所有线程都在处理数据时,新的数据将被阻塞)
    private Integer processThreadCount = 5;

    // 每个加工线程一次处理的数据量
    private Integer processNumberCountPerThread = 100;

    // 导出任务最大执行时长 30分钟 s为单位
    private Long asyncExportTaskExpireTime = 30 * 60L;

    // 是否使用同步导出 0:不使用 1:使用
    private int abandonSyncExport = 1;

    // 同步导出和异步导出的边界值,当导出数量超过此值则使用异步导出,否则使用同步导出
    private int boundOfSyncAndAsync = 1000;
}
