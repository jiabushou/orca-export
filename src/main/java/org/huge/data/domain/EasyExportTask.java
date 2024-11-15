package org.huge.data.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EasyExportTask {
    private Long id;

    private Integer status;

    private String dbFunctionMethod;

    private String dbFunctionParams;

    private String dbResultsProcess;

    private String fileName;

    private String downloadUrlProcessorClassName;

    private String downloadUrlProcessorParams;

    private Date triggerExecutedTime;

    private Date finishedTime;

    private String uploadClassName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDbFunctionMethod() {
        return dbFunctionMethod;
    }

    public void setDbFunctionMethod(String dbFunctionMethod) {
        this.dbFunctionMethod = dbFunctionMethod == null ? null : dbFunctionMethod.trim();
    }

    public String getDbFunctionParams() {
        return dbFunctionParams;
    }

    public void setDbFunctionParams(String dbFunctionParams) {
        this.dbFunctionParams = dbFunctionParams == null ? null : dbFunctionParams.trim();
    }

    public String getDbResultsProcess() {
        return dbResultsProcess;
    }

    public void setDbResultsProcess(String dbResultsProcess) {
        this.dbResultsProcess = dbResultsProcess == null ? null : dbResultsProcess.trim();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    public String getDownloadUrlProcessorClassName() {
        return downloadUrlProcessorClassName;
    }

    public void setDownloadUrlProcessorClassName(String downloadUrlProcessorClassName) {
        this.downloadUrlProcessorClassName = downloadUrlProcessorClassName == null ? null : downloadUrlProcessorClassName.trim();
    }

    public String getDownloadUrlProcessorParams() {
        return downloadUrlProcessorParams;
    }

    public void setDownloadUrlProcessorParams(String downloadUrlProcessorParams) {
        this.downloadUrlProcessorParams = downloadUrlProcessorParams == null ? null : downloadUrlProcessorParams.trim();
    }

    public Date getTriggerExecutedTime() {
        return triggerExecutedTime;
    }

    public void setTriggerExecutedTime(Date triggerExecutedTime) {
        this.triggerExecutedTime = triggerExecutedTime;
    }

    public Date getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(Date finishedTime) {
        this.finishedTime = finishedTime;
    }

    public String getUploadClassName() {
        return uploadClassName;
    }

    public void setUploadClassName(String uploadClassName) {
        this.uploadClassName = uploadClassName == null ? null : uploadClassName.trim();
    }
}