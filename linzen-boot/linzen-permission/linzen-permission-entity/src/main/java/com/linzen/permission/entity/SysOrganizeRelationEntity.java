package com.linzen.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 组织关系
 * </p>
 *
 * @author FHNP
 * @since 2022-01-19
 */
@Data
@TableName("sys_organize_relation")
@Schema(description = "OrganizeRelation对象", name = "组织关系")
public class SysOrganizeRelationEntity extends SuperExtendEntity<String> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 组织主键
     */
    @TableField("F_ORGANIZE_ID")
    private String organizeId;

    /**
     * 对象类型（角色：role）
     */
    @TableField("F_OBJECT_TYPE")
    private String objectType;

    /**
     * 对象主键
     */
    @TableField("F_OBJECT_ID")
    private String objectId;

}
