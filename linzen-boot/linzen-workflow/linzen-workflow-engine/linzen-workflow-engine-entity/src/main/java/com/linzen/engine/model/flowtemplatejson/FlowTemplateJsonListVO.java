package com.linzen.engine.model.flowtemplatejson;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Data
public class FlowTemplateJsonListVO {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "流程基本主键")
    private String templateId;
    @Schema(description = "版本")
    private String version;
    @Schema(description = "流程json")
    private String flowTemplateJson;
    @Schema(description = "创建时间")
    private Long creatorTime;
    @Schema(description = "创建用户")
    private String creatorUserId;
    @Schema(description = "创建人")
    private String creatorUser;
    @Schema(description = "修改时间")
    private Long updateTime;
    @Schema(description = "修改用户")
    private String updateUserId;
    @Schema(description = "状态")
    private Integer enabledMark;
}
