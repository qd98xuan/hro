package com.linzen.integrate.model.nodeJson;

import com.linzen.base.UserInfo;
import lombok.Data;

/**
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP管理员/admin
 * @date 2023-04-01
 */
@Data
public class IntegrateModel {
    private UserInfo userInfo;
    private String id;
    private String cron;
    private Long startTime = System.currentTimeMillis();
    private Long endTime = System.currentTimeMillis();
    private Integer endTimeType = 1;
    private Integer endLimit = 1;
    private Integer num = 0;
    private Integer state = 0;
    private Long time = System.currentTimeMillis();
}
