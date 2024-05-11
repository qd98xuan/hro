package com.linzen.database.datatype.utils;

import java.util.regex.Pattern;

/**
 * 类功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class DataTypeUtil {

    /**
     * 数据类型判断
     */
    public static Boolean numFlag(String... nums){
        for (String num : nums) {
            if(!(Pattern.compile("^[-\\+]?[\\d]*$").matcher(num).matches())){
                return false;
            }
        }
        return true;
    }

}
