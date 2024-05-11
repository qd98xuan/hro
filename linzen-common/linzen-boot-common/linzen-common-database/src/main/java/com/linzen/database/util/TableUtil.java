package com.linzen.database.util;

import com.linzen.database.constant.DbConst;

import java.util.Random;

/**
 * 表字段相关工具类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class TableUtil {


    /**
     * 随机生成包含大小写字母及数字的字符串
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String getStringRandom(int length) {
        StringBuilder val = new StringBuilder();
        Random random = new Random();
        //参数length，表示生成几位随机数
        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val.append((char) (random.nextInt(26) + temp));
            } else {
                val.append(random.nextInt(10));
            }
        }
        return val.toString();
    }

    /**
     * 检测自带表
     *
     * @param tableName 表明
     * @return ignore
     */
    public static Boolean checkByoTable(String tableName) {
        String[] tables = DbConst.BYO_TABLE.split(",");
        boolean exists;
        for (String table : tables) {
            exists = tableName.toLowerCase().equals(table);
            if (exists) {
                return true;
            }
        }
        return false;
    }

}
