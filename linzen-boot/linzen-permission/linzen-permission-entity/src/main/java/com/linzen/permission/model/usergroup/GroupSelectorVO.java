package com.linzen.permission.model.usergroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class GroupSelectorVO implements Serializable {

    @Schema(description = "主键")
    private String id;
    @Schema(description = "名称")
    private String fullName;
    /**
     * 子集个数
     */
    @Schema(description = "子集个数")
    private Long num;
    /**
     * 子集
     */
    @Schema(description = "子集")
    private List<GroupSelectorVO> children;
    @Schema(description = "父级id")
    private String parentId;
    @Schema(description = "类型")
    private String type;

    @Schema(description = "有线标志")
    private Integer delFlag;

    @Schema(description = "按钮")
    private String icon;
}
