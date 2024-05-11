package com.linzen.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * 账号配置表
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息
 * @date 2023-04-01
 */
@Data
@TableName("msg_account_config")
public class AccountConfigEntity extends SuperExtendEntity.SuperExtendDEEntity<String> {

    @TableField("F_CATEGORY")
    private String type;

    @TableField("F_FULL_NAME")
    private String fullName;

    @TableField("F_EN_CODE")
    private String enCode;

    @TableField("F_ADDRESSOR_NAME")
    private String addressorName;

    @TableField("F_SMTP_SERVER")
    private String smtpServer;

    @TableField("F_SMTP_PORT")
    private Integer smtpPort;

    @TableField("F_SSL_LINK")
    private String sslLink;

    @TableField("F_SMTP_USER")
    private String smtpUser;

    @TableField("F_SMTP_PASSWORD")
    private String smtpPassword;

    @TableField("F_CHANNEL")
    private String channel;

    @TableField("F_SMS_SIGNATURE")
    private String smsSignature;

    @TableField("F_APP_ID")
    private String appId;

    @TableField("F_APP_SECRET")
    private String appSecret;

    @TableField("F_END_POINT")
    private String endPoint;

    @TableField("F_SDK_APP_ID")
    private String sdkAppId;

    @TableField("F_APP_KEY")
    private String appKey;

    @TableField("F_ZONE_NAME")
    private String zoneName;

    @TableField("F_ZONE_PARAM")
    private String zoneParam;

    @TableField("F_ENTERPRISE_ID")
    private String enterpriseId;

    @TableField("F_AGENT_ID")
    private String agentId;

    @TableField("F_WEBHOOK_TYPE")
    private String webhookType;

    @TableField("F_WEBHOOK_ADDRESS")
    private String webhookAddress;

    @TableField("F_APPROVE_TYPE")
    private String approveType;

    @TableField("F_BEARER")
    private String bearer;

    @TableField("F_USER_NAME")
    private String userName;

    @TableField("F_PASSWORD")
    private String password;

}
