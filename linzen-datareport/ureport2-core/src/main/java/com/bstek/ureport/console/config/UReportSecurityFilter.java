package com.bstek.ureport.console.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.exception.BackResultException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bstek.ureport.exception.ReportException;
import com.linzen.base.ActionResultCode;
import com.linzen.base.UserInfo;
import com.linzen.config.ConfigValueUtil;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.filter.SecurityFilter;
import com.linzen.model.tenant.TenantVO;
import com.linzen.properties.SecurityProperties;
import com.linzen.util.*;
import org.springframework.http.HttpHeaders;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class UReportSecurityFilter extends SecurityFilter {

    private static final String ALL = "*";
    private static final String MAX_AGE = "18000L";
    private ConfigValueUtil configValueUtil;
    private RedisUtil redisUtil;

    public UReportSecurityFilter(SecurityProperties securityProperties, ConfigValueUtil configValueUtil, RedisUtil redisUtil) {
        super(securityProperties, configValueUtil);
        this.configValueUtil = configValueUtil;
        this.redisUtil = redisUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(coress()){
            return;
        }
        super.doFilter(request, response, chain);
    }

    @Override
    protected void initAuthenticationInfo() {
        SaRequest request = SaHolder.getRequest();
        String token = request.getHeader("Authorization");
        if(token == null){
            token = request.getParam("token");
        }
        if(StringUtil.isEmpty(token)){
            throwNoLogin();
        }
        UserInfo userInfo;
        TenantVO tenantVO;
        String[] strings = token.split(" ");
        token = strings[strings.length-1];
        JWT jwt = JWTUtil.parseToken(token);
        Object javaToken = jwt.getPayload("token");
        if(javaToken != null) {
            userInfo = UserProvider.getUser(token);
            UserProvider.setLocalLoginUser(userInfo);
            if(userInfo == null || StringUtil.isEmpty(userInfo.getUserId())){
                throwNoLogin();
            }
            if(configValueUtil.isMultiTenancy()){
                tenantVO = TenantDataSourceUtil.switchTenant(userInfo.getTenantId());
                if(tenantVO == null){
                    throwNoLogin();
                }
            }
        }else{
            //Net最新版
            String userId = jwt.getPayload("UserId").toString();
            String tenantId = jwt.getPayload("TenantId").toString();

            if(StringUtil.isEmpty(userId) || StringUtil.isEmpty(tenantId)){
                throwNoLogin();
            }
            tenantVO = new TenantVO();
            tenantVO.setAccountNum(0L);
            tenantVO.setEnCode(tenantId);
            if(configValueUtil.isMultiTenancy()){
                Map<String, Object> uinfoMap = getUserInfoNet(userId, tenantId);
                if(uinfoMap != null && !uinfoMap.isEmpty()){
                    tenantVO = getDbInfoNet(tenantId);
                }
                if(tenantVO == null){
                    throwNoLogin();
                }
            }else{
                tenantVO.setType(0);
            }
            userInfo = new UserInfo();
            userInfo.setId(userId);
            userInfo.setUserId(userId);
            userInfo.setTenantDbType(tenantVO.getType());
            userInfo.setTenantId(tenantId);
            userInfo.setTenantDbConnectionString(tenantVO.getDbName());

            UserProvider.setLocalLoginUser(userInfo);
            TenantHolder.setLocalTenantCache(tenantVO);
        }

    }


    private void throwNoLogin(){
        throw new BackResultException((ActionResultCode.SessionOverdue.getMessage()));
    }


    private boolean coress(){
        SaRequest request = SaHolder.getRequest();
        String origin = request.getHeader(HttpHeaders.ORIGIN);
        if(origin == null) {
            //origin = request.getHeader("referer");
        }
        SaHolder.getResponse()
                // 允许指定域访问跨域资源
                .setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin)
                // 允许的header参数
                .setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, ALL)
                // 允许所有请求方式
                .setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, ALL)
                .setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
                .setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, ALL)
                // 有效时间
                .setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);
        return request.getMethod().equalsIgnoreCase("options");
    }

    public Map<String, Object> getUserInfoNet(String userId, String tenantId){
        String userToken = String.format("%s:linzen:permission:user:%s", tenantId, userId);
        Object uinfoObj = redisUtil.getString(userToken);
        if(uinfoObj == null){
            return Collections.EMPTY_MAP;
        }
        Map<String, Object> uinfoMap = JsonUtil.stringToMap(JSONObject.toJSONString(uinfoObj));
        return uinfoMap;
    }

    public TenantVO getDbInfoNet(String tenantId){
        //Net3.5.0
        String tenantToken = "linzen:global:tenant";
        Object uinfoObj = redisUtil.getString(tenantToken);
        if(uinfoObj == null){
            return null;
        }
        TenantVO tenantVO = null;
        JSONArray tenantInfos = JSONArray.parseArray(JSONArray.toJSONString(uinfoObj));
        for (Object tenantInfo : tenantInfos) {
            JSONObject tenantJson = (JSONObject) tenantInfo;
            if(Objects.equals(tenantId, tenantJson.get("TenantId"))){
                JSONObject connectionConfig = tenantJson.getJSONObject("connectionConfig");
                boolean isCustome = connectionConfig.getBooleanValue("IsCustom");
                if(isCustome){
                    throw new ReportException("暂不支持指定数据源模式多租户");
                }
                tenantVO = new TenantVO();
                tenantVO.setEnCode(tenantId);
                JSONArray configList = connectionConfig.getJSONArray("ConfigList");
                for (int i = 0; i < configList.size(); i++) {
                    JSONObject config = configList.getJSONObject(i);
                    if(config.getBooleanValue("IsMaster")){
                        //return config.getString("ServiceName");
                        tenantVO.setDbName(config.getString("ServiceName"));
                    }

                }
            }
        }
        return tenantVO;
    }



}
