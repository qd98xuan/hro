package com.linzen.model.productclassify;

import com.linzen.util.treeutil.SumTree;
import lombok.Data;

/**
 *
 * 产品分类
 *
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class ProductclassifyModel extends SumTree {

    /** 名称 */
    private String fullName;

}
