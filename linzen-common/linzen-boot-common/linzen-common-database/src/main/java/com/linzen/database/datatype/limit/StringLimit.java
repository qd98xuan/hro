package com.linzen.database.datatype.limit;

import com.linzen.database.datatype.db.interfaces.DtInterface;
import com.linzen.database.datatype.db.interfaces.DtLimitBase;
import com.linzen.database.datatype.limit.base.DtLimitModel;
import com.linzen.database.datatype.limit.util.DtLimitUtil;
import com.linzen.database.datatype.model.DtModelDTO;
import com.linzen.database.source.DbBase;
import com.linzen.database.datatype.db.DtMySQLEnum;
import com.linzen.database.datatype.model.DtModel;
import lombok.NoArgsConstructor;

import java.util.function.BiFunction;

/**
 * 字符串数据类型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@NoArgsConstructor
public class StringLimit extends DtLimitBase {

    public final static String CATEGORY = "type-String";
    public final static String JAVA_TYPE = "String";

    @Override
    public String initDtCategory() {
        return CATEGORY;
    }

    public StringLimit(Boolean modify){
        this.isModifyFlag = modify;
    }

    @Override
    public DtModel convert(DtModelDTO dto){
        DtInterface originEnum = dto.getDtEnum();
        DtInterface targetEnum = dto.getConvertTargetDtEnum();
        DtModel dataTypeModel = new DtModel(targetEnum);
        special(dto);
        if(this.isModifyFlag){
            // 设置模型
            BiFunction<Long, DtLimitModel, Long> setMod = (inputVarLength, varLengthLm)->{
                // 长度设置;
                Long targetLength = DtLimitUtil.convertCharacter(inputVarLength, dto.getConvertType(), varLengthLm);
                // 获取字符类型显示
                dataTypeModel.setFormatLengthStr(targetLength.toString());
                return targetLength;
            };
            // 字节字符转换规则
            boolean originBigFlag = originEnum.getBitLengthLm() != null;
            boolean originCharFlag = originEnum.getCharLengthLm() != null;
            boolean targetBigFlag = targetEnum.getBitLengthLm() != null;
            boolean targetCharFlag = targetEnum.getCharLengthLm() != null;
            /* GBK 1字符=2字节，UTF8 1字符=3字节 */
            if(originBigFlag && targetCharFlag){
                // 字节 -> 字符 /3
                dataTypeModel.setCharLength(setMod.apply(dto.getBitLength() / 3, targetEnum.getCharLengthLm()));
            }else if(originCharFlag && targetBigFlag){
                // 字符 -> 字节 *3
                dataTypeModel.setBitLength(setMod.apply(dto.getCharLength() * 3, targetEnum.getBitLengthLm()));
            }else if(originBigFlag && targetBigFlag){
                // 字节 -> 字节 ==
                dataTypeModel.setBitLength(setMod.apply(dto.getBitLength(), targetEnum.getBitLengthLm()));
            }else if(originCharFlag){
                // 字符 -> 字符 ==
                dataTypeModel.setCharLength(setMod.apply(dto.getCharLength(), targetEnum.getCharLengthLm()));
            }
        }
        return dataTypeModel;
    }

    /**
     * 特殊情况
     */
    public void special(DtModelDTO dto){
        DtInterface originEnum = dto.getDtEnum();
        if(originEnum.getDbType().equals(DbBase.MYSQL) &&
                originEnum.getDataType().equals(DtMySQLEnum.BIT.getDataType()) &&
                dto.getCharLength() == 1){
            /* MySQL设置tinyint类型且长度为1时，JDBC读取时会变成BIT类型，java类型为Boolean类型。
            1:true , 0:false , 当前先转换成字符串格式，长度为5*/
            dto.setCharLength(15L);
        }
    }

}
