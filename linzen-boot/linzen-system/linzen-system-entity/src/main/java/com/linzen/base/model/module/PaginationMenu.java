package com.linzen.base.model.module;

import com.linzen.base.Page;
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
public class PaginationMenu extends Page {
   @Schema(description = "分类")
   private String category;
   @Schema(description = "状态")
   private Integer enabledMark;
   @Schema(description = "类型")
   private Integer type;
}
