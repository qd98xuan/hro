package com.linzen.scheduletask.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
@Data
@TableName("xxl_job_group")
public class XxlJobGroup {

    @TableId(type = IdType.ASSIGN_ID)
	private String id;

    @TableField("APP_NAME")
    private String appname;

    @TableField("TITLE")
    private String title;

    @TableField("ADDRESS_TYPE")
    private int addressType;

    // 执行器地址类型：0=自动注册、1=手动录入
    @TableField("ADDRESS_LIST")
    private String addressList;     // 执行器地址列表，多地址逗号分隔(手动录入)

    @TableField("UPDATE_TIME")
    private Date updateTime;

    // registry list
    @TableField(exist = false)
    private List<String> registryList;  // 执行器地址列表(系统注册)

    public List<String> getRegistryList() {
        if (addressList!=null && addressList.trim().length()>0) {
            registryList = new ArrayList<String>(Arrays.asList(addressList.split(",")));
        }
        return registryList;
    }

}
