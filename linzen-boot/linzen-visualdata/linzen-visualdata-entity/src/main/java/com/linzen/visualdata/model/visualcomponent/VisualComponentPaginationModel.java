package com.linzen.visualdata.model.visualcomponent;

import com.linzen.visualdata.model.VisualPagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class VisualComponentPaginationModel extends VisualPagination {
    @Schema(description = "组件类型(0,1)")
    private Integer type;

}
