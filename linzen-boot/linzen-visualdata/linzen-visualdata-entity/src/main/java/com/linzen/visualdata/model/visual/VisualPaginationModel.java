package com.linzen.visualdata.model.visual;

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
public class VisualPaginationModel extends VisualPagination {
    @Schema(description ="分类")
    private Integer category;


}
