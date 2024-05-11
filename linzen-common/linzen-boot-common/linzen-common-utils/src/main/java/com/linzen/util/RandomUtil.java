package com.linzen.util;

import com.github.yitter.idgen.YitIdHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Component
public class RandomUtil {

    /**
     * 生成主键id
     *
     * @return
     */
    public static String uuId() {
        long newId = YitIdHelper.nextId();
        return newId + "";
    }

    /**
     * 生成6位数随机英文
     *
     * @return
     */
    public static String enUuId() {
        String str = "";
        for (int i = 0; i < 6; i++) {
            //你想生bai成几个字符的，du就把3改成zhi几，dao如果改成１,那就生成一个1653随机字母．
            str = str + (char) (Math.random() * 26 + 'a');
        }
        return str;
    }

    /**
     * 生成排序编码
     *
     * @return
     */
    public static Long parses() {
        Long time = 0L;
        return time;
    }

    /**
     * 生成短信验证码
     *
     * @return
     */
    public static String getRandomCode() {
        String code = "";
        Random rand = new Random();
        for (int i = 0; i < 6; i++) {
            int ran = rand.nextInt(10);
            code = code + ran;
        }
        return code;
    }

}
