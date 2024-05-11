package com.linzen.permission.model.userrelation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class UserRelationForm {

   @Schema(description = "对象类型")
   private String objectType;

   @Schema(description = "用户id")
   private List<String> userIds;
}
