package com.linzen.permission.model.user.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 超级管理员设置表单参数
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class UserUpdateAdminForm {

    @Schema(description = "超级管理id集合")
    List<String> adminIds;

}
