package com.linzen.onlinedev.model.OnlineImport;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 导入失败的数据
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Schema(description="导入参数")
public class VisualImportModel {
	@Schema(description = "数据数组")
	private List<Map<String, Object>> list;

	@Schema(description = "流程引擎")
	private String flowId;
}
