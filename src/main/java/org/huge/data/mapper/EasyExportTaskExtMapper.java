package org.huge.data.mapper;

import org.apache.ibatis.annotations.Param;
import org.huge.data.domain.EasyExportTask;

import java.util.List;

public interface EasyExportTaskExtMapper {

    List<EasyExportTask> selectExportTasksByStatusWithPageForUpdate(@Param("exportTaskStatusList") List<Integer> exportTaskStatusList, @Param("offsetSize") int offsetSize, @Param("limitSize") int limitSize);
}
