package com.linzen.message.service;


import com.linzen.base.PageModel;
import com.linzen.base.service.SuperService;
import com.linzen.message.entity.ImContentEntity;
import com.linzen.message.model.ImUnreadNumModel;

import java.util.List;

/**
 * 聊天内容
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface ImContentService extends SuperService<ImContentEntity> {

    /**
     * 获取消息列表
     *
     * @param sendUserId    发送者
     * @param receiveUserId 接收者
     * @param pageModel
     * @return
     */
    List<ImContentEntity> getMessageList(String sendUserId, String receiveUserId, PageModel pageModel);

    /**
     * 获取未读消息
     *
     * @param receiveUserId 接收者
     * @return
     */
    List<ImUnreadNumModel> getUnreadList(String receiveUserId);

    /**
     * 获取未读消息
     *
     * @param receiveUserId 接收者
     * @return
     */
    int getUnreadCount(String sendUserId, String receiveUserId);

    /**
     * 发送消息
     *
     * @param sendUserId    发送者
     * @param receiveUserId 接收者
     * @param message       消息内容
     * @param messageType   消息类型
     * @return
     */
    void sendMessage(String sendUserId, String receiveUserId, String message, String messageType);

    /**
     * 已读消息
     *
     * @param sendUserId    发送者
     * @param receiveUserId 接收者
     * @return
     */
    void readMessage(String sendUserId, String receiveUserId);
    /**
     * 删除聊天记录
     *
     * @return
     */
    boolean deleteChatRecord(String sendUserId, String receiveUserId);

}
