package com.linzen.permission.model.user.page;

import com.linzen.base.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 通过组织id或关键字查询
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PageUser extends Page implements Serializable {
    @Schema(description = "组织id")
    private String organizeId;
}
