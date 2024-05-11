package com.linzen.model.login.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class AllMenuSelectVO {

    @Schema(description = "主键")
    private String id;

    @Schema(description = "名称")
    private String fullName;

    @Schema(description = "菜单编码")
    private String enCode;

    @Schema(description = "父主键")
    private String parentId;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "是否有下级菜单")
    private Boolean hasChildren;

    @Schema(description = "菜单地址")
    private String urlAddress;

    @Schema(description = "链接目标")
    private String linkTarget;

    @Schema(description = "下级菜单列表")
    private List<AllMenuSelectVO> children;

    @Schema(description = "菜单分类")
    private Integer type;

    private String propertyJson;

    private Long sortCode;

    private String systemId;
}
