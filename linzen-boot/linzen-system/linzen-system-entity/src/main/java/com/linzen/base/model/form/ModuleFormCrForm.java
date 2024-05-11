package com.linzen.base.model.form;

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
public class ModuleFormCrForm {

    @Schema(description = "编码")
    private String enCode;

    @Schema(description = "状态")
    private Integer enabledMark;

    @Schema(description = "名称")
    private String fullName;

    @Schema(description = "备注")
    private String description;

    @Schema(description = "菜单id")
    private String moduleId;

    @Schema(description = "排序码")
    private Long sortCode;
    @Schema(description = "规则")
    private Integer fieldRule;
    @Schema(description = "绑定表")
    private String bindTable;
    private String childTableKey;
}
