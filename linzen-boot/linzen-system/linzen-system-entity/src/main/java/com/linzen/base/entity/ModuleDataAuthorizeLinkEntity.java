package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("sys_module_link")
public class ModuleDataAuthorizeLinkEntity extends SuperExtendEntity<String> {

	/**
	 * 菜单主键
	 */
	@TableField("F_MODULE_ID")
	private String moduleId;

	/**
	 * 数据源连接
	 */
	@TableField("f_link_id")
	private String linkId;

	/**
	 * 连接表名
	 */
	@TableField("f_link_tables")
	private String linkTables;

	/**
	 * 权限类型（表单权限，数据权限，列表权限）
	 */
	@TableField("F_TYPE")
	private Integer dataType;

}
