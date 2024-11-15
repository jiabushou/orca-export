package org.huge.data.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.huge.data.domain.EasyExportTask;
import org.huge.data.domain.EasyExportTaskExample;

public interface EasyExportTaskMapper {
    long countByExample(EasyExportTaskExample example);

    int deleteByExample(EasyExportTaskExample example);

    int deleteByPrimaryKey(Long id);

    int insert(EasyExportTask record);

    int insertSelective(EasyExportTask record);

    List<EasyExportTask> selectByExample(EasyExportTaskExample example);

    EasyExportTask selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") EasyExportTask record, @Param("example") EasyExportTaskExample example);

    int updateByExample(@Param("record") EasyExportTask record, @Param("example") EasyExportTaskExample example);

    int updateByPrimaryKeySelective(EasyExportTask record);

    int updateByPrimaryKey(EasyExportTask record);
}