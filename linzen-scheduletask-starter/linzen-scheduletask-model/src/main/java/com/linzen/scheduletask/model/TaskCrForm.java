package com.linzen.scheduletask.model;

import com.linzen.base.UserInfo;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class TaskCrForm {
    private String id;
    @NotBlank(message = "必填")
    private String fullName;
    @NotBlank(message = "必填")
    private String executeType;
    private String description;
    @NotBlank(message = "必填")
    private String executeContent;
    private long sortCode;
    private String enCode;

    private Integer enabledMark;

    private UserInfo userInfo;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;
}
