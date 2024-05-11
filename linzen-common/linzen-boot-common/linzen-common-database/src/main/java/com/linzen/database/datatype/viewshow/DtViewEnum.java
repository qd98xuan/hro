package com.linzen.database.datatype.viewshow;


import com.linzen.database.datatype.db.interfaces.DtInterface;
import com.linzen.database.datatype.db.DtMySQLEnum;

/**
 * 数据库数据类型向前端显示
 * 转换规则
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public enum DtViewEnum {
    /**
     * 整型
     */
    INT("int", new DtInterface[]{
                DtMySQLEnum.INT,
    }),
    /**
     * 长整型
     */
    BIGINT("bigint", new DtInterface[]{
                DtMySQLEnum.BIGINT,
    }),
    /**
     * 字符串
     */
    VARCHAR("varchar", new DtInterface[]{
                    DtMySQLEnum.CHAR,
                    DtMySQLEnum.VARCHAR,
    }),
    /**
     * 文本
     */
    TEXT("text", new DtInterface[]{
                    DtMySQLEnum.TINY_TEXT,
                    DtMySQLEnum.TEXT,
                    DtMySQLEnum.MEDIUM_TEXT,
                    DtMySQLEnum.LONG_TEXT,
    }),
    /**
     * 浮点型
     */
    DECIMAL("decimal", new DtInterface[]{
                    DtMySQLEnum.DECIMAL,
    }),
    /**
     * 日期时间
     */
    DATE_TIME("datetime", new DtInterface[]{
                    DtMySQLEnum.DATE,
                    DtMySQLEnum.DATE_TIME,
    }),
    ;

    DtViewEnum(String view, DtInterface[] dtEnums){}

}
