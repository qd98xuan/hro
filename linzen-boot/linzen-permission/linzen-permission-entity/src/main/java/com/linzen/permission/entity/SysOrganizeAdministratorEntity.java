package com.linzen.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 *
 * 机构分级管理员
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("sys_organize_administrator")
public class SysOrganizeAdministratorEntity extends SuperExtendEntity.SuperExtendDEEntity<String> {
    /**
     * 用户主键
     */
    @TableField("F_USER_ID")
    private String userId;

    /**
     * 机构主键
     */
    @TableField("F_ORGANIZE_ID")
    private String organizeId;

    /**
     * 机构类型
     */
    @TableField("F_ORGANIZE_TYPE")
    private String organizeType;

    /**
     * 本层添加
     */
    @TableField("F_THIS_LAYER_ADD")
    private Integer thisLayerAdd;

    /**
     * 本层编辑
     */
    @TableField("F_THIS_LAYER_EDIT")
    private Integer thisLayerEdit;

    /**
     * 本层删除
     */
    @TableField("F_THIS_LAYER_DELETE")
    private Integer thisLayerDelete;

    /**
     * 子层添加
     */
    @TableField("F_SUB_LAYER_ADD")
    private Integer subLayerAdd;

    /**
     * 子层编辑
     */
    @TableField("F_SUB_LAYER_EDIT")
    private Integer subLayerEdit;

    /**
     * 子层删除
     */
    @TableField("F_SUB_LAYER_DELETE")
    private Integer subLayerDelete;

    /**
     * 本层查看
     */
    @TableField("F_THIS_LAYER_SELECT")
    private Integer thisLayerSelect;

    /**
     * 子层查看
     */
    @TableField("F_SUB_LAYER_SELECT")
    private Integer subLayerSelect;

}
