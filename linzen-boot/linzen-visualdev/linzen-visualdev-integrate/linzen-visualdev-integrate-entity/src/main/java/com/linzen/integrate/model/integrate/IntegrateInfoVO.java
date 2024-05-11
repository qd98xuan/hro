package com.linzen.integrate.model.integrate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP管理员/admin
 * @date 2023-04-01
 */
@Data
public class IntegrateInfoVO {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "说明")
    private String description;
    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "编码")
    private String enCode;
    @Schema(description = "有效标志")
    private Integer enabledMark;
    @Schema(description = "排序码")
    private Long sortCode;
    @Schema(description = "类型")
    private Integer type;
    @Schema(description = "json")
    private String templateJson;
}
