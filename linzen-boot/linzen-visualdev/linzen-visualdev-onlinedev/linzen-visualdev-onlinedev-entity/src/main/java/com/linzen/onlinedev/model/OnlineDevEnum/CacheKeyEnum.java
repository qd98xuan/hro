package com.linzen.onlinedev.model.OnlineDevEnum;

/**
 *
 * 在线开发缓存的key
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public enum CacheKeyEnum {
	/**
	 * 修改用户，创建用户，用户组件
	 */
	USER("_user","用户"),

	POS("_position","岗位"),

	ORG("_organization","组织"),

	AllORG("_organizationAll","组织多级"),

	PRO("_province","省份"),

	ROLE("_role","角色"),

	GROUP("_group","分组");
	private final String name;
	private final String message;

	CacheKeyEnum(String name, String message) {
		this.name = name;
		this.message = message;
	}

	public String getName() {
		return name;
	}

	public String getMessage() {
		return message;
	}


}
