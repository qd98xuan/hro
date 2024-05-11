package com.linzen.base.model.province;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ProvinceListVO {
    @Schema(description = "主键")
    private String id;

    @Schema(description = "名称")
    private String fullName;

    @Schema(description = "编码")
    private String enCode;

    @Schema(description = "有效标志")
    private Integer enabledMark;
    @Schema(description = "是否有子集")
    private Boolean hasChildren;

    @Schema(description = "是否有子集取反")
    private Boolean isLeaf;
    @Schema(description = "排序码")
    private long sortCode;
}
