package com.linzen.emnus;

/**
 * 导入导出模板类型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */

public enum ExportModelTypeEnum {
	/**
	 * 功能设计
	 */
	Design(1,"design"),

	/**
	 * APP
	 */
	App(2,"app"),

	/**
	 *门户
	 */
	Portal(5,"portal");
	private final int code;
	private final String message;

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	ExportModelTypeEnum(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
