package com.linzen.scheduletask.model;

import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class TaskVO {
    private String fullName;
    private String enCode;
    private String runCount;
    private Long lastRunTime;
    private Long nextRunTime;
    private String description;
    private String id;
    private Integer enabledMark;
    private Long sortCode;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;
}
