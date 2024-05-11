package com.linzen.base.model.button;

import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ModuleButtonInfoVO {
    private String enCode;
    private Integer delFlag;
    private String fullName;
    private String icon;
    private String id;
    private String parentId;
    private String description;
    private String moduleId;
    private Long sortCode;
}
