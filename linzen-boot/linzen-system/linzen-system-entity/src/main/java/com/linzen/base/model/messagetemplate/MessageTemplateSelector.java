package com.linzen.base.model.messagetemplate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class MessageTemplateSelector implements Serializable {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "模板名称")
    private String fullName;
    @Schema(description = "消息类型")
    private String category;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "内容")
    private String content;
    @Schema(description = "模板参数JSON")
    private String templateJson;
    @Schema(description = "编码")
    private String enCode;
}
