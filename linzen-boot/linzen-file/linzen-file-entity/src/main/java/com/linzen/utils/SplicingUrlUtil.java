package com.linzen.utils;

import com.linzen.model.YozoFileParams;
import com.linzen.model.YozoParams;
import com.linzen.util.XSSEscape;
import org.springframework.util.StringUtils;

/**
 * @author FHNP
 */
public class SplicingUrlUtil {
    /**
     * 永中预览url拼接
     * @param params
     * @return
     */
    public static String getPreviewUrl(YozoFileParams params) {
        StringBuilder paramsUrl = new StringBuilder();
        if (!StringUtils.isEmpty(params.getNoCache())) {
            paramsUrl.append("&noCache=" + params.getNoCache());
        }
        if (!StringUtils.isEmpty(params.getWatermark())) {
            String watermark = XSSEscape.escape(params.getWatermark());
            paramsUrl.append("&watermark=" + watermark);
        }
        if (!StringUtils.isEmpty(params.getIsCopy())) {
            paramsUrl.append("&isCopy=" + params.getIsCopy());
        }
        if (!StringUtils.isEmpty(params.getPageStart())) {
            paramsUrl.append("&pageStart=" + params.getPageStart());
        }
        if (!StringUtils.isEmpty(params.getPageEnd())) {
            paramsUrl.append("&pageEnd=" + params.getPageEnd());
        }
        if (!StringUtils.isEmpty(params.getType())) {
            String type = XSSEscape.escape(params.getType());
            paramsUrl.append("&type=" + type);
        }
        String s = paramsUrl.toString();
        String previewUrl= YozoParams.DOMAIN+"?k=" + YozoParams.DOMAIN_KEY + "&url=" + params.getUrl() + s;
        return previewUrl;
    }

}
