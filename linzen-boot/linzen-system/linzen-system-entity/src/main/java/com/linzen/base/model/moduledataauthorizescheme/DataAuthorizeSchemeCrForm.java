package com.linzen.base.model.moduledataauthorizescheme;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DataAuthorizeSchemeCrForm {
    @NotBlank(message = "方案名称不能为空")
    private String fullName;

    private Object conditionJson;

    private String conditionText;

    private String moduleId;

    @NotBlank(message = "方案编码不能为空")
    private String enCode;

    /**
     * 全部数据标识
     */
    private Integer allData;
    /**
     * 分组匹配逻辑
     */
    private String matchLogic;
}
