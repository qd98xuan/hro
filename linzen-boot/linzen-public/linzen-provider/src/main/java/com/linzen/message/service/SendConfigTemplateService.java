
package com.linzen.message.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.service.SuperService;
import com.linzen.message.entity.SendConfigTemplateEntity;
import com.linzen.message.model.sendmessageconfig.SendMessageConfigPagination;

import java.util.List;

/**
 * 消息发送配置
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
public interface SendConfigTemplateService extends SuperService<SendConfigTemplateEntity> {

    QueryWrapper<SendConfigTemplateEntity> getChild(SendMessageConfigPagination pagination, QueryWrapper<SendConfigTemplateEntity> sendConfigTemplateQueryWrapper);

    SendConfigTemplateEntity getInfo(String id);

    List<SendConfigTemplateEntity> getDetailListByParentId(String id);

    /**
     * 根据消息发送配置id获取启用的配置模板
     * @param id
     * @return
     */
    List<SendConfigTemplateEntity> getConfigTemplateListByConfigId(String id);

    boolean isUsedAccount(String accountId);

    boolean isUsedTemplate(String templateId);
}
