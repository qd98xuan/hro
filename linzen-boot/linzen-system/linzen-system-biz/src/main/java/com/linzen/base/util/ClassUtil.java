package com.linzen.base.util;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class ClassUtil {

    private static void getProxyPojoValue(Object object, Set<String> key1){
        String id = null;
        // 返回参数
        HashMap<String,Object> hashMap = new HashMap<>(16);
        for (String s : key1) {
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);

                // 获取表名
                TableName table = object.getClass().getAnnotation(TableName.class);
                if (table != null) {
                    String tableName = table.value();
                    hashMap.putIfAbsent("tableName", tableName);
                }
                // 获取主键id
                if (id == null) {
                    boolean isIdField = field.isAnnotationPresent(TableId.class);
                    if (isIdField) {
                        TableField tableField = field.getAnnotation(TableField.class);
                        if (s.toLowerCase().equals(field.getName().toLowerCase())) {
                            String tableId = tableField.value();
                            hashMap.put(s,tableId);
                            id = tableId;
                        }
                    }
                }

                // 获取字段的值
                boolean isTableField = field.isAnnotationPresent(TableField.class);
                if (isTableField) {
                    TableField tableField = field.getAnnotation(TableField.class);
                    if (s.toLowerCase().equals(field.getName().toLowerCase())) {
                        String fieldValue = tableField.value();
                        hashMap.put(s,fieldValue);
                    }
                }
            }
        }
        System.out.println(hashMap);
    }
}
