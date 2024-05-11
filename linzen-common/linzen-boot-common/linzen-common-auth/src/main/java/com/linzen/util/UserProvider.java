package com.linzen.util;

import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.TokenSign;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.text.StrPool;
import com.linzen.base.UserInfo;
import com.linzen.enums.DeviceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.linzen.consts.AuthConsts.TOKEN_PREFIX_SP;


/**
 * 用户信息提供者
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Component
public class UserProvider {

    private static RedisUtil redisUtil;

    private static CacheKeyUtil cacheKeyUtil;

    public static final String USER_INFO_KEY = "USER_INFO_";

    private static final ThreadLocal<UserInfo> USER_CACHE = new ThreadLocal<>();

    /**
     * 构造函数构建
     *
     * @param redisUtil    RedisUtil
     * @param cacheKeyUtil CacheKeyUtil
     */
    public UserProvider(RedisUtil redisUtil, CacheKeyUtil cacheKeyUtil) {
        UserProvider.redisUtil = redisUtil;
        UserProvider.cacheKeyUtil = cacheKeyUtil;
    }

    /**
     * 登录系统 适用于Request环境
     *
     * @param userInfo 登录用户信息
     */
    public static void login(UserInfo userInfo) {
        setLocalLoginUser(userInfo);
        StpUtil.login(concatLoginId(userInfo.getUserId()));
        userInfo.setToken(StpUtil.getTokenValueNotCut());
        setLoginUser(userInfo);
    }

    /**
     * 登录系统 适用于Request环境
     *
     * @param userInfo   UserInfo 用户信息
     * @param loginModel SaLoginModel 登录参数
     */
    public static void login(UserInfo userInfo, SaLoginModel loginModel) {
        setLocalLoginUser(userInfo);
        StpUtil.login(concatLoginId(userInfo.getUserId()), loginModel);
        userInfo.setToken(StpUtil.getTokenValueNotCut());
        setLoginUser(userInfo);
    }

    /**
     * 适用于非Request环境
     *
     * @param userInfo   UserInfo 用户信息
     * @param loginModel SaLoginModel 登录参数
     */
    public static void loginNoRequest(UserInfo userInfo, SaLoginModel loginModel) {
        setLocalLoginUser(userInfo);
        String token = StpUtil.createLoginSession(concatLoginId(userInfo.getUserId()), loginModel);
        userInfo.setToken(TOKEN_PREFIX_SP + token);
        setLoginUser(userInfo);
    }

    /**
     * 获取指定TOKEN用户ID
     *
     * @param token String
     * @return String
     */
    public static String getLoginUserId(String token) {
        // saToken
        String loginId = (String) StpUtil.getLoginIdByToken(token);
        return parseLoginId(loginId);
    }

    /**
     * 获取当前用户ID, 包含临时切换用户ID
     *
     * @return String
     */
    public static String getLoginUserId() {
        String loginId = getUser().getUserId();
        return parseLoginId(loginId);
    }

    /**
     * 拼接租户下的用户ID 重载
     *
     * @param userId String
     * @return String
     */
    public static String concatLoginId(String userId) {
        return concatLoginId(userId, null);
    }

    /**
     * 拼接租户下的用户ID
     *
     * @param userId   String
     * @param tenantId String
     * @return String
     */
    private static String concatLoginId(String userId, String tenantId) {
        if (StringUtil.isEmpty(tenantId)) {
            tenantId = TenantHolder.getDatasourceId();
        }
        if (!StringUtil.isEmpty(tenantId)) {
            return tenantId + StrPool.COLON + userId;
        }
        return userId;
    }

    /**
     * 解析租户下的登录ID
     *
     * @param loginId String
     * @return String
     */
    private static String parseLoginId(String loginId) {
        if (loginId != null && loginId.contains(StrPool.COLON)) {
            loginId = loginId.substring(loginId.indexOf(StrPool.COLON) + 1);
        }
        return loginId;
    }

    /**
     * Token是否有效
     *
     * @param token String
     * @return Boolean
     */
    public static Boolean isValidToken(String token) {
        UserInfo userInfo = getUser(token);
        return userInfo.getUserId() != null;
    }

    /**
     * 设置Redis用户数据
     */
    public static void setLoginUser(UserInfo userInfo) {
        StpUtil.getTokenSessionByToken(cutToken(userInfo.getToken())).set(USER_INFO_KEY, userInfo);
    }

    /**
     * 设置本地用户数据
     */
    public static void setLocalLoginUser(UserInfo userInfo) {
        USER_CACHE.set(userInfo);
    }

    /**
     * 获取本地用户数据
     */
    public static UserInfo getLocalLoginUser() {
        return USER_CACHE.get();
    }

    /**
     * 清空本地用户数据
     */
    public static void clearLocalUser() {
        USER_CACHE.remove();
    }


    /**
     * 获取用户缓存
     * 保留旧方法
     *
     * @param token String
     * @return UserInfo
     */
    public UserInfo get(String token) {
        return UserProvider.getUser(token);
    }

    /**
     * 获取用户缓存
     *
     * @return UserInfo
     */
    public UserInfo get() {
        return UserProvider.getUser();
    }


    /**
     * 根据用户ID, 租户ID获取随机获取一个UserInfo
     *
     * @param userId   String
     * @param tenantId String
     * @return UserInfo
     */
    public static UserInfo getUser(String userId, String tenantId) {
        return getUser(userId, tenantId, null, null);
    }

    /**
     * 根据用户ID, 租户ID, 设备类型获取随机获取一个UserInfo
     *
     * @param userId        String
     * @param tenantId      String
     * @param includeDevice List 指定的设备类型中查找
     * @param excludeDevice List 排除指定设备类型
     * @return UserInfo
     */
    public static UserInfo getUser(String userId, String tenantId, List<String> includeDevice, List<String> excludeDevice) {
        SaSession session = StpUtil.getSessionByLoginId(concatLoginId(userId, tenantId), false);
        if (session != null) {
            List<TokenSign> tokenSignList = session.tokenSignListCopy();
            if (!tokenSignList.isEmpty()) {
                tokenSignList = tokenSignList.stream().filter(tokenSign -> {
                    if (!ObjectUtils.isEmpty(excludeDevice)) {
                        if (excludeDevice.contains(tokenSign.getDevice())) {
                            return false;
                        }
                    }
                    if (!ObjectUtils.isEmpty(includeDevice)) {
                        return includeDevice.contains(tokenSign.getDevice());
                    }
                    return true;
                }).collect(Collectors.toList());
                if (!tokenSignList.isEmpty()) {
                    return getUser(tokenSignList.get(0).getValue());
                }
            }
        }
        return new UserInfo();
    }

    /**
     * 获取用户缓存
     *
     * @param token
     * @return
     */
    public static UserInfo getUser(String token) {
        UserInfo userInfo = null;
        String tokens = null;
        if (token != null) {
            tokens = cutToken(token);
        } else {
            try {
                //处理非Web环境报错
                tokens = StpUtil.getTokenValue();
            } catch (Exception ignored) {

            }
        }
        if (tokens != null) {
            if (StpUtil.getLoginIdByToken(tokens) != null) {
                userInfo = (UserInfo) StpUtil.getTokenSessionByToken(tokens).get(USER_INFO_KEY);
            }
        }
        if (userInfo == null) {
            userInfo = new UserInfo();
        }
        return userInfo;
    }

    /**
     * 获取用户缓存
     *
     * @return
     */
    public static UserInfo getUser() {
        UserInfo userInfo = USER_CACHE.get();
        if (userInfo != null) {
            return userInfo;
        }
        userInfo = UserProvider.getUser(null);
        if (userInfo.getUserId() != null) {
            USER_CACHE.set(userInfo);
        }
        return userInfo;
    }

    /**
     * 去除Token前缀
     *
     * @param token String
     * @return String
     */
    public static String cutToken(String token) {
        if (token != null && token.startsWith(TOKEN_PREFIX_SP)) {
            return token.substring(TOKEN_PREFIX_SP.length());
        }
        return token;
    }

    /**
     * 获取token
     */
    public static String getToken() {
        return getAuthorize();
    }


    /**
     * 获取Authorize
     */
    public static String getAuthorize() {
        return ServletUtil.getHeader(Constants.AUTHORIZATION);
    }


    /**
     * TOKEN续期
     */
    public static void renewTimeout() {
        if (StpUtil.getTokenValue() != null) {
            UserInfo userInfo = UserProvider.getUser();
            if (userInfo.getUserId() == null || userInfo.getTokenTimeout() == null) {
                return;
            }
            StpUtil.renewTimeout(userInfo.getTokenTimeout() * 60L);
            SaSession saSession = StpUtil.getSessionByLoginId(concatLoginId(userInfo.getUserId()), false);
            if (saSession != null) {
                saSession.updateTimeout(userInfo.getTokenTimeout() * 60L);
            }
        }
    }

    /**
     * 获取所有Token记录
     * 包含无效状态的用户、临时用户
     *
     * @return List
     */
    public static List<String> getLoginUserListToken() {
        List<String> searchTokenValue = StpUtil.searchTokenValue("", -1, -1, true);
        return searchTokenValue.stream().map(token -> token.replace(StpUtil.stpLogic.splicingKeyTokenValue(""), "")).collect(Collectors.toList());
    }

    /**
     * 获取内部服务传递验证TOKEN
     *
     * @return String
     */
    public static String getInnerAuthToken() {
        return SaSameUtil.getToken();
    }

    /**
     * 验证内部传递Token是否有效 抛出异常
     *
     * @param token
     */
    public static void checkInnerToken(String token) {
        SaSameUtil.checkToken(token);
    }

    /**
     * 验证内部传递Token是否有效
     *
     * @param token
     */
    public static boolean isValidInnerToken(String token) {
        return SaSameUtil.isValid(token);
    }

    /**
     * 根据用户ID踢出全部用户
     *
     * @param userId
     */
    public static void kickoutByUserId(String userId, String tenantId) {
        StpUtil.kickout(UserProvider.concatLoginId(userId, tenantId));
    }

    /**
     * 根据Token踢出指定会话
     *
     * @param tokens
     */
    public static void kickoutByToken(String... tokens) {
        for (String token : tokens) {
            StpUtil.kickoutByTokenValue(token);
        }
    }

    /**
     * 退出当前Token, 不清除用户其他系统缓存
     */
    public static void logout() {
        StpUtil.logout();

    }

    /**
     * 退出指定Token, 不清除用户其他系统缓存
     *
     * @param token
     */
    public static void logoutByToken(String token) {
        if (token == null) {
            UserProvider.logout();
        } else {
            StpUtil.logoutByTokenValue(UserProvider.cutToken(token));
        }
    }

    /**
     * 退出指定设备类型的用户的全部登录信息, 不清除用户其他系统缓存
     *
     * @param userId
     * @param deviceType
     */
    public static void logoutByUserId(String userId, DeviceType deviceType) {
        StpUtil.logout(UserProvider.concatLoginId(userId), deviceType.getDevice());
    }

    /**
     * 退出指定用户的全部登录信息, 清除相关缓存
     *
     * @param userId
     */
    public static void logoutByUserId(String userId) {
        StpUtil.logout(UserProvider.concatLoginId(userId));
        UserProvider.removeOtherCache(userId);

    }

    /**
     * 获取当前用户拥有的权限列表(菜单编码列表、功能ID列表)
     *
     * @return
     */
    public static List<String> getPermissionList() {
        return StpUtil.getPermissionList();
    }

    /**
     * 获取当前用户拥有的角色列表
     *
     * @return
     */
    public static List<String> getRoleList() {
        return StpUtil.getRoleList();
    }

    /**
     * 移除
     */
    public static void removeOtherCache(String userId) {
        redisUtil.remove(cacheKeyUtil.getUserAuthorize() + userId);
        redisUtil.remove(cacheKeyUtil.getSystemInfo());
    }

    /**
     * 是否在线
     */
    public boolean isOnLine(String userId) {
        return StpUtil.getTokenValueByLoginId(UserProvider.concatLoginId(userId), UserProvider.getDeviceForAgent().getDevice()) != null;
    }


    /**
     * 是否登陆
     */
    public static boolean isLogined() {
        return StpUtil.isLogin();
    }

    /**
     * 指定Token是否有效
     *
     * @param token
     * @return
     */
    public static boolean isValid(String token) {
        return StpUtil.getLoginIdByToken(token) != null;
    }


    public static DeviceType getDeviceForAgent() {
        if (ServletUtil.getIsMobileDevice()) {
            return DeviceType.APP;
        } else {
            return DeviceType.PC;
        }
    }

    /**
     * 判断用户是否是临时用户
     *
     * @param userInfo UserInfo
     * @return boolean
     */
    public static boolean isTempUser(UserInfo userInfo) {
        if (userInfo == null) {
            userInfo = UserProvider.getUser();
        }
        String loginDevice = userInfo.getLoginDevice();
        return DeviceType.TEMPORALITIES.getDevice().equals(loginDevice) || DeviceType.TEMPUSER.getDevice().equals(loginDevice);
    }


}
