package com.linzen.engine.model.flowengine;

import lombok.Data;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FlowTaskRejectModel {
    private String id;
    private String thisStep;
    private String thisStepId;
    private Integer status;
    private String nodeNext;
    private Integer completion;
}
