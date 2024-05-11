package com.linzen.database.datatype.db;


import com.linzen.database.datatype.db.interfaces.DtInterface;
import com.linzen.database.datatype.db.interfaces.DtLimitBase;
import com.linzen.database.datatype.limit.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PostgreSQL数据类型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Getter
@AllArgsConstructor
public enum DtPostgreSQLEnum implements DtInterface {

    /*================================== 整数型 ========================================*/
    /**
     * 类型名称: smallint
     */
    INT2        ("int2",        new IntegerLimit()),
    /**
     * 类型名称: integer
     * 默认长度：32
     */
    INT4        ("int4",        new IntegerLimit()),
    /**
     * 类型名称: bigint
     * 默认长度：64
     */
    INT8        ("int8",        new IntegerLimit()),
    /*================================== 文本字符串类型 ========================================*/
    CHAR        ("char",        new StringLimit(true).charLength(10485760L, 50L)),
    VARCHAR     ("varchar",     new StringLimit(true).charLength(10485760L, 50L)),
    /**
     * 默认长度：无
     */
    TEXT        ("text",        new StringLimit()),
    /*================================== 浮点型 ========================================*/
    FLOAT4      ("float4",      new FloatLimit(true).precision(24, 18).scale(18, 3)),
    /**
     * 类型名称: double
     */
    FLOAT8      ("float8",      new FloatLimit(true).precision(53, 18).scale(18, 3)),
    /*================================== 定点型 ========================================*/
    NUMERIC     ("numeric",     new DecimalLimit(true).precision(1000, 18).scale(100, 3)),
    /*================================== 时间日期 ========================================*/
    TIME        ("time",        new DateTimeLimit()),
    DATE        ("date",        new DateTimeLimit()),
    /**
     * 默认长度：6
     */
    TIMESTAMP   ("timestamp",   new DateTimeLimit()),
    ;

    /**
     * 数据库字段类型
     */
    private final String dataType;

    /**
     * 长度、精度、标度规则模型
     */
    private final DtLimitBase dtLimit;



}
