package com.linzen.engine.model.flowcandidate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Data
public class FlowCandidateVO {

    @Schema(description = "节点")
    private List<FlowCandidateListModel> list;
    /**
     * 1.有分支 //2.没有分支有候选人 //3.没有分支也没有候选人
     */
    @Schema(description = "类型")
    private Integer type;

    @Schema(description = "会签是否完成")
    private Boolean countersignOver = true;
}
