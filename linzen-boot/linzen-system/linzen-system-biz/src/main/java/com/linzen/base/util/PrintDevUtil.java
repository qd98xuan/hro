package com.linzen.base.util;

import com.linzen.database.model.dbfield.JdbcColumnModel;
import com.linzen.database.model.dbtable.JdbcTableModel;
import com.linzen.database.model.entity.DbLinkEntity;

import java.util.List;

/**
 * 打印模板-工具类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class PrintDevUtil {

    /**
     * 获取字段注释
     * [1]:表注释 [2]:字段注释
     */
    public static String[] getTableColumnComment(DbLinkEntity dbLinkEntity, String table, String columnName){
        try {
            JdbcTableModel jdbcTableModel = new JdbcTableModel(dbLinkEntity, table);
            String tableComment = jdbcTableModel.getComment();
            String columnComment = "";
            List<JdbcColumnModel> columnList = jdbcTableModel.getJdbcColumnModelList();
            for(JdbcColumnModel column : columnList){
                if(column.getField().equalsIgnoreCase(columnName)){
                    columnComment =  column.getComment();
                }
            }
            return new String[] {tableComment, columnComment};
        } catch (Exception e) {
            throw new RuntimeException("表信息抽取异常！", e);
        }
    }

}
