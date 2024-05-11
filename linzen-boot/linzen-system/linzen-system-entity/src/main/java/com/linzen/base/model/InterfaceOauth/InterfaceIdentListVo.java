package com.linzen.base.model.InterfaceOauth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 接口认证列表对象
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class InterfaceIdentListVo {
    @Schema(description = "id")
    private String id;

    @Schema(description = "应用id")
    private String appId;

    @Schema(description = "应用名称")
    private String appName;

    @Schema(description = "使用期限")
    private Date usefulLife;

    @Schema(description = "创建人id")
    private String creatorUserId;

    @Schema(description = "创建人")
    private String creatorUser;

    @Schema(description = "创建时间")
    private Long creatorTime;

    @Schema(description = "修改时间")
    private Long updateTime;

    @Schema(description = "排序")
    private Long sortCode;

    @Schema(description = "状态")
    private Integer enabledMark;

    @Schema(description = "租户id")
    private String tenantId;

    @Schema(description = "绑定接口")
    private String dataInterfaceIds;
}
