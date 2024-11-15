package org.huge.data.enums;

import org.huge.data.exception.OrcaExportException;

public enum TriggerSourceEnum {

    EVENT_BASED(0, "基于事件"),
    TIME_BASED(1, "基于时间");

    private Integer status;

    private String description;

    TriggerSourceEnum(Integer status, String description) {
        this.status = status;
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public static TriggerSourceEnum fromStatus(Integer status) {
        for (TriggerSourceEnum tmpEnum : TriggerSourceEnum.values()) {
            if (status.equals(tmpEnum.getStatus())) {
                return tmpEnum;
            }
        }
        throw new OrcaExportException(OrcaExportResultCodeEnum.ERROR_PARAM_ILLEGAL, "没有对应的枚举类型：" + status);
    }
}
