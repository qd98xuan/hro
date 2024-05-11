package com.linzen.base.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class RequestParameterModel {
    @Schema(description = "参数名称")
    private String parameter;
    @Schema(description = "绑定字段")
    private String field;
    @Schema(description = "参数类型")
    private String type;
    @Schema(description = "操作符")
    private String opt;
    @Schema(description = "1-必填 ，0-非必填")
    private Integer required;
    @Schema(description = "默认值")
    private String defaultVal;
}
