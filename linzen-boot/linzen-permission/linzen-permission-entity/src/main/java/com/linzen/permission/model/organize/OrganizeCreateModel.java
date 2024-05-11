package com.linzen.permission.model.organize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class OrganizeCreateModel {
    @NotBlank(message = "必填")
    @Schema(description = "公司简称")
    private String shortName;
    private String webSite;
    @NotBlank(message = "必填")
    @Schema(description = "所属行业")
    private String industry;
    private Long foundedTime;
    private String address;
    private String managerName;
    private String managerTelePhone;
    private String managerMobilePhone;
    private String manageEmail;
    private String bankName;
    private String bankAccount;
    private String businessscope;
    @NotBlank(message = "必填")
    @Schema(description = "公司性质")
    private String enterpriseNature;
    private String fax;
    private String telePhone;

}
