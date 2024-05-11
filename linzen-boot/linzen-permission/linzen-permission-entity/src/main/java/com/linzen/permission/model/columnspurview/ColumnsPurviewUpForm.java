package com.linzen.permission.model.columnspurview;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 列表权限修改模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ColumnsPurviewUpForm implements Serializable {

    @Schema(description = "列表字段数组")
    private String fieldList;

    @Schema(description = "模块ID")
    @NotBlank(message = "操作模块不能为空")
    private String moduleId;
}
