package com.linzen.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * 消息发送配置表
 *
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
@TableName("base_msg_send")
public class SendMessageConfigEntity extends SuperExtendEntity.SuperExtendDEEntity<String> {

    @TableField("F_FULL_NAME")
    private String fullName;

    @TableField("F_EN_CODE")
    private String enCode;

//    @TableField("F_MESSAGETYPE")
//
//    private String messageType;

    @TableField("f_template_type")
    private String templateType;

    @TableField("f_message_source")
    private String messageSource;

}
