package com.linzen.permission.model.authorize;

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
public class AuthorizeDataReturnVO {
    @Schema(description = "权限模型集合")
    List<AuthorizeDataReturnModel> list;
    @Schema(description = "id集合")
    List<String> ids;
    //all字段里面不包括菜单id
    @Schema(description = "所有的id")
    List<String> all;
}
