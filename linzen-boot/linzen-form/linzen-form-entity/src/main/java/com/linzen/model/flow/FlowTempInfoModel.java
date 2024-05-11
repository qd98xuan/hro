package com.linzen.model.flow;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Schema(description="流程引擎信息模型")
public class FlowTempInfoModel {
    @Schema(description = "编码")
    private String enCode;
    @Schema(description = "流程引擎id")
    private String id;
    @Schema(description = "是否启用")
    private Integer enabledMark;
}
