package com.linzen.onlinedev.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 批量删除id集合
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Schema(description="批量处理参数")
public class BatchRemoveIdsVo {
	@Schema(description = "批量处理数据id")
	private String[] ids;
}
