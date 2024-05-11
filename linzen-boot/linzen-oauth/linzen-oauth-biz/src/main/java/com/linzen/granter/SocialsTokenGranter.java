package com.linzen.granter;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.consts.AuthConsts;
import com.linzen.enums.LoginTicketStatus;
import com.linzen.exception.LoginException;
import com.linzen.model.BaseSystemInfo;
import com.linzen.model.LoginTicketModel;
import com.linzen.model.LoginVO;
import com.linzen.model.SocialUnbindModel;
import com.linzen.permission.controller.SocialsUserController;
import com.linzen.permission.model.socails.SocialsUserInfo;
import com.linzen.util.ServletUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.TicketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

import static com.linzen.granter.SocialsTokenGranter.GRANT_TYPE;


@Slf4j
@Component(GRANT_TYPE)
public class SocialsTokenGranter extends AbstractTokenGranter {


    public static final String GRANT_TYPE = "socials";
    public static final Integer ORDER = 5;
    private static final String URL_LOGIN = "/Login/socials/**";

    @Autowired
    private SocialsUserController socialsUserController;

    public SocialsTokenGranter() {
        super(URL_LOGIN);
    }

    protected String getGrantType() {
        return GRANT_TYPE;
    }

    public ServiceResult granter(Map<String, String> map) throws LoginException {
        SaRequest req = SaHolder.getRequest();
        String code = req.getParam("code");
        String state = req.getParam("state");
        String source = req.getParam("source");
        String uuid = req.getParam("uuid");
        if (StringUtil.isEmpty(code)) {
            code = req.getParam("authCode") != null ? req.getParam("authCode") : req.getParam("auth_code");
        }
        //是否是微信qq唤醒或者小程序登录
        if (StringUtil.isNotEmpty(uuid)) {
            try {
                return loginByCode(source, code, null, uuid, null);
            } catch (Exception e) {
                //更新登录结果
                outError(e.getMessage());
            }
        }


        if (StringUtil.isEmpty(req.getParam(AuthConsts.PARAMS_LINZEN_TICKET))) {
            //租户列表登陆标识
            boolean tenantLogin = StringUtil.isEmpty(req.getParam("tenantLogin")) ? false : req.getParam("tenantLogin").equals("true") ? true : false;
            //租户列表点击登录调用
            if (!tenantLogin) {
                //绑定
                socialsBinding(req, code, state, source);
                return null;
            } else {//租户列表点击登录
                LoginVO loginVO = tenantLogin(req);
                return ServiceResult.success("登录成功！", loginVO);
            }
        } else {
            //票据登陆
            if (!isValidLinzenTicket()) {
                outError("登录票据已失效");
                return null;
            }
            //接受CODE 进行登录
            if (SaFoxUtil.isNotEmpty(code)) {
                try {
                    String socialName = req.getParam("socialName");
                    ServiceResult ServiceResult = loginByCode(source, code, state, null, socialName);
                    if(400==ServiceResult.getCode()||"wechat_applets".equals(req.getParam("source"))){
                        return ServiceResult;
                    }
                    return null;
                } catch (Exception e) {
                    //更新登录结果
                    outError(e.getMessage());
                }
                return null;
            }
            return null;
        }

    }

    /**
     * 租户列表登录
     *
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    private LoginVO tenantLogin(SaRequest req) throws LoginException {
        String userId = req.getParam("userId");
        String account = req.getParam("account");
        String tenantId = req.getParam("tenantId");
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setTenantId(tenantId);
        userInfo.setUserAccount(tenantId + "@" + account);
        //切换租户
        switchTenant(userInfo);
        //获取系统配置
        BaseSystemInfo baseSystemInfo = getSysconfig(userInfo);
        //登录账号
        super.loginAccount(userInfo, baseSystemInfo);
        //返回登录信息
        LoginVO loginVo = getLoginVo(userInfo);
        return loginVo;
    }

    /**
     * 第三方绑定
     */
    private void socialsBinding(SaRequest req, String code, String state, String source) {
        String userId = req.getParam("userId");
        String tenantId = req.getParam("tenantId");
        PrintWriter out = null;
        try {
            HttpServletResponse response = ServletUtil.getResponse();
            response.setCharacterEncoding("utf-8");
            response.setHeader("content-type", "text/html;charset=utf-8");
            out = response.getWriter();
            JSONObject binding = socialsUserController.binding(source, userId, tenantId, code, state);
            out.print(
                    "<script>\n" +
                            "window.opener.postMessage(\'" + binding.toJSONString() + "\', '*');\n" +
                            "window.open('','_self','');\n" +
                            "window.close();\n" + "</script>");
            out.close();
        } catch (Exception e) {

        }
    }

    public ServiceResult logout() {
        return super.logout();
    }

    public int getOrder() {
        return ORDER;
    }

    @Override
    protected void loginSuccess(UserInfo userInfo, BaseSystemInfo baseSystemInfo) {
    }

    @Override
    protected void loginFailure(UserInfo userInfo, BaseSystemInfo baseSystemInfo, Exception e) {
        super.loginFailure(userInfo, baseSystemInfo, e);
    }


