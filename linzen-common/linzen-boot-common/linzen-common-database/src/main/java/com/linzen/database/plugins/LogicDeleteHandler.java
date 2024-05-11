package com.linzen.database.plugins;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

import java.util.List;

/**
 * @author FHNP
 * @user N
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface LogicDeleteHandler {

    /**
     * 获取非删除的值
     *
     * @return Expression
     */
    Expression getNotDeletedValue();

    /**
     * 删除的列名
     *
     * @return String
     */
    default String getLogicDeleteColumn() {
        return "f_del_flag";
    }

    /**
     * 删除的语句
     *
     * @return String
     */
    default String getDeleteSql() {
        return "UPDATE a SET f_del_flag = 1";
    }

    /**
     * 根据表名判断是否忽略拼接逻辑删除条件
     * <p>
     * 默认都要进行解析并拼接逻辑删除条件
     *
     * @param tableName 表名
     * @return 是否忽略, true:表示忽略，false:需要解析并拼接多租户条件
     */
    default boolean ignoreTable(String tableName) {
        return false;
    }

    /**
     * 忽略插入租户字段逻辑
     *
     * @param columns        插入字段
     * @param logicDeleteColumn 租户 ID 字段
     * @return boolean
     */
    default boolean ignoreInsert(List<Column> columns, String logicDeleteColumn) {
        return columns.stream().map(Column::getColumnName).anyMatch(i -> i.equalsIgnoreCase(logicDeleteColumn));
    }


}
