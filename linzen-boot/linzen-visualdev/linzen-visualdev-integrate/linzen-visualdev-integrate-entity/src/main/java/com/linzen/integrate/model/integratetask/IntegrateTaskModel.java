package com.linzen.integrate.model.integratetask;

import lombok.Data;

/**
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP管理员/admin
 * @date 2023-04-01
 */
@Data
public class IntegrateTaskModel {
    private String taskId;
    private String nodeCode;
    private String nodeType;
    private String nodeName;
    private Integer resultType;
    private String errorMsg;
    private Long startTime;
    private Long endTime;
    private String parentId;
    private Boolean isRetry;
    private Integer type;
    private String id;
}
