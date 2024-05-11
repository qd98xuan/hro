package com.linzen.util;

import java.util.List;

/**
 * 短信参数解析
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class ParameterUtil {

    /**
     * 获取参数
     *
     * @param text 需要解析的文本
     * @param list 存放参数的集合
     * @return
     */
    public static String parse(String openToken, String closeToken, String text, List<String> list) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        // search open token
        int start = text.indexOf(openToken);
        if (start == -1) {
            return text;
        }
        char[] src = text.toCharArray();
        int offset = 0;
        final StringBuilder builder = new StringBuilder();
        StringBuilder expression = null;
        while (start > -1) {
            if (start > 0 && src[start - 1] == '\\') {
                // this open token is escaped. remove the backslash and continue.
                builder.append(src, offset, start - offset - 1).append(openToken);
                offset = start + openToken.length();
            } else {
                // found open token. let's search close token.
                if (expression == null) {
                    expression = new StringBuilder();
                } else {
                    expression.setLength(0);
                }
                builder.append(src, offset, start - offset);
                offset = start + openToken.length();
                int end = text.indexOf(closeToken, offset);
                while (end > -1) {
                    if (end > offset && src[end - 1] == '\\') {
                        // this close token is escaped. remove the backslash and continue.
                        expression.append(src, offset, end - offset - 1).append(closeToken);
                        offset = end + closeToken.length();
                        end = text.indexOf(closeToken, offset);
                    } else {
                        expression.append(src, offset, end - offset);
                        break;
                    }
                }
                // 塞到list中
                list.add(expression.toString());
                if (end == -1) {
                    // close token was not found.
                    builder.append(src, start, src.length - start);
                    offset = src.length;
                } else {
                    offset = end + closeToken.length();
                }
            }
            start = text.indexOf(openToken, offset);
        }
        if (offset < src.length) {
            builder.append(src, offset, src.length - offset);
        }
        return builder.toString();
    }

    /**
     * 判断数据库类型
     *
     * @param driverName 驱动名称
     * @return
     */
    public static String getDbType(String driverName) {
        if (StringUtil.isNotEmpty(driverName)) {
            // 不是使用自定义URL
            String dbType = "";
            if (driverName.contains("mysql")) {
                dbType = "MySQL";
            } else if (driverName.contains("sqlserver")) {
                dbType = "SQLServer";
            } else if (driverName.contains("oracle")) {
                dbType = "Oracle";
            } else if (driverName.contains("dm")) {
                dbType = "DM";
            } else if (driverName.contains("kingbase8")) {
                dbType = "KingbaseES";
            } else if (driverName.contains("postgresql")) {
                dbType = "PostgreSQL";
            }
            return dbType;
        }
        return null;
    }

    /**
     * 检查是否含有关键字
     *
     * @param sql
     * @param sensitive
     * @return
     */
    public static String checkContainsSensitive(String sql, String sensitive) {
        if (StringUtil.isNotEmpty(sql)) {
            String[] split = sensitive.split(",");
            for (String str : split) {
                str = str.trim();
                String[] matchStr = new String[]{str + " ", str.trim() + "-"};
                for (String s : matchStr) {
                    boolean contains = sql.toUpperCase().contains(s);
                    if (contains) {
                        return str.trim();
                    }
                }
            }
        }
        return "";
    }

}
