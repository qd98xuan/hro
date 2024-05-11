package com.linzen.base.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 *
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @author FHNP
 * @date 2023-04-01
 */
@Data
@Schema(description="功能树形VO" )
public class VisualdevTreeVO {
    @Schema(description = "主键" )
    private String id;
    @Schema(description = "名称" )
    private String fullName;
    @Schema(description = "是否有子集" )
    private Boolean hasChildren;
    @Schema(description = "排序" )
    private Long sortCode;
    @Schema(description = "子集对象" )
    private List<VisualdevTreeChildModel> children;
}
