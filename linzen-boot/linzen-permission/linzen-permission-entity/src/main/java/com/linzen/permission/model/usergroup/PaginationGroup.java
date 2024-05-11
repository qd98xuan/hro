package com.linzen.permission.model.usergroup;

import com.linzen.base.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户分组管理列表返回
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PaginationGroup extends Pagination implements Serializable {
    @Schema(description = "状态")
    private Integer enabledMark;
    @Schema(description = "类型")
    private String type;
}
