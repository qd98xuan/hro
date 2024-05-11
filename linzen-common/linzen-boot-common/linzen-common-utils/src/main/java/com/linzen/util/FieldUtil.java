package com.linzen.util;

import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.property.PropertyNamer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字段名称相关工具类
 *
 * @author Linzer
 * @since 2022.1.0
 */
@Slf4j
public class FieldUtil {

    /**
     * 获取字段映射
     *
     * @param column 需要解析的 lambda 对象
     * @param <T>    对象的类型
     * @return Field Name
     */
    public static <T> String getField(SFunction<T, ?> column) {
        LambdaMeta meta = LambdaUtils.extract(column);
        return PropertyNamer.methodToProperty(meta.getImplMethodName());
    }

    /**
     * 获取类的所有属性，包括父类
     *
     * @param object Object
     * @return Field
     */
    public static Field[] getAllFields(Object object) {
        Class<?> clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

    /**
     * 获取类的所有属性，包括父类
     *
     * @param object
     * @return
     */
    public static Map<String, Field> getAllFieldMap(Object object) {
        Class<?> clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        return fieldList.stream().collect(Collectors.toMap(Field::getName, (p) -> p));
    }
}
