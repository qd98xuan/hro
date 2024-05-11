package com.linzen.permission.model.organize;

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
public class OrganizeDepartInfoVO {
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
    @Schema(description = "主管id")
    private String managerId;
    @Schema(description = "排序码")
    private Long sortCode;

    @Schema(description = "组织id树")
    private List<String> organizeIdTree;
}
