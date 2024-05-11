package com.linzen.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Schema(description = "常用模型")
public class AppUserInfoVO {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "户名")
    private String realName;
    @Schema(description = "部门名称")
    private String organizeName;
    @Schema(description = "账号")
    private String account;
    @Schema(description = "岗位名称")
    private String positionName;
    @Schema(description = "办公电话")
    private String telePhone;
    @Schema(description = "办公座机")
    private String landline;
    @Schema(description = "手机号码")
    private String mobilePhone;
    @Schema(description = "用户头像")
    private String headIcon;
    @Schema(description = "邮箱")
    private String email;
}
