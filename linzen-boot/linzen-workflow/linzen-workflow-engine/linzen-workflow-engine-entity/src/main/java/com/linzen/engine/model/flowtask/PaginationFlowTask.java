package com.linzen.engine.model.flowtask;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linzen.base.PaginationTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PaginationFlowTask extends PaginationTime {
    @Schema(description = "所属流程")
    private String templateId;
    @Schema(description = "所属名称")
    private String flowId;
    @Schema(description = "所属分类")
    private String flowCategory;
    @Schema(description = "用户主键")
    private String creatorUserId;
    @Schema(description = "状态")
    private Integer status;
    @Schema(description = "编码")
    private String nodeCode;
    @Schema(description = "紧急程度")
    private Integer flowUrgent;
    @JsonIgnore
    private Boolean delegateType = false;
    @JsonIgnore
    private String userId;
    @JsonIgnore
    private Integer isBatch;
}
