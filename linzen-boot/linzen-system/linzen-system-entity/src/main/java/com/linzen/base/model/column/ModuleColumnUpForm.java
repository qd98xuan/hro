package com.linzen.base.model.column;

import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ModuleColumnUpForm {
    private String creatorUserId;

    private Integer delFlag;

    private String fullName;

    private String description;

    private Long sortCode;

    private String enCode;

    private String creatorTime;

    private String moduleId;

    private String bindTable;

    private String bindTableName;

    private Integer fieldRule;

    private String tableName;

    private String childTableKey;
}
