package com.linzen.base.model.dblink;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 数据连接表单对象
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DbLinkCreUpForm extends DbLinkBaseForm{

    @Schema(description = "有效标识")
    @NotNull(message = "必填")
    private boolean delFlag;

}
