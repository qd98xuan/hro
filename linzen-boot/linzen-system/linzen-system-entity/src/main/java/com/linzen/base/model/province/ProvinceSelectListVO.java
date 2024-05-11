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
public class ProvinceSelectListVO {
    @Schema(description = "主键")
    private String id;

    @Schema(description = "名称")
    private String fullName;

    @Schema(description = "是否显示下级可点击按钮")
    private Boolean isLeaf;
}
