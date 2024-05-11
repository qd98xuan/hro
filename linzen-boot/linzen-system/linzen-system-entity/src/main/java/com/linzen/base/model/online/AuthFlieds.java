package com.linzen.base.model.online;

import lombok.Builder;
import lombok.Data;
/**
 *
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Builder
public class AuthFlieds {

	private String id;
	/**
	 * 字段名
	 */
	private String fullName;
	/**
	 * 是否启用
	 */
	private Boolean status;

	/**
	 * encode
	 */
	private String encode;

	/**
	 * 规则 （0.主表规则 1.副表规则 2.子表规则）
	 */
	private Integer rule;

	/**
	 * 控件类型
	 */
	private String projectKey;

	/**
	 * 数据权限条件
	 */
	private String AuthCondition;

	/**
	 * 表名
	 */
	private String bindTableName;

	/**
	 * 子表规则key
	 */
	private String childTableKey;
}
