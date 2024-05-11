package com.linzen.database.sql.param.base;

import com.linzen.database.source.DbBase;
import com.linzen.util.context.SpringContext;
import com.linzen.database.model.dbfield.DbFieldModel;
import com.linzen.database.model.dbfield.JdbcColumnModel;
import com.linzen.database.sql.model.SqlPrintHandler;
import com.linzen.database.sql.param.FormatSqlMySQL;
import com.linzen.database.sql.param.FormatSqlOracle;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 数据一些特殊处理
 *
 * @author FHNP
 * @version V0.0.1
 * @copyrignt 引迈信息技术有限公司
 * @date 2023-04-01
 */
@Data
@AllArgsConstructor
public class FormatSql {


    /* =========================== 字段值处理 ============================ */

    public static Object convertValue(JdbcColumnModel dbColumnModel, DbBase toDb) throws Exception {
        switch (toDb.getLinzenDbEncode()){
            case DbBase.ORACLE:
            case DbBase.POSTGRE_SQL:
                return getPostgreValue(dbColumnModel);
            case DbBase.MYSQL:
                return FormatSqlMySQL.getMysqlValue(dbColumnModel);
            default:
        }
        return dbColumnModel.getValue();
    }

    /**
     * Postgre一些类型的特殊处理
     */
    public static Object getPostgreValue(JdbcColumnModel dbColumnModel) {
//        DtInterface dtEnum = DtInterface.newInstance(dataType, DbBase.MYSQL, false);
//        DtInterface toEnum = DtSyncUtil.getToFixCovert(dtEnum, DbBase.POSTGRE_SQL);
        return dbColumnModel.getValue();
    }




    private static SqlPrintHandler sqlPrintHandler = SpringContext.getBean(SqlPrintHandler.class);

    /**
     * 数据库类型编码
     */
    private String dbEncode;

    public static String getFieldName(String fieldName, String dbEncode){
        switch (dbEncode){
            case DbBase.MYSQL:
                return "`" + fieldName + "`";
            case DbBase.SQL_SERVER:
                return fieldName;
            case DbBase.ORACLE:
            case DbBase.DM:
                return "\"" + fieldName.toUpperCase() + "\"";
            case DbBase.POSTGRE_SQL:
                return "\"" + fieldName.toLowerCase() + "\"";
            default:
                return fieldName;
        }
    }
    public static String formatValue(Object value, String dbEncode) {
        // NULL空值 =======================================
        if(value == null){
            return "Null";
        // 时间值 =======================================
        }else if(value instanceof LocalDateTime || value instanceof Date){
            Date date;
            if(value instanceof LocalDateTime){
                date  = Date.from(((LocalDateTime)value).atZone(ZoneId.systemDefault()).toInstant());
            }else{
                date = (Date) value;
            }
            String dateInfo = "'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date) + "'";
            // Oracle时间处理
            dateInfo = FormatSqlOracle.dateTime(dbEncode, dateInfo);
            return dateInfo;
        // 字符串值 =======================================
        }else if(value instanceof String){
            // 对单引号转义 ：两个单引号
            String context = value.toString().replace("'", "''");
            return "'" + context + "'";
        // 数值 =======================================
        }else if(value instanceof Integer){
            return value.toString();
        }
        return value.toString();
    }

    public static String defaultCheck(DbFieldModel fieldModel, String dbEncode){
        if(fieldModel.getDefaultValue() != null) {
            String defaultValue = "'" + fieldModel.getDefaultValue() + "'";
            if(DbBase.ORACLE.equals(dbEncode)){
                fieldModel.setNullSign("");
                if(fieldModel.getDataType().equalsIgnoreCase("DATETIME")){
                    defaultValue = "to_date(" + defaultValue + ", 'yyyy-mm-dd hh24:mi:ss')";
                }
            }
            return "DEFAULT " + defaultValue;
        }else {
            return "";
        }
    }

}
