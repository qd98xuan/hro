package com.linzen.granter;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.config.ConfigValueUtil;
import com.linzen.exception.LoginException;
import com.linzen.model.BaseSystemInfo;
import com.linzen.model.tenant.TenantVO;
import com.linzen.service.LoginService;
import com.linzen.util.RedisUtil;
import com.linzen.consts.AuthConsts;
import com.linzen.enums.DeviceType;
import com.linzen.enums.LoginTicketStatus;
import com.linzen.model.LoginTicketModel;
import com.linzen.util.TenantProvider;
import com.linzen.util.TicketUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.HashMap;
import java.util.Map;

import static com.linzen.consts.AuthConsts.DEFAULT_TENANT_DATABASE;
import static com.linzen.consts.AuthConsts.DEFAULT_TENANT_ID;


/**
 * @author FHNP
 * @copyright 领致信息技术有限公司
 */
public abstract class AbstractTokenGranter implements TokenGranter, Ordered {


    @Autowired(required = false)
    protected LoginService loginService;

    @Autowired
    protected UserProvider userProvider;

    @Autowired
    protected ConfigValueUtil configValueUtil;

    @Autowired
    protected RedisUtil redisUtil;

    protected static PathMatcher pathMatcher = new AntPathMatcher();

    private String authenticationUrl;


    public AbstractTokenGranter(String authenticationUrl) {
        this.authenticationUrl = authenticationUrl;
    }


    /**
     * 最终登录用户
     *
     * @param userInfo 包含账户名, 登录方式
     * @return
     */
    protected String loginAccount(UserInfo userInfo, BaseSystemInfo baseSystemInfo) throws LoginException {
        try {
            //获取用户实现类接口名称
            userInfo.setUserDetailKey(getUserDetailKey());
            //获取登录信息
            userInfo = getUserInfo(userInfo, baseSystemInfo);
            //预登陆
            preLogin(userInfo, baseSystemInfo);
            //登录
            login(userInfo, baseSystemInfo);
        } catch (Exception e) {
            loginFailure(userInfo, baseSystemInfo, e);
            throw e;
        }
        loginSuccess(userInfo, baseSystemInfo);
        //返回token信息
        return userInfo.getToken();
    }

    /**
     * 切换多租户
     *
     * @param userInfo
     * @return userAccount, tenantId, tenandDb
     * @throws LoginException
     */
    protected UserInfo switchTenant(UserInfo userInfo) throws LoginException {
        if (configValueUtil.isMultiTenancy()) {
            userInfo = loginService.getTenantAccount(userInfo);
            return userInfo;

        }
        userInfo.setTenantId(DEFAULT_TENANT_ID);
        userInfo.setTenantDbConnectionString(DEFAULT_TENANT_DATABASE);
        userInfo.setTenantDbType(TenantVO.NONE);
        return userInfo;
    }

    /**
     * 获取系统配置
     *
     * @param userInfo
     * @return
     */
    protected BaseSystemInfo getSysconfig(UserInfo userInfo) throws LoginException {
        BaseSystemInfo baseSystemInfo = loginService.getBaseSystemConfig(userInfo.getTenantId());
        if (baseSystemInfo != null && baseSystemInfo.getSingleLogin() != null) {
            TenantProvider.setBaseSystemInfo(baseSystemInfo);
        } else {
            if (configValueUtil.isMultiTenancy() && configValueUtil.getMultiTenancyUrl().contains("https")) {
                throw new LoginException("租户登录失败，请用手机验证码登录");
            } else {
                throw new LoginException("数据库异常，请联系管理员处理");
            }
        }
        return baseSystemInfo;
    }

    /**
     * 获取登录设备
     *
     * @return
     */
    protected DeviceType getDeviceType() {
        return UserProvider.getDeviceForAgent();
    }


    /**
     * 生成登录用户信息
     *
     * @param userInfo
     * @return
     */
    protected UserInfo getUserInfo(UserInfo userInfo, BaseSystemInfo sysConfigInfo) throws LoginException {
        userInfo.setGrantType(getGrantType());
        userInfo = loginService.userInfo(userInfo, sysConfigInfo);
        return userInfo;
    }


    /**
     * 登录前执行
     *
     * @param userInfo
     * @param baseSystemInfo
     */
    protected void preLogin(UserInfo userInfo, BaseSystemInfo baseSystemInfo) throws LoginException {

    }

