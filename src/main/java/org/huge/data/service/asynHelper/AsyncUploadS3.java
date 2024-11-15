package org.huge.data.service.asynHelper;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.ProxyOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.huge.data.enums.OrcaExportResultCodeEnum;
import org.huge.data.exception.OrcaExportException;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;


public interface AsyncUploadS3 {


    /**
     * 将文件上传到文件服务器
     * @param fileName 文件名
     * @param workbook excel文件的内存对象
     * @return 下载URL
     */
    String upload(String fileName, Workbook workbook);
}
