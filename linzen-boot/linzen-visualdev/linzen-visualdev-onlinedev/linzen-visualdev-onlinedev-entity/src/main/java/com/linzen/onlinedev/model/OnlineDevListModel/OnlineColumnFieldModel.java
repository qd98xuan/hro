package com.linzen.onlinedev.model.OnlineDevListModel;
import lombok.Data;

/**
 * 列表字段
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class OnlineColumnFieldModel {
	/**
	 * 表名
	 */
	private String tableName;
	/**
	 * 字段
	 */
	private String field;

	/**
	 * 原本字段
	 */
	private String OriginallyField;

	/**
	 * 别名
	 */
	private String otherName;

}
