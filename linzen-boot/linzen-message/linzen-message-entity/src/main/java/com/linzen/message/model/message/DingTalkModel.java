package com.linzen.message.model.message;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 钉钉发送信息配置模型
 *
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class DingTalkModel {
    @NotBlank(message = "应用凭证必填")
    private String dingSynAppKey;
    @NotBlank(message = "凭证密钥必填")
    private String dingSynAppSecret;
    private String dingAgentId;
}
