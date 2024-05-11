package com.linzen.model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数据建模DTO
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DbTableConModel {
    /**
     * 标识
     */
    private String id;
    /**
     * 表名
     */
    private String table;
    /**
     * 新表名
     */
    private String newTable;
    /**
     * 表说明
     */
    private String tableName;
    /**
     * 大小
     */
    private String size;
    /**
     * 总数
     */
    private Integer sum;
    /**
     * 说明
     */
    private String description;
    /**
     * 主键
     */
    private String primaryKey;
    /**
     * 数据源主键
     */
    private String dataSourceId;
}
