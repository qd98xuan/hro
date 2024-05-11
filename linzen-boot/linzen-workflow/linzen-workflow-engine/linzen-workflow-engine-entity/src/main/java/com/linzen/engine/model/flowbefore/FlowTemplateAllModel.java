package com.linzen.engine.model.flowbefore;

import com.linzen.engine.entity.FlowTemplateEntity;
import com.linzen.engine.entity.FlowTemplateJsonEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FlowTemplateAllModel {
    private FlowTemplateJsonEntity templateJson = new FlowTemplateJsonEntity();
    private FlowTemplateEntity template = new FlowTemplateEntity();
    private List<FlowTemplateJsonEntity> templateJsonList = new ArrayList<>();
}
