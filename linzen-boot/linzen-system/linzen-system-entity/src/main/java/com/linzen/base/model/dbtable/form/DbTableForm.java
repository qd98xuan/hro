package com.linzen.base.model.dbtable.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 表信息表单信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DbTableForm  {

    @Schema(description = "表名")
    private String table;

    @NotBlank(message = "必填")
    @Schema(description = "表说明")
    private String tableName;

    @Schema(description = "新表名")
    private String newTable;

}
