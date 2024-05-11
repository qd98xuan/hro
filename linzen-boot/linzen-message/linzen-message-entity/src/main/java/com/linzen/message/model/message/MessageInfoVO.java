package com.linzen.message.model.message;

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
public class MessageInfoVO {

    @Schema(description = "主键")
    private String id;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "类型")
    private Integer type;
    @Schema(description = "修改时间")
    private long updateTime;
    @Schema(description = "创建用户")
    private String creatorUser;
    @Schema(description = "是否已读")
    private Integer isRead;

    @Schema(description = "有效标志")
    private Integer enabledMark;

    /**
     * 发布人员
     */
    @Schema(description = "发布人员")
    private String releaseUser;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间")
    private Long releaseTime;

    @Schema(description = "修改用户")
    private String updateUserId;
    @Schema(description = "流程类型")
    private Integer flowType;
}
