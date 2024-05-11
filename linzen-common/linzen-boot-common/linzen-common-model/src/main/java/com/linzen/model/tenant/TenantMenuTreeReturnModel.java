package com.linzen.model.tenant;

import lombok.Data;

import java.util.List;

/**
 * 租户菜单树
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class TenantMenuTreeReturnModel {
    private String fullName;
    private String icon;
    private Integer type;
    private Long sortCode;
    private String category;
    private boolean disabled;
    private String id;
    private String parentId;
    private Boolean hasChildren;
    private String urlAddress;
    private List<TenantMenuTreeReturnModel> children;
}
