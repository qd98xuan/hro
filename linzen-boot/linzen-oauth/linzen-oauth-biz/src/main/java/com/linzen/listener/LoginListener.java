package com.linzen.listener;

import cn.dev33.satoken.listener.SaTokenListenerForSimple;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.linzen.base.UserInfo;
import com.linzen.message.service.MessageService;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.util.DateUtil;
import com.linzen.util.IpUtil;
import com.linzen.util.LoginHolder;
import com.linzen.util.UserProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author FHNP
 * @copyright 领致信息技术有限公司
 */
@Slf4j
@Component
public class LoginListener extends SaTokenListenerForSimple {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private MessageService messageApi;
    @Autowired
    private UserService userService;

    @Override
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginModel loginModel) {
        println("用户登录：{}, 设备：{}, TOKEN：{}", loginId, loginType, tokenValue);
        UserInfo userInfo = userProvider.get();
        //临时用户登录不记录
        if (!UserProvider.isTempUser(userInfo)) {

            SysUserEntity entity = LoginHolder.getUserEntity();
            entity.setLogErrorCount(0);
            entity.setUnlockTime(null);
            entity.setEnabledMark(1);
            entity.setPrevLogIp(IpUtil.getIpAddr());
            entity.setPrevLogTime(DateUtil.getNowDate());
            entity.setLastLogIp(IpUtil.getIpAddr());
            entity.setLastLogTime(DateUtil.getNowDate());
            entity.setLogSuccessCount(entity.getLogSuccessCount() != null ? entity.getLogSuccessCount() + 1 : 1);

            userService.updateById(entity);
        }
    }

    @Override
    public void doLogout(String loginType, Object loginId, String tokenValue) {
        println("用户退出：{}, 设备：{}, TOKEN：{}", loginId, loginType, tokenValue);
    }

    @Override
    public void doKickout(String loginType, Object loginId, String tokenValue) {
        println("用户踢出：{}, 设备：{}, TOKEN：{}", loginId, loginType, tokenValue);
        messageApi.logoutWebsocketByToken(tokenValue, null);
        // 删除用户信息缓存, 保留Token状态记录等待自动过期, 如果用户不在线下次打开浏览器会提示被踢下线
        StpUtil.getTokenSessionByToken(tokenValue).logout();
    }

    @Override
    public void doReplaced(String loginType, Object loginId, String tokenValue) {
        println("用户顶替：{}, 设备：{}, TOKEN：{}", loginId, loginType, tokenValue);
        messageApi.logoutWebsocketByToken(tokenValue, null);
        StpUtil.getTokenSessionByToken(tokenValue).logout();
    }

    /**
     * 打印指定字符串
     *
     * @param str 字符串
     */
    public void println(String str, Object... params) {
        if (log.isDebugEnabled()) {
            log.debug(str, params);
        }
    }
}
