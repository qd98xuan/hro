package com.linzen.permission.model.user.vo;

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
public class UserSelectorVO {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "父主键")
    private String parentId;
    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "是否有子节点")
    private Boolean hasChildren;
    @Schema(description = "状态")
    private Integer enabledMark;
    @Schema(description = "子节点")
    private List<UserSelectorVO> children;
    @JSONField(name="category")
    private String type;
    @Schema(description = "图标")
    private String icon;
    private Boolean isLeaf;
    private String headIcon;
    private String organize;
    private String gender;
    private Integer isAdministrator;
}
