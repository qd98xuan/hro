package com.linzen.base.model;

import com.linzen.util.treeutil.SumTree;
import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Data
public class VisualTreeModel extends SumTree {
    private String fullName;
    private Long num;
    private String enCode;
    private Integer state;
    private String type;
    private String tables;
    private Long creatorTime;
    private String creatorUser;
    private Long updateTime;
    private String updateUser;
    private Long sortCode;
}
