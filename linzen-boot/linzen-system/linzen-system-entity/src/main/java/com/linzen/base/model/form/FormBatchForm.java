package com.linzen.base.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FormBatchForm {
    @Schema(description = "菜单id")
    @NotBlank(message = "必填")
    private String moduleId;
    @Schema(description = "数据")
    private Object formJson;
    @Schema(description = "排序码")
    private Long sortCode;
    @Schema(description = "规则")
    private Integer fieldRule;
    @Schema(description = "绑定表")
    private String bindTable;
}
