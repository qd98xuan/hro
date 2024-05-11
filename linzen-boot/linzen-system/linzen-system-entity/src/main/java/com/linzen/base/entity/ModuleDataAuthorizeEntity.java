package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 数据权限配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("sys_module_authorize")
public class ModuleDataAuthorizeEntity extends SuperExtendEntity.SuperExtendDEEntity<String> implements Serializable {

    /**
     * 字段名称
     */
    @TableField("F_FULL_NAME")
    private String fullName;

    /**
     * 时间类型
     */
    @TableField("f_format")
    private String format;

    /**
     * 字段编码
     */
    @TableField("F_EN_CODE")
    private String enCode;

    /**
     * 字段类型
     */
    @TableField("F_TYPE")
    private String type;

    /**
     * 条件符号
     */
    @TableField("f_condition_symbol")
    private String conditionSymbol;

    /**
     * 条件符号Json
     */
    @TableField("F_PROPERTY_JSON")
    private String conditionSymbolJson;

    /**
     * 条件内容
     */
    @TableField("f_condition_text")
    private String conditionText;

    /**
     * 扩展属性
     */
    @TableField("F_PROPERTY_JSON")
    private String propertyJson;

    /**
     * 菜单主键
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
