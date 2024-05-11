package com.linzen.engine.model.flowdelegate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FlowDelegateInfoVO {
    @Schema(description = "主键id")
    private String id;
    @Schema(description = "委托类型0-发起委托，1-审批委托")
    private String type;
    @Schema(description = "委托人id")
    private String userId;
    @Schema(description = "委托人")
    private String userName;
    @Schema(description = "流程分类")
    private String flowCategory;
    @Schema(description = "被委托人")
    private String toUserName;
    @Schema(description = "被委托人")
    private String toUserId;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "开始日期")
    private Long startTime;
    @Schema(description = "结束日期")
    private Long endTime;
    @Schema(description = "委托流程id")
    private String flowId;
    @Schema(description = "委托流程名称")
    private String flowName;
}
