package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * 常用字段表
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP管理员/admin
 * @date 2023-04-01
 */
@Data
@TableName("base_common_fields")
public class ComFieldsEntity extends SuperExtendEntity.SuperExtendDEEntity<String> implements Serializable {

    @TableField("f_field_name")
    private String fieldName;

    @TableField("f_field")
    private String field;

    @TableField("f_data_type")
    private String datatype;

    @TableField("f_data_length")
    private String datalength;

    @TableField("f_allow_null")
    private String allowNull;

}

