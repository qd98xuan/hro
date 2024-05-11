package com.linzen.onlinedev.model.OnlineDevListModel;
import lombok.Data;

import java.util.List;

/**
 * 列表子表
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class OnlineColumnChildFieldModel {
	/**
	 * 子表表名
	 */
	private String table;
	/**
	 * 关联外键
	 */
	private String tableField;

	/**
	 * 关联主键
	 */
	private String relationField;

	/**
	 * 子表字段集合
	 */
	private List<String> fieldList;

}
