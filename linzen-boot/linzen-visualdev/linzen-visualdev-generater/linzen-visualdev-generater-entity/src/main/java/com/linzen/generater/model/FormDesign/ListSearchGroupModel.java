package com.linzen.generater.model.FormDesign;
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
public class ListSearchGroupModel {
	/**
	 * 模型名
	 */
	private String modelName;
	/**
	 * 表名
	 */
	private String tableName;
	/**
	 * 外键
	 */
	private String ForeignKey;
	/**
	 * 关联主键
	 */
	private String mainKey;

	/**
	 * 该表下的查询字段
	 */
	private List<SearchTypeModel> searchTypeModelList;
}
