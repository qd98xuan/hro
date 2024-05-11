package com.linzen.message.model.message;

import lombok.Data;

/**
 * 发送邮件配置模型
 *
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class EmailModel {
    private String emailPop3Host;
    private String emailPop3Port;
    private String emailSmtpHost;
    private String emailSmtpPort;
    private String emailSenderName;
    private String emailAccount;
    private String emailPassword;
    private String emailSsl;

    private String emailToUsers;
    private String emailContent;
    private String emailTitle;
}
