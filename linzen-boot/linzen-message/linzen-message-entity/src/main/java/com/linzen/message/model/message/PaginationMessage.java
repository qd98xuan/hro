package com.linzen.message.model.message;

import com.linzen.base.Pagination;
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
public class PaginationMessage extends Pagination {
    /**
     * 类型
     */
    @Schema(description = "类型")
    private Integer type;

    /**
     * 是否已读
     */
    @Schema(description = "是否已读")
    private Integer isRead;

}
