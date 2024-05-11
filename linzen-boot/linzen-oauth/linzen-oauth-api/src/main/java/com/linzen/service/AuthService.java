package com.linzen.service;

import com.linzen.base.ServiceResult;
import com.linzen.exception.LoginException;
import com.linzen.model.LoginVO;

import java.util.Map;

public interface AuthService {
    ServiceResult<LoginVO> login(Map<String, String> parameters) throws LoginException;

    ServiceResult kickoutByToken(String... tokens);

    ServiceResult kickoutByUserId(String userId, String tenantId);
}
