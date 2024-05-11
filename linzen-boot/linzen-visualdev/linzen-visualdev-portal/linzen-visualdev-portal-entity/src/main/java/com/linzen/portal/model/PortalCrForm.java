package com.linzen.portal.model;


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
@Schema(description="门户创建表单")
public class PortalCrForm  {

     @Schema(description = "名称")
     private String fullName;
     @Schema(description = "编码")
     private String enCode;
     @Schema(description = "是否启用")
     private Integer delFlag;
     @Schema(description = "描述")
     private String description;
     @Schema(description = "表单数据json")
     private String formData;
     @Schema(description = "分类")
     private String category;
     @Schema(description = "排序")
     private Long sortCode;
     @Schema(description = "类型：0-门户设计,1-配置路径")
     private Integer type;
     @Schema(description = "配置路径")
     private String customUrl;
     @Schema(description = "链接路径")
     private String appCustomUrl;
     @Schema(description = "链接类型")
     private Integer linkType;
     @Schema(description = "锁定开关0-未锁定，1-锁定")
     private Integer enabledLock;

}
