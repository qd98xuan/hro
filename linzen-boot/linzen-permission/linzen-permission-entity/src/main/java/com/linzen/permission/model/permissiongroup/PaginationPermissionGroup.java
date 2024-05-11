package com.linzen.permission.model.permissiongroup;

import com.linzen.base.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class PaginationPermissionGroup extends Pagination implements Serializable {
    @Schema(description = "状态")
    private Integer enabledMark;
}
