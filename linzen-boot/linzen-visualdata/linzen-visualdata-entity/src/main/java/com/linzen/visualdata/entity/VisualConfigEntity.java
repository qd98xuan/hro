package com.linzen.visualdata.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 大屏基本配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("blade_visual_config")
public class VisualConfigEntity {
    /** 主键 */
    @TableId("ID")
    private String id;

    /** 可视化表主键 */
    @TableField("VISUAL_ID")
    private String visualId;

    /** 配置json */
    @TableField("DETAIL")
    private String detail;

    /** 组件json */
    @TableField("COMPONENT")
    private String component;

    /**
     * 租户id
     */
    @TableField("f_tenant_id")
    private String tenantId;

}
