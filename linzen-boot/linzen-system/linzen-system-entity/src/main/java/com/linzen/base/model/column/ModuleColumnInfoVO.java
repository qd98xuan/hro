package com.linzen.base.model.column;

import com.linzen.util.visiual.ProjectKeyConsts;
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
public class ModuleColumnInfoVO {
    @Schema(description = "是否启用")
    private Integer delFlag;

    @Schema(description = "列表名称")
    private String fullName;

    @Schema(description = "说明")
    private String description;

    @Schema(description = "编码")
    private String enCode;

    @Schema(description = "主键")
    private String id;

    @Schema(description = "表名")
    private String bindTable;

    @Schema(description = "绑定表说明")
    private String bindTableName;

    @Schema(description = "菜单id")
    private String moduleId;

    @Schema(description = "排序码")
    private Long sortCode;

    @Schema(description = "字段规则")
    private Integer fieldRule;

    private String childTableKey = ProjectKeyConsts.CHILD_TABLE_PREFIX;
}
