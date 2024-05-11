package com.linzen.scheduletask.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 定时任务记录
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("base_timetasklog")
public class TimeTaskLogEntity {
    /**
     * 执行任务主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 定时任务主键
     */
    @TableField("F_TASKID")
    private String taskId;

    /**
     * 执行时间
     */
    @TableField("F_RUNTIME")
    private Date runTime;

    /**
     * 执行结果
     */
    @TableField("F_RUNRESULT")
    private Integer runResult;

    /**
     * 执行说明
     */
    @TableField("F_DESCRIPTION")
    private String description;
}
