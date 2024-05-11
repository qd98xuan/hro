package com.linzen.base.model.advancedquery;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *
 * 高级查询表单
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class AdvancedQuerySchemeForm implements Serializable {
	@Schema(description = "名称")
	@NotBlank(message = "必填")
	private String fullName;
	@Schema(description = "条件")
	@NotBlank(message = "必填")
	private String conditionJson;
	@Schema(description = "匹配标志")
	@NotBlank(message = "必填")
	private String matchLogic;
	@Schema(description = "菜单id")
	@NotBlank(message = "当前菜单不能为空")
	private String moduleId;
}
