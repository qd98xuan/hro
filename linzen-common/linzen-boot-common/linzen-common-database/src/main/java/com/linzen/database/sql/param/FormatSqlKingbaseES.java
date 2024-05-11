package com.linzen.database.sql.param;

import com.linzen.database.constant.DbFieldConst;
import com.linzen.database.model.dbfield.JdbcColumnModel;
import com.linzen.database.source.DbBase;

import java.util.Map;

/**
 * 类功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyrignt 引迈信息技术有限公司
 * @date 2023-04-01
 */
public class FormatSqlKingbaseES {

    /**
     * 非空时空串报错，因Oracle空串存储为NULL，用一个空格代替空串
     */
    public static void nullValue(String dbEncode, JdbcColumnModel model, Map<String, Object> map){
        if(DbBase.KINGBASE_ES.equals(dbEncode)){
            // 字符串类型 && 字符串不为空 && 空串
            if(model.getValue() instanceof String && model.getNullSign().equals(DbFieldConst.NOT_NULL)
                    && model.getValue().toString().equals("")){
                map.put(model.getField(), " ");
            }
        }
    }

}
