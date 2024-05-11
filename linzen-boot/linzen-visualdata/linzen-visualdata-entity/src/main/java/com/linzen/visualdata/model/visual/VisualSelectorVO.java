package com.linzen.visualdata.model.visual;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 *
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class VisualSelectorVO {
    @Schema(description ="主键")
    private String id;
    @Schema(description ="名称")
    private String fullName;
    @Schema(description ="是否有下级")
    private Boolean hasChildren;
    @Schema(description ="下级")
    private List<VisualSelectorVO> children;
}
