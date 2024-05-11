package com.linzen.permission.model.user.page;

import com.linzen.base.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PaginationUser extends Pagination {

    @Schema(description = "组织id")
    private String organizeId;

    @Schema(description = "角色id")
    private String roleId;

    @Schema(description = "状态")
    private Integer enabledMark;

    @Schema(description = "性别")
    private String gender;

}
