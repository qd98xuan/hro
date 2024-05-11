package com.linzen.base.model.dictionarytype;

import com.linzen.util.treeutil.SumTree;
import lombok.Data;

@Data
public class DictionaryTypeModel extends SumTree {
    private String id;
    private String parentId;
    private String fullName;
    private Integer isTree;
    private String enCode;
    private long sortCode;
    private String category;
}
