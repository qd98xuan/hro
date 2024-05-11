package com.linzen.permission.model.organize;

import com.alibaba.fastjson2.annotation.JSONField;
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
public class OrganizeListVO {

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

    @Schema(description = "状态")
    private Integer enabledMark;

    private Long creatorTime;

    @Schema(description = "是否有下级菜单")
    private Boolean hasChildren;

    @Schema(description = "下级菜单列表")
    private List<OrganizeListVO> children;

    @Schema(description = "排序")
    private Long sortCode;

    private String organizeIdTree;

    @Schema(description = "")
    private Boolean isLeaf;

    @JSONField(name="category")
    private String type;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "修改用户")
    private String lastFullName;

    private String organize;

    @Schema(description ="组织id树")
    private List<String> organizeIds;
}
