package com.linzen.base.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 同步菜单类型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Schema(description="发布功能参数" )
public class VisualDevPubModel {
	@Schema(description = "pc" )
	private Integer pc;
	@Schema(description = "app" )
	private Integer app;
	@Schema(description = "pc菜单父id" )
	private String pcModuleParentId;
	@Schema(description = "app菜单父id" )
	private String appModuleParentId;
	@Schema(description = "pc系统id" )
	private String pcSystemId;
	@Schema(description = "app系统id" )
	private String appSystemId;
}
