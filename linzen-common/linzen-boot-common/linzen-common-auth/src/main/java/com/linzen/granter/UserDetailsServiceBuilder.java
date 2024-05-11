package com.linzen.granter;

import com.linzen.service.UserDetailService;
import com.linzen.consts.AuthConsts;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author FHNP
 * @copyright 领致信息技术有限公司
 */
@Component
public class UserDetailsServiceBuilder {


    private final Map<String, UserDetailService> userDetailServices = new ConcurrentHashMap<>();

    public UserDetailsServiceBuilder(Map<String, UserDetailService> userDetailServices) {
        userDetailServices.forEach(this.userDetailServices::put);
    }


    /**
     * 根据类型获取合适的UserDetailService
     * @param detailType
     * @return
     */
    public UserDetailService getUserDetailService(String detailType){
        if(detailType == null){
            detailType = AuthConsts.USER_ACCOUNT;
        }
        return userDetailServices.get(detailType);
    }

}
