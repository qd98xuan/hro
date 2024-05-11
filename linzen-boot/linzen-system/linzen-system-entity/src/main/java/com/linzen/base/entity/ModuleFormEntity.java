package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 *
 * 表单权限
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
@TableName("sys_module_form")
public class ModuleFormEntity extends SuperExtendEntity.SuperExtendDEEntity<String> {

    /**
     * 表单上级
     */
    @TableField("F_PARENT_ID")
    private String parentId;

    /**
     * 表单名称
     */
    @TableField("F_FULL_NAME")
    private String fullName;

    /**
     * 表单编码
     */
    @TableField("F_EN_CODE")
    private String enCode;

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
     * 绑定表格Id
     */
    @TableField("f_bind_table")
    private String bindTable;

    /**
     * 子表规则key
     */
    @TableField("f_child_table_key")
    private String childTableKey;

}
