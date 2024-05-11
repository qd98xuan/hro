package com.linzen.engine.model.flowtemplate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FlowSelectVO {
    @Schema(description = "流程主键")
    private String id;
    @Schema(description = "流程名称")
    private String fullName;
    @Schema(description = "所属流程")
    private String flowName;
    @Schema(description = "流程类型")
    private String flowType;

}
