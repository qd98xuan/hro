package com.linzen.database.sql.enums.base;

import com.linzen.database.model.dto.PrepSqlDTO;
import com.linzen.database.model.interfaces.DbSourceOrDbLink;
import com.linzen.database.sql.enums.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 通用 SQL语句模板
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Getter
@AllArgsConstructor
public enum SqlComEnum implements SqlFrameBase {

    /*
     * []:可选、<>:必填 《》:配置开关填写、{}:参数、【】:循环 N:(选择框架   =========================================
     */
    /* =============================== 系统语句 ==================================== */
    TABLES(MySQLSqlEnum.TABLES,
            OracleSqlEnum.TABLES,
            SQLServerSqlEnum.TABLES,
            SqlDMEnum.TABLES,
            KingbaseSqlEnum.TABLES,
            PostgreSQLSqlEnum.TABLES
    ),
    TABLESANDVIEW(MySQLSqlEnum.TABLESANDVIEW,
            OracleSqlEnum.TABLESANDVIEW,
            SQLServerSqlEnum.TABLESANDVIEW,
            SqlDMEnum.TABLESANDVIEW,
            KingbaseSqlEnum.TABLESANDVIEW,
            PostgreSQLSqlEnum.TABLESANDVIEW
    ),
    TABLE(MySQLSqlEnum.TABLE,
            OracleSqlEnum.TABLE,
            SQLServerSqlEnum.TABLE,
            SqlDMEnum.TABLE,
            KingbaseSqlEnum.TABLE,
            PostgreSQLSqlEnum.TABLE
    ),
    FIELDS(MySQLSqlEnum.FIELDS,
            OracleSqlEnum.FIELDS,
            SQLServerSqlEnum.FIELDS,
            SqlDMEnum.FIELDS,
            KingbaseSqlEnum.FIELDS,
            PostgreSQLSqlEnum.FIELDS
    ),
    EXISTS_TABLE(MySQLSqlEnum.EXISTS_TABLE,
            OracleSqlEnum.EXISTS_TABLE,
            SQLServerSqlEnum.EXISTS_TABLE,
            SqlDMEnum.EXISTS_TABLE,
            KingbaseSqlEnum.EXISTS_TABLE,
            PostgreSQLSqlEnum.EXISTS_TABLE
    ),
    /* =============================== 定义语句 ==================================== */
    // （Data Definition Language）简称 DDL：用来建立数据库、数据库对象和定义列的命令。包括：create、alter、drop
    /**
     * 创表
     */
    CREATE_TABLE(OracleSqlEnum.CREATE_TABLE,
            Arrays.asList(
                    "{table}",
                    "{column}",
                    "{dataType}",
                    "[<DEFAULT> {defaultValue}]",
                    "[[NOT] [NULL]]",
                    "[<PRIMARY KEY>]",
                    "[AUTO_INCREMENT]",
                    "[<COMMENT> {comment}]"
            ),
            MySQLSqlEnum.CREATE_TABLE,
            SQLServerSqlEnum.CREATE_TABLE,
            SqlDMEnum.CREATE_TABLE,
            KingbaseSqlEnum.CREATE_TABLE,
            PostgreSQLSqlEnum.CREATE_TABLE
    ),
    ADD_COLUMN(MySQLSqlEnum.ALTER_ADD_MODIFY,
            Arrays.asList(
                    "<ADD|MODIFY>",
                    "{table}",
                    "{column}",
                    "{dataType}",
                    "[[NOT] [NULL]]",
                    "[<DEFAULT> {defaultValue}]",
                    "{comment}"
            ),
            OracleSqlEnum.ADD_COLUMN,
            PostgreSQLSqlEnum.ADD_COLUMN,
            SqlDMEnum.ALTER_ADD,
            KingbaseSqlEnum.ALTER_ADD,
            SQLServerSqlEnum.ALTER_COLUMN
    ),
    /**
     * 删除表
     */
    DROP_TABLE(OracleSqlEnum.DROP_TABLE,
            Arrays.asList(
                    "{table}"
            ),
            MySQLSqlEnum.DROP_TABLE
    ),
    /**
     * 表重命名
     */
    RE_TABLE_NAME(MySQLSqlEnum.RE_TABLE_NAME,
            Arrays.asList(
                    "{oldTable}",
                    "{newTable}"
            ),
            KingbaseSqlEnum.RE_TABLE_NAME,
            PostgreSQLSqlEnum.RE_TABLE_NAME,
            SQLServerSqlEnum.RE_TABLE_NAME
    ),
    /**
     * 表注释
     */
    COMMENT_TABLE(OracleSqlEnum.COMMENT_TABLE,
            Arrays.asList(
                    "{table}",
                    "'{comment}'"
            ),
            MySQLSqlEnum.COMMENT_TABLE,
            SQLServerSqlEnum.COMMENT_TABLE
    ),
    /**
     * 字段注释
     */
    COMMENT_COLUMN(OracleSqlEnum.COMMENT_COLUMN,
            Arrays.asList(
                    "{table}",
                    "{column}",
                    "'{comment}'",
                    "{dataType}",
                    "[DEFAULT {defaultValue}]"
            ),
            MySQLSqlEnum.COMMENT_COLUMN,
            SQLServerSqlEnum.COMMENT_COLUMN
    ),
    /* =============================== DML操作语句 ==================================== */
    // （Data Manipulation Language）简称 DML：用来操纵数据库中数据的命令。包括：select、insert、update、delete。
    /**
     * 获取表数据SQL
     */
    SELECT_TABLE(MySQLSqlEnum.SELECT_TABLE,
            Arrays.asList(
                    "{table}"
            )
    ),
    COUNT_SIZE(MySQLSqlEnum.COUNT_SIZE,
            Arrays.asList(
                    "{totalAlias}",
                    "{selectSql}"
            )
    ),
    COUNT_SIZE_TABLE(MySQLSqlEnum.COUNT_TABLE_SIZE,
            Arrays.asList(
                    "{totalAlias}",
                    "{table}"
            )
    ),
    INSERT(MySQLSqlEnum.INSERT,
            Arrays.asList(
                    "{table}",
                    "[【{column},】]",
                    "【{value},】"
            )
    ),
    DELETE_ALL(MySQLSqlEnum.DELETE_ALL,
            Collections.singletonList(
                    "{table}"
            )

    ),
    /* =============================== 后缀 ==================================== */
    /**
     * beginIndex（起始下标）
     * = (currentPage - 1) * pageSize（当前页 * 页大小）
     * endIndex（结束下标）
     * = currentPage * pageSize
     * 先查询还是先排序
     */
    ORDER_PAGE(MySQLSqlEnum.ORDER_PAGE,
            Arrays.asList(
                    "{selectSql}",
                    "{orderColumn}",
                    "{beginIndex}",
                    "{endIndex}",
                    "{pageSize}",
                    "[DESC]"
            ),
            OracleSqlEnum.ORDER_PAGE,
            SQLServerSqlEnum.ORDER_PAGE,
            PostgreSQLSqlEnum.ORDER_PAGE
    ),
    /**
     * ASC（ascend）：升序 1234 放空默认
     * DESC（descend）：降序 4321
     */
    ORDER(MySQLSqlEnum.ORDER,
            Arrays.asList(
                    "{column}",
                    "[DESC]"
            )
    ),
    /**
     * 模糊查询
     * * : 多字符, c*c代表cc,cBc,cbc,cabdfec等
     * % : 多个字符, %c%代表agdcagd等
     * ? : 单个字符, %c%代表agdcagd等
     * # : 单数字, k#k代表k1k,k8k,k0k
     * [*] : 特殊字符, a[*]a代表a*a
     * [a-z] : 字符范围,  [a-z]代表a到z的26个字母中任意一个 指定一个范围中任意一个
     */
    LIKE(MySQLSqlEnum.LIKE,
            Arrays.asList(
                    "{selectSql}",
                    "{column}",
                    "{condition}"
            )
    ),


