package com.linzen.model;

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
public class ErrorLogVO {
    @Schema(description = "创建用户")
    private String userName;
    @Schema(description = "创建时间",example = "1")
    private Long creatorTime;
    @Schema(description = "IP")
    private String ipAddress;
    private Long requestDuration;
    @Schema(description = "地点")
    private String ipAddressName;
    @Schema(description = "id")
    private String id;
    @Schema(description = "请求设备")
    private String platForm;
    @Schema(description = "浏览器")
    private String browser;
    @Schema(description = "请求类型")
    private String requestMethod;
    @Schema(description = "请求地址")
    private String requestUrl;
}
