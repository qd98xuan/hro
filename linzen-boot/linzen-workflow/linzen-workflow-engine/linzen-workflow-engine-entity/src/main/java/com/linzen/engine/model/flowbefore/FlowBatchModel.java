package com.linzen.engine.model.flowbefore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FlowBatchModel {
    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "主键")
    private String id;
    @Schema(description = "数量")
    private Long num;
}
