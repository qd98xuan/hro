package com.linzen.message.model.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class NoticeCrForm {
    @Schema(description = "内容")
    private String bodyText;
    @NotBlank(message = "必填")
    @Schema(description = "标题")
    private String title;
    @Schema(description = "接收人")
    private String toUserIds;
    @Schema(description = "附件")
    private String files;

    @Schema(description = "封面图片")
    private String coverImage;
    @Schema(description = "过期时间")
    private Long expirationTime;
    @Schema(description = "分类")
    private String category;
    @Schema(description = "提醒方式")
    private Integer remindCategory;
    @Schema(description = "发送配置")
    private String sendConfigId;

    @Schema(description = "摘要")
    private String excerpt;
}
