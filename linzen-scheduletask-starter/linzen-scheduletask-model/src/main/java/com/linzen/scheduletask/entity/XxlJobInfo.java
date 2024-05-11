package com.linzen.scheduletask.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * xxl-job info
 *
 * @author FHNP
 */
@Data
@TableName("xxl_job_info")
public class XxlJobInfo {

	@TableId(type = IdType.ASSIGN_ID)
	private String id;				// 主键ID

	@TableField("JOB_GROUP")
	private String jobGroup;		// 执行器主键ID

	@TableField("JOB_DESC")
	private String jobDesc;

	@TableField("ADD_TIME")
	private Date addTime;

	@TableField("UPDATE_TIME")
	private Date updateTime;

	@TableField("AUTHOR")
	private String author;		// 负责人
	@TableField("ALARM_EMAIL")
	private String alarmEmail;	// 报警邮件

	@TableField("SCHEDULE_TYPE")
	private String scheduleType;			// 调度类型
	@TableField("SCHEDULE_CONF")
	private String scheduleConf;			// 调度配置，值含义取决于调度类型
	@TableField("MISFIRE_STRATEGY")
	private String misfireStrategy;			// 调度过期策略

	@TableField("EXECUTOR_ROUTE_STRATEGY")
	private String executorRouteStrategy;	// 执行器路由策略
	@TableField("EXECUTOR_HANDLER")
	private String executorHandler;		    // 执行器，任务Handler名称
	@TableField("EXECUTOR_PARAM")
	private String executorParam;		    // 执行器，任务参数
	@TableField("EXECUTOR_BLOCK_STRATEGY")
	private String executorBlockStrategy;	// 阻塞处理策略
	@TableField("EXECUTOR_TIMEOUT")
	private int executorTimeout;     		// 任务执行超时时间，单位秒
	@TableField("EXECUTOR_FAIL_RETRY_COUNT")
	private int executorFailRetryCount;		// 失败重试次数

	@TableField("GLUE_TYPE")
	private String glueType;		// GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
	@TableField("GLUE_SOURCE")
	private String glueSource;		// GLUE源代码
	@TableField("GLUE_REMARK")
	private String glueRemark;		// GLUE备注
	@TableField("GLUE_UPDATETIME")
	private Date glueUpdatetime;	// GLUE更新时间

	@TableField("CHILD_JOBID")
	private String childJobId;		// 子任务ID，多个逗号分隔

	@TableField("TRIGGER_STATUS")
	private int triggerStatus;		// 调度状态：0-停止，1-运行
	@TableField("TRIGGER_LAST_TIME")
	private long triggerLastTime;	// 上次调度时间
	@TableField("TRIGGER_NEXT_TIME")
	private long triggerNextTime;	// 下次调度时间

	/**
	 * 租户编码
	 * @return
	 */
	@TableField("TENANTID")
	private String tenantId;
	/**
	 * 租户编码
	 * @return
	 */
	@TableField("TASKID")
	private String taskId;
}