    /**
     * 登录操作
     *
     * @param userInfo
     * @param baseSystemInfo
     */
    protected void login(UserInfo userInfo, BaseSystemInfo baseSystemInfo) throws LoginException {
        UserProvider.login(userInfo, getLoginModel(userInfo, baseSystemInfo));
    }

    /**
     * 登录成功触发
     *
     * @param userInfo
     * @param baseSystemInfo
     */
    protected void loginSuccess(UserInfo userInfo, BaseSystemInfo baseSystemInfo) {

    }

    /**
     * 登录失败触发
     *
     * @param baseSystemInfo
     */
    protected void loginFailure(UserInfo userInfo, BaseSystemInfo baseSystemInfo, Exception e) {

    }

    protected abstract String getUserDetailKey();

    protected String createToken(UserInfo userInfo, BaseSystemInfo baseSystemInfo) {
        //登录
        UserProvider.login(userInfo, getLoginModel(userInfo, baseSystemInfo));
        return StpUtil.getTokenValueNotCut();
    }

    /**
     * 更新轮询结果为成功
     */
    protected void updateTicketSuccess(UserInfo userInfo) {
        String ticket = getLinzenTicket();
        if (!ticket.isEmpty()) {
            LoginTicketModel loginTicketModel = new LoginTicketModel().setStatus(LoginTicketStatus.Success.getStatus()).setValue(StpUtil.getTokenValueNotCut()).setTheme(userInfo.getTheme());
            TicketUtil.updateTicket(ticket, loginTicketModel, null);
        }
    }

    /**
     * 更新轮询结果为失败
     */
    protected void updateTicketError(String msg) {
        String ticket = getLinzenTicket();
        if (!ticket.isEmpty()) {
            LoginTicketModel loginTicketModel = new LoginTicketModel().setStatus(LoginTicketStatus.ErrLogin.getStatus()).setValue(msg);
            TicketUtil.updateTicket(ticket, loginTicketModel, null);
        }
    }


    /**
     * 获取轮询ticket
     *
     * @return
     */
    protected String getLinzenTicket() {
        return SaHolder.getRequest().getParam(AuthConsts.PARAMS_LINZEN_TICKET, "");
    }

    protected boolean isValidLinzenTicket() {
        String linzenTicket = getLinzenTicket();
        if (!linzenTicket.isEmpty()) {
            LoginTicketModel loginTicketModel = TicketUtil.parseTicket(linzenTicket);
            if (loginTicketModel == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取登录参数
     *
     * @param userInfo
     * @param baseSystemInfo
     * @return
     */
    protected SaLoginModel getLoginModel(UserInfo userInfo, BaseSystemInfo baseSystemInfo) {
        SaLoginModel loginModel = new SaLoginModel();
        loginModel.setTimeout(userInfo.getTokenTimeout() * 60L);
        loginModel.setExtraData(getTokenExtraData(userInfo, baseSystemInfo));
        if (userInfo.getLoginDevice() == null) {
            loginModel.setDevice(getDeviceType().getDevice());
            userInfo.setLoginDevice(loginModel.device);
        } else {
            loginModel.setDevice(userInfo.getLoginDevice());
        }
        return loginModel;
    }

    /**
     * 获取额外的JWT内容
     *
     * @param userInfo
     * @param baseSystemInfo
     * @return
     */
    protected Map<String, Object> getTokenExtraData(UserInfo userInfo, BaseSystemInfo baseSystemInfo) {
        Map<String, Object> tokenInfo = new HashMap<>();
//        tokenInfo.put("token", StpUtil.getTokenValue());
        tokenInfo.put("singleLogin", baseSystemInfo == null ? null : baseSystemInfo.getSingleLogin());
        tokenInfo.put("user_name", userInfo.getUserAccount());
        tokenInfo.put("user_id", userInfo.getUserId());
        tokenInfo.put("exp", userInfo.getOverdueTime().getTime());
        tokenInfo.put("token", userInfo.getId());
        return tokenInfo;
    }

    @Override
    public ServiceResult logout() {
        UserProvider.logout();
        return ServiceResult.success();
    }

    protected abstract String getGrantType();


    @Override
    public boolean requiresAuthentication() {
        String path = SaHolder.getRequest().getRequestPath();
        if (path != null && path.startsWith("/api/oauth")) {
            path = path.replace("/api/oauth", "");
        }
        return pathMatcher.match(authenticationUrl, path);
    }
}
