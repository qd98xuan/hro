package com.linzen.engine.model.flowengine.shuntjson.childnode;

import com.linzen.engine.util.FlowNature;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 解析引擎
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Data
public class CounterSign {
    /**
     * 通过类型 0.无 1.百分比 2.人数
     */
    @Schema(description = "通过类型")
    private Integer auditType = FlowNature.RejectPercent;
    /**
     * 通过百分比
     */
    @Schema(description = "通过百分比")
    private Integer auditRatio = 100;
    /**
     * 通过人数
     */
    @Schema(description = "通过人数")
    private Integer auditNum = 1;
    /**
     * 拒绝类型 0.无 1.百分比 2.人数
     */
    @Schema(description = "拒绝类型")
    private Integer rejectType = FlowNature.RejectNo;
    /**
     * 拒绝百分比
     */
    @Schema(description = "拒绝百分比")
    private Integer rejectRatio = 10;
    /**
     * 拒绝人数
     */
    @Schema(description = "拒绝人数")
    private Integer rejectNum = 1;
}
