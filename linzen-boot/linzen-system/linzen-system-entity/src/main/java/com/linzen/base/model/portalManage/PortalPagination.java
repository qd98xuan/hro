package com.linzen.base.model.portalManage;


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
@Schema(description = "查询条件")
public class PortalPagination extends Pagination {
	/**
	 * 分类
	 */
	@Schema(description = "分类（字典）")
	private String category;

	/**
	 * 类型(0-门户设计,1-配置路径)
	 */
	@Schema(description = "类型(0-门户设计,1-配置路径)")
	private Integer type;
}
