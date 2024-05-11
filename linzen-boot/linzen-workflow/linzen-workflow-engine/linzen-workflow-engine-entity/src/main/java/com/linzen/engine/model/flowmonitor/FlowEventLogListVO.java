package com.linzen.engine.model.flowmonitor;

import lombok.Data;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FlowEventLogListVO {
    private String fullName;
    private String interfaceId;
    private String interfaceName;
    private String interfaceCode;
    private Long creatorTime;
    private String result;
}
