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
public class ResponseParametersModel {
    @Schema(description = "参数名称")
    private String parameter;
    @Schema(description = "绑定字段")
    private String field;
    @Schema(description = "参数类型")
    private String type;
    @Schema(description = "示例值")
    private String sample;
    @Schema(description = "描述")
    private String remark;
    @Schema(description = "是否分页(1-分页 ，0-不分页)")
    private String pagination;
}
