package com.linzen.scheduletask.model;

import com.linzen.base.UserInfo;
import lombok.Data;

import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ContentNewModel {
    /**
     * 表达式设置
     */
    private String cron;
    /**
     * 数据接口Id
     */
    private String interfaceId;
    /**
     * 数据接口名称
     */
    private String interfaceName;
    /**
     * 本地任务Id
     */
    private String localHostTaskId;

//    /**
//     * 租户id
//     */
//    private String tenantId;
//
//    /**
//     * 租户库
//     */
//    private String tenantDbConnectionString;

    /**
     * 用户信息
     */
    private UserInfo userInfo;

    /**
     * token
     */
    private String token;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    private String executeType;

    /**
     * 请求参数
     */
    private List<TaskParameterModel> parameter;

}
