package com.linzen.util.dbcolum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 常用字段常量类
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ColumnType {

    /**
     *MySQL字段常量
     */
    public static final String MYSQL_VARCHAR = "varchar";

    public static final String MYSQL_DATETIME = "datetime";

    public static final String MYSQL_DECIMAL = "decimal";

    public static final String MYSQL_TEXT = "text";

    /**
     *Oracle字段常量
     */
    public static final String ORACLE_NVARCHAR = "NVARCHAR2";

    public static final String ORACLE_DATE = "DATE";

    public static final String ORACLE_DECIMAL = "DECIMAL";

    public static final String ORACLE_CLOB = "CLOB";

}
