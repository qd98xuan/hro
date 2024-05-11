package com.linzen.permission.model.usergroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class GroupUpForm implements Serializable {

    /**
     * 名称
     **/
    @Schema(description = "名称")
    @NotBlank(message = "名称不能为空")
    private String fullName;

    /**
     * 编码
     **/
    @Schema(description = "编码")
    @NotBlank(message = "编码不能为空")
    private String enCode;

    /**
     * 说明
     **/
    @Schema(description = "说明")
    private String description;

    /**
     * 类型
     **/
    @Schema(description = "类型")
    @NotBlank(message = "类型不能为空")
    private String type;

    /**
     * 排序
     **/
    @Schema(description = "排序")
    private String sortCode;

    /**
     * 状态
     **/
    @Schema(description = "状态")
    private Integer enabledMark;

}
