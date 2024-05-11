package com.linzen.permission.model.organize;

import com.linzen.base.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class OrganizeConditionModel extends Page implements Serializable {

    @Schema(description = "部门id集合")
    private List<String> departIds;

    private Map<String, String> orgIdNameMaps;

}
