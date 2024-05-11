package com.linzen.engine.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * 流程引擎
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("flow_template")
public class FlowTemplateEntity extends SuperExtendEntity.SuperExtendDEEntity<String> {

    /**
     * 流程编码
     */
    @TableField("F_EN_CODE")
    private String enCode;

    /**
     * 流程名称
     */
    @TableField("F_FULL_NAME")
    private String fullName;

    /**
     * 流程类型(0.发起流程 1.功能流程)
     */
    @TableField("F_TYPE")
    private Integer type;

    /**
     * 流程分类
     */
    @TableField("F_CATEGORY")
    private String category;

    /**
     * 图标
     */
    @TableField("F_ICON")
    private String icon;

    /**
     * 图标背景色
     */
    @TableField("F_ICON_BACKGROUND")
    private String iconBackground;


}
