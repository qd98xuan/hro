package com.linzen.message.model.messagetemplateconfig;


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
public class MessageTemplateConfigPagination extends Pagination {

    @Schema(description = "selectKey")
    private String selectKey;

    @Schema(description = "json")
    private String json;

    @Schema(description = "数据类型")
    private String dataType;

    @Schema(description = "特殊查询json")
    private String superQueryJson;



    /**
     * 模板类型
     */
    @Schema(description = "模板类型")
    private String templateType;

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
     * 状态
     */
    @Schema(description = "状态")
    private String enabledMark;

    /**
     * 消息来源
     */
    @Schema(description = "消息来源")
    private String messageSource;

    /**
     * 菜单id
     */
    @Schema(description = "菜单id")
    private String menuId;
}