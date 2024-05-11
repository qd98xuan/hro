package com.linzen.message.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 企业微信的模型
 *
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class QyWebChatModel {
    @Schema(description = "CorpId")
    private String qyhCorpId;
    @Schema(description = "AgentId")
    private String qyhAgentId;
    @Schema(description = "AgentSecret")
    private String qyhAgentSecret;
    @Schema(description = "CorpSecret")
    private String qyhCorpSecret;
}
