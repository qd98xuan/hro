package com.linzen.database.sql.param;

import com.linzen.database.constant.DbFieldConst;
import com.linzen.database.model.dbfield.DbFieldModel;
import com.linzen.database.model.dbfield.JdbcColumnModel;
import com.linzen.database.model.dbtable.DbTableFieldModel;
import com.linzen.database.model.dto.PrepSqlDTO;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.source.DbBase;
import com.linzen.database.sql.model.SqlPrintHandler;
import com.linzen.database.util.DbTypeUtil;
import com.linzen.database.util.JdbcUtil;
import com.linzen.database.util.NotTenantPluginHolder;
import com.linzen.util.context.SpringContext;
import com.linzen.database.sql.enums.OracleSqlEnum;
import com.linzen.database.sql.util.SqlFrameUtil;
import lombok.Data;
import oracle.sql.TIMESTAMP;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Oracle一些语句的特殊处理
 *
 * @author FHNP
 * @version V0.0.1
 * @copyrignt 引迈信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FormatSqlOracle {

    private static SqlPrintHandler sqlPrintHandler = SpringContext.getBean(SqlPrintHandler.class);

    /**
     * 时间格式化
     * "TO_DATE('2022-12-12 00:00:00','YYYY-MM-DD HH24:MI:SS')"
     * to_date('2022-12-12 00:00:00', 'yyyy-mm-dd hh24:mi:ss')
     */
    public static String dateTime(String dbEncode, String dateTime){
        if(DbBase.ORACLE.equals(dbEncode)){
            return "to_date(" + dateTime + ", 'yyyy-mm-dd hh24:mi:ss')";
        }else {
            return dateTime;
        }
    }

    /**
     * 格式Oracle时间戳类型
     */
    public static Object timestamp(Object value){
        if(value instanceof TIMESTAMP){
            try {
                return ((TIMESTAMP)value).dateValue();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    /**
     * 非空时空串报错，因Oracle空串存储为NULL，用一个空格代替空串
     */
    public static void nullValue(String dbEncode, JdbcColumnModel model, Map<String, Object> map){
        if(DbBase.ORACLE.equals(dbEncode) || DbBase.DM.equals(dbEncode)){
            // 字符串类型 && 字符串不为空 && 空串
            if(model.getValue() instanceof String && model.getNullSign().equals(DbFieldConst.NOT_NULL)
                    && model.getValue().toString().equals("")){
                map.put(model.getField(), " ");
            }
        }
    }

    /**
     * 获取主键值
     */
    public static Function<Map<String, Object>,Map.Entry<String, Object>> getPrimaryVal = (fieldMap)->{
        for (Map.Entry<String, Object> field : fieldMap.entrySet()) {
            String primaryKey = field.getKey();
            if (primaryKey.equalsIgnoreCase("F_Id") || primaryKey.equalsIgnoreCase("ID")) {
                return field;
            }
        }
        return null;
    };

    public static Object clobExecute(String dbEncode, Object context, String table, String column, Map<String, Object> dataMap, StringBuilder sqlBuilder){
        if(DbBase.ORACLE.equals(dbEncode) && context instanceof String){
            Map.Entry<String, Object> field = FormatSqlOracle.getPrimaryVal.apply(dataMap);
            if(field != null){
                String clobUpdateSql = FormatSqlOracle.clobUpdate(context.toString(), table, column, field.getKey(), field.getValue().toString());
                if(clobUpdateSql != null){
                    sqlPrintHandler.updateClob(clobUpdateSql);
                    // 连续的匿名存储过程块，结尾都必须跟上 ; / 隔离
                    sqlBuilder.append(clobUpdateSql).append(";\n/\n");
                    return "context";
                }
            }
        }
        return context;
    }

    /**
     * Oracle处理超2000字符
     */
    public static String clobUpdate(String context, String table, String column, String primaryColumn, String primaryValue){
        if(context.length() > 1500){
            context = context.replace("'", "''");
            List<String> splitStrList = SqlFrameUtil.splitStrRepeat(context, 1500);
            StringBuilder contextInfo = new StringBuilder();
            for (String contextFragment : splitStrList) {
                contextInfo.append("\t").append(OracleSqlEnum.CLOB_APPEND.getSqlFrame().replace("{context}", contextFragment)).append(";\n");
            }
            return OracleSqlEnum.CLOB_UPDATE.getFastSql(Arrays.asList(table, column, contextInfo.toString(), primaryColumn, primaryValue));
        } else {
            return null;
        }
    }


    public static void autoIncrement(DbLinkEntity dbLinkEntity, DbTableFieldModel dbTableFieldModel) throws Exception {
        if (DbTypeUtil.checkOracle(dbLinkEntity)) {
            String table = dbTableFieldModel.getTable();
            boolean autoInc = false;
            String autoIncField = "";
            for (DbFieldModel dbFieldModel : dbTableFieldModel.getDbFieldModelList()) {
                if(dbFieldModel.getIsAutoIncrement() != null && dbFieldModel.getIsAutoIncrement()){
                    autoInc = true;
                    autoIncField = dbFieldModel.getField();
                }
            }
            if (autoInc) {
                NotTenantPluginHolder.setNotSwitchFlag();
                String autoIncrement = OracleSqlEnum.CREATE_AUTO_INCREMENT.getSqlFrame().replace("{table}", table);
                String autoIncrementTrigger = OracleSqlEnum.CREATE_AUTO_INCREMENT_TRIGGER.getSqlFrame().replace("{table}", table).replace("{autoInc_field}", autoIncField);
                if(!sqlPrintHandler.oracleAutoIncrement(autoIncrement + ";\n" + autoIncrementTrigger)){
                    JdbcUtil.creUpDe(new PrepSqlDTO(autoIncrement).withConn(dbLinkEntity));
                    JdbcUtil.update(new PrepSqlDTO(autoIncrementTrigger).withConn(dbLinkEntity));
                }
            }
        }
    }

}
