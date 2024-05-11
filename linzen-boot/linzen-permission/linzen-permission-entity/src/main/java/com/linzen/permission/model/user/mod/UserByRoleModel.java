package com.linzen.permission.model.user.mod;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class UserByRoleModel implements Serializable {
    /**
     * 关键字
     */
    @Schema(description = "关键字")
    private String keyword;

    /**
     * 组织id
     */
    @Schema(description = "组织id")
    private String organizeId;

    /**
     * 角色id
     */
    @Schema(description = "角色id")
    private String roleId;

}
