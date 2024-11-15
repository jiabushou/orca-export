package org.huge.data.controller;


import org.huge.data.dao.OrcaExportDao;
import org.huge.data.domain.EasyExportTask;
import org.huge.data.enums.ExportTaskStatusEnum;
import org.huge.data.enums.TriggerSourceEnum;
import org.huge.data.service.trigger.EventBasedTrigger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.Objects;

public class OrcaExportController {

    @Resource
    private OrcaExportDao orcaExportDao;

    @Resource
    private EventBasedTrigger eventBasedTrigger;


    @GetMapping("/orca-export/triggerEvent")
    public String triggerEvent() {
        eventBasedTrigger.triggerAsyncExportTaskHandle(TriggerSourceEnum.EVENT_BASED);
        return "触发成功";
    }

    @GetMapping("/orca-export/updateStatus")
    public String updateStatus(@RequestParam("taskId") Long taskId, @RequestParam("status") ExportTaskStatusEnum status) {
        if (taskId == null || status == null) {
            return "参数错误";
        }
        if (Objects.equals(status.getStatus(), ExportTaskStatusEnum.RUNNING.getStatus())) {
            return "不允许更新任务状态为运行中";
        }
        // 除原状态=运行中外,其他状态都可以更新
        EasyExportTask easyExportTask = orcaExportDao.selectExportTaskById(taskId);
        if (easyExportTask == null) {
            return "任务不存在";
        }
        if (Objects.equals(easyExportTask.getStatus(), ExportTaskStatusEnum.RUNNING.getStatus())) {
            return "任务正在运行中,不允许更新状态";
        }
        easyExportTask.setStatus(status.getStatus());
        int updateCount = orcaExportDao.updateExportTaskById(easyExportTask);
        if (updateCount == 1) {
            return "更新成功";
        } else {
            return "更新失败" + updateCount;
        }
    }
}
