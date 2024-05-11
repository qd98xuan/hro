package com.linzen.base.model;

import com.linzen.util.treeutil.SumTree;
import lombok.Data;

/**
 * 类功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PrintTableTreeModel extends SumTree<PrintTableTreeModel> {

    private String fullName;

}
