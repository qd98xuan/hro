package com.linzen.base.model.dictionarytype;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DictionaryTypeCrForm {
    @Schema(description = "父级主键")
    @org.hibernate.validator.constraints.NotBlank(message = "必填")
    private String parentId;
    @Schema(description = "名称")
    @org.hibernate.validator.constraints.NotBlank(message = "必填")
    private String fullName;
    @Schema(description = "编码")
    @NotBlank(message = "必填")
    private String enCode;
    @Schema(description = "是否树形")
    @NotNull(message = "必填")
    private Integer isTree;
    @Schema(description = "说明")
    private String description;
    @Schema(description = "排序码")
    private long sortCode;
    @Schema(description = "类型")
    private Integer category;
}
