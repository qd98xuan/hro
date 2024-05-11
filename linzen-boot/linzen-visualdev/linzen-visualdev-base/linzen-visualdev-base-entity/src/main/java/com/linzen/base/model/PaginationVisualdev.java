package com.linzen.base.model;


import com.linzen.base.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @author FHNP
 * @date 2023-04-01
 */
@Data
@Schema(description="列表查询参数")
public class PaginationVisualdev extends Pagination {
   @Schema(description = "类型")
   private Integer type=1;
   @Schema(description = "关键字")
   private String keyword;
   /**
    *0-在线开发(无表)，1-表单设计(有表)
    */
   @Schema(description = "模型分类：0-在线开发(无表)，1-表单设计(有表)")
   private String  model="0";

   @Schema(description = "表单类型：1-表单，2-列表，4-视图")
   private Integer webType;

   @Schema(description = "表单类型：0-普通表单，1-流程表单")
   private Integer enableFlow;
   /**
    * 分类
    */
   @Schema(description = "类别：字典分类")
   private String category;

   @Schema(description = "状态：0-未发布，1-已发布，2-已修改")
   private Integer isRelease;
}
