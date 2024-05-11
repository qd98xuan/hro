package com.linzen.permission.model.role;

import com.linzen.permission.model.permission.PermissionVoBase;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class RoleInfoVO extends PermissionVoBase {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "编码")
    private String enCode;
    @Schema(description = "组织id数组树")
    private List<LinkedList<String>> organizeIdsTree;
    @Schema(description = "全局标识")
    private Integer globalMark;
    @Schema(description = "类型")
    private String type;
    @Schema(description = "状态")
    private Integer enabledMark;
    @Schema(description = "备注")
    private String description;
    @Schema(description = "排序")
    private Long sortCode;


}
