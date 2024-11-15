package org.huge.data.service.subtask;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.huge.data.config.OrcaExportProperties;
import org.huge.data.config.OrcaExportVariableHolder;
import org.huge.data.enums.OrcaExportResultCodeEnum;
import org.huge.data.exception.OrcaExportException;
import org.huge.data.service.asynHelper.AsyncUploadS3;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class WriteSubTask implements Callable<String> {

    private BlockingQueue<Future<List<Object>>> processFutureResultQueue;

    private Long asyncTaskId;

    private String fileName;

    private Class<?> dataClass;

    private OrcaExportProperties orcaExportProperties;

    private AsyncUploadS3 asyncUploadS3;

    @Override
    public String call() {
        // 内存中仅保留500行数据,超过500行则写入磁盘临时文件
        Workbook workbook = new SXSSFWorkbook(orcaExportProperties.getSxssfRowAccessWindowSize());
        int rowSize = 0;
        // 创建sheet
        Sheet sheet = workbook.createSheet("sheet1");
        Row rowTemp = sheet.createRow(rowSize);

        // 3.1 第一行:表头
        List<Field> fields = getManagedFields(dataClass.getDeclaredFields());
        String[] headerNameList = new String[fields.size()];

        for (int i = 0; i < fields.size(); i++) {
            ExcelProperty apiModelProperty = fields.get(i).getAnnotation(ExcelProperty.class);
            headerNameList[i] = apiModelProperty.value()[0];
        }

        for (int i = 0; i < headerNameList.length; i++) {
            rowTemp.createCell(i).setCellValue(headerNameList[i]);
        }

        Sheet tempSheet = sheet;

        //int count = 1;
        for (;;){
            // 1. 从阻塞队列队头取出数据
            Future<List<Object>> processFutureResult = null;
            try {
                //log.info("阻塞式获取第{}个加工结果", count);
                // 阻塞式获取
                processFutureResult = processFutureResultQueue.take();
                //log.info("阻塞式获取第{}个加工结果成功", count);
                //count++;
                // 如果是终止标志,则跳出循环
                if(Objects.equals(OrcaExportVariableHolder.PROCESS_SUB_TASK_END, processFutureResult)){
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // 2. 获取加工后的数据
            List<Object> processAfterResultList = null;
            try {
                processAfterResultList = processFutureResult.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("任务ID:{}, 加工结果获取异常,终止本次导出任务", asyncTaskId, e);
                throw new RuntimeException(e);
            }

            // 3. 将加工后的数据流式写入workbook

            // 3.2 其它行:数据
            // 遍历每行数据
            for (Object data : processAfterResultList) {
                // 如果当前sheet行数达到1000000
                if (tempSheet.getLastRowNum() >= 1000000){

                    // sheet限制
                    if (workbook.getNumberOfSheets() >= 11){
                        throw new OrcaExportException(OrcaExportResultCodeEnum.ERROR_INNER_COMPONENT, "导出sheet数量超过10,请检查导出数据");
                    }

                    int numberOfSheetsNext = workbook.getNumberOfSheets() + 1;
                    log.info("创建第{}个sheet", numberOfSheetsNext);
                    tempSheet = workbook.createSheet("sheet" + numberOfSheetsNext);

                    // 往新sheet写表头数据
                    rowSize = 0;
                    rowTemp = tempSheet.createRow(rowSize);
                    for (int i = 0; i < fields.size(); i++) {
                        ExcelProperty apiModelProperty = fields.get(i).getAnnotation(ExcelProperty.class);
                        headerNameList[i] = apiModelProperty.value()[0];
                    }

                    for (int i = 0; i < headerNameList.length; i++) {
                        rowTemp.createCell(i).setCellValue(headerNameList[i]);
                    }
                }
                // 行数自增
                rowSize++;
                // 创建Row对象
                rowTemp = tempSheet.createRow(rowSize);

                // 遍历每列数据
                for (int i = 0; i < fields.size(); i++) {
                    fields.get(i).setAccessible(true);
                    Object fieldValue = null;
                    try {
                        fieldValue = fields.get(i).get(data);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    if(!Objects.isNull(fieldValue)){
                        rowTemp.createCell(i).setCellValue(fieldValue.toString());
                    }
                }
            }
        }

        // 4. 上传到S3
        return asyncUploadS3.upload(fileName, workbook);
    }

    private List<Field> getManagedFields(Field[] declaredFields) {
        List<Field> list = new ArrayList<>();
        for (Field field : declaredFields) {
            ExcelProperty apiModelProperty = field.getAnnotation(ExcelProperty.class);
            if(!Objects.isNull(apiModelProperty)){
                list.add(field);
            }
        }
        return list;
    }


}
