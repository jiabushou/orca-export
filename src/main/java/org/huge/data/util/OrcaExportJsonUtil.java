package org.huge.data.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.huge.data.enums.OrcaExportResultCodeEnum;
import org.huge.data.exception.OrcaExportException;

import java.io.IOException;

public class OrcaExportJsonUtil {
    private static final class ObjectMapperSingletonHolder {
        private static final ObjectMapper INSTANCE = new ObjectMapper();
    }

    private static ObjectMapper getInstance() {
        return ObjectMapperSingletonHolder.INSTANCE;
    }

    /**
     * Object 转换为 JSON字符串
     * @param obj
     * @return
     */
    public static String obj2JsonStr(Object obj) {
        if (obj == null) {
            return "";
        }
        try {
            return getInstance().writeValueAsString(obj);
        } catch (IOException e) {
            throw new OrcaExportException(OrcaExportResultCodeEnum.ERROR_OUT_COMPONENT, e);
        }
    }

    /**
     * JSON字符串 转换为 Object
     * @description: 知道自己的json字符串是什么类型的，可以直接用这个方法
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T jsonStr2Obj(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return getInstance().readValue(json, clazz);
        } catch (IOException e) {
            throw new OrcaExportException(OrcaExportResultCodeEnum.ERROR_OUT_COMPONENT, e);
        }
    }

    /**
     * JSON字符串 转换为 Object
     * @description: 不知道自己的json字符串是什么类型的，可以直接用这个方法 这种方式比JavaType简洁
     * @param json
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T jsonStr2Obj(String json, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return getInstance().readValue(json, typeReference);
        } catch (IOException e) {
            throw new OrcaExportException(OrcaExportResultCodeEnum.ERROR_OUT_COMPONENT, e);
        }
    }

    /**
     * JSON字符串 转换为 Object
     * @description: 不知道自己的json字符串是什么类型的，可以直接用这个方法 这种方式比TypeReference更加灵活
     * @param clazz
     * @return boolean
     */
    public static <T> T jsonStr2Obj(String json, JavaType clazz) {
        try {
            return getInstance().readValue(json, clazz);
        } catch (IOException e) {
            throw new OrcaExportException(OrcaExportResultCodeEnum.ERROR_OUT_COMPONENT, e);
        }
    }
}
