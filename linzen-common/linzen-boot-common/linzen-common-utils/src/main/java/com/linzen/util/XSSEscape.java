package com.linzen.util;

import lombok.extern.slf4j.Slf4j;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.springframework.util.ObjectUtils;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 防止XSS注入
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
public class XSSEscape {


    /**
     * 非法路径符号
     */
    private static final Pattern PATH_PATTERN = Pattern.compile("\\.\\.|~[/\\\\]|[<]|>|\"|[*]|[|]|[?]", Pattern.CASE_INSENSITIVE);

    private static InputStream inputStream;
    private static Policy policy;
    private static Policy emptyPolicy;
    private static Policy imgOnlyBase64Policy;

    static{
        try{
            inputStream = XSSEscape.class.getClassLoader().getResourceAsStream("antisamy-ebay.xml");
            policy = Policy.getInstance(inputStream);
            inputStream.close();
            inputStream = XSSEscape.class.getClassLoader().getResourceAsStream("antisamy-ebay-imgonlybase64.xml" );
            imgOnlyBase64Policy = Policy.getInstance(inputStream);
            inputStream.close();
            inputStream = XSSEscape.class.getClassLoader().getResourceAsStream("antisamy-empty.xml");
            emptyPolicy = Policy.getInstance(inputStream);
            inputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 跨站式脚本攻击字符串过滤
     * @param character 需要转义的字符串
     */
    public static String escape(String character) {
        try {
            AntiSamy antiSamy = new AntiSamy();
            String str = character.replaceAll("&quot;", "\"");
            str = str.replaceAll("&amp;", "&");
            str = str.replaceAll("&lt;", "<");
            str = str.replaceAll("&gt;", ">");
            str = str.replaceAll("&nbsp;", " ");
            CleanResults scan = antiSamy.scan(str, policy);
            str = scan.getCleanHTML();
            return str;
        } catch (Exception e) {
            log.error("转换错误：" + e.getMessage());
        }
        return null;
    }


    /**
     * 跨站式脚本攻击字符串过滤(图片标签只允许base64格式)
     * @param character 需要转义的字符串
     */
    public static String escapeImgOnlyBase64(String character) {
        try {
            AntiSamy antiSamy = new AntiSamy();
            String str = character.replaceAll("&quot;", "\"");
            str = str.replaceAll("&amp;", "&");
            str = str.replaceAll("&lt;", "<");
            str = str.replaceAll("&gt;", ">");
            str = str.replaceAll("&nbsp;", " ");
            CleanResults scan = antiSamy.scan(str, imgOnlyBase64Policy);
            str = scan.getCleanHTML();
            return str;
        } catch (Exception e) {
            log.error("转换错误：" + e.getMessage());
        }
        return null;
    }


    /**
     * 此方法伪过滤
     * @param character 需要转义的字符串
     */
    public static  <T> T escapeObj(T character) {
        try {
            if(ObjectUtils.isEmpty(character)){
                return character;
            }
            String str = escapeEmpty(character.toString());
            if(ObjectUtils.isEmpty(str.trim())){
                return character;
            }
            return (T) JsonUtil.createJsonToBean(str, character.getClass());
        } catch (Exception e) {
        }
        return character;
    }

    /**
     * 此方法伪过滤
     * @param character 需要转义的字符串
     */
    public static String escapeEmpty(String character) {
        try {
            AntiSamy antiSamy = new AntiSamy();
            CleanResults scan = antiSamy.scan(character, emptyPolicy);
            return scan.getCleanHTML();
        } catch (Exception e) {
        }
        return character;
    }

    /**
     * 过滤非法路径
     * @param path
     * @return
     */
    public static String escapePath(String path){
        Matcher matcher;
        while((matcher = PATH_PATTERN.matcher(path)).find()){
            path = matcher.replaceAll("");
        }
        return escapeEmpty(path);
    }


}
