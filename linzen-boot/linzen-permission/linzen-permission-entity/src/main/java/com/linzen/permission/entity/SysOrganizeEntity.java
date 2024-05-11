package com.linzen.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 组织机构
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_organize")
public class SysOrganizeEntity extends PermissionBaseEntity {
    /**
     * 机构上级
     */
    @TableField("F_PARENT_ID")
    private String parentId;

    /**
     * 机构分类
     */
    @TableField("F_CATEGORY")
    private String category;

    /**
     * 机构编号
     */
    @TableField("F_EN_CODE")
    private String enCode;

    /**
     * 机构名称
     */
    @TableField("F_FULL_NAME")
    private String fullName;

    /**
     * 机构主管
     */
    @TableField("F_MANAGER_ID")
    private String managerId;

    /**
     * 扩展属性
     */
    @TableField("F_PROPERTY_JSON")
    private String propertyJson;

    /**
     * 父级组织
     */
    @TableField("F_ORGANIZE_ID_TREE")
    private String organizeIdTree;
}
