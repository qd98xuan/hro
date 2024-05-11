package com.linzen.util.sql;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sql注入处理工具类
 *
 * @author zhoujf
 */
@Slf4j
public class SqlInjectionUtil {
    /**
     * sign 用于表字典加签的盐值【SQL漏洞】
     * （上线修改值 20200501，同步修改前端的盐值）
     */
    private final static String TABLE_DICT_SIGN_SALT = "20200501";
    private final static String XSS_STR = "and |extractvalue|updatexml|exec |insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |;|or |+|user()";

    /**
     * 正则 user() 匹配更严谨
     */
    private final static String REGULAR_EXPRE_USER = "user[\\s]*\\([\\s]*\\)";
    /**
     * 正则 show tables
     */
    private final static String SHOW_TABLES = "show\\s+tables";

    /**
     * sql注释的正则
     */
    private final static Pattern SQL_ANNOTATION = Pattern.compile("/\\*.*\\*/");

    /**
     * 针对表字典进行额外的sign签名校验（增加安全机制）
     *
     * @param dictCode:
     * @param sign:
     * @param request:
     * @Return: void
     */
    public static void checkDictTableSign(String dictCode, String sign, HttpServletRequest request) {
        //表字典SQL注入漏洞,签名校验
        String accessToken = request.getHeader("X-Access-Token");
        String signStr = dictCode + SqlInjectionUtil.TABLE_DICT_SIGN_SALT + accessToken;
        String javaSign = SecureUtil.md5(signStr);
        if (!javaSign.equals(sign)) {
            log.error("表字典，SQL注入漏洞签名校验失败 ：" + sign + "!=" + javaSign + ",dictCode=" + dictCode);
            throw new RuntimeException("无权限访问！");
        }
        log.info(" 表字典，SQL注入漏洞签名校验成功！sign=" + sign + ",dictCode=" + dictCode);
    }

    /**
     * sql注入过滤处理，遇到注入关键字抛异常
     *
     * @param value String
     * @return String
     */
    public static String filterContent(String value) {
        return filterContent(value, null);
    }

    /**
     * sql注入过滤处理，遇到注入关键字抛异常
     *
     * @param value           String
     * @param customXssString String
     * @return String
     */
    public static String filterContent(String value, String customXssString) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        // 校验sql注释 不允许有sql注释
        checkSqlAnnotation(value);
        // 统一转为小写
        value = value.toLowerCase();
        //SQL注入检测存在绕过风险 https://gitee.com/jeecg/jeecg-boot/issues/I4NZGE
        //value = value.replaceAll("/\\*.*\\*/","");

        String[] xssArr = XSS_STR.split("\\|");
        for (int i = 0; i < xssArr.length; i++) {
            if (value.contains(xssArr[i])) {
                log.error("请注意，存在SQL注入关键词---> {}", xssArr[i]);
                log.error("请注意，值可能存在SQL注入风险!---> {}", value);
                throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
            }
        }
        if (customXssString != null) {
            String[] xssArr2 = customXssString.split("\\|");
            for (int i = 0; i < xssArr2.length; i++) {
                if (value.contains(xssArr2[i])) {
                    log.error("请注意，存在SQL注入关键词---> {}", xssArr2[i]);
                    log.error("请注意，值可能存在SQL注入风险!---> {}", value);
                    throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
                }
            }
        }
        if (Pattern.matches(SHOW_TABLES, value) || Pattern.matches(REGULAR_EXPRE_USER, value)) {
            throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
        }
        return value;
    }

    /**
     * sql注入过滤处理，遇到注入关键字抛异常
     *
     * @param values
     */
    public static String[] filterContent(String[] values) {
        return filterContent(values, null);
    }

    /**
     * sql注入过滤处理，遇到注入关键字抛异常
     *
     * @param values String
     */
    public static String[] filterContent(String[] values, String customXssString) {
        String[] xssArr = XSS_STR.split("\\|");
        for (String value : values) {
            if (value == null || value.isEmpty()) {
                return null;
            }
            // 校验sql注释 不允许有sql注释
            checkSqlAnnotation(value);
            // 统一转为小写
            value = value.toLowerCase();
            //SQL注入检测存在绕过风险 https://gitee.com/jeecg/jeecg-boot/issues/I4NZGE
            //value = value.replaceAll("/\\*.*\\*/","");

            for (int i = 0; i < xssArr.length; i++) {
                if (value.contains(xssArr[i])) {
                    log.error("请注意，存在SQL注入关键词---> {}", xssArr[i]);
                    log.error("请注意，值可能存在SQL注入风险!---> {}", value);
                    throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
                }
            }
            if (customXssString != null) {
                String[] xssArr2 = customXssString.split("\\|");
                for (int i = 0; i < xssArr2.length; i++) {
                    if (value.contains(xssArr2[i])) {
                        log.error("请注意，存在SQL注入关键词---> {}", xssArr2[i]);
                        log.error("请注意，值可能存在SQL注入风险!---> {}", value);
                        throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
                    }
                }
            }
            if (Pattern.matches(SHOW_TABLES, value) || Pattern.matches(REGULAR_EXPRE_USER, value)) {
                throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
            }
        }
        return values;
    }

    /**
     * 判断给定的字段是不是类中的属性
     *
     * @param field 字段名
     * @param clazz 类对象
     * @return
     */
    public static boolean isClassField(String field, Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i].getName();
            String tableColumnName = StrUtil.toUnderlineCase(fieldName);
            if (fieldName.equalsIgnoreCase(field) || tableColumnName.equalsIgnoreCase(field)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断给定的多个字段是不是类中的属性
     *
     * @param fieldSet 字段名set
     * @param clazz    类对象
     * @return
     */
    public static boolean isClassField(Set<String> fieldSet, Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (String field : fieldSet) {
            boolean exist = false;
            for (int i = 0; i < fields.length; i++) {
                String fieldName = fields[i].getName();
                String tableColumnName = StrUtil.toUnderlineCase(fieldName);
                if (fieldName.equalsIgnoreCase(field) || tableColumnName.equalsIgnoreCase(field)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验是否有sql注释
     *
     * @return
     */
    public static void checkSqlAnnotation(String str) {
        Matcher matcher = SQL_ANNOTATION.matcher(str);
        if (matcher.find()) {
            String error = "请注意，值可能存在SQL注入风险---> \\*.*\\";
            log.error(error);
            throw new RuntimeException(error);
        }
    }
}