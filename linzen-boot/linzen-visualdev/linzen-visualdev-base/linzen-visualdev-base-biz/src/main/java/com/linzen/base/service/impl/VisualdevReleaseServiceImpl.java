package com.linzen.base.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.entity.VisualdevReleaseEntity;
import com.linzen.base.mapper.VisualdevReleaseMapper;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.base.service.VisualdevReleaseService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class VisualdevReleaseServiceImpl extends SuperServiceImpl<VisualdevReleaseMapper, VisualdevReleaseEntity> implements VisualdevReleaseService {

	@Override
	public long beenReleased(String id) {
		QueryWrapper<VisualdevReleaseEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.lambda().eq(VisualdevReleaseEntity::getId, id);
		return this.count(queryWrapper);
	}

	@Override
	public List<VisualdevReleaseEntity> selectorList() {
		QueryWrapper<VisualdevReleaseEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.lambda().select(
				VisualdevReleaseEntity::getId,
				VisualdevReleaseEntity::getFullName,
				VisualdevReleaseEntity::getWebType,
				VisualdevReleaseEntity::getEnableFlow,
				VisualdevReleaseEntity::getType,
				VisualdevReleaseEntity::getCategory);
		return this.list(queryWrapper);
	}
}
