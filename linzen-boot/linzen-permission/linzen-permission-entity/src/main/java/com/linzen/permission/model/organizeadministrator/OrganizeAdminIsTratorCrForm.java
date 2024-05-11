package com.linzen.permission.model.organizeadministrator;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 机构分级管理员
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class OrganizeAdminIsTratorCrForm implements Serializable {

    /**
     * 用户主键
     **/
    @Schema(description = "用户主键")
    @NotBlank(message = "管理员不能为空")
    private String userId;

    /**
     * 分级管理员模型集合
     */
    @Schema(description = "分级管理员模型集合")
    private List<OrganizeAdministratorCrModel> orgAdminModel;


    @Schema(description = "菜单集合")
    private List<String> moduleIds;
    @Schema(description = "应用集合")
    private List<String> systemIds;

}
