package com.linzen.onlinedev.model;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @author FHNP
 * @date 2023-04-01
 */
@Data
@Schema(description="导出参数")
public class PaginationModelExport extends PaginationModel {
    @Schema(description = "导出selectKey")
    private String[] selectKey;
    @Schema(description = "导出选中数据")
    private String[] selectIds;
    @Schema(description = "导出json")
    private String json;
}
