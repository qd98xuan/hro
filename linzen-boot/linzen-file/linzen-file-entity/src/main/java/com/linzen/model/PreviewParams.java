package com.linzen.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class PreviewParams {
	@Schema(description = "文件名")
	private String fileName;
	@Schema(description = "预览文件id")
	private String fileVersionId;
	@Schema(description = "文件下载路径")
	private String fileDownloadUrl;
}
