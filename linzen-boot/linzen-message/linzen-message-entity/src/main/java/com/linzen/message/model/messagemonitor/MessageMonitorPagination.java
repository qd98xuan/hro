package com.linzen.message.model.messagemonitor;


import com.linzen.base.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class MessageMonitorPagination extends Pagination {

    @Schema(description = "selectKey")
    private String selectKey;

    @Schema(description = "json")
    private String json;

    @Schema(description = "数据类型")
    private String dataType;

    @Schema(description = "特殊查询json")
    private String superQueryJson;


    /**
     * 消息来源
     */
    @Schema(description = "消息来源")
    private String messageSource;

    /**
     * 消息类型
     */
    @Schema(description = "消息类型")
    private String messageType;

    /**
     * 关键词
     */
    @Schema(description = "关键词")
    private String keyword;

    /**
     * 发送时间（开始时间）
     */
    @Schema(description = "发送时间")
    private Long startTime;

    /**
     * 接收时间
     */
    @Schema(description = "接收时间")
    private Long endTime;
    /**
     * 菜单id
     */
    @Schema(description = "菜单id")
    private String menuId;
}