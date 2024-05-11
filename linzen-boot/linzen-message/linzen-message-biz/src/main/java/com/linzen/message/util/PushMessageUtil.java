package com.linzen.message.util;

import com.alibaba.fastjson2.JSONObject;
import com.linzen.base.UserInfo;
import com.linzen.message.entity.MessageReceiveEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 消息推送工具类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Component
public class PushMessageUtil {

    /**
     * 工作流消息发送
     *
     *
     * @param userInfo
     */
    public static void pushMessage(Map<String, MessageReceiveEntity> map, UserInfo userInfo, int messageType){
        for (String userId : map.keySet()) {
            for (OnlineUserModel item : OnlineUserProvider.getOnlineUserList()) {
                if (userId.equals(item.getUserId()) && userInfo.getTenantId().equals(item.getTenantId())) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("method", "messagePush");
                    jsonObject.put("unreadNoticeCount", 1);
                    jsonObject.put("messageType", messageType);
                    jsonObject.put("userId", userInfo.getTenantId());
                    jsonObject.put("toUserId", userId);
                    jsonObject.put("title", map.get(userId).getTitle());
                    jsonObject.put("id",map.get(userId).getId());
                    jsonObject.put("messageDefaultTime", map.get(userId).getUpdateTime() != null ? map.get(userId).getUpdateTime().getTime() : null);
                    OnlineUserProvider.sendMessage(item, jsonObject);
                }
            }
        }
    }

}
