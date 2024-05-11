package com.linzen.portal.model;

import com.linzen.base.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Schema(description="查询条件")
public class PortalPagination extends Pagination {

	@Schema(description = "分类（字典）")
	private String category;

	@Schema(description = "类型(0-门户设计,1-配置路径)")
	private Integer type;

	@Schema(description = "锁定(0-禁用,1-启用)")
	private Integer enabledLock;

	@Schema(description = "平台")
	private String platform = "web";

	@Schema(description = "状态：0-未发布，1-已发布，2-已修改")
	private Integer isRelease;

}