    protected void outError(String message) {
        updateTicketError(message);
    }

    @Override
    protected String getUserDetailKey() {
        return AuthConsts.USERDETAIL_USER_ID;
    }

    protected LoginTicketModel updateTicketUnbind(String socialType, String socialUnionid, String socialName) {
        LoginTicketModel loginTicketModel = null;
        SocialUnbindModel obj = new SocialUnbindModel(socialType, socialUnionid, socialName);
        String ticket = this.getLinzenTicket();
        if (!ticket.isEmpty()) {
            loginTicketModel = (new LoginTicketModel()).setStatus(LoginTicketStatus.UnBind.getStatus()).setValue(JSONObject.toJSONString(obj));
            TicketUtil.updateTicket(ticket, loginTicketModel, (Long) 300L);
        }
        return loginTicketModel;
    }

    protected LoginTicketModel updateTicketMultitenancy(JSONArray jsonArray) {
        LoginTicketModel loginTicketModel = null;
        String ticket = this.getLinzenTicket();
        if (!ticket.isEmpty()) {
            loginTicketModel = (new LoginTicketModel()).setStatus(LoginTicketStatus.Multitenancy.getStatus()).setValue(jsonArray.toJSONString());
            TicketUtil.updateTicket(ticket, loginTicketModel, (Long) null);
        }
        return loginTicketModel;
    }


    protected LoginTicketModel updateTicketSuccessReturn(UserInfo userInfo) {
        LoginTicketModel loginTicketModel = null;
        String ticket = getLinzenTicket();
        if (!ticket.isEmpty()) {
            loginTicketModel = new LoginTicketModel()
                    .setStatus(LoginTicketStatus.Success.getStatus())
                    .setValue(StpUtil.getTokenValueNotCut())
                    .setTheme(userInfo.getTheme());
            TicketUtil.updateTicket(ticket, loginTicketModel, null);
        }
        return loginTicketModel;
    }

    protected LoginVO getLoginVo(UserInfo userInfo) {
        LoginVO loginVO = new LoginVO();
        loginVO.setTheme(userInfo.getTheme());
        loginVO.setToken(userInfo.getToken());
        return loginVO;
    }

    /**
     * 小程序登录微信授权
     * app微信，qq唤醒
     *
     * @param code
     * @throws LoginException
     */
    protected ServiceResult loginByCode(String source, String code, String state, String uuid, String socialName) throws LoginException {
        log.debug("Auth2 Code: {}", code);
        SocialsUserInfo socialsUserInfo = null;
        if (StringUtil.isNotEmpty(code)) {
            socialsUserInfo = socialsUserController.getSocialsUserInfo(source, code, state);
        } else if (StringUtil.isNotEmpty(uuid)) {//微信和qq唤醒
            socialsUserInfo = socialsUserController.getUserInfo(source, uuid, state);
            if (StringUtil.isEmpty(socialsUserInfo.getSocialName()) && StringUtil.isNotEmpty(socialName)) {
                socialsUserInfo.setSocialName(socialName);//小程序名称前端传递
            }
        }
        if (configValueUtil.isMultiTenancy()) {
            if (socialsUserInfo == null || CollectionUtil.isEmpty(socialsUserInfo.getTenantUserInfo())) {
                updateTicketUnbind(source, socialsUserInfo.getSocialUnionid(), socialsUserInfo.getSocialName());//第三方未绑定账号!
                return ServiceResult.error("第三方未绑定账号!");
            }
            if (socialsUserInfo.getTenantUserInfo().size() == 1) {
                UserInfo userInfo = socialsUserInfo.getUserInfo();
                //切换租户
                switchTenant(userInfo);
                //获取系统配置
                BaseSystemInfo baseSystemInfo = getSysconfig(userInfo);
                //登录账号
                super.loginAccount(userInfo, baseSystemInfo);
                //返回登录信息
                LoginTicketModel loginTicketModel = updateTicketSuccessReturn(userInfo);
                return ServiceResult.success(loginTicketModel);
            } else {
                JSONArray tenantUserInfo = socialsUserInfo.getTenantUserInfo();
                LoginTicketModel loginTicketModel = updateTicketMultitenancy(tenantUserInfo);
                return ServiceResult.success(loginTicketModel);
            }
        } else {
            if (socialsUserInfo == null || socialsUserInfo.getUserInfo() == null) {
                updateTicketUnbind(source, socialsUserInfo.getSocialUnionid(), socialsUserInfo.getSocialName());//第三方未绑定账号!
                return ServiceResult.error("第三方未绑定账号!");
            }
            UserInfo userInfo = socialsUserInfo.getUserInfo();
            //切换租户
            switchTenant(userInfo);
            //获取系统配置
            BaseSystemInfo baseSystemInfo = getSysconfig(userInfo);
            //登录账号
            super.loginAccount(userInfo, baseSystemInfo);
            LoginTicketModel loginTicketModel = updateTicketSuccessReturn(userInfo);
            return ServiceResult.success(loginTicketModel);
        }
    }
}
