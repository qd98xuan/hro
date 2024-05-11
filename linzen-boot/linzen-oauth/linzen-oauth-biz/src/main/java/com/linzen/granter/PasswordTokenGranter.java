package com.linzen.granter;

import cn.dev33.satoken.context.SaHolder;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.constant.MsgCode;
import com.linzen.consts.AuthConsts;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.enums.DeviceType;
import com.linzen.exception.LoginException;
import com.linzen.message.entity.UserDeviceEntity;
import com.linzen.message.service.UserDeviceService;
import com.linzen.model.*;
import com.linzen.model.tenant.TenantVO;
import com.linzen.permission.controller.SocialsUserController;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static com.linzen.granter.PasswordTokenGranter.GRANT_TYPE;
import static com.linzen.util.Constants.ADMIN_KEY;


/**
 * 账号密码认证
 *
 * @author FHNP
 * @user N
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Component(GRANT_TYPE)
public class PasswordTokenGranter extends AbstractTokenGranter {

    public static final String GRANT_TYPE = "password";
    public static final Integer ORDER = 1;
    private static final String URL_LOGIN = "";


    public PasswordTokenGranter() {
        super(URL_LOGIN);
    }

    public PasswordTokenGranter(String authenticationUrl) {
        super(authenticationUrl);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceBuilder userDetailsServiceBuilder;

    @Autowired
    private SocialsUserController socialsUserController;

    @Autowired
    private UserDeviceService userDeviceService;


    /**
     * 授权
     *
     * @param loginParameters
     * @return
     * @throws LoginException
     */
    @Override
    public ServiceResult<LoginVO> granter(Map<String, String> loginParameters) throws LoginException {
        LoginForm loginForm = BeanUtil.toBean(loginParameters, LoginForm.class);
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

    @Override
    public int getOrder() {
        return ORDER;
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
        userInfo.setUserId(userEntity.getId());
        userInfo.setUserName(userEntity.getRealName());
        UserProvider.setLocalLoginUser(userInfo);
        // 判断是否开启验证码
        if (Objects.nonNull(sysConfigInfo) && "1".equals(String.valueOf(sysConfigInfo.getEnableVerificationCode()))
                && !Objects.equals("code", loginForm.getOrigin())) {
            // 验证验证码
            String timestamp = String.valueOf(redisUtil.getString(loginForm.getTimestamp()));
            if (StringUtil.isEmpty(timestamp)) {
                throw new LoginException(MsgCode.LOG107.get());
            }
            if (!loginForm.getCode().equalsIgnoreCase(timestamp)) {
                throw new LoginException(MsgCode.LOG104.get());
            }
        }
        try {
            authenticate(loginForm, userEntity, sysConfigInfo);
        } catch (Exception e) {
            authenticateFailure(userEntity, sysConfigInfo);
            throw e;
        }
        LoginHolder.setUserEntity(userEntity);
    }

    protected void authenticate(LoginForm loginForm, SysUserEntity userEntity, BaseSystemInfo systemInfo) throws LoginException {
        authenticateLock(userEntity, systemInfo);
        authenticatePassword(loginForm, userEntity, systemInfo);
    }

    protected void authenticateLock(SysUserEntity userEntity, BaseSystemInfo systemInfo) throws LoginException {
        // 判断当前账号是否被锁定
        Integer lockMark = userEntity.getEnabledMark();
        if (Objects.nonNull(lockMark) && lockMark == 2) {
            // 获取解锁时间
            Date unlockTime = userEntity.getUnlockTime();
            // 账号锁定
            if (systemInfo.getLockType() == 1 || Objects.isNull(unlockTime)) {
                throw new LoginException(MsgCode.LOG012.get());
            }
            // 延迟登陆锁定
            long millis = System.currentTimeMillis();
            if (unlockTime.getTime() > millis) {
                // 转成分钟
                int time = (int) ((unlockTime.getTime() - millis) / (1000 * 60));
                throw new LoginException(MsgCode.LOG108.get().replace("{time}", Integer.toString(time + 1)));
            } else if (unlockTime.getTime() < millis && userEntity.getLogErrorCount() >= systemInfo.getPasswordErrorsNumber()){
                // 已经接触错误时间锁定的话就重置错误次数
                userEntity.setLogErrorCount(0);
                userEntity.setEnabledMark(1);
                userService.updateById(userEntity);
            }
        }
    }

    protected void authenticatePassword(LoginForm loginForm, SysUserEntity userEntity, BaseSystemInfo systemInfo) throws LoginException {
        String inputPwd = loginForm.getPassword();
        try{
            //前端md5后进行aes加密
            inputPwd = DesUtil.aesOrDecode(inputPwd, false, true);
        }catch (Exception e){
            log.error("登录密码解密失败:{}", inputPwd, e);
            inputPwd = "";
        }
        if (!userEntity.getPassword().equals(Md5Util.getStringMd5(inputPwd + userEntity.getSecretkey().toLowerCase()))) {
            throw new LoginException(MsgCode.LOG101.get());
        }
    }

    protected void authenticateFailure(SysUserEntity entity, BaseSystemInfo sysConfigInfo) {
        if (entity != null) {
            // 超级管理员特权，不会锁定
            if (!ADMIN_KEY.equals(entity.getAccount())) {
                // 判断是否需要锁定账号，哪种锁定方式
                // 大于2则判断有效
                Integer errorsNumber = sysConfigInfo.getPasswordErrorsNumber();
                // 判断是否开启
                if (errorsNumber != null && errorsNumber > 2) {
                    // 加入错误次数
                    Integer errorCount = entity.getLogErrorCount() != null ? entity.getLogErrorCount() + 1 : 1;
                    entity.setLogErrorCount(errorCount);
                    Integer lockType = sysConfigInfo.getLockType();
                    if (errorCount >= errorsNumber) {
                        entity.setEnabledMark(2);
                        // 如果是延时锁定
                        if (Objects.nonNull(lockType) && lockType == 2) {
                            Integer lockTime = sysConfigInfo.getLockTime();
                            Date date = new Date((System.currentTimeMillis() + (lockTime * 60 * 1000)));
                            entity.setUnlockTime(date);
                        }
                    }
                    if (lockType == 1) {
                        entity.setUnlockTime(null);
                    }
                    userService.updateById(entity);
                }
            }
        }
    }

    @Override
    protected void preLogin(UserInfo userInfo, BaseSystemInfo baseSystemInfo) throws LoginException {

    }

    @Override
    protected void loginSuccess(UserInfo userInfo, BaseSystemInfo baseSystemInfo) {
        super.loginSuccess(userInfo, baseSystemInfo);
        //登录成功绑定第三方
        if (SaHolder.getRequest().hasParam(AuthConsts.PARAMS_LINZEN_TICKET)) {
            String ticket = SaHolder.getRequest().getParam(AuthConsts.PARAMS_LINZEN_TICKET);
            LoginTicketModel ticketModel = TicketUtil.parseTicket(ticket);
            if (ticketModel != null) {
                SocialUnbindModel jsonToBean = JsonUtil.createJsonToBean(ticketModel.getValue(), SocialUnbindModel.class);
                if (jsonToBean != null) {
                    socialsUserController.loginAutoBinding(jsonToBean.getSocialType(), jsonToBean.getSocialUnionid(), jsonToBean.getSocialName(),
                            userInfo.getUserId(), userInfo.getTenantId());
                }
            }
        }
        if (SaHolder.getRequest().hasParam(AuthConsts.CLIENT_ID)) {
            String Client_Id = SaHolder.getRequest().getParam(AuthConsts.CLIENT_ID);
            if(StringUtil.isNotBlank(Client_Id) && !"null".equals(Client_Id)) {
                UserDeviceEntity userDeviceEntity = userDeviceService.getInfoByClientId(Client_Id);
                if (userDeviceEntity != null) {
                    userDeviceEntity.setUserId(userInfo.getUserId());
                    userDeviceEntity.setUpdateTime(DateUtil.getNowDate());
                    userDeviceEntity.setUpdateUserId(userInfo.getUserId());
                    userDeviceService.update(userDeviceEntity.getId(), userDeviceEntity);
                } else {
                    userDeviceEntity = new UserDeviceEntity();
                    userDeviceEntity.setId(RandomUtil.uuId());
                    userDeviceEntity.setUserId(userInfo.getUserId());
                    userDeviceEntity.setClientId(Client_Id);
                    userDeviceEntity.setCreatorTime(DateUtil.getNowDate());
                    userDeviceEntity.setCreatorUserId(userInfo.getUserId());
                    userDeviceService.create(userDeviceEntity);
                }
            }
        }
    }

    protected LoginVO getLoginVo(UserInfo userInfo) {
        LoginVO loginVO = new LoginVO();
        loginVO.setTheme(userInfo.getTheme());
        loginVO.setToken(userInfo.getToken());
        if(configValueUtil.isMultiTenancy()){
            //删除卫翎信息
            TenantVO tenantVO = TenantDataSourceUtil.getCacheTenantInfo(userInfo.getTenantId());
            if(tenantVO != null && tenantVO.getWl_qrcode() != null){
                loginVO.setWl_qrcode(tenantVO.getWl_qrcode());
                tenantVO.setWl_qrcode(null);
                TenantDataSourceUtil.setTenantInfo(tenantVO);
            }
        }
        return loginVO;
    }

    @Override
    public ServiceResult logout() {
        UserInfo userInfo = UserProvider.getUser();
        if (userInfo.getUserId() != null) {
            if ("1".equals(String.valueOf(loginService.getBaseSystemConfig(userInfo.getTenantId()).getSingleLogin()))) {
                UserProvider.logoutByUserId(userInfo.getUserId(), DeviceType.valueOf(userInfo.getLoginDevice()));
            } else {
                UserProvider.logoutByToken(userInfo.getToken());
            }
        }
        return ServiceResult.success("注销成功");
    }

    @Override
    protected String getGrantType() {
        return GRANT_TYPE;
    }

    @Override
    protected String getUserDetailKey() {
        return AuthConsts.USER_ACCOUNT;
    }
}
