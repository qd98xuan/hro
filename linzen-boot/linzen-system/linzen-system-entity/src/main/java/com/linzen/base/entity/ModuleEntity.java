package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("sys_module")
public class ModuleEntity extends SuperExtendEntity.SuperExtendDEEntity<String> implements Serializable {

    /**
     * 功能上级
     */
    @TableField("F_PARENT_ID")
    private String parentId;

    /**
     * 功能类别
     */
    @TableField("F_TYPE")
    private Integer type;

    /**
     * 功能名称
     */
    @TableField("F_FULL_NAME")
    private String fullName;

    /**
     * 功能编码
     */
    @TableField("F_EN_CODE")
    private String enCode;

    /**
     * 功能地址
     */
    @TableField("F_URL_ADDRESS")
    private String urlAddress;

    /**
     * 按钮权限
     */
    @TableField("F_IS_BUTTON_AUTHORIZE")
    private Integer isButtonAuthorize;

    /**
     * 列表权限
     */
    @TableField("F_IS_COLUMN_AUTHORIZE")
    private Integer isColumnAuthorize;

    /**
     * 数据权限
     */
    @TableField("F_IS_DATA_AUTHORIZE")
    private Integer isDataAuthorize;

    /**
     * 表单权限
     */
    @TableField("F_IS_FORM_AUTHORIZE")
    private Integer isFormAuthorize;

    /**
     * 扩展属性
     */
    @TableField("F_PROPERTY_JSON")
    private String propertyJson;

    /**
     * 菜单图标
     */
    @TableField("F_ICON")
    private String icon;
    /**
     * 链接目标
     */
    @TableField("F_LINK_TARGET")
    private String linkTarget;
    /**
     * 菜单分类 Web、App
     */
    @TableField("F_CATEGORY")
    private String category;

    /**
     * 关联功能id
     */
    @TableField("F_MODULE_ID")
    private String moduleId;

    /**
     * 关联系统id
     */
    @TableField("F_SYSTEM_ID")
    private String systemId;

}
