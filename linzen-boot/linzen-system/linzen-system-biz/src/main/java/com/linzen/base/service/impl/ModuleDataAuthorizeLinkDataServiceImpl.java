package com.linzen.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.entity.ModuleDataAuthorizeLinkEntity;
import com.linzen.base.mapper.ModuleDataAuthorizeLinkDataMapper;
import com.linzen.base.service.ModuleDataAuthorizeLinkDataService;
import com.linzen.base.service.SuperServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据权限方案
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class ModuleDataAuthorizeLinkDataServiceImpl extends SuperServiceImpl<ModuleDataAuthorizeLinkDataMapper, ModuleDataAuthorizeLinkEntity> implements ModuleDataAuthorizeLinkDataService {


	@Override
	public ModuleDataAuthorizeLinkEntity getLinkDataEntityByMenuId(String menuId,Integer type) {
		QueryWrapper<ModuleDataAuthorizeLinkEntity> linkEntityQueryWrapper = new QueryWrapper<>();
		linkEntityQueryWrapper.lambda().eq(ModuleDataAuthorizeLinkEntity::getModuleId,menuId).eq(ModuleDataAuthorizeLinkEntity::getDataType,type);
		List<ModuleDataAuthorizeLinkEntity> list = this.list(linkEntityQueryWrapper);
		if (list.size()>0){
			return list.get(0);
		}
		return new ModuleDataAuthorizeLinkEntity();
	}
}
