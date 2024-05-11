package com.linzen.base.service;

import com.linzen.base.entity.ModuleDataAuthorizeLinkEntity;


/**
 * 数据权限配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface ModuleDataAuthorizeLinkDataService extends SuperService<ModuleDataAuthorizeLinkEntity> {
	/**
	 * 根据菜单id获取数据连接
	 * @param menuId
	 * @return
	 */
	ModuleDataAuthorizeLinkEntity getLinkDataEntityByMenuId(String menuId,Integer type);

}
