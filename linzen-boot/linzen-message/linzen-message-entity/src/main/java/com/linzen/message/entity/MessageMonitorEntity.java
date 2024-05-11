package com.linzen.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

import java.util.Date;
/**
 *
 * 消息监控表
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
@TableName("base_msg_monitor")
public class MessageMonitorEntity extends SuperExtendEntity<String> {

    @TableField("f_account_id")
    private String accountId;

    @TableField("f_account_name")
    private String accountName;

    @TableField("f_account_code")
    private String accountCode;

    @TableField("f_message_type")
    private String messageType;

    @TableField("f_message_source")
    private String messageSource;

    @TableField("f_send_time")
    private Date sendTime;

    @TableField("f_message_template_id")
    private String messageTemplateId;

    @TableField("f_title")
    private String title;

    @TableField("f_receive_user")
    private String receiveUser;

    @TableField("f_content")
    private String content;

}
