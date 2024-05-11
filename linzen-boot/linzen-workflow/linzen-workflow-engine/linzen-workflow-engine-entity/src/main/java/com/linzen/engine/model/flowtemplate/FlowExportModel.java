package com.linzen.engine.model.flowtemplate;

import com.linzen.engine.entity.FlowTemplateEntity;
import com.linzen.engine.entity.FlowTemplateJsonEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程导出用到的实体
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FlowExportModel {

    /**
     * 流程模板
     */
    private FlowTemplateEntity flowTemplate;

    /**
     * 流程模板实例
     */
    private List<FlowTemplateJsonEntity> flowTemplateJson = new ArrayList<>();

}
