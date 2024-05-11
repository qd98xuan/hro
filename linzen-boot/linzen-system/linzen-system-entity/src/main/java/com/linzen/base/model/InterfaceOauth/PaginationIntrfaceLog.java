package com.linzen.base.model.InterfaceOauth;

import com.linzen.base.PaginationTime;
import lombok.Data;

/**
 * 日志列表查询
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PaginationIntrfaceLog extends PaginationTime {
    private String keyword;
}
