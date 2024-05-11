package com.linzen.permission.util.socials;

import me.zhyd.oauth.config.AuthSource;
import me.zhyd.oauth.request.AuthDefaultRequest;
import me.zhyd.oauth.request.AuthDingTalkRequest;

/**
 * 流程设计
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public enum AuthDefaultSourceNew implements AuthSource {
    DINGTALK_NEW {
        public String authorize() {
            return "https://login.dingtalk.com/oauth2/auth";
//            return "https://oapi.dingtalk.com/connect/sns_authorize";
        }

        public String accessToken() {
            return "https://api.dingtalk.com/v1.0/oauth2/userAccessToken";
//            return "https://oapi.dingtalk.com/gettoken";

        }

        public String userInfo() {
            return "https://api.dingtalk.com/v1.0/contact/users/me";
//            return "https://oapi.dingtalk.com/user/getuserinfo";
        }

        public Class<? extends AuthDefaultRequest> getTargetClass() {
            return AuthDingTalkRequest.class;
        }
    },
    WECHAT_APPLETS {
        @Override
        public String authorize() {
            return null;
        }

        @Override
        public String accessToken() {
            return null;
        }

        public String userInfo() {
            return "https://api.weixin.qq.com/sns/jscode2session";
        }
        public Class<? extends AuthDefaultRequest> getTargetClass() {
            return AuthWechatAppletsRequest.class;
        }
    },;
    private AuthDefaultSourceNew() {
    }
}
