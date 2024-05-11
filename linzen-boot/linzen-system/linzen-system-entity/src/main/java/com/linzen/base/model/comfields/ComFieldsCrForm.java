package com.linzen.base.model.comfields;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ComFieldsCrForm {
    @Schema(description = "字段名")
    @NotBlank(message = "必填")
    private String fieldName;
    @Schema(description = "字段")
    @NotBlank(message = "必填")
    private String field;
    @Schema(description = "类型")
    @NotBlank(message = "必填")
    private String dataType;
    @Schema(description = "长度")
    @NotBlank(message = "必填")
    private String dataLength;
    @Schema(description = "是否必填")
    @NotNull(message = "必填")
    private Integer allowNull;
}
