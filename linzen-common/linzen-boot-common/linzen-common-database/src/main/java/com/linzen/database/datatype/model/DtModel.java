package com.linzen.database.datatype.model;


import com.linzen.database.datatype.db.interfaces.DtInterface;
import com.linzen.database.datatype.limit.base.DtModelBase;
import com.linzen.util.StringUtil;
import lombok.Data;

/**
 * 数据类型模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DtModel extends DtModelBase {

    /**
     * 显示长度
     */
    private String formatLengthStr;


    public DtModel(DtInterface dtEnum){
        this.dtEnum = dtEnum;
    }

    /**
     * 表字段数据类型名
     */
    public String getDataType(){
        return this.dtEnum.getDataType();
    }

    /**
     * java数据类型
     */
    public String getJavaType(){
        return this.dtEnum.getJavaType();
    }

    /**
     * 当精度（>=1）小于标度
     * 重置标度，让其小于精度(精度-1)
     */
    public void formatNumLength(Integer numPrecision, Integer numScale){
        if(numScale != null && numPrecision < numScale){
            this.numScale = numPrecision - 1;
        }
    }

    public String formatDataType(){
        String lengthInfo = getFormatLengthStr();
        return getDataType() + (StringUtil.isNotEmpty(lengthInfo) ? "(" + lengthInfo + ")" : "");
    }

}
