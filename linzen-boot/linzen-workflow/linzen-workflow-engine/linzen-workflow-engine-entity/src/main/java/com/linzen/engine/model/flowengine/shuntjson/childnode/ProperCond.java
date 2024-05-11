package com.linzen.engine.model.flowengine.shuntjson.childnode;

import com.linzen.emnus.SearchMethodEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析引擎
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ProperCond {
    @Schema(description = "表达式")
    private String logic = SearchMethodEnum.And.getSymbol();
    @Schema(description = "条件")
    private List<GroupsModel> groups = new ArrayList<>();
}
