package com.linzen.database.model.dbtable.base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 类功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DbTableModelBase {

    /**
     * 表名
     */
    private String table;

    /**
     * 表说明
     */
    private String comment;

    /**
     * 大小
     */
    private String size;

    /**
     * 数据条数
     */
    private String sum;

    /**
     * 主键
     */
    private String primaryKeyField;

}
