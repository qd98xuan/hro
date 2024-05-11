package com.linzen.onlinedev.model.OnlineDevEnum;

/**
 * 控件多选字符
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */

public enum MultipleControlEnum {
	/**
	 * 数组
	 */
	MULTIPLE_JSON_ONE("[",1),
	/**
	 * 二维数组
	 */
	MULTIPLE_JSON_TWO("[[",2),
	/**
	 * 普通字符
	 */
	MULTIPLE_JSON_THREE("",3);


	MultipleControlEnum(String multipleChar, int dataType) {
		MultipleChar = multipleChar;
		DataType = dataType;
	}

	public String getMultipleChar() {
		return MultipleChar;
	}

	public int getDataType() {
		return DataType;
	}

	private String MultipleChar;
	private int DataType;

}
