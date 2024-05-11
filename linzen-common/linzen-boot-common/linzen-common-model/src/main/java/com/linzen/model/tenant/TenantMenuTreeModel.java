package com.linzen.model.tenant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.linzen.util.treeutil.SumTree;
import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TenantMenuTreeModel extends SumTree {
    private String fullName;
    private String icon;
    private Integer type;
    private Long sortCode;
    private String category;
    private boolean disabled;
    private String urlAddress;
}
