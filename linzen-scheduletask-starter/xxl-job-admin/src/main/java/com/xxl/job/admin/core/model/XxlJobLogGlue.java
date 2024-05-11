package com.xxl.job.admin.core.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * xxl-job log for glue, used to track job code process
 * @author FHNP
 */
@Data
@TableName("xxl_job_logglue")
public class XxlJobLogGlue {
	@TableId(type = IdType.ASSIGN_ID)
	private String id;
	@TableField("JOB_ID")
	private String jobId;				// 任务主键ID
	@TableField("GLUE_TYPE")
	private String glueType;		// GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
	@TableField("GLUE_SOURCE")
	private String glueSource;
	@TableField("GLUE_REMARK")
	private String glueRemark;
	@TableField("ADD_TIME")
	private Date addTime;
	@TableField("UPDATE_TIME")
	private Date updateTime;

}
