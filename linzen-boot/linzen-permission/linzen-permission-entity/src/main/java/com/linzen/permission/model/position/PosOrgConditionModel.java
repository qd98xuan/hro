package com.linzen.permission.model.position;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PosOrgConditionModel extends PosOrgModel {

    private String organizeIdTree;

    private String organizeId;

    @Schema(description ="前端解析唯一标识")
    private String onlyId;

}
