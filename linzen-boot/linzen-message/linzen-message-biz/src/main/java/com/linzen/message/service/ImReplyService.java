package com.linzen.message.service;

import com.linzen.base.service.SuperService;
import com.linzen.message.entity.ImReplyEntity;
import com.linzen.message.model.ImReplyListModel;

import java.util.List;

/**
 * 聊天会话
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface ImReplyService extends SuperService<ImReplyEntity> {

    /**
     * 获取消息会话列表
     *
     * @return
     */
    List<ImReplyEntity> getList();

    /**
     * 保存聊天会话
     *
     * @param entity
     * @return
     */
    boolean savaImReply(ImReplyEntity entity);

    /**
     * 获取聊天会话列表
     *
     * @return
     */
    List<ImReplyListModel> getImReplyList();

    /**
     * 移除聊天会话列表
     *
     * @return
     */
    boolean relocation(String sendUserId,String receiveUserId);

}
