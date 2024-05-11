package com.linzen.base.service;

import com.linzen.base.UserInfo;
import com.linzen.base.entity.AdvancedQueryEntity;

import java.util.List;

/**
 *
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */

public interface AdvancedQueryService extends SuperService<AdvancedQueryEntity> {

	void create(AdvancedQueryEntity advancedQueryEntity);

	AdvancedQueryEntity getInfo(String id,String userId);

	List<AdvancedQueryEntity> getList(String moduleId, UserInfo userInfo);
}
