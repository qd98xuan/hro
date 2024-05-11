package com.linzen.permission.model.organizeadministrator;

import com.linzen.util.treeutil.SumTree;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class ModuleSelectorModel extends SumTree implements Serializable {
    private String id;
    private String fullName;
    private String enCode;
    private String parentId;
    private String icon;
    private Integer type;
    private Long sortCode;
    private String category;
    private String propertyJson;

    private String systemId;
    private Boolean hasModule;

    @Schema(description = "是否有权限")
    private Integer isPermission;

    private boolean disabled;

    private long creatorTime;

//    private String description;
//    private Boolean isData;
//    private Integer delFlag;
//    private String urlAddress;
//    private String linkTarget;
//    private Integer isButtonAuthorize;
//    private Integer isColumnAuthorize;
//    private Integer isDataAuthorize;
//    private Integer isFormAuthorize;
}
