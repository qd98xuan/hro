package com.linzen.database.datatype.limit.base;

import com.linzen.database.datatype.db.interfaces.DtInterface;
import lombok.Data;

/**
 * 类功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyrignt 引迈信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DtModelBase {

    /**
     * 数据类型枚举
     */
    protected DtInterface dtEnum;

    /**
     * 字符长度（1个字母1个字节，1个汉字1个字节）
     */
    protected Long charLength;

    /**
     * 字节长度（1个字母1个字节，1个汉字GBK:2个字节,UTF8:3个字节）
     */
    protected Long bitLength;

    /**
     * 精度（数值整体长度）
     */
    protected Integer numPrecision;

    /**
     * 标度（数值小数点长度）
     */
    protected Integer numScale;


}
