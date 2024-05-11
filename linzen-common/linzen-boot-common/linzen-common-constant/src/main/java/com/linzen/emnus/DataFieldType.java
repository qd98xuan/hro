package com.linzen.emnus;
/**
 * 数据权限字段类型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public enum DataFieldType {
	/**
	 * 浮点型
	 */
	Double("Double"),
	/**
	 * 字符型
	 */
	Varchar("String"),

	/**
	 * 数值型
	 */
	Number("Int32");

	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	DataFieldType(String message) {
		this.message = message;
	}
}
