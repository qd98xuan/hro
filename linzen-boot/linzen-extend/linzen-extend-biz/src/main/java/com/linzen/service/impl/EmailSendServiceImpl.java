package com.linzen.service.impl;

import com.linzen.base.service.SuperServiceImpl;
import com.linzen.entity.EmailSendEntity;
import com.linzen.mapper.EmailSendMapper;
import com.linzen.service.EmailSendService;
import org.springframework.stereotype.Service;


/**
 * 邮件发送
 *
 * @copyright 领致信息技术有限公司
 * @author FHNP
 * @date 2023-04-01
 */
@Service
public class EmailSendServiceImpl extends SuperServiceImpl<EmailSendMapper, EmailSendEntity> implements EmailSendService {

}
