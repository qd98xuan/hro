package com.linzen.visualdata.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 全局变量
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("blade_visual_glob")
public class VisualGlobEntity {

    /** 主键 */
    @TableId("ID")
    private String id;

    /** 变量名称 */
    @TableField("globalName")
    private String globalName;

    /** 变量Key */
    @TableField("globalKey")
    private Integer globalKey;

    /** 组变量值 */
    @TableField("globalValue")
    private String globalValue;

    /**
     * 租户id
     */
    @TableField("f_tenant_id")
    private String tenantId;

}
