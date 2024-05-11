package com.linzen.database.datatype.limit;

import com.linzen.database.datatype.db.interfaces.DtLimitBase;
import com.linzen.database.datatype.model.DtModel;
import com.linzen.database.datatype.model.DtModelDTO;
import lombok.NoArgsConstructor;

/**
 * 时间数据类型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@NoArgsConstructor
public class DateTimeLimit extends DtLimitBase {

    public final static String CATEGORY = "type-DateTime";
    public final static String JAVA_TYPE = "date";

    public DateTimeLimit(Boolean modify){
        this.isModifyFlag = modify;
    }

    @Override
    public String initDtCategory() {
        return CATEGORY;
    }

    @Override
    public DtModel convert(DtModelDTO dtModelDTO){
        DtModel dataTypeModel = new DtModel(dtModelDTO.getConvertTargetDtEnum());
        if(this.isModifyFlag){
            dataTypeModel.setFormatLengthStr("");
        }
        return dataTypeModel;
    }

}
