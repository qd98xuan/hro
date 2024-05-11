package com.linzen.scheduletask.model;

import com.linzen.base.Pagination;
import lombok.Data;

/**
 *
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class TaskPage extends Pagination {
    private Integer runResult;

    private Long startTime;
    private Long endTime;
}
