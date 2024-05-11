package com.linzen.util.context;

import com.linzen.util.ServletUtil;
import java.util.Objects;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class RequestContext {

    public static boolean isVue3(){
        //2, 3
        return Objects.equals(ServletUtil.getHeader("vue-version"), "3");
    }

    public static boolean isOrignPc(){
        //pc, app
        return !Objects.equals(ServletUtil.getHeader("linzen-origin"), "app");
    }

}
