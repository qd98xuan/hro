package com.linzen.base.model.online;

import com.linzen.base.entity.ModuleDataAuthorizeSchemeEntity;
import lombok.Data;

import java.util.List;

/**
 *
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PerColModels {
	/**
	 * 数据权限
	 */
	private List<AuthFlieds> dataPermission;

	/**
	 * 表单权限
	 */
	private List<AuthFlieds> formPermission;

	/**
	 * 列表权限
	 */
	private List<AuthFlieds> listPermission;

	/**
	 * 按钮权限
	 */
	private List<AuthFlieds> buttonPermission;

	/**
	 * 数据权限方案
	 */
	private List<ModuleDataAuthorizeSchemeEntity> dataPermissionScheme;

}
