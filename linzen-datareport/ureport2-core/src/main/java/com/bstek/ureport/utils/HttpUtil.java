package com.bstek.ureport.utils;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.linzen.util.JsonUtil;
import com.linzen.util.ServletUtil;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Slf4j
public class HttpUtil {
    /**
     * https请求
     *
     * @param requestUrl    url
     * @param requestMethod GET/POST
     * @param outputStr     参数
     * @return
     */
    public static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {
        JSONObject jsonObject = null;
        try {
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(requestMethod);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(3000);
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(outputStr.getBytes(Constants.UTF_8));
                outputStream.close();
            }
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Constants.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            conn.disconnect();
            jsonObject = JSONObject.parseObject(buffer.toString());
        } catch (Exception e) {
            if (e.getMessage().contains("400")) {
                jsonObject = JSONObject.parseObject("{\"code\":400,\"message\":\"租户不存在,请先注册\",\"data\":null}");
            }
            log.error(e.getMessage());
        }
        return jsonObject;
    }

    /**
     * http请求
     *
     * @param requestUrl    url
     * @param requestMethod GET/POST
     * @param outputStr     参数
     * @return
     */
    public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr, String... token) {
        JSONObject jsonObject = null;
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(requestMethod);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(6000);
            conn.setRequestProperty("Content-Type", "application/json");
            if (ObjectUtil.isNotEmpty(token)) {
                conn.setRequestProperty("Authorization", token[0]);
                // 处理请求头参数
                if (token.length > 1 && ObjectUtil.isNotEmpty(token[1])) {
                    Map<String, Object> requestHeader = JsonUtil.stringToMap(token[1]);
                    for (String field : requestHeader.keySet()) {
                        conn.setRequestProperty(field, requestHeader.get(field) + "");
                    }
                }
            }
            String agent = ServletUtil.getUserAgent();
            if (ObjectUtil.isNotEmpty(agent)) {
                conn.setRequestProperty("User-Agent", agent);
            }
            if (ObjectUtil.isNotEmpty(outputStr)) {
                @Cleanup OutputStream outputStream = conn.getOutputStream();
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }
            @Cleanup InputStream inputStream = conn.getInputStream();
            @Cleanup InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            @Cleanup BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            conn.disconnect();
            jsonObject = JSONObject.parseObject(buffer.toString());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return jsonObject;
    }
}
