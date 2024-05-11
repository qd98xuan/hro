package com.linzen.message.mapper;

import com.linzen.base.mapper.SuperMapper;
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
public interface ImReplyMapper extends SuperMapper<ImReplyEntity> {

    /**
     * 聊天会话列表
     * @return
     */
    List<ImReplyListModel> getImReplyList();

}
