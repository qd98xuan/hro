package com.linzen.model.projectgantt;

import com.linzen.util.treeutil.SumTree;
import lombok.Data;

@Data
public class ProjectGanttTaskTreeModel extends SumTree {
    private String fullName;
}
