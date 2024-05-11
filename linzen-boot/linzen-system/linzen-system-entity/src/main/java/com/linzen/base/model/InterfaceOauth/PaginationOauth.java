package com.linzen.base.model.InterfaceOauth;

import com.linzen.base.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 接口认证查询参数
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PaginationOauth extends Pagination {
    private String keyword;
    @Schema(description = "有效标志")
    private Integer enabledMark;
}
