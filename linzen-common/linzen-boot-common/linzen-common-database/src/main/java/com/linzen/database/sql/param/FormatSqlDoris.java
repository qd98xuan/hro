package com.linzen.database.sql.param;

import com.linzen.database.model.dbfield.DbFieldModel;
import com.linzen.database.model.dbtable.DbTableFieldModel;
import com.linzen.database.source.DbBase;
import lombok.Data;

import java.util.List;

/**
 * 类功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyrignt 引迈信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FormatSqlDoris {

    private String primaryField;

    /**
     * 添加主键相关内容
     * @param sql 建表SQL
     * @param dbTableFieldModel 表字段对象
     * @param dbType 数据类型
     * @return sql语句
     */
    public static String getPrimaryFieldSql(String sql, DbTableFieldModel dbTableFieldModel, String dbType){
        if(dbType.equals(DbBase.DORIS)) {
            List<DbFieldModel> dbFieldModelList = dbTableFieldModel.getDbFieldModelList();
            for (DbFieldModel dbFieldModel : dbFieldModelList) {
                if (dbFieldModel.getIsPrimaryKey()) {
                    sql = sql.replace("{primary_column}", dbFieldModel.getField());
                }
            }
            sql = sql.replace("{tableComment}", dbTableFieldModel.getComment());
        }
        return sql;
    }


    public static void orderUniqueColumn(String dbType, List<String> columnSqlList, DbTableFieldModel dbTableFieldModel){
        if(dbType.equals(DbBase.DORIS)) {
            for (DbFieldModel dbFieldModel : dbTableFieldModel.getDbFieldModelList()) {
                if (dbFieldModel.getIsPrimaryKey()) {
                    for (int i = 0; i < columnSqlList.size(); i++) {
                        if(columnSqlList.get(i).contains("\t" + dbFieldModel.getField())){
                            columnSqlList.add(0, columnSqlList.remove(i));
                        }
                    }
                }
            }

        }

    }


}
