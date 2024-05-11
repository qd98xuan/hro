package com.linzen.base.model.cachemanage;

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
public class CacheManageListVO {
    @Schema(description = "名称")
    private String name;
    @Schema(description = "过期时间",example = "1")
    private Long overdueTime;
    @Schema(description = "大小")
    private Integer cacheSize;
}
