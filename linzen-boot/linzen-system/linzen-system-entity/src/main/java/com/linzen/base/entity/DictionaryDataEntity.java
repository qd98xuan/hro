package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 字典数据
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("base_dictionary_data")
public class DictionaryDataEntity extends SuperExtendEntity.SuperExtendDEEntity<String> implements Serializable {

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
     * 拼音
     */
    @TableField("f_simple_spelling")
    private String simpleSpelling;

    /**
     * 默认
     */
    @TableField("f_is_default")
    private Integer isDefault;

    /**
     * 类别主键
     */
    @TableField("f_dictionary_type_id")
    private String dictionaryTypeId;

}
