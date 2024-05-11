package com.linzen.engine.model.flowengine;

import lombok.Data;

import java.util.Date;

/**
 * 流程设计
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FlowTaskOperatorRejectModel {
    private String id;
    private Integer state;
    private Integer handleStatus;
    private Date handleTime;
    //    private Integer completion;
    private String draftData;
}
