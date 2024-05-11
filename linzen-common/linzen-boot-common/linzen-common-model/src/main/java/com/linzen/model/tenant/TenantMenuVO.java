package com.linzen.model.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class TenantMenuVO {

    @Schema(description = "权限模型集合")
    List<TenantMenuTreeReturnModel> list = new ArrayList<>();

    @Schema(description = "ID集合")
    List<String> ids = new ArrayList<>();

    //这里面不包括菜单的ID
    @Schema(description = "除菜单外的ID")
    List<String> all = new ArrayList<>();
}
