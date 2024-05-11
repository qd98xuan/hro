package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 邮件配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("ext_email_config")
public class EmailConfigEntity extends SuperExtendEntity.SuperExtendDEEntity<String> implements Serializable {

    /**
     * POP3服务
     */
    @TableField("F_POP3_HOST")
    private String pop3Host;

    /**
     * POP3端口
     */
    @TableField("F_POP3_PORT")
    private Integer pop3Port;

    /**
     * SMTP服务
     */
    @TableField("F_SMTP_HOST")
    private String smtpHost;

    /**
     * SMTP端口
     */
    @TableField("F_SMTP_PORT")
    private Integer smtpPort;

    /**
     * 账户
     */
    @TableField("F_ACCOUNT")
    private String account;

    /**
     * 密码
     */
    @TableField("F_PASSWORD")
    private String password;

    /**
     * SSL登录
     */
    @TableField("F_SSL")
    private Integer emailSsl=0;

    /**
     * 发件人名称
     */
    @TableField("F_SENDER_NAME")
    private String senderName;

    /**
     * 我的文件夹
     */
    @TableField("F_FOLDER_JSON")
    private String folderJson;

}
