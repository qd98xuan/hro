package com.linzen.model.login.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PermissionVO {

    @Schema(description = "主键")
    private String id;

    @Schema(description = "权限名称")
    private String fullName;

    @Schema(description = "权限英文名称")
    private String enCode;
}
