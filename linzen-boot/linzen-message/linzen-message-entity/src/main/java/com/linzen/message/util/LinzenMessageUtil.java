package com.linzen.message.util;

import com.linzen.message.entity.MessageEntity;
import com.linzen.message.entity.MessageReceiveEntity;
import com.linzen.util.RandomUtil;

import java.util.Date;

/**
 * 消息实体类
 *
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
public class LinzenMessageUtil {
    public static MessageEntity setMessageEntity(String userId,String title,String bodyText,Integer recType) {
        MessageEntity entity = new MessageEntity();
        entity.setTitle(title);
        entity.setBodyText(bodyText);
        entity.setId(RandomUtil.uuId());
//        entity.setType(recType);
        entity.setCreatorUserId(userId);
        entity.setCreatorTime(new Date());
        entity.setUpdateTime(entity.getCreatorTime());
        entity.setUpdateUserId(entity.getCreatorUserId());
        return entity;
    }

    public static MessageReceiveEntity setMessageReceiveEntity(String toUserId, String title, Integer sendType){
        MessageReceiveEntity entity = new MessageReceiveEntity();
        entity.setId(RandomUtil.uuId());
        entity.setUserId(toUserId);
        entity.setIsRead(0);
        entity.setType(sendType);
        entity.setFlowType(1);
        entity.setTitle(title);
        return entity;
    }
}
