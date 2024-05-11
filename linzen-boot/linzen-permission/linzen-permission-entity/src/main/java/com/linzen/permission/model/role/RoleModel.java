package com.linzen.permission.model.role;

import com.linzen.util.treeutil.SumTree;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class RoleModel extends SumTree<RoleModel> {

    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "编码")
    private String enCode;
    @Schema(description = "角色类型")
    private String type;
    @Schema(description = "备注")
    private String description;
    @Schema(description = "状态")
    private Integer enabledMark;
    @Schema(description = "创建时间")
    private Date creatorTime;
    @Schema(description = "排序")
    private Long sortCode;
    @Schema(description = "数量")
    private Long num;
    @Schema(description = "前端解析唯一标识")
    private String onlyId;
    @Schema(description = "图标")
    private String icon;


    private String organize;
    @Schema(description = "组织id树")
    private List<String> organizeIds;
}
