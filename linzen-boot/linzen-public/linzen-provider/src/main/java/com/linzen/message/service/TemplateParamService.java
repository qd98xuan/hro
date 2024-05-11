
package com.linzen.message.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.service.SuperService;
import com.linzen.message.entity.TemplateParamEntity;
import com.linzen.message.model.messagetemplateconfig.MessageTemplateConfigPagination;

import java.util.List;
/**
 *
 * 消息模板（新）
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
public interface TemplateParamService extends SuperService<TemplateParamEntity> {

	QueryWrapper<TemplateParamEntity> getChild(MessageTemplateConfigPagination pagination, QueryWrapper<TemplateParamEntity> templateParamQueryWrapper);

	TemplateParamEntity getInfo(String id);

	List<TemplateParamEntity> getDetailListByParentId(String id);

	List<TemplateParamEntity> getParamList(String id,List<String> params);
}
