package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_system")
public class SysSystemEntity extends SuperExtendEntity.SuperExtendDEEntity<String> {
    /**
     * 系统名称
     */
    @TableField("F_FULL_NAME")
    private String fullName;

    /**
     * 系统编号
     */
    @TableField("F_EN_CODE")
    private String enCode;

    /**
     * 系统图标
     */
    @TableField("F_ICON")
    private String icon;

    /**
     * 是否是主系统（0-不是，1-是）
     */
    @TableField("F_IS_MAIN")
    private Integer isMain;

    /**
     * 扩展属性
     */
    @TableField("F_PROPERTY_JSON")
    private String propertyJson;

    /**
     * 导航图标
     */
    @TableField("f_navigation_icon")
    private String navigationIcon;

    /**
     * Logo图标
     */
    @TableField("f_work_logo_icon")
    private String workLogoIcon;

    /**
     * 已启用工作流
     */
    @TableField("f_workflow_enabled")
    private Integer workflowEnabled;

}
