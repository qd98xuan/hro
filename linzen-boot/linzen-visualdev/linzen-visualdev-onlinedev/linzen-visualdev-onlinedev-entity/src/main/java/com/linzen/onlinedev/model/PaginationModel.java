package com.linzen.onlinedev.model;


import com.linzen.base.Pagination;
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
@Schema(description="查询条件模型")
public class PaginationModel extends Pagination {
    @Schema(description = "查询条件json")
    private String queryJson;
    @Schema(description = "菜单id")
    private String menuId;
    @Schema(description = "关联字段")
    private String relationField;
    @Schema(description = "字段对象")
    private String columnOptions;
    @Schema(description = "数据类型")
    private String dataType;
    @Schema(description = "高级查询条件json")
    private String superQueryJson;
    @Schema(description = "异步查询父id")
    private String parentId;
}
