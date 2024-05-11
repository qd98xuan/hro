package com.linzen.base.model.dbtable.vo;

import com.linzen.database.model.dbtable.DbTableFieldModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@NoArgsConstructor
public class DbTableVO {

    @NotBlank(message = "必填")
    @Schema(description = "表名")
    private String table;
    @NotBlank(message = "必填")
    @Schema(description = "表注释")
    private String tableName;

    public DbTableVO(DbTableFieldModel dbTableFieldModel){
        this.table = dbTableFieldModel.getTable();
        this.tableName = dbTableFieldModel.getComment();
    }

}
