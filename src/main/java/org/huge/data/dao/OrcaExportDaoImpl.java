package org.huge.data.dao;


import org.huge.data.domain.EasyExportTask;
import org.huge.data.domain.EasyExportTaskExample;
import org.huge.data.enums.ExportTaskStatusEnum;
import org.huge.data.mapper.EasyExportTaskExtMapper;
import org.huge.data.mapper.EasyExportTaskMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class OrcaExportDaoImpl implements OrcaExportDao {

    @Resource
    private EasyExportTaskMapper easyExportTaskMapper;

    @Resource
    private EasyExportTaskExtMapper easyExportTaskExtMapper;

    @Override
    public int insertExportTask(EasyExportTask exportTask) {
        return easyExportTaskMapper.insertSelective(exportTask);
    }

    @Override
    public EasyExportTask selectExportTaskById(Long taskId) {
        return easyExportTaskMapper.selectByPrimaryKey(taskId);
    }

    @Override
    public int updateExportTaskById(EasyExportTask easyExportTask) {
        return easyExportTaskMapper.updateByPrimaryKeySelective(easyExportTask);
    }

    @Override
    public List<EasyExportTask> selectExportTasksByStatus(List<ExportTaskStatusEnum> exportTaskStatusEnums) {
        EasyExportTaskExample example = new EasyExportTaskExample();
        example.createCriteria()
                .andStatusIn(exportTaskStatusEnums.stream().map(ExportTaskStatusEnum::getStatus).collect(Collectors.toList()));
        return easyExportTaskMapper.selectByExample(example);
    }

    @Override
    public List<EasyExportTask> selectExportTasksByStatusWithPageForUpdate(List<ExportTaskStatusEnum> exportTaskStatusEnums, int offsetSize, int limitSize) {
        EasyExportTaskExample example = new EasyExportTaskExample();
        example.createCriteria()
                .andStatusIn(exportTaskStatusEnums.stream().map(ExportTaskStatusEnum::getStatus).collect(Collectors.toList()));
        return easyExportTaskExtMapper.selectExportTasksByStatusWithPageForUpdate(
                exportTaskStatusEnums.stream().map(ExportTaskStatusEnum::getStatus).collect(Collectors.toList()),
                offsetSize,
                limitSize);
    }
}
