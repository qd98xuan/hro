package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 *  高级查询
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("sys_advanced_query_scheme")
public class AdvancedQueryEntity extends SuperExtendEntity<String> {

	/**
	 * 方案名称
	 */
	@TableField("F_FULL_NAME")
	private String fullName;

	/**
	 * 方案名称
	 */
	@TableField("F_MATCH_LOGIC")
	private String matchLogic;

	/**
	 * 条件规则Json
	 */
	@TableField("F_CONDITION_JSON")
	private String conditionJson;

	/**
	 * 菜单主键
	 */
	@TableField("F_MODULE_ID")
	private String moduleId;

}
