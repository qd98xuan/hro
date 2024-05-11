package com.linzen.base.model.datainterface;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 自定义参数模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DataInterfaceModel extends ParamModel implements Serializable {

    /**
     * 是否为空（0允许，1不允许）
     */
    @Schema(description = "是否为空（0允许，1不允许）")
    private Integer required;

}
