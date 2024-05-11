package com.linzen.engine.model.flowengine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linzen.base.Pagination;
import lombok.Data;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PaginationFlowEngine extends Pagination {
    private Integer enabledMark;
    @JsonIgnore
    private Integer type;
}
