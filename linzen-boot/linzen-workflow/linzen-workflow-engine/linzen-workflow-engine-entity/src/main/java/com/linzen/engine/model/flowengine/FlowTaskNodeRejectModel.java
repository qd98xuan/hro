package com.linzen.engine.model.flowengine;

import lombok.Data;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FlowTaskNodeRejectModel {
    private String id;
    private String nodeCode;
    private String nodeName;
    private String completion;
    private Integer state;
    private String nodeNext;
}
