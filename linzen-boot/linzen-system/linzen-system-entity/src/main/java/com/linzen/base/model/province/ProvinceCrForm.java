package com.linzen.base.model.province;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ProvinceCrForm {
    @Schema(description = "编码")
    @org.hibernate.validator.constraints.NotBlank(message = "必填")
    private String enCode;

    @Schema(description = "有效标志")
    private Integer enabledMark;

    @Schema(description = "名称")
    @org.hibernate.validator.constraints.NotBlank(message = "必填")
    private String fullName;

    @Schema(description = "说明")
    private String description;

    @Schema(description = "上级id")
    @NotBlank(message = "必填")
    private String parentId;

    @Schema(description = "分类")
    private String type;

    @Schema(description = "排序码")
    private long sortCode;
}
