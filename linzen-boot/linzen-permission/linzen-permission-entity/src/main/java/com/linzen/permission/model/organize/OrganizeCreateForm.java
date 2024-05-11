package com.linzen.permission.model.organize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class OrganizeCreateForm {

    @NotBlank(message = "公司上级不能为空")
    private String parentId;

    @NotBlank(message = "公司名称不能为空")
    private String fullName;

    @NotBlank(message = "公司编码不能为空")
    private String enCode;

    private String description;

    @NotNull(message = "公司状态不能为空")
    private Integer enabledMark;

    private OrganizeCreateModel propertyJson;

    @Schema(description = "排序")
    private Long sortCode;
}
