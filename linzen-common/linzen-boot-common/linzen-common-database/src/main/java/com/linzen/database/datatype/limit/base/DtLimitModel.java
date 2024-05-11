package com.linzen.database.datatype.limit.base;

import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 类功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class DtLimitModel {

    /**
     * 最大长度
     */
    private Object max;

    /**
     * 最小长度
     */
    private Object min;

    /**
     * 默认长度
     */
    private Object defaults;

    /**
     * 固定长度
     */
    private Object fixed;

    public DtLimitModel(Object fixed){
        this.fixed = fixed;
    }

    /**
     * 生成类型限制对象
     */
    public DtLimitModel(Object maxLength, Object minLength, Object defaultLength){
        this.max = maxLength;
        this.min = minLength;
        this.defaults = defaultLength;
        this.fixed = defaultLength;
    }

}
