package com.linzen.permission.model.position;

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
public class PositionCrForm {
    @NotBlank(message = "必填")
    @Schema(description = "岗位编码")
    private String enCode;
    @NotBlank(message = "必填")
    @Schema(description = "所属部门(id)")
    private String organizeId;
    @NotNull(message = "必填")
    @Schema(description = "岗位状态")
    private Integer enabledMark;
    @NotBlank(message = "必填")
    @Schema(description = "岗位名称")
    private String fullName;

    private String description;
    @NotNull(message = "必填")
    @Schema(description = "岗位类型(id)")
    private Integer type;
    @Schema(description = "排序")
    private Long sortCode;
}
