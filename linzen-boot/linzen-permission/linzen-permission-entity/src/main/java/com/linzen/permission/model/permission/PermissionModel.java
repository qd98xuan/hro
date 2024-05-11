package com.linzen.permission.model.permission;

import com.linzen.util.treeutil.SumTree;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * 个人信息设置 我的组织/我的岗位/（我的角色：暂无）
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PermissionModel extends SumTree {

    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "id")
    private String id;
    @Schema(description = "是否为默认")
    private Boolean isDefault;

}
