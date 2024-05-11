package com.linzen.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;
/**
 *
 * 消息模板参数表
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
@TableName("base_msg_template_param")
public class TemplateParamEntity extends SuperExtendEntity.SuperExtendEnabledEntity<String> {

    /** 消息模板id **/
    @TableField("f_template_id")
    private String templateId;

    /** 参数 **/
    @TableField("f_field")
    private String field;

    /** 参数说明 **/
    @TableField("f_field_name")
    private String fieldName;

}
