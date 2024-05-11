package com.linzen.service.impl;

import com.linzen.base.entity.EmailConfigEntity;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.mapper.EmailConfigMapper;
import com.linzen.service.EmailConfigService;
import org.springframework.stereotype.Service;


/**
 * 邮件配置
 *
 * @copyright 领致信息技术有限公司
 * @author FHNP
 * @date 2023-04-01
 */
@Service
public class EmailConfigServiceImpl extends SuperServiceImpl<EmailConfigMapper, EmailConfigEntity> implements EmailConfigService {

}
