说明：组件目前仍有缺陷，由于异步导出需要处理存放位置,目前这块尚未完成（下述第3步），但内部实现思路可以借鉴

导出任务提交入口：org.huge.data.service.OrcaExportEntryService.submitExportTask
全量配置信息位置：org.huge.data.config.OrcaExportProperties

1. 建立导出任务存储表
```sql
CREATE TABLE `easy_export_task` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `status` int NOT NULL DEFAULT '0' COMMENT '任务状态,0:等待中,1:已取消,2:已完成,3:运行中,4:失败',
  `db_function_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `db_function_params` varchar(2048) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `db_results_process` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '数据库返回结果加工逻辑',
  `ctime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `mtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `file_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '导出文件名',
  `download_url_processor_class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '异步下载URL处理类',
  `download_url_processor_params` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '异步下载URL处理类额外入参',
  `trigger_executed_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '触发执行时间(重复触发时记录最新触发时间)',
  `finished_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '完成时间(包括成功,失败,取消)',
  `upload_class_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '上传至文件服务器处理类',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
```
2. 导入依赖
```xml
<dependency>
    <groupId>org.huge.data</groupId>
    <artifactId>orca-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
3. 根据实际情况修改配置信息,配置信息位置：org.huge.data.config.OrcaExportProperties
```properties
#导出文件存储路径
orca.easy-export.export-file-path=D:/export
```
4. 通过方法org.huge.data.service.OrcaExportEntryService.submitExportTask提交导出任务
5. 任务处理异常时,通过org.huge.data.controller.OrcaExportController手动处理
