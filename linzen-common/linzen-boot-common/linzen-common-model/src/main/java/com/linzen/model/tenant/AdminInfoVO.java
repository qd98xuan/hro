package com.linzen.model.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 */
@Data
public class AdminInfoVO implements Serializable {

    @Schema(description ="主键")
    private String id;

    @Schema(description ="账号")
    private String account;

    @NotNull(message = "姓名不能为空")
    @Schema(description ="姓名")
    private String realName;

    @NotNull(message = "手机号码不能为空")
    @Schema(description ="手机号码")
    private String mobilePhone;

    @NotNull(message = "电子邮箱不能为空")
    @Schema(description ="电子邮箱")
    private String email;

    @Schema(description ="租户id")
    private String tenantId;
}
