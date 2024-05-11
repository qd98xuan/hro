package com.linzen.granter;

import com.linzen.base.ServiceResult;
import com.linzen.exception.LoginException;

import java.util.Map;


/**
 *
 * @author FHNP
 * @copyright 领致信息技术有限公司
 */
public interface TokenGranter {

    ServiceResult granter(Map<String, String> loginParameters) throws LoginException;


    ServiceResult logout();

    boolean requiresAuthentication();

}
