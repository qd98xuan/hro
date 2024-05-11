package com.linzen.base.model.column;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ColumnBatchForm {
    @Schema(description = "菜单id")
    @NotBlank(message = "必填")
    private String moduleId;
    @Schema(description = "表名")
    private String bindTable;
    @Schema(description = "绑定表说明")
    private String bindTableName;
    @Schema(description = "列JSON")
    private Object columnJson;
    @Schema(description = "排序码")
    private Long sortCode;
    @Schema(description = "字段规则")
    private Integer fieldRule;
    private String childTableKey;
}
