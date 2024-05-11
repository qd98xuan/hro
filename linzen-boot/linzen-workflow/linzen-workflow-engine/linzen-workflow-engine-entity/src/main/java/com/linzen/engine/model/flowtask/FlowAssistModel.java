package com.linzen.engine.model.flowtask;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FlowAssistModel {
    @Schema(description = "主键")
    private String ids;
    @Schema(description = "用户")
    private List<String> list;
    @Schema(description = "流程基本主键")
    private String templateId;
}
