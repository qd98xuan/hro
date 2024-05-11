package com.linzen.permission.model.organize;

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
public class OrganizeInfoVO extends PermissionVoBase {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "父主键")
    private String parentId;
    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "编码")
    private String enCode;
    @Schema(description = "状态")
    private Integer enabledMark;
    @Schema(description = "备注")
    private String description;
    @Schema(description = "扩展属性")
    private String propertyJson;
    @Schema(description = "排序")
    private Long sortCode;

    @Schema(description = "组织id树")
    private List<String> organizeIdTree;
}
