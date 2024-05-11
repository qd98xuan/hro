package com.linzen.consts;


import cn.dev33.satoken.same.SaSameUtil;
import com.linzen.service.UserDetailService;

/**
 *
 * @author FHNP
 * @copyright 领致信息技术有限公司
 */
public class AuthConsts {

    public static final String DEFAULT_TENANT_ID = "";
    public static final String DEFAULT_TENANT_DATABASE = "";

    public static final String TENANT_SESSION = "tenant:";

    /**
     * 跨服务调用验证KEY
     */
    public static final String INNER_TOKEN_KEY = SaSameUtil.SAME_TOKEN;

    public static final String ACCOUNT_TYPE_DEFAULT = "login";
    public static final String ACCOUNT_TYPE_TENANT = "tenant";
    public static final String ACCOUNT_LOGIC_BEAN_DEFAULT = "defaultStpLogic";
    public static final String ACCOUNT_LOGIC_BEAN_TENANT = "tenantStpLogic";

    public static final String PAR_GRANT_TYPE = "grant_type";

    public static final String SYSTEM_INFO = "system_info";

    /**
     * 网关调用验证KEY
     */
    public static final String INNER_GATEWAY_TOKEN_KEY = INNER_TOKEN_KEY + "_GATEWAY";

    public static final String TOKEN_PREFIX = "bearer";
    public static final String TOKEN_PREFIX_SP = TOKEN_PREFIX + " ";

    public static final String PARAMS_LINZEN_TICKET = "linzen_ticket";
    public static final String PARAMS_SSO_LOGOUT_TICKET = "ticket";

    public static final Integer REDIRECT_PAGETYPE_LOGIN = 1;
    public static final Integer REDIRECT_PAGETYPE_LOGOUT = 2;

    public static final Integer TMP_TOKEN_UNLOGIN = -1;
    public static final Integer TMP_TOKEN_ERRLOGIN = -2;

    public static final String ONLINE_TICKET_KEY = "online_ticket:";
    public static final String ONLINE_TICKET_TOKEN = "online_token";

    public static final String JWT_SECRET = "WviMjFNC72VKwGqm5LPoheQo5XN9iN4d";

    /**
     * clientId
     */
    public static final String CLIENT_ID = "Client_Id";


    /**
     * 用户信息获取方式 account
     */
    public static final String USER_ACCOUNT = UserDetailService.USER_DETAIL_PREFIX + "UserAccount";
    
    /**
     * 用户信息获取方式 user_id
     */
    public static final String USERDETAIL_USER_ID = UserDetailService.USER_DETAIL_PREFIX + "UserId";

    /**
     * 认证方式 常规账号密码
     */
    public static final String GRANT_TYPE_PASSWORD = "password";
    
    /**
     * 认证方式 单点 CAS
     */
    public static final String GRANT_TYPE_CAS = "cas";
    
    /**
     * 认证方式 单点 OAUTH
     */
    public static final String GRANT_TYPE_OAUTH = "auth2";

}
