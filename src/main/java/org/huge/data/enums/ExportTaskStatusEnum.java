package org.huge.data.enums;

import org.huge.data.exception.OrcaExportException;

public enum ExportTaskStatusEnum {

    WAITING(0, "等待中"),
    // 暂无使用, 保留
    CANCEL(1, "已取消"),
    SUCCESS(2, "已完成"),

    RUNNING(3, "运行中"),

    FAILED(4, "失败");

    private Integer status;

    private String description;

    ExportTaskStatusEnum(Integer status, String description) {
        this.status = status;
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public static ExportTaskStatusEnum fromStatus(Integer status) {
        for (ExportTaskStatusEnum tmpEnum : ExportTaskStatusEnum.values()) {
            if (status.equals(tmpEnum.getStatus())) {
                return tmpEnum;
            }
        }
        throw new OrcaExportException(OrcaExportResultCodeEnum.ERROR_PARAM_ILLEGAL, "没有对应的枚举类型：" + status);
    }
}
