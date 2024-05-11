
package com.linzen.message.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.service.SuperService;
import com.linzen.message.entity.SmsFieldEntity;
import com.linzen.message.model.messagetemplateconfig.MessageTemplateConfigPagination;

import java.util.List;
import java.util.Map;

/**
 *
 * 消息模板（新）
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
public interface SmsFieldService extends SuperService<SmsFieldEntity> {

	QueryWrapper<SmsFieldEntity> getChild(MessageTemplateConfigPagination pagination, QueryWrapper<SmsFieldEntity> smsFieldQueryWrapper);

	SmsFieldEntity getInfo(String id);

	List<SmsFieldEntity> getDetailListByParentId(String id);

	List<SmsFieldEntity> getParamList(String id,List<String> params);

	Map<String,Object> getParamMap(String templateId,Map<String,Object> map);
}
