package com.linzen.database.constant;

import com.google.common.collect.ImmutableMap;
import com.linzen.exception.DataBaseException;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.Optional;

/**
 * 字段别名特殊标识
 *
 * @author FHNP
 * @version V0.0.1
 * @copyrignt 引迈信息技术有限公司
 * @date 2023-04-01
 */
public class DbAliasConst {

    /**
     * 允空
     */
    public static final String NULL = "NULL";

    /**
     * 非空
     */
    public static final String NOT_NULL = "NOT NULL";

    /**
     * 允空
     * 0:空值 NULL、1:非空值 NOT NULL
     */
    public static final NumFieldAttr<String> ALLOW_NULL = new NumFieldAttr<>(ImmutableMap.of(
            1, NULL,
            0, NOT_NULL,
            -1, NOT_NULL
    ));

    /**
     * 主键
     *  0:非主键、1：主键
     */
    public static final NumFieldAttr<Boolean> PRIMARY_KEY = new NumFieldAttr<>(ImmutableMap.of(
            1, true,
            0, false,
            -1, false
    ));

    public static final NumFieldAttr<Boolean> AUTO_INCREMENT = new NumFieldAttr<>(ImmutableMap.of(
            1, true,
            0, false,
            -1, false
    ));

    /**
     * 数值对应字段属性
     * @param <T>
     */
    @AllArgsConstructor
    public static class NumFieldAttr<T>{

        private Map<Integer, T> config;

        /**
         * 获取标识
         */
        public T getSign(Integer i) {
            return config.get(i == null ? -1 : i);
        }

        /**
         * 获取数值
         */
        public Integer getNum(T sign) throws DataBaseException {
            if(sign == null){
                return 0;
            }
            Optional<Map.Entry<Integer, T>> first = config.entrySet().stream().filter(map -> map.getValue().equals(sign)).findFirst();
            if(first.isPresent()){
                return first.get().getKey();
            }else {
                throw new DataBaseException("表示对应获取数值失败");
            }
        }
    }


}
