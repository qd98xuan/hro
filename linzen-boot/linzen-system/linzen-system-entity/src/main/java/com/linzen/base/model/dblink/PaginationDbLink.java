package com.linzen.base.model.dblink;

import com.linzen.base.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数据连接分页
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PaginationDbLink extends Pagination {

    @Schema(description = "数据库类型")
    private String dbType;

}
