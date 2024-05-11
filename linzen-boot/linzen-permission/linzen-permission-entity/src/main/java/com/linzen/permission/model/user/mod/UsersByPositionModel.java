package com.linzen.permission.model.user.mod;

import com.linzen.base.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 获取岗位成员
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class UsersByPositionModel extends Page {
    @Schema(description = "岗位id")
    private String positionId;
}
