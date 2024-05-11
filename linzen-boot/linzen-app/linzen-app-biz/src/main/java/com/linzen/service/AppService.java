package com.linzen.service;

import com.linzen.model.AppUserInfoVO;
import com.linzen.model.AppUsersVO;

/**
 * app用户信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface AppService {

    /**
     * app用户信息
     * @return
     */
    AppUsersVO userInfo();

    /**
     * 通讯录
     * @return
     */
    AppUserInfoVO getInfo(String id);

}
