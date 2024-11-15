package org.huge.data.exception;

import org.huge.data.enums.OrcaExportResultCodeEnum;

public class OrcaExportException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private OrcaExportResultCodeEnum code;

    private String message;

    public OrcaExportException(OrcaExportResultCodeEnum code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public OrcaExportException(OrcaExportResultCodeEnum code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public OrcaExportException(OrcaExportResultCodeEnum code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public OrcaExportResultCodeEnum getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
