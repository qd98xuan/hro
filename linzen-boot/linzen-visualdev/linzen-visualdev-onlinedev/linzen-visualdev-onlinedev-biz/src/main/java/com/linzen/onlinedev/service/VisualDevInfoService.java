package com.linzen.onlinedev.service;

import com.linzen.base.entity.VisualdevEntity;
import com.linzen.onlinedev.model.OnlineInfoModel;
import com.linzen.onlinedev.model.VisualdevModelDataInfoVO;

/**
 *
 * 功能设计表单数据
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface VisualDevInfoService {

	/**
	 *	编辑页数据回显
	 * @param id 主键id
	 * @param visualdevEntity 可视化实体
	 * @return
	 */
	VisualdevModelDataInfoVO getEditDataInfo(String id, VisualdevEntity visualdevEntity);

	/**
	 * 详情页数据
	 * @param id
	 * @param visualdevEntity
	 * @return
	 */
	VisualdevModelDataInfoVO getDetailsDataInfo(String id, VisualdevEntity visualdevEntity);

	/**
	 * 详情页数据(过滤字段)
	 * @param id
	 * @param visualdevEntity
	 * @return
	 */
	VisualdevModelDataInfoVO getDetailsDataInfo(String id, VisualdevEntity visualdevEntity, OnlineInfoModel model);
}
