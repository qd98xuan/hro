package com.linzen.permission.model.position;

import com.linzen.permission.model.permission.PermissionVoBase;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PositionInfoVO extends PermissionVoBase {
    @Schema(description = "id")
    private String id;
    @Schema(description = "上级id")
    private String organizeId;
    @Schema(description = "岗位名称")
    private String fullName;
    @Schema(description = "岗位编码")
    private String enCode;
    @Schema(description = "岗位类型")
    private String type;
    @Schema(description = "岗位状态")
    private Integer delFlag;
    @Schema(description = "岗位说明")
    private String description;
    @Schema(description = "排序")
    private Long sortCode;

    @Schema(description = "组织id树")
    private List<String> organizeIdTree;

}
