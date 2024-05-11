package com.linzen.permission.model.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * IM获取用户接口
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ImUserListVo extends UserBaseVO{

    @Schema(description = "用户头像")
    private String headIcon;
    @Schema(description = "部门")
    private String department;

}
