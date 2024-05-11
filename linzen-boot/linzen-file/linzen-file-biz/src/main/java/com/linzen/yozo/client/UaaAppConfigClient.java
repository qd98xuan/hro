package com.linzen.yozo.client;

import com.linzen.yozo.constants.EnumResultCode;
import com.linzen.yozo.utils.DefaultResult;
import com.linzen.yozo.utils.IResult;

import java.util.Map;

public class UaaAppConfigClient {
    public UaaAppConfigClient() {
    }

    public IResult<String> generateSign(String appId, String secret, Map<String, String[]> params) {
        UaaAppAuthenticator authenticator = new UaaAppAuthenticator("sign", (String)null, "appId");

        try {
            String[] appIds = (String[])params.get("appId");
            if (appIds == null || appIds.length != 1 || appIds[0] == null || "".equals(appIds[0])) {
                params.put("appId", new String[]{appId});
            }

            String sign = authenticator.generateSign(secret, params);
            return DefaultResult.successResult(sign);
        } catch (Exception var7) {
            return DefaultResult.failResult(EnumResultCode.E_GENERATE_SIGN_FAIL.getInfo());
        }
    }
}
