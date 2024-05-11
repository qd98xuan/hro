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
public class OrganizeDepartListVO {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "父主键")
    private String parentId;
    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "编码")
    private String enCode;
    @Schema(description = "备注")
    private String description;
    @Schema(description = "创建时间")
    private Long creatorTime;
    @Schema(description = "部门经理")
    private String manager;
    @Schema(description = "状态")
    private Integer enabledMark;
    @Schema(description = "是否有下级菜单")
    private Boolean hasChildren;
    @Schema(description = "下级菜单列表")
    private List<OrganizeDepartListVO> children;
    @Schema(description = "排序")
    private Long sortCode;
}
