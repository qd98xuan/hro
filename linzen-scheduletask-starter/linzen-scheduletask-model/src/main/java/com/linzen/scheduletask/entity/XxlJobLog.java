package com.linzen.scheduletask.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * xxl-job log, used to track trigger process
 * @author FHNP
 */
@Data
@TableName("xxl_job_log")
public class XxlJobLog {

	@TableId(type = IdType.ASSIGN_ID)
	private String id;
	
	// job info
	@TableField("JOB_GROUP")
	private String jobGroup;
	@TableField("JOB_ID")
	private String jobId;

	// execute info
	@TableField("EXECUTOR_ADDRESS")
	private String executorAddress;
	@TableField("EXECUTOR_HANDLER")
	private String executorHandler;
	@TableField("EXECUTOR_PARAM")
	private String executorParam;
	@TableField("EXECUTOR_SHARDING_PARAM")
	private String executorShardingParam;
	@TableField("EXECUTOR_FAIL_RETRY_COUNT")
	private int executorFailRetryCount;
	
	// trigger info
	@TableField("TRIGGER_TIME")
	private Date triggerTime;
	@TableField("TRIGGER_CODE")
	private int triggerCode;
	@TableField("TRIGGER_MSG")
	private String triggerMsg;
	
	// handle info
	@TableField("HANDLE_TIME")
	private Date handleTime;
	@TableField("HANDLE_CODE")
	private int handleCode;
	@TableField("HANDLE_MSG")
	private String handleMsg;

	// alarm info
	@TableField("ALARM_STATUS")
	private int alarmStatus;

}
