package com.linzen.permission.util.socials;

import lombok.Data;
import me.zhyd.oauth.model.AuthCallback;

/**
 * 流程设计
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class AuthCallbackNew extends AuthCallback {
    private String authCode;
}
