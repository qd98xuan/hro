package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 按钮权限
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("sys_module_button")
public class ModuleButtonEntity extends SuperExtendEntity.SuperExtendDEEntity<String> implements Serializable {

    /**
     * 按钮上级
     */
    @TableField("F_PARENT_ID")
    private String parentId;

    /**
     * 按钮名称
     */
    @TableField("F_FULL_NAME")
    private String fullName;

    /**
     * 按钮编码
     */
    @TableField("F_EN_CODE")
    private String enCode;

    /**
     * 按钮图标
     */
    @TableField("F_ICON")
    private String icon;

    /**
     * 请求地址
     */
    @TableField("F_URL_ADDRESS")
    private String urlAddress;

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

}
