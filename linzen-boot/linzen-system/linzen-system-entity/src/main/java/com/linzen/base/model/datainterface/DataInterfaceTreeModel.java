package com.linzen.base.model.datainterface;

import com.linzen.util.treeutil.SumTree;
import lombok.Data;

@Data
public class DataInterfaceTreeModel extends SumTree {
//    private String id;
//    private String parentId;
    private String fullName;
    private String category;
}
