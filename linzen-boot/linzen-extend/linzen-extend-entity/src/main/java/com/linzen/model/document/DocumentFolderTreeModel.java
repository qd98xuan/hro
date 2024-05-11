package com.linzen.model.document;

import com.linzen.util.treeutil.SumTree;
import lombok.Data;

@Data
public class DocumentFolderTreeModel extends SumTree {
    private String icon;
    private String fullName;
}
