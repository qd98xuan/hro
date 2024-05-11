package com.linzen.portal.model;

import com.alibaba.fastjson2.annotation.JSONField;
import com.linzen.util.treeutil.SumTree;
import lombok.Data;

/**
 *
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class PortalSelectModel extends SumTree {
    private String fullName;
    private Long sortCode;
    @JSONField(name="category")
    private String  parentId;
}
