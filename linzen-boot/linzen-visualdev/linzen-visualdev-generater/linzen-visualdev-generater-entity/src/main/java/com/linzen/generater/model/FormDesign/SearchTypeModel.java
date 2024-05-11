package com.linzen.generater.model.FormDesign;

import com.linzen.model.visualJson.config.ConfigModel;
import lombok.Data;

/**
 * 代码生成器查询条件
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class SearchTypeModel {
	private String vModel;
	private String dataType;
	private Integer searchType;
	private String label;
	private String projectKey;
	private String format;
	private String multiple;
	/**
	 * 搜索框显示
	 */
	private String placeholder;
	private ConfigModel config;

	private String TableName;
	private String afterVModel;

	private String showLevel;

	//新增  拼接之后的vmodel和label
	/**
	 * vmodel 子表副表拼接后得名称
	 */
	private String id;
	/**
	 * label 子表副表拼接后得名称
	 */
	private String fullName;
	/**
	 * 查询是否多选
	 */
	private String searchMultiple;
	/**
	 * 是否关键词
	 */
	private Boolean isKeyword;
}
