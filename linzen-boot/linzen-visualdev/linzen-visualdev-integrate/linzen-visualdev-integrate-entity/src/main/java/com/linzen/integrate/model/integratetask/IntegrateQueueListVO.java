package com.linzen.integrate.model.integratetask;

import lombok.Data;

/**
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP管理员/admin
 * @date 2023-04-01
 */
@Data
public class IntegrateQueueListVO {
    private String fullName;
    private Integer state;
    private Long executionTime;
}
