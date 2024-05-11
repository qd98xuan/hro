package com.linzen.database.datatype.viewshow;

import com.linzen.database.datatype.viewshow.constant.DtViewConst;
import com.linzen.database.datatype.db.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字段类型枚举
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Getter
@AllArgsConstructor
public enum ViewDataTypeEnum {

    /* 如{主类型},{次类型}:({默认字符长度},{限制长度}(*:不允许设置))*/

    /**
     * 字符
     */
    VARCHAR(
            DtViewConst.VARCHAR,
            DtMySQLEnum.VARCHAR,
            DtOracleEnum.VARCHAR2,
            DtSQLServerEnum.VARCHAR,
            DtDMEnum.VARCHAR,
            DtKingbaseESEnum.VARCHAR,
            DtPostgreSQLEnum.VARCHAR
    ),
    /**
     * 日期时间
     * 日期统一不指定长度
     */
    DATE_TIME(
            DtViewConst.DATE_TIME,
            DtMySQLEnum.DATE_TIME,
            DtOracleEnum.TIMESTAMP,
            DtSQLServerEnum.DATE_TIME,
            DtDMEnum.DATE_TIME,
            DtKingbaseESEnum.TIMESTAMP,
            DtPostgreSQLEnum.TIMESTAMP
    ),
    /**
     * 浮点
     */
    DECIMAL(
            DtViewConst.DECIMAL,
            DtMySQLEnum.DECIMAL,
            DtOracleEnum.NUMBER,
            DtSQLServerEnum.DECIMAL,
            DtDMEnum.DECIMAL,
            DtKingbaseESEnum.NUMERIC,
            DtPostgreSQLEnum.NUMERIC
    ),
    /**
     * 文本
     */
    TEXT(
            DtViewConst.TEXT,
            DtMySQLEnum.TEXT,
            DtOracleEnum.CLOB,
            DtSQLServerEnum.TEXT,
            DtDMEnum.TEXT,
            DtKingbaseESEnum.TEXT,
            DtPostgreSQLEnum.TEXT
    ),
    /**
     * 整型
     * SqlServer、PostGre:int不能指定长度
     */
    INT(
            DtViewConst.INT,
            DtMySQLEnum.INT,
            DtOracleEnum.NUMBER,
            DtSQLServerEnum.INT,
            DtDMEnum.INT,
            DtKingbaseESEnum.INTEGER,
            DtPostgreSQLEnum.INT4
    ),
    /**
     * 长整型
     */
    BIGINT(
            DtViewConst.BIGINT,
            DtMySQLEnum.BIGINT,
            DtOracleEnum.NUMBER,
            DtSQLServerEnum.BIGINT,
            DtDMEnum.BIGINT,
            DtKingbaseESEnum.BIGINT,
            DtPostgreSQLEnum.INT8
    ),
    /**
     * oracle数字类型
     */
    ORACLE_NUMBER(
            DtViewConst.ORACLE_NUMBER,
            null,
            DtOracleEnum.NUMBER,
            null,
            null,
            null,
            null
    );

    private final String viewFieldType;
    private final DtMySQLEnum dtMySQLEnum;
    private final DtOracleEnum dtOracleEnum;
    private final DtSQLServerEnum dtSQLServerEnum;
    private final DtDMEnum dtDMEnum;
    private final DtKingbaseESEnum dtKingbaseESEnum;
    private final DtPostgreSQLEnum dtPostgreSQLEnum;

}
