package com.linzen.engine.model.flowengine.shuntjson.childnode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 解析引擎
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class RuleListModel {
    /**
     * 父字段
     **/
    @Schema(description = "父字段")
    private String parentField;
    /**
     * 子字段
     **/
    @Schema(description = "子字段")
    private String childField;
}
