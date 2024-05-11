package com.linzen.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 *
 * 短信变量表
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
@TableName("base_msg_sms_field")
public class SmsFieldEntity extends SuperExtendEntity.SuperExtendEnabledEntity<String> {

    /** 模板 **/
    @TableField("F_TEMPLATE_ID")
    private String templateId;

    /** 参数id **/
    @TableField("F_FIELD_ID")
    private String fieldId;

    /** 短信变量 **/
    @TableField("F_SMS_FIELD")
    private String smsField;

    /** 参数 **/
    @TableField("F_FIELD")
    private String field;

    /** 是否标题 **/
    @TableField("F_IS_TITLE")
    private Integer isTitle;

}
