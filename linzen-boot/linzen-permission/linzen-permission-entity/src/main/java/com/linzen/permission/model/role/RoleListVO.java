package com.linzen.permission.model.role;

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
public class RoleListVO {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "编码")
    private String enCode;
    @Schema(description = "角色类型")
    private String type;
    @Schema(description = "所属组织")
    private String organizeInfo;
    @Schema(description = "备注")
    private String description;
    @Schema(description = "状态")
    private Integer enabledMark;
    private Long creatorTime;
    @Schema(description = "排序")
    private Long sortCode;
}
