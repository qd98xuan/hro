package com.linzen.engine.model.flowcandidate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Data
public class FlowCandidateListModel {
    @Schema(description = "节点编码")
    private String nodeId;
    @Schema(description = "节点名称")
    private String nodeName;
    @Schema(description = "是否分流")
    private Boolean isCandidates = false;
    @Schema(description = "是否选择分支")
    private Boolean isBranchFlow = false;
    @Schema(description = "是否候选人")
    private Boolean hasCandidates = false;
}