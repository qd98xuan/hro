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
public class MessageNoticeVO {
    @Schema(description = "id")
    private String id;
    @Schema(description = "标题")
    private String title;

    @Schema(description = "创建人员")
    private String creatorUser;

    @Schema(description = "发布时间",example = "1")
    private Long updateTime;

    @Schema(description = "状态(0-存草稿，1-已发布，2-已过期)",example = "1")
    private Integer enabledMark;

    @Schema(description = "创建时间")
    private Long creatorTime;

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

    @Schema(description = "类型")
    private Long expirationTime;

    @Schema(description = "摘要")
    private String excerpt;

    /**
     * 分类 1-公告 2-通知
     */
    @Schema(description = "分类")
    private String category;
}
