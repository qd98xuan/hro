package com.linzen.portal.model;

import com.linzen.util.treeutil.SumTree;
import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Data
public class PortalTreeModel extends SumTree {
    private String fullName;
    private Long num;
    private String enCode;
    private Long creatorTime;
    private Integer delFlag;
    private String creatorUser;
    private Long updateTime;
    private String updateUser;
    private Long sortCode;
}
