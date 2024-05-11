package com.linzen.database.datatype.limit;

import com.linzen.database.datatype.db.interfaces.DtLimitBase;
import com.linzen.database.datatype.limit.util.DtLimitUtil;
import com.linzen.database.datatype.model.DtModel;
import com.linzen.database.datatype.model.DtModelDTO;
import lombok.NoArgsConstructor;

/**
 * 浮点数据类型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@NoArgsConstructor
public class FloatLimit extends DtLimitBase {

    public final static String CATEGORY = "type-Float";
    public final static String JAVA_TYPE = "float";

    public FloatLimit(Boolean modify){
        this.isModifyFlag = modify;
    }

    @Override
    public String initDtCategory() {
        return CATEGORY;
    }

    @Override
    public DtModel convert(DtModelDTO viewDtModel){
        DtModel dataTypeModel = DtLimitUtil.convertNumeric(viewDtModel);
        if(this.isModifyFlag){
            DtLimitUtil.getNumericLength(dataTypeModel);
        }
        return dataTypeModel;
    }

}
