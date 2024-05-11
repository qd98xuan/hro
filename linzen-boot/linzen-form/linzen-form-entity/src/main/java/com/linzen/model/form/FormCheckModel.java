package com.linzen.model.form;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;

/**
 *
 * 表单验证
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Schema(description="表单验证模型")
public class FormCheckModel {
	@Schema(description = "名称")
	private String label;
	@Schema(description = "选择值")
	private SelectStatementProvider statementProvider;
}
