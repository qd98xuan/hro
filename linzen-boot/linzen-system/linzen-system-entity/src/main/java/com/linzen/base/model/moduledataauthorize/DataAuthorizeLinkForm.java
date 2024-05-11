package com.linzen.base.model.moduledataauthorize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 数据权限 连接表单
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DataAuthorizeLinkForm {
	@Schema(description = "主键")
	private String id;
	@Schema(description = "菜单id")
	@NotBlank(message = "必填")
	private String moduleId;
	@Schema(description = "连接id")
	private String linkId;
	@Schema(description = "连接表")
	private String linkTables;
	@Schema(description = "数据类型")
	private Integer dataType;
}
