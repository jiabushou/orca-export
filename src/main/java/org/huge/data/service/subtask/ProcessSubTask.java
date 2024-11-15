package org.huge.data.service.subtask;


import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.huge.data.enums.OrcaExportResultCodeEnum;
import org.huge.data.exception.OrcaExportException;
import org.huge.data.interfaces.DBBatchProcessFunction;
import org.huge.data.interfaces.DBProcessFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;


@NoArgsConstructor
@Slf4j
public class ProcessSubTask implements Callable<List<Object>> {

    public ProcessSubTask(List<Object> processBeforeResultList, DBProcessFunction<Object, Object> dbProcessFunction, DBBatchProcessFunction<Object, Object> dbBatchProcessFunction) {
        // 拷贝一份加工前结果
        this.processBeforeResultList = new ArrayList<>(processBeforeResultList);
        this.dbProcessFunction = dbProcessFunction;
        this.dbBatchProcessFunction = dbBatchProcessFunction;
    }

    private List<Object> processBeforeResultList;

    private DBProcessFunction<Object, Object> dbProcessFunction;

    private DBBatchProcessFunction<Object, Object> dbBatchProcessFunction;

    @Override
    public List<Object> call() {
        // 如果加工函数为空,则直接返回加工前结果
        if (Objects.isNull(dbProcessFunction) && Objects.isNull(dbBatchProcessFunction)){
            return processBeforeResultList;
        }

        // 如果加工函数不为空,则执行加工函数,返回加工后结果
        // 注: 这个list因为还会被写任务获取,所以必须使用线程安全的list
        List<Object> processAfterResultList = new CopyOnWriteArrayList<>();

        // a.1 批量加工逻辑
        if (Objects.nonNull(dbBatchProcessFunction)) {
            //log.info("开始执行批量加工函数");
            Object tempObject = dbBatchProcessFunction.process(processBeforeResultList);
            // 判断tempObject是否为List
            if (tempObject instanceof List) {
                processAfterResultList = (List<Object>) tempObject;
            } else {
                //log.error("批量加工函数返回结果不是List,返回结果={}", tempObject);
                throw new OrcaExportException(OrcaExportResultCodeEnum.ERROR_PARAM_ILLEGAL, "批量加工函数返回结果不是List");
            }
            //log.info("批量加工函数执行完毕,加工前结果数量={},加工后结果数量={}", processBeforeResultList.size(), processAfterResultList.size());
            return processAfterResultList;
        }

        // a.2 逐条加工逻辑
        //log.info("开始执行逐条加工函数");
        for (Object data : processBeforeResultList) {
            Object processAfterData = dbProcessFunction.process(data);
            if (Objects.nonNull(processAfterData)) {
                processAfterResultList.add(processAfterData);
            }
        }
        //log.info("逐条加工函数执行完毕,加工前结果数量={},加工后结果数量={}", processBeforeResultList.size(), processAfterResultList.size());
        return processAfterResultList;
    }
}
