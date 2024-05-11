package com.linzen.permission.model.position;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PositionConditionSelectorVO extends PositionSelectorVO implements Serializable {

    @Schema(description = "组织id树")
    @JsonIgnore
    private String organizeIdTree;

}
