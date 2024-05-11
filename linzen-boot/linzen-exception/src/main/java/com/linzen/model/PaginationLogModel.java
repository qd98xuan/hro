package com.linzen.model;

import com.linzen.base.PaginationTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 日志分页参数
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PaginationLogModel extends PaginationTime {
    /**
     * 操作类型
     */
    private String requestMethod;
    /**
     * 类型
     */
    private int category;
    @Schema(description = "是否登录成功标志")
    private Integer loginMark;
    @Schema(description = "登录类型")
    private Integer loginType;

}
