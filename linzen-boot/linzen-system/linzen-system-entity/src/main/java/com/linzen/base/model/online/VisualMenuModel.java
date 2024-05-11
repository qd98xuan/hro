package com.linzen.base.model.online;

import lombok.Data;

/**
 * 可视化菜单对象
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class VisualMenuModel {
	/**
	 * 功能id
	 */
	private String id;

	/**
	 * pc 按钮配置
	 */
	private PerColModels pcPerCols;

	/**
	 * app 按钮配置
	 */
	private PerColModels appPerCols;

	/**
	 * 功能名
	 */
	private String fullName;

	/**
	 * 功能编码
	 */
	private String encode;

	private Integer pc;

	private Integer app;

	private String pcModuleParentId;

	private String appModuleParentId;

	private String pcSystemId;

	private String appSystemId;

	private Integer type;
}
