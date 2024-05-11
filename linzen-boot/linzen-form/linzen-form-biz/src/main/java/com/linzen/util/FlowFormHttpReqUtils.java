package com.linzen.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.linzen.base.ServiceResult;
import com.linzen.base.ServiceResultCode;
import com.linzen.config.OauthConfigration;
import com.linzen.entity.FlowFormEntity;
import com.linzen.exception.WorkFlowException;
import com.linzen.util.wxutil.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 流程表单 http请求处理表单
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Component
public class FlowFormHttpReqUtils {

    private static OauthConfigration oauthConfigration;

    @Autowired
    public void setOauthConfigration(OauthConfigration oauthConfigration) {
        FlowFormHttpReqUtils.oauthConfigration = oauthConfigration;
    }

    public Map<String, Object> info(FlowFormEntity flowFormEntity, String id, String token) {
        String requestURL = this.getReqURL(flowFormEntity, id);
        JSONObject jsonObject = HttpUtil.httpRequest(requestURL, "GET" , null, token);
        ServiceResult ServiceResult = JSON.toJavaObject(jsonObject, ServiceResult.class);
        if (ServiceResult == null) {
            return new HashMap<>();
        }
        Object data = ServiceResult.getData();
        return data != null ? JsonUtil.entityToMap(data) : new HashMap<>();
    }

    public boolean isUpdate(FlowFormEntity flowFormEntity, String id, String token) {
        String requestURL = this.getReqURL(flowFormEntity, id);
        JSONObject jsonObject = HttpUtil.httpRequest(requestURL, "GET" , null, token);
        ServiceResult ServiceResult = JSON.toJavaObject(jsonObject, ServiceResult.class);
        return ServiceResult != null && ServiceResult.getData() != null;
    }

    public void create(FlowFormEntity flowFormEntity, String id, String token, Map<String, Object> map) throws WorkFlowException {
        String requestURL = this.getReqURL(flowFormEntity, id);
        JSONObject jsonObject = HttpUtil.httpRequest(requestURL, "POST" , JsonUtil.createObjectToString(map), token);
        ServiceResult ServiceResult = JSON.toJavaObject(jsonObject, ServiceResult.class);
        boolean b = ServiceResult!=null && ServiceResultCode.Success.getCode().equals(ServiceResult.getCode());
        if (!b) {
            String msg = ServiceResult!=null?ServiceResult.getMsg():"未找到接口";
            throw new WorkFlowException(msg);
        }
    }

    public void update(FlowFormEntity flowFormEntity, String id, String token, Map<String, Object> map) throws WorkFlowException {
        String requestURL = this.getReqURL(flowFormEntity, id);
        JSONObject jsonObject = HttpUtil.httpRequest(requestURL, "PUT" , JsonUtil.createObjectToString(map), token);
        ServiceResult ServiceResult = JSON.toJavaObject(jsonObject, ServiceResult.class);
        boolean b = ServiceResult!=null && ServiceResultCode.Success.getCode().equals(ServiceResult.getCode());
        if (!b) {
            String msg = ServiceResult!=null?ServiceResult.getMsg():"未找到接口";
            throw new WorkFlowException(msg);
        }
    }

    public void saveOrUpdate(FlowFormEntity flowFormEntity, String id, String token, Map<String, Object> map) throws WorkFlowException {
        boolean update = this.isUpdate(flowFormEntity, id, token);
        if (update) {
            this.update(flowFormEntity, id, token, map);
        } else {
            this.create(flowFormEntity, id, token, map);
        }
    }


    private String getReqURL(FlowFormEntity flowFormEntity, String id) {
        HttpServletRequest request = ServletUtil.getRequest();
        //请求来源
        String requestURL = flowFormEntity.getInterfaceUrl();
        boolean isHttp = requestURL.toLowerCase().startsWith("http" );
        if (!isHttp) {
            //补全(内部)
            requestURL = oauthConfigration.getLinzenDomain() + requestURL;
        }
        return requestURL + "/" + id;
    }


}
