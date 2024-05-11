package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 行政区划
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("base_province")
public class ProvinceEntity extends SuperExtendEntity.SuperExtendDEEntity<String> implements Serializable {
    /**
     * 区域上级
     */
    @TableField("F_PARENT_ID")
    private String parentId;

    /**
     * 区域编号
     */
    @TableField("F_EN_CODE")
    private String enCode;

    /**
     * 区域名称
     */
    @TableField("F_FULL_NAME")
    private String fullName;

    /**
     * 快速查询
     */
    @TableField("F_QUICK_QUERY")
    private String quickQuery;

    /**
     * 区域类型
     */
    @TableField("F_TYPE")
    private String type;

}
