package com.linzen.permission.model.user.mod;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class UserAuthorizeModel {
    @Schema(description = "主键")
    private String id;

    @Schema(description = "名称")
    private String fullName;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "下级菜单列表")
    private List<UserAuthorizeModel> children;
}
