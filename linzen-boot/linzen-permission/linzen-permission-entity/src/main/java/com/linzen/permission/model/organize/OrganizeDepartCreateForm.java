package com.linzen.permission.model.organize;

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
public class OrganizeDepartCreateForm {

    private String managerId;
    @NotBlank(message = "必填")
    @Schema(description = "上级ID")
    private String parentId;
    @NotBlank(message = "必填")
    @Schema(description = "部门名称")
    private String fullName;
    @NotBlank(message = "必填")
    @Schema(description = "部门编码")
    private String enCode;
    @Schema(description = "状态")
    private int delFlag;
    private String description;
    @Schema(description = "排序")
    private Long sortCode;
}
