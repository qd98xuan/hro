package com.linzen.portal.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *  门户
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("base_portal")
public class PortalEntity extends SuperExtendEntity.SuperExtendDEEntity<String> {

	@Schema(description = "名称")
	@TableField("F_FULL_NAME")
	private String fullName;

	@Schema(description = "编码")
	@TableField("F_EN_CODE")
	private String enCode;

	@Schema(description = "分类(数据字典维护)")
	@TableField("F_CATEGORY")
	private String category;

	@Schema(description = "类型(0-页面设计,1-自定义路径)")
	@TableField("F_TYPE")
	private Integer type;

	@Schema(description = "静态页面路径")
	@TableField("F_CUSTOM_URL")
	private String customUrl;

	@Schema(description = "类型(0-页面,1-外链)")
	@TableField("F_LINK_TYPE")
	private Integer linkType;

	@TableField("F_STATE")
	private Integer state;

	@Schema(description = "移动锁定(0-未锁定,1-锁定)")
	@TableField("F_ENABLED_LOCK")
	private Integer enabledLock;

}
