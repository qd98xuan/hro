package com.linzen.database.sql.param;

import com.alibaba.druid.proxy.jdbc.NClobProxyImpl;

import java.io.BufferedReader;

/**
 * 类功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyrignt 引迈信息技术有限公司
 * @date 2023-04-01
 */
public class FormatSqlDM {

    public static String getClob(NClobProxyImpl nClobProxy) throws Exception {
        BufferedReader br = new BufferedReader(nClobProxy.getCharacterStream());
        String s = br.readLine();
        StringBuilder sb = new StringBuilder();
        while (s != null) {// 执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING
            sb.append(s).append("\n");
            s = br.readLine();
        }
        return sb.toString();
    }

}
