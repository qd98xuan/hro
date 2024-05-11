package com.linzen.model.login;


import com.linzen.util.treeutil.SumTree;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AllUserMenuModel extends SumTree {

    @Schema(description = "名称")
    private String fullName;

    @Schema(description = "菜单编码")
    private String enCode;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "菜单地址")
    private String urlAddress;

    @Schema(description = "链接目标")
    private String linkTarget;

    @Schema(description = "菜单分类")
    private Integer type;

    private String propertyJson;

    private Long sortCode;

    private String systemId;
}
