package com.linzen.engine.model.flowcandidate;

import com.linzen.engine.model.flowtask.TaskNodeModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Data
public class FlowRejectVO {
    @Schema(description = "节点")
    private List<TaskNodeModel> list;
    @Schema(description = "是否选择")
    private Boolean isLastAppro = true;
}
