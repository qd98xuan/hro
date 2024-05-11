package com.linzen.util.treeutil;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;


/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SumTree<T> {
    private String id;
    private String parentId;
    private Boolean hasChildren;
    private List<SumTree<T>> children;
}
