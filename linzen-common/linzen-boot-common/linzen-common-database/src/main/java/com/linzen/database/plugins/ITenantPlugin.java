package com.linzen.database.plugins;

import cn.dev33.satoken.context.SaHolder;
import com.linzen.database.model.tenant.NoTenantInfoModel;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;

import java.util.function.Consumer;

public interface ITenantPlugin {

    default void printNoTenant(Consumer<NoTenantInfoModel> logConsumer) {
        String token = null;
        String url = null;
        String stack = null;
        String userId = UserProvider.getUser().getTenantId();
        try {
            token = UserProvider.getToken();
            url = SaHolder.getRequest().getRequestPath();
            if (url == null) {
                stack = StringUtil.join(Thread.currentThread().getStackTrace(), "\n");
            }
        } catch (Exception e) {
        }
        logConsumer.accept(new NoTenantInfoModel(token, url, stack, userId));
    }

}

