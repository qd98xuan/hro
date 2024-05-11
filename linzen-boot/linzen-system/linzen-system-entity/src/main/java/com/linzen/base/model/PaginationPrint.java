package com.linzen.base.model;

import com.linzen.base.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PaginationPrint extends Pagination {
    private String category;
    @Schema(description = "状态")
    private Integer enabledMark;
}
