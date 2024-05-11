package com.linzen.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperEntity;
import lombok.Data;

/**
 * 产品分类
 *
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
@TableName("ext_product_classify")
public class ProductclassifyEntity extends SuperEntity<String> {

    /**
     * 上级
     */
    @TableField("F_PARENT_ID")
    private String parentId;

    /**
     * 名称
     */
    @TableField("F_FULL_NAME")
    private String fullName;

}
