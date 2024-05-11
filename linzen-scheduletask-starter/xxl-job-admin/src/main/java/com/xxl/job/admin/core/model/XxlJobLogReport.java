package com.xxl.job.admin.core.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("xxl_job_log_report")
public class XxlJobLogReport {

    @TableId(type = IdType.ASSIGN_ID)
	private String id;

    @TableField(value = "TRIGGER_DAY")
    private Date triggerDay;


    @TableField(value = "RUNNING_COUNT")
    private int runningCount;

    @TableField(value = "SUC_COUNT")
    private int sucCount;

    @TableField(value = "FAIL_COUNT")
    private int failCount;

    @TableField(value = "UPDATE_TIME")
    private Date updateTime;

}
