package com.linzen.permission.model.user.mod;

import com.linzen.util.treeutil.SumTree;
import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class UserAllModel extends SumTree {
    private String id;
    private String account;
    private String gender;
    private String realName;
    private String headIcon;
    private String department;
    private String departmentId;
    private String organizeId;
    private String organize;
    private String roleId;
    private String roleName;
    private String positionId;
    private String positionName;
    private String managerId;
    private String managerName;
    private String quickQuery;
    private String portalId;
    private Integer isAdministrator;
}
