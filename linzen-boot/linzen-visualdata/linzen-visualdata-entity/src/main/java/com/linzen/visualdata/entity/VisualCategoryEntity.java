package com.linzen.visualdata.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 大屏分类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("blade_visual_category")
public class VisualCategoryEntity {
    /** 主键 */
    @TableId("ID")
    private String id;

    /** 分类值 */
    @TableField("CATEGORY_KEY")
    private String categoryKey;

    /** 分类名称 */
    @TableField("CATEGORY_VALUE")
    private String categoryValue;

    /** 是否删除 */
    @TableField("IS_DELETED")
    private String isdeleted;

    /**
     * 租户id
     */
    @TableField("f_tenant_id")
    private String tenantId;

}
