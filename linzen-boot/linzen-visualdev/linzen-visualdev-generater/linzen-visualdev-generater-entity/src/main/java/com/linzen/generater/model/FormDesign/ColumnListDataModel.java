package com.linzen.generater.model.FormDesign;

import com.linzen.model.visualJson.FieLdsModel;
import com.linzen.model.visualJson.analysis.FormMastTableModel;
import lombok.Data;

import java.util.List;

/**
 * 列表字段
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ColumnListDataModel {
	/**
	 * model别名
	 */
	private String modelName;

	/**
	 * 外键
	 */
	private String relationField;

	/**
	 * 外键首字母大写
	 */
	private String relationUpField;

	/**
	 * 关联主键
	 */
	private String mainKey;

	/**
	 * 关联主键首字母大写
	 */
	private String mainUpKey;

	/**
	 * 所拥有字段
	 */
	private List<String> fieldList;

	/**
	 * 控件属性
	 */
	private List<FormMastTableModel> fieLdsModelList;

	/**
	 * 表名
	 */
	private String tableName;

	/**
	 * 首字母小写
	 */
	private String modelLowName;

	/**
	 * 首字母大写
	 */
	private String modelUpName;

	/**
	 * 当前表主键
	 */
	private String mainField;

	/**
	 * 对应控件(去除linzen)
	 */
	private List<FieLdsModel> fieLdsModels;
}
