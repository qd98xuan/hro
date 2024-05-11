package com.linzen.base.model.cachemanage;

import com.linzen.base.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PaginationCacheManage extends Page {
    @Schema(description = "开始时间")
    private Long overdueStartTime;
    @Schema(description = "结束时间")
    private Long overdueEndTime;
}
