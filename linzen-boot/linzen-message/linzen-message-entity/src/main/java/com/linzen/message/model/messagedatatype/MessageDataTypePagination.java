package com.linzen.message.model.messagedatatype;


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
public class MessageDataTypePagination extends Pagination {

    @Schema(description = "selectKey")
    private String selectKey;

    @Schema(description = "json")
    private String json;

    @Schema(description = "数据类型")
    private String dataType;

    @Schema(description = "特殊查询json")
    private String superQueryJson;


    /**
     * 数据名称
     */
    @Schema(description = "数据名称")
    private String name;

    /**
     * 数据编码
     */
    @Schema(description = "数据编码")
    private String code;
    /**
     * 菜单id
     */
    @Schema(description = "菜单id")
    private String menuId;
}