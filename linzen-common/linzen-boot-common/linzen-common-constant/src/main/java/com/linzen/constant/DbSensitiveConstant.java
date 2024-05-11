package com.linzen.constant;
import lombok.Data;

/**
 * 数据库敏感词
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DbSensitiveConstant {

    /**
     * 数据库敏感词
     * INSERT,DELETE,UPDATE 不为敏感字
     */
    public static final String SENSITIVE = "CREATE,UNIQUE,CHECK,DEFAULT,DROP,INDEX,ALTER,TABLE,VIEW";

    /**
     * 数据库敏感词
     * INSERT,DELETE,UPDATE 不为敏感字
     */
    public static final String PRINT_SENSITIVE = SENSITIVE + ",INSERT,DELETE,UPDATE";

    /**
     * 文件路径敏感词
     */
    public static final String FILE_SENSITIVE = "<,>,/,\\\\,:,|";
}
