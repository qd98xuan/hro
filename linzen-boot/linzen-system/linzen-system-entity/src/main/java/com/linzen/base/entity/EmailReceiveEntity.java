package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 邮件接收
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("ext_email_receive")
public class EmailReceiveEntity extends SuperExtendEntity.SuperExtendDEEntity<String> {

    /**
     * 类型
     */
    @TableField("F_TYPE")
    private Integer type;

    /**
     * 邮箱账户
     */
    @TableField(value = "F_MACCOUNT",fill= FieldFill.INSERT)
    private String mAccount;

    /**
     * F_MID
     */
    @TableField("F_MID")
    private String mID;

    /**
     * 发件人
     */
    @TableField("F_SENDER")
    private String sender;

    /**
     * 发件人名称
     */
    @TableField("F_SENDER_NAME")
    private String senderName;

    /**
     * 主题
     */
    @TableField("F_SUBJECT")
    private String subject;

    /**
     * 正文
     */
    @TableField("F_BODY_TEXT")
    private String bodyText;

    /**
     * 附件
     */
    @TableField("F_ATTACHMENT")
    private String attachment;

    /**
     * 阅读
     */
    @TableField("F_READ")
    private Integer isRead;

    /**
     * F_Date
     */
    @TableField("F_DATE")
    private Date fdate;

    /**
     * 星标
     */
    @TableField("F_STARRED")
    private Integer starred;

}