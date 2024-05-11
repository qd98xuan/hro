package com.linzen.util.time;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class Utils {
    static int toNumber(String str, int index, int length) {
        int result = 0;
        for (int i = length, x = 0; i > 0; i--, x++) {
            char c = str.charAt(index + x);
            if (c < '0' || c > '9') {
                return -1;
            }
            int val = c - 48;
            result = result + (int) (Math.pow(10, i - 1)) * val;
        }
        return result;
    }
}