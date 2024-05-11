package com.linzen.base.model.module;

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
public class MenuListVO {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "是否有下级菜单")
    private Boolean hasChildren;
    @Schema(description = "上级ID")
    private String parentId;
    @Schema(description = "状态")
    private Integer enabledMark;
    @Schema(description = "菜单名称")
    private String fullName;
    @Schema(description = " 图标")
    private String icon;
    @Schema(description = "链接地址")
    private String urlAddress;
    @Schema(description = "菜单类型",example = "1")
    private Integer type;
    @Schema(description = "下级菜单列表")
    private List<MenuListVO> children;
    private Integer isButtonAuthorize;
    private Integer isColumnAuthorize;
    private Integer isDataAuthorize;
    private Integer isFormAuthorize;
    private Long sortCode;

    private String systemId;
    @Schema(description = "编码")
    private String enCode;
}
