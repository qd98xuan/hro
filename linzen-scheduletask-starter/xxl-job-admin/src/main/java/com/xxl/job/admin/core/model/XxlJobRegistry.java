package com.xxl.job.admin.core.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Created by xuxueli on 16/9/30.
 */
@Data
@TableName("xxl_job_registry")
public class XxlJobRegistry {

    @TableId(type = IdType.ASSIGN_ID)
	private String id;
    @TableField("REGISTRY_GROUP")
    private String registryGroup;
    @TableField("REGISTRY_KEY")
    private String registryKey;
    @TableField("REGISTRY_VALUE")
    private String registryValue;
    @TableField("UPDATE_TIME")
    private Date updateTime;
    
}
