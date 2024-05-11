package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 字典分类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("base_dictionary_type")
public class DictionaryTypeEntity extends SuperExtendEntity.SuperExtendDEEntity<String> implements Serializable {

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

    /**
     * 编码
     */
    @TableField("F_EN_CODE")
    private String enCode;

    /**
     * 树形
     */
    @TableField("f_is_tree")
    private Integer isTree;

    /**
     * 类型
     */
    @TableField("F_TYPE")
    private Integer category;

}
