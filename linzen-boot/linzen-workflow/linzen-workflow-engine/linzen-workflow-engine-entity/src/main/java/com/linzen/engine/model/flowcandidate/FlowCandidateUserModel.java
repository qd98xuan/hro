package com.linzen.engine.model.flowcandidate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Data
public class FlowCandidateUserModel {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "头像")
    private String headIcon;
    @Schema(description = "组织")
    private String organize;
}
