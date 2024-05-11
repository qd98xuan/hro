package com.linzen.granter;

import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.consts.AuthConsts;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.exception.LoginException;
import com.linzen.model.BaseSystemInfo;
import com.linzen.model.LoginForm;
import com.linzen.model.LoginVO;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.util.LoginHolder;
import com.linzen.util.UserProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.linzen.granter.LinzenOfficialTokenGranter.GRANT_TYPE;


/**
 * LINZEN官网专用短信认证
 *
 * @author FHNP
 * @user N
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Component(GRANT_TYPE)
public class LinzenOfficialTokenGranter extends PasswordTokenGranter {

    public static final String GRANT_TYPE = "official";

    @Autowired
    private UserDetailsServiceBuilder userDetailsServiceBuilder;


    @Override
    public ServiceResult granter(Map<String, String> loginParameters) throws LoginException {
        LoginForm loginForm = BeanUtil.toBean(loginParameters, LoginForm.class);
        //校验短信验证码
        TenantDataSourceUtil.checkOfficialSmsCode(loginForm.getAccount(), loginForm.getCode(), 1);

        UserInfo userInfo = UserProvider.getUser();
        //切换租户
        switchTenant(userInfo);
        //获取系统配置
        BaseSystemInfo baseSystemInfo = getSysconfig(userInfo);
        //预检信息
        preAuthenticate(loginForm, userInfo, baseSystemInfo);
        //登录账号
        super.loginAccount(userInfo, baseSystemInfo);
        //返回登录信息
        LoginVO loginResult = getLoginVo(userInfo);
        return ServiceResult.success(loginResult);
    }

    /**
     * 可重写实现邮箱、短信、TOTP验证
     *
     * @param loginForm
     * @param sysConfigInfo
     * @throws LoginException
     */
    protected void preAuthenticate(LoginForm loginForm, UserInfo userInfo, BaseSystemInfo sysConfigInfo) throws LoginException {
        //验证密码
        SysUserEntity userEntity = userDetailsServiceBuilder.getUserDetailService(AuthConsts.USER_ACCOUNT).loadUserEntity(userInfo);
        try {
            authenticateLock(userEntity, sysConfigInfo);
        } catch (Exception e) {
            authenticateFailure(userEntity, sysConfigInfo);
            throw e;
        }
        LoginHolder.setUserEntity(userEntity);
    }

    @Override
    protected String getGrantType() {
        return GRANT_TYPE;
    }
}
