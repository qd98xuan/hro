package com.linzen.base.model.dictionarydata;

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
public class DictionaryDataCrForm {
    @NotBlank(message = "必填")
    @Schema(description = "项目代码")
    private String enCode;

    @Schema(description = "有效标志")
    private Integer enabledMark;

    @NotBlank(message = "必填")
    @Schema(description = "上级项目名称")
    private String fullName;

    @Schema(description = "说明")
    private String description;

    @NotBlank(message = "必填")
    @Schema(description = "上级id,没有传0")
    private String parentId;
    @Schema(description = "分类id")
    private String dictionaryTypeId;
    @Schema(description = "排序码")
    private long sortCode;
}
