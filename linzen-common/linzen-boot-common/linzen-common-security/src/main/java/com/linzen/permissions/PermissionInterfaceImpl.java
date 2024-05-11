package com.linzen.permissions;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.linzen.base.UserInfo;
import com.linzen.model.BaseSystemInfo;
import com.linzen.properties.SecurityProperties;
import com.linzen.util.TenantProvider;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.linzen.util.Constants.ADMIN_KEY;


/**
 * 权限认证接口实现
 *
 * @author FHNP
 * @copyright 领致信息技术有限公司
 */
@Component
public class PermissionInterfaceImpl implements StpInterface {

    public static final String PERMISSION_KEY = "user_permission";
    public static final String ROLE_KEY = "user_roles";

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        if(!securityProperties.isEnablePreAuth()){
            return Collections.emptyList();
        }
        UserInfo userInfo = userProvider.get();
        String account = userInfo.getIsAdministrator()?ADMIN_KEY:userInfo.getUserId();
        account = UserProvider.concatLoginId(account);
        SaSession saSession = StpUtil.getSessionByLoginId(account, false);
        if(saSession == null){
            return Collections.emptyList();
        }
        return saSession.get(PERMISSION_KEY, Collections.emptyList());
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if(!securityProperties.isEnablePreAuth()){
            return Collections.emptyList();
        }
        UserInfo userInfo = userProvider.get();
        String account = userInfo.getIsAdministrator()?ADMIN_KEY:userInfo.getUserId();
        account = UserProvider.concatLoginId(account);
        SaSession saSession = StpUtil.getSessionByLoginId(account, false);
        if(saSession == null){
            return Collections.emptyList();
        }
        return saSession.get(ROLE_KEY, Collections.emptyList());
    }

    public static void setAuthorityList(String userAccount, Set<String> authority, BaseSystemInfo baseSystemInfo){
        userAccount = UserProvider.concatLoginId(userAccount);
        try {
            TenantProvider.setBaseSystemInfo(baseSystemInfo);
            StpUtil.getSessionByLoginId(userAccount, true).set(PERMISSION_KEY, new ArrayList<>(authority));
        }finally {
            TenantProvider.clearBaseSystemIfo();
        }
    }

    public static void setRoleList(String userAccount, Set<String> role, BaseSystemInfo baseSystemInfo){
        userAccount = UserProvider.concatLoginId(userAccount);
        try {
            TenantProvider.setBaseSystemInfo(baseSystemInfo);
            StpUtil.getSessionByLoginId(userAccount, true).set(ROLE_KEY, new ArrayList<>(role));
        }finally {
            TenantProvider.clearBaseSystemIfo();
        }
    }

}
