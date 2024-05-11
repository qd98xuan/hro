package com.linzen.message.mapper;


import com.linzen.base.mapper.SuperMapper;
import com.linzen.message.entity.ImContentEntity;
import com.linzen.message.model.ImUnreadNumModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 聊天内容
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface ImContentMapper extends SuperMapper<ImContentEntity> {

    List<ImUnreadNumModel> getUnreadList(@Param("receiveUserId") String receiveUserId);

    List<ImUnreadNumModel> getUnreadLists(@Param("receiveUserId") String receiveUserId);

    int readMessage(@Param("map") Map<String, String> map);
}

