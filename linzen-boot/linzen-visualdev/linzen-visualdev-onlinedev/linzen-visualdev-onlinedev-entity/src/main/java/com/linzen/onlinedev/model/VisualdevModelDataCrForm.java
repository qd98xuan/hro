package com.linzen.onlinedev.model;


import com.linzen.engine.model.flowengine.FlowModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 *
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @author FHNP
 * @date 2023-04-01
 */
@Data
@Schema(description="功能数据创建表单")
public class VisualdevModelDataCrForm extends FlowModel {
    @Schema(description = "数据内容")
    private String data;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "流程候选人列表")
    private Map<String, List<String>> candidateList;
    @Schema(description = "流程紧急度")
    private Integer flowUrgent = 1;
    @Schema(description = "是否外链")
    private Boolean isLink = false;
}
