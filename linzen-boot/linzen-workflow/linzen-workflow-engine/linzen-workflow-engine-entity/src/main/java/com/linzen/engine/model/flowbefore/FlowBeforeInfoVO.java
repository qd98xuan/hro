package com.linzen.engine.model.flowbefore;

import com.linzen.engine.model.flowengine.shuntjson.childnode.Properties;
import com.linzen.model.form.FlowFormVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FlowBeforeInfoVO {
    @Schema(description = "流程对象")
    private FlowTaskModel flowTaskInfo = new FlowTaskModel();
    @Schema(description = "流程节点")
    private List<FlowTaskNodeModel> flowTaskNodeList = new ArrayList<>();
    @Schema(description = "流程经办数据")
    private List<FlowTaskOperatorModel> flowTaskOperatorList = new ArrayList<>();
    @Schema(description = "流程已办数据")
    private List<FlowTaskOperatorRecordModel> flowTaskOperatorRecordList = new ArrayList<>();
    @Schema(description = "草稿数据")
    private Map<String, Object> draftData;
    @Schema(description = "流程json对象")
    private FlowTemplateModel flowTemplateInfo = new FlowTemplateModel();
    @Schema(description = "表单对象")
    private FlowFormVo flowFormInfo = new FlowFormVo();
    @Schema(description = "表单权限")
    private List<Map<String, Object>> formOperates = new ArrayList<>();
    @Schema(description = "表单审批权限")
    private Properties approversProperties = new Properties();
    @Schema(description = "表单数据")
    private Map<String, Object> formData = new HashMap<>();
    @Schema(description = "属性")
    private Boolean noOperateAuth = false;
}
