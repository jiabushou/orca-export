package org.huge.data.dao;



import org.huge.data.domain.EasyExportTask;
import org.huge.data.enums.ExportTaskStatusEnum;

import java.util.List;

public interface OrcaExportDao {

    int insertExportTask(EasyExportTask exportTask);

    EasyExportTask selectExportTaskById(Long taskId);

    int updateExportTaskById(EasyExportTask easyExportTask);

    List<EasyExportTask> selectExportTasksByStatus(List<ExportTaskStatusEnum> exportTaskStatusEnums);

    List<EasyExportTask> selectExportTasksByStatusWithPageForUpdate(List<ExportTaskStatusEnum> exportTaskStatusEnums, int offsetSize, int limitSize);
}
