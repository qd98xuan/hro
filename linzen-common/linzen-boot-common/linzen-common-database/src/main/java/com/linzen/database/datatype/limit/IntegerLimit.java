package com.linzen.database.datatype.limit;

import com.linzen.database.datatype.db.interfaces.DtInterface;
import com.linzen.database.datatype.db.interfaces.DtLimitBase;
import com.linzen.database.datatype.sync.util.DtSyncUtil;
import com.linzen.database.source.DbBase;
import com.linzen.database.datatype.db.DtMySQLEnum;
import com.linzen.database.datatype.model.DtModel;
import com.linzen.database.datatype.model.DtModelDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 整型数据类型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@NoArgsConstructor
public class IntegerLimit extends DtLimitBase {

    public final static String CATEGORY = "type-Integer";
    public final static String JAVA_TYPE = "int";

    @Override
    public String initDtCategory() {
        return CATEGORY;
    }

    @Override
    public DtModel convert(DtModelDTO viewDtModel){
        DtInterface targetDtEnum = viewDtModel.getConvertTargetDtEnum();
        DtModel toModel = new DtModel(targetDtEnum);
        // 当转换成Oracle的数字类型
        if(targetDtEnum.getDtCategory().equals(NumberLimit.CATEGORY)){
            try{
                // 先当前数据库转成DtMySQL枚举
                DtMySQLEnum dtEnum = (DtMySQLEnum) DtSyncUtil.getToFixCovert(targetDtEnum, DbBase.MYSQL);
                // 在进行转换对比
                switch (dtEnum){
                    case TINY_INT:
                        toModel.setNumPrecision(3);
                        break;
                    case SMALL_INT:
                        toModel.setNumPrecision(5);
                        break;
                    case MEDIUM_INT:
                        toModel.setNumPrecision(7);
                        break;
                    case INT:
                        toModel.setNumPrecision(10);
                        break;
                    case BIGINT:
                        toModel.setNumPrecision(19);
                        break;
                    default:
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(this.isModifyFlag){
            toModel.setFormatLengthStr("");
        }
        return toModel;
    }

}
