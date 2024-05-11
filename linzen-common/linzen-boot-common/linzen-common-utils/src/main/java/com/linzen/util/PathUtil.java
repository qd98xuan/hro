package com.linzen.util;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * URL匹配工具
 */
public class PathUtil {

    private static AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    public static boolean isIgnorePath(String url, List<String> ignoreUrls){
        if(ObjectUtils.isEmpty(url)){
            return true;
        }
        if(ObjectUtils.isEmpty(ignoreUrls)){
            return false;
        }
        return ignoreUrls.stream().anyMatch(u -> url.startsWith(u) || ANT_PATH_MATCHER.match(u, url));
    }

}
