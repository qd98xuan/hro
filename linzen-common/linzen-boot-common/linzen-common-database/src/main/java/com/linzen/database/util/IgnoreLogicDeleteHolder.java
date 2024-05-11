package com.linzen.database.util;

/**
 * 调用setIsIgnoreLogicDelete 设置后续Mapper查询忽略字段多租户
 *
 * @author FHNP
 * @user N
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class IgnoreLogicDeleteHolder {


    private static final ThreadLocal<Boolean> ISIGNORELOGICDELETE_HOLDER = new ThreadLocal<>();

    public static boolean isIgnoreLogicDelete(){
        return Boolean.TRUE.equals(ISIGNORELOGICDELETE_HOLDER.get());
    }

    public static void setIgnoreLogicDelete(){
        ISIGNORELOGICDELETE_HOLDER.set(true);
    }

    public static void clear(){
        ISIGNORELOGICDELETE_HOLDER.remove();
    }

}
