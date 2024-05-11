package com.linzen.message.service.impl;


import com.linzen.base.service.SuperServiceImpl;
import com.linzen.message.entity.MessageReceiveEntity;
import com.linzen.message.mapper.MessagereceiveMapper;
import com.linzen.message.service.MessagereceiveService;
import org.springframework.stereotype.Service;

/**
 * 消息接收 服务实现类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class MessagereceiveServiceImpl extends SuperServiceImpl<MessagereceiveMapper, MessageReceiveEntity> implements MessagereceiveService {

}
