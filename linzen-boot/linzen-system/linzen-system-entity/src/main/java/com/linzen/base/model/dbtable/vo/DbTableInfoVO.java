package com.linzen.base.model.dbtable.vo;

import com.linzen.database.model.dbfield.DbFieldModel;
import com.linzen.database.model.dbtable.DbTableFieldModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@NoArgsConstructor
public class DbTableInfoVO {

    @Schema(description = "表信息")
    private DbTableVO tableInfo;
    @Schema(description = "字段信息集合")
    private List<DbFieldVO> tableFieldList;
    @Schema(description = "表是否存在信息")
    private Boolean hasTableData;

    public DbTableInfoVO(DbTableFieldModel dbTableModel, List<DbFieldModel> dbFieldModelList){
        if(dbTableModel != null){
            List<DbFieldVO> list = new ArrayList<>();
            for (DbFieldModel dbFieldModel : dbFieldModelList) {
                list.add(new DbFieldVO(dbFieldModel));
            }
            this.tableFieldList = list;
            this.tableInfo = new DbTableVO(dbTableModel);
            this.hasTableData = dbTableModel.getHasTableData();
        }
    }

}
