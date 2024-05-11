package com.linzen.engine.model.flowcomment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 */
@Data
public class FlowCommentInfoVO {

    @Schema(description = "附件")
    private String file;

    @Schema(description = "图片")
    private String image;

    @Schema(description = "流程id")
    private String taskId;

    @Schema(description = "文本")
    private String text;

    @Schema(description = "主键")
    private String id;
}
