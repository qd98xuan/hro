package com.linzen.permission.model.position;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linzen.base.Pagination;
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
public class PaginationPosition extends Pagination {
   @Schema(description = "组织id")
   private String organizeId;
   @Schema(description = "状态")
   private Integer enabledMark;
   @Schema(description = "类型")
   private String type;
   @JsonIgnore
   private String enCode;
}
