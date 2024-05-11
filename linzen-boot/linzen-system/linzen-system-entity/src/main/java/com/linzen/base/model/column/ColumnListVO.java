package com.linzen.base.model.column;

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
public class ColumnListVO {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "列表名称")
    private String fullName;
    @Schema(description = "编码")
    private String enCode;
    @Schema(description = "表格")
    private String bindTable;
    @Schema(description = "是否启用")
    private Integer delFlag;
    @Schema(description = "排序码")
    private Long sortCode;
}
