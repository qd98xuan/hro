package com.linzen.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author FHNP
 * @user N
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class LockObjectUtil {

    private static Map<Object, Object> lockMap = new HashMap<>();

    public static synchronized Object addLockKey(Object key){
        Object val = lockMap.get(key);
        if(val == null){
            lockMap.put(key, key);
            val = key;
        }
        return val;
    }


}
