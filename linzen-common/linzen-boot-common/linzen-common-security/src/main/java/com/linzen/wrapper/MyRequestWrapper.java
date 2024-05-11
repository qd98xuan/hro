package com.linzen.wrapper;

import cn.dev33.satoken.context.SaHolder;
import com.alibaba.fastjson2.JSONObject;
import com.linzen.handler.IRestHandler;
import com.linzen.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Request封装
 * 处理类型：
 * application/json
 * application/x-www-form-urlencoded
 */
@Slf4j
public class MyRequestWrapper extends HttpServletRequestWrapper {

    protected Map<String, String[]> paramHashValues;
    protected String requestBody = null;
    protected HttpServletRequest req;

    private final List<IRestHandler> handlers;

    private final String[] EMPTY_ARRAY = new String[0];

    /**
     * 是否需要处理Header, Body, Form
     */
    private boolean supportHeader, supportBody, supportParameter;

    public MyRequestWrapper(HttpServletRequest request, List<IRestHandler> handlers) throws IOException {
        super(request);
        this.req = request;
        this.handlers = handlers == null ? Collections.emptyList() : handlers;
        assert handlers != null;
        if(!handlers.isEmpty()){
            try {
                if (isJsonBodyRequest()) {
                    List<IRestHandler> bodyHandlers = this.handlers.stream().filter(IRestHandler::supportBodyJson).collect(Collectors.toList());
                    if (!bodyHandlers.isEmpty()) {
                        this.requestBody = convertInputStreamToString(req.getInputStream());
                        if(StringUtil.isNotEmpty(this.requestBody)) {
                            JSONObject jsonData = JSONObject.parse(this.requestBody);
                            for (IRestHandler bodyHandler : bodyHandlers) {
                                jsonData = bodyHandler.initBodyJson(jsonData);
                            }
                            requestBody = jsonData.toJSONString();
                            supportBody = true;
                        }
                    }
                } else if (isParameterRequest()) {
                    List<IRestHandler> parameterHandlers = this.handlers.stream().filter(IRestHandler::supportParameter).collect(Collectors.toList());
                    if (!parameterHandlers.isEmpty()) {
                        paramHashValues = req.getParameterMap();
                        /*
                        //解除锁定直接添加
                        if(paramHashValues instanceof ParameterMap){
                            ((ParameterMap<String, String[]>) paramHashValues).setLocked(false);
                        }
                        */
                        parameterHandlers.forEach(h -> paramHashValues = h.initParameter(paramHashValues));
                        supportParameter = true;
                    }
                }
            } catch (Exception e){
                log.error("请求解析失败：{}", SaHolder.getRequest().getRequestPath());
                throw e;
            }
        }
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if(supportBody){
            return new BufferedReader(new StringReader(requestBody));
        }else{
            return super.getReader();
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if(supportBody){
            return new ServletInputStream() {
                private InputStream in = new ByteArrayInputStream(
                        requestBody.getBytes(req.getCharacterEncoding()));
                @Override
                public int read() throws IOException {
                    return in.read();
                }
                @Override
                public boolean isFinished() {
                    return false;
                }
                @Override
                public boolean isReady() {
                    return false;
                }
                @Override
                public void setReadListener(ReadListener readListener) {

                }
            };
        }else {
            return super.getInputStream();
        }
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        if(supportParameter){
            return this.paramHashValues;
        }else {
            return super.getParameterMap();
        }
    }

    @Override
    public String getParameter(String name) {
        if(supportParameter){
            String[] parameter = this.paramHashValues.getOrDefault(name, EMPTY_ARRAY);
            return parameter.length == 0 ? null: parameter[0];
        }else {
            return super.getParameter(name);
        }
    }

    @Override
    public String[] getParameterValues(String name) {
        if(supportParameter){
            return this.paramHashValues.get(name);
        }else {
            return super.getParameterValues(name);
        }
    }

    @Override
    public Enumeration<String> getParameterNames() {
        if(supportParameter){
            return Collections.enumeration(this.paramHashValues.keySet());
        }else {
            return super.getParameterNames();
        }
    }

    protected boolean isParameterRequest(){
        String contentType = this.req.getContentType();
        if(StringUtil.isNotEmpty(contentType)) {
            if (StringUtils.substringMatch(contentType, 0, MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isJsonBodyRequest(){
        String contentType = this.req.getContentType();
        if(StringUtil.isNotEmpty(contentType)) {
            if (StringUtils.substringMatch(contentType, 0, MediaType.APPLICATION_JSON_VALUE)) {
                return true;
            }
        }
        return false;
    }

    protected String convertInputStreamToString(InputStream inputStream) throws IOException {
        return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
    }
}