package org.huge.data.enums;

public enum OrcaExportResultCodeEnum {

    SUCCESS(0, "成功"),
    ERROR_PARAM_ILLEGAL(1, "参数校验失败"),
    ERROR_INNER_COMPONENT(2, "内部组件异常"),
    ERROR_OUT_COMPONENT(2, "外部组件异常"),
    ERROR_UNKNOWN(3, "未知异常");

    private Integer code;
    private String message;

    OrcaExportResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static OrcaExportResultCodeEnum fromCode(Integer code) {
        for (OrcaExportResultCodeEnum tmpEnum : OrcaExportResultCodeEnum.values()) {
            if (code.equals(tmpEnum.getCode())) {
                return tmpEnum;
            }
        }
        return null;
    }
}
