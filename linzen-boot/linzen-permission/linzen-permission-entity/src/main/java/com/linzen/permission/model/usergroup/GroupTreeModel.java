package com.linzen.permission.model.usergroup;

import com.linzen.util.treeutil.SumTree;
import lombok.Data;

/**
 * 转树模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class GroupTreeModel extends SumTree {
    private String fullName;
    private String type;
    private Long num;
    
    private Integer delFlag;
    private String icon;
}
