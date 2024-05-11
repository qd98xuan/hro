package com.linzen.database.datatype.limit;

import com.linzen.database.datatype.db.interfaces.DtLimitBase;
import com.linzen.database.datatype.limit.util.DtLimitUtil;
import com.linzen.database.datatype.model.DtModelDTO;
import com.linzen.database.source.DbBase;
import com.linzen.database.datatype.db.DtOracleEnum;
import com.linzen.database.datatype.model.DtModel;

/**
 * 数字数据类型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class NumberLimit extends DtLimitBase {

    public final static String CATEGORY = "type-Number";
    public final static String JAVA_TYPE = "number";

    public NumberLimit(Boolean modify){
        this.isModifyFlag = modify;
    }

    @Override
    public String initDtCategory() {
        return CATEGORY;
    }

    @Override
    public DtModel convert(DtModelDTO viewDtModel){
        DtModel dataTypeModel;
        switch (viewDtModel.getDtEnum().getDtCategory()){
            case DecimalLimit.CATEGORY:
            case IntegerLimit.CATEGORY:
            case NumberLimit.CATEGORY:
                dataTypeModel = DtLimitUtil.convertNumeric(viewDtModel);
                break;
            default:
                dataTypeModel = new DtModel(viewDtModel.getDtEnum());
        }
        if(viewDtModel.getConvertTargetDtEnum().getIsModifyFlag()){
            if(viewDtModel.getConvertTargetDtEnum().getDbType().equals(DbBase.ORACLE)){
                if(dataTypeModel.getNumPrecision().equals(0) && dataTypeModel.getNumScale().equals(0)){
                    dataTypeModel.setNumPrecision(Integer.valueOf(DtOracleEnum.NUMBER.getNumPrecisionLm().getDefaults().toString()));
                    dataTypeModel.setNumScale(Integer.valueOf(DtOracleEnum.NUMBER.getNumScaleLm().getDefaults().toString()));
                }
            }
            DtLimitUtil.getNumericLength(dataTypeModel);
        }
        return dataTypeModel;
    }

}
