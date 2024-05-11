package com.linzen.message.mapper;

import com.linzen.base.mapper.SuperMapper;
import com.linzen.message.entity.MessageEntity;
import com.linzen.message.entity.MessageReceiveEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 消息实例
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface MessageMapper extends SuperMapper<MessageEntity> {

    List<MessageReceiveEntity> getMessageList(@Param("map") Map<String, Object> map);

    int getUnreadCount(@Param("userId") String userId,@Param("type") Integer type);

    List<MessageEntity> getInfoDefault(@Param("type") int type);
}
