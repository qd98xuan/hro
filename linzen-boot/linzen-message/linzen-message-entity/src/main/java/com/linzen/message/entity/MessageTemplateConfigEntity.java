package com.linzen.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;
/**
 *
 * 消息模板表
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
@TableName("base_msg_template")
public class MessageTemplateConfigEntity extends SuperExtendEntity.SuperExtendDEEntity<String> {

    @TableField("F_FULL_NAME")
    private String fullName;

    @TableField("F_EN_CODE")
    private String enCode;

    @TableField("f_template_type")
    private String templateType;

    @TableField("f_message_source")
    private String messageSource;

    @TableField("f_message_type")
    private String messageType;

    @TableField("f_title")
    private String title;

    @TableField("f_content")
    private String content;

    @TableField("f_template_code")
    private String templateCode;

    @TableField("f_wx_skip")
    private String wxSkip;

    @TableField("f_xcx_app_id")
    private String xcxAppId;

}
