package com.linzen.engine.model.flowdelegate;

import com.linzen.base.Pagination;
import lombok.Data;

/**
 * 流程设计
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FlowDelegatePagination extends Pagination {
    private String myOrDelagateToMe;
}