    ;

    private String sqlFrame;
    private SqlFrameBase baseSqlEnum;
    private List<SqlFrameBase> frameEnums;
    private List<String> frameParamList;
    private final String dbEncode = "common";

    /**
     * 构造
     *
     * @param baseSqlEnum 基础枚举（其他数据库没有独特SQL，遵循这个枚举的SQL）
     */
    SqlComEnum(SqlFrameBase baseSqlEnum, List<String> frameParamList) {
        this.baseSqlEnum = baseSqlEnum;
        this.sqlFrame = baseSqlEnum.getSqlFrame();
        this.frameParamList = frameParamList;
        this.frameEnums = new ArrayList<>();
        this.frameEnums.add(baseSqlEnum);
    }

    /**
     * 构造
     *
     * @param frameEnums 不同库自身对应的SQL框架
     */
    SqlComEnum(SqlFrameBase baseSqlEnum, List<String> frameParamList, SqlFrameBase... frameEnums) {
        this.baseSqlEnum = baseSqlEnum;
        this.sqlFrame = baseSqlEnum.getSqlFrame();
        this.frameParamList = frameParamList;
        List<SqlFrameBase> frameEnumsList = new ArrayList<>(Arrays.asList(frameEnums));
        frameEnumsList.add(baseSqlEnum);
        this.frameEnums = frameEnumsList;
    }

    SqlComEnum(SqlFrameBase... frameEnums) {
        this.frameEnums = Arrays.asList(frameEnums);
    }

    /**
     * 获取子类的枚举
     *
     * @return 返回子类枚举
     */
    public SqlFrameBase getSqlFrameEnum(String dbEncode) {
        if (this.getFrameEnums() != null) {
            for (SqlFrameBase sqlEnum : this.getFrameEnums()) {
                if (sqlEnum.getDbEncode().equals(dbEncode)) {
                    return sqlEnum;
                }
            }
        }
        return null;
    }

    public SqlFrameBase getSqlFrameEnum(SqlFrameBase sqlFrameBase) {
        if (this.getFrameEnums() != null) {
            for (SqlFrameBase sqlEnum : this.getFrameEnums()) {
                if (sqlEnum.equals(sqlFrameBase)) {
                    return sqlEnum;
                }
            }
        }
        return null;
    }

    public PrepSqlDTO getPrepSqlDto(DbSourceOrDbLink dataSourceMod, String table) {
        SqlFrameBase sysTemSqlEnum = getSqlFrameEnum(dataSourceMod.init().getDbType());
        return sysTemSqlEnum.getPrepSqlDto(dataSourceMod, table);
    }

}
