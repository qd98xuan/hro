package com.linzen.base.model.billrule;

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
public class BillRuleListVO {

    @Schema(description = "id")
    private String id;
    @Schema(description = "业务名称")
    private String fullName;
    @Schema(description = "业务编码")
    private String enCode;
    @Schema(description = "流水起始")
    private String startNumber;
    @Schema(description = "当前流水号")
    private String outputNumber;
    @Schema(description = "状态(0-禁用，1-启用)")
    private Integer delFlag;
    @Schema(description = "排序码")
    private Long sortCode;
    @Schema(description = "创建人")
    private String creatorUser;
    @Schema(description = "创建时间")
    private Long creatorTime;
    @Schema(description = "最后修改时间")
    private Long updateTime;
    @Schema(description = "业务分类")
    private String category;

}
