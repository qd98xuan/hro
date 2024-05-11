package com.linzen.visualdata.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 大屏组件库
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("blade_visual_component")
public class VisualComponentEntity {

    /** 主键 */
    @TableId("ID")
    private String id;

    /** 组件名称 */
    @TableField("name")
    private String name;

    /** 组件内容 */
    @TableField("content")
    private String content;

    /** 组件类型 */
    @TableField("type")
    private Integer type;

    /** 组件图片 */
    @TableField("img")
    private String img;

    /**
     * 租户id
     */
    @TableField("f_tenant_id")
    private String tenantId;

}
