package com.linzen.database.sql.enums;

import com.linzen.database.enums.DbAliasEnum;
import com.linzen.database.enums.ParamEnum;
import com.linzen.database.source.DbBase;
import com.linzen.database.sql.enums.base.SqlFrameBase;
import com.linzen.database.sql.model.DbStruct;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * 金仓 SQL语句模板
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Getter
@AllArgsConstructor
public enum KingbaseSqlEnum implements SqlFrameBase {
    /* =============================== 系统语句 ==================================== */
    FIELDS(
            PostgreSQLSqlEnum.FIELDS.getSqlFrame()
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            PostgreSQLSqlEnum.FIELDS.setStructParams(table, dbStruct, list);
        }
    },
    TABLES(
            "SELECT t.TABLE_NAME AS " + DbAliasEnum.TABLE_NAME.getAlias() + ",c.COMMENTS AS " + DbAliasEnum.TABLE_COMMENT.getAlias() + ", 0 AS " + DbAliasEnum.TABLE_SUM.getAlias() + " FROM\n" +
            "information_schema.TABLES AS t\n" +
            "LEFT JOIN\n" +
            "(SELECT TABLE_NAME,COMMENTS FROM DBA_TAB_COMMENTS)AS c\n" +
            "ON\n" +
            "upper(t.TABLE_NAME) = upper(c.TABLE_NAME)\n"
            + "WHERE\n" +
            " TABLE_SCHEMA = " + ParamEnum.DB_SCHEMA.getParamSign()
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getKingBaseDbSchema());
        }
    },
    TABLESANDVIEW(
            "SELECT t.table_name AS " + DbAliasEnum.TABLE_NAME.getAlias() +
                     ",c.COMMENTS AS " + DbAliasEnum.TABLE_COMMENT.getAlias() +
                    ",t.table_type AS " + DbAliasEnum.TABLE_TYPE.getAlias() +
                    "\n FROM information_schema.TABLES\n" +
                    "\tAS T LEFT JOIN ( SELECT TABLE_NAME, COMMENTS FROM DBA_TAB_COMMENTS ) AS C ON UPPER ( T.TABLE_NAME ) = UPPER ( C.TABLE_NAME ) \n" +
                    "WHERE\n" +
                    "\tTABLE_SCHEMA = " + ParamEnum.DB_SCHEMA.getParamSign()
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getKingBaseDbSchema());
        }
    },
    SqlKingbaseESEnum(
            "SELECT t.TABLE_NAME AS " + DbAliasEnum.TABLE_NAME.getAlias() + ",c.COMMENTS AS " + DbAliasEnum.TABLE_COMMENT.getAlias() + ", 0 AS " + DbAliasEnum.TABLE_SUM.getAlias() + " FROM\n" +
            "information_schema.TABLES AS t\n" +
            "LEFT JOIN\n" +
            "(SELECT TABLE_NAME,COMMENTS FROM DBA_TAB_COMMENTS)AS c\n" +
            "ON\n" +
            "upper(t.TABLE_NAME) = upper(c.TABLE_NAME)\n"
            + "WHERE\n" +
            " TABLE_SCHEMA = " + ParamEnum.DB_SCHEMA.getParamSign()
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getKingBaseDbSchema());
        }
    },
    TABLE(
            TABLES.sqlFrame + " AND t.TABLE_NAME = " + ParamEnum.TABLE.getParamSign()
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getKingBaseDbSchema());
            list.add(table);
        }
    },
    EXISTS_TABLE(
            "SELECT COUNT (*) AS TOTAL FROM (" +
            "SELECT t.TABLE_NAME AS " + DbAliasEnum.TABLE_NAME.getAlias() + " FROM\n" +
            "information_schema.TABLES AS t WHERE TABLE_SCHEMA = " + ParamEnum.DB_SCHEMA.getParamSign() +
            " and t.TABLE_NAME = " + ParamEnum.TABLE.getParamSign()
            + ") AS COUNT_TAB"
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getKingBaseDbSchema());
            list.add(table);
        }
    },


    /**
     * 建库
     */
    CREATE_DATABASE(
            "CREATE DATABASE WITH owner=\"{database}\" "
    ),
    DROP_DATABASE(
            "DROP DATABASE \"{database}\""
    ),
    CREATE_SCHEMA(
            "CREATE SCHEMA AUTHORIZATION \"{schema}\""
    ),
    DROP_SCHEMA(
            "DROP SCHEMA \"{schema}\" CASCADE;"
    ),

    /*==================== 操作表 ======================*/
    DROP(
            "DROP TABLE {table}"
    ),
    DROP_TABLE(
            PostgreSQLSqlEnum.DROP_TABLE
    ),
    COMMENT_COLUMN(
            PostgreSQLSqlEnum.COMMENT_COLUMN
    ),
    COMMENT_TABLE(
            PostgreSQLSqlEnum.COMMENT_TABLE
    ),
    CREATE(
            PostgreSQLSqlEnum.CREATE
    ),

    /*=============================== ALTER ====================================*/
    CREATE_TABLE(PostgreSQLSqlEnum.CREATE_TABLE.getSqlFrame()){
        @Override
        public String createIncrement(String sqlFrame,  Map<String, String> paramsMap) {
            return PostgreSQLSqlEnum.CREATE_TABLE.createIncrement(sqlFrame, paramsMap);
        }
    },
    ALTER_DROP(
            "ALTER TABLE 《schema》.{table} DROP COLUMN {column}"
    ),
    /**
     * 添加字段
     */
    ALTER_ADD(
            "ALTER TABLE 《schema》.{table} ADD COLUMN {column} {dataType}"
    ),
    /**
     * 修改字段
     */
    ALTER_TYPE(
            "ALTER TABLE 《schema》.{table} ALTER COLUMN {column} TYPE {dataType}"
    ),
    /**
     * 修改: 表名
     */
    RE_TABLE_NAME(
            PostgreSQLSqlEnum.RE_TABLE_NAME
    ),
    /*=============================== ALTER ====================================*/

    INSERT(
            "INSERT INTO 《schema》.{table} (【{column},】) VALUES (【{value},】)"
    ),
    ;
    // ALTER TABLE flow_taskoperator DROP COLUMN F_DraftData

    private final String dbEncode = DbBase.KINGBASE_ES;
    private String sqlFrame;

    KingbaseSqlEnum(SqlFrameBase sqlEnum){
        this.sqlFrame = sqlEnum.getSqlFrame();
    }

}
