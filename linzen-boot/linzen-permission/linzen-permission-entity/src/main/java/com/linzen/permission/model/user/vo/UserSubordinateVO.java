package com.linzen.permission.model.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Builder
public class UserSubordinateVO {
    private String id;
    @Schema(description = "头像")
    private String avatar;
    @Schema(description = "用户名")
    private String userName;
    @Schema(description = "部门")
    private String department;
    @Schema(description = "岗位")
    private String position;

    @Schema(description = "是否显示下级按钮")
    private Boolean isLeaf;
}
