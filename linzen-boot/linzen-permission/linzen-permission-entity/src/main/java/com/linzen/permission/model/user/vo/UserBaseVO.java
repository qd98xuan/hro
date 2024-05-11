package com.linzen.permission.model.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户视图对象基类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class UserBaseVO {

    @Schema(description = "主键")
    private String id;
    @Schema(description = "账号")
    private String account;
    @Schema(description = "名称")
    private String realName;

}
