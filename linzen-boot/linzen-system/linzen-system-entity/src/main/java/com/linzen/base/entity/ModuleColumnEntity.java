package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 列表权限
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("sys_module_column")
public class ModuleColumnEntity extends SuperExtendEntity.SuperExtendDEEntity<String> implements Serializable {

    /**
     * 列表上级
     */
    @TableField("F_PARENT_ID")
    private String parentId;

    /**
     * 列表名称
     */
    @TableField("F_FULL_NAME")
    private String fullName;

    /**
     * 列表编码
     */
    @TableField("F_EN_CODE")
    private String enCode;

    /**
     * 绑定表格Id
     */
    @TableField("f_bind_table")
    private String bindTable;

    /**
     * 绑定表格描述
     */
    @TableField("f_bind_table_name")
    private String bindTableName;

    /**
     * 扩展属性
     */
    @TableField("F_PROPERTY_JSON")
    private String propertyJson;

    /**
     * 功能主键
     */
    @TableField("F_MODULE_ID")
    private String moduleId;

    /**
     * 字段规则 主从
     */
    @TableField("f_field_rule")
    private Integer fieldRule;

    /**
     * 子表规则key
     */
    @TableField("f_child_table_key")
    private String childTableKey;

}
