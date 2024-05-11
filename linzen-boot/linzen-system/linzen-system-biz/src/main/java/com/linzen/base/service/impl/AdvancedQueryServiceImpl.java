package com.linzen.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.UserInfo;
import com.linzen.base.entity.AdvancedQueryEntity;
import com.linzen.base.mapper.AdvancedQueryMapper;
import com.linzen.base.service.AdvancedQueryService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.util.RandomUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 *
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class AdvancedQueryServiceImpl extends SuperServiceImpl<AdvancedQueryMapper, AdvancedQueryEntity> implements AdvancedQueryService {
	@Override
	public void create(AdvancedQueryEntity advancedQueryEntity) {
		String mainId = Optional.ofNullable(advancedQueryEntity.getId()).orElse(RandomUtil.uuId());
		advancedQueryEntity.setId(mainId);
		this.save(advancedQueryEntity);
	}

	@Override
	public AdvancedQueryEntity getInfo(String id,String userId) {
		QueryWrapper<AdvancedQueryEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.lambda().eq(AdvancedQueryEntity::getId, id).eq(AdvancedQueryEntity::getCreatorUserId, userId);
		return this.getOne(queryWrapper);
	}

	@Override
	public List<AdvancedQueryEntity> getList(String moduleId, UserInfo userInfo) {
		QueryWrapper<AdvancedQueryEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.lambda().eq(AdvancedQueryEntity::getModuleId, moduleId).eq(AdvancedQueryEntity::getCreatorUserId, userInfo.getUserId());
		List<AdvancedQueryEntity> list = this.list(queryWrapper);
		return list;
	}

}
