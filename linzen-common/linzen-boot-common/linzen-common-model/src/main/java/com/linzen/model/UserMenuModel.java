package com.linzen.model;

import com.linzen.util.treeutil.SumTree;
import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

/**
 * 用户DTO
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class UserMenuModel extends SumTree<T> {
    private String id;
    private String fullName;
    private Integer isButtonAuthorize;
    private Integer isColumnAuthorize;
    private Integer isDataAuthorize;
    private Integer isFormAuthorize;
    private String enCode;
    private String parentId;
    private String icon;
    private String urlAddress;
    private String linkTarget;
    private Integer type;
    private Boolean isData;
    private Integer delFlag;
    private Long sortCode;
    private String category;
    private String description;
    private String propertyJson;

    private String systemId;
    private Boolean hasModule;
    private Long creatorTime;
}
