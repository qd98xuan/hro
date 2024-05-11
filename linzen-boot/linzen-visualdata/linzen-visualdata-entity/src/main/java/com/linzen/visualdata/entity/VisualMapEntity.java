package com.linzen.visualdata.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 大屏地图配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("blade_visual_map")
public class VisualMapEntity {
    /** 主键 */
    @TableId("ID")
    private String id;

    /** 地图名称 */
    @TableField("NAME")
    private String name;

    /** 地图数据 */
    @TableField("DATA")
    private String data;

    /**
     * 租户id
     */
    @TableField("f_tenant_id")
    private String tenantId;

}
