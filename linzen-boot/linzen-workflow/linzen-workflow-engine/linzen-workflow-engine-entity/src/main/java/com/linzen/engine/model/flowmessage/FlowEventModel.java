package com.linzen.engine.model.flowmessage;

import com.linzen.engine.entity.FlowTaskOperatorRecordEntity;
import com.linzen.engine.model.flowengine.shuntjson.childnode.TemplateJsonModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowEventModel {

    //数据
    private String dataJson;
    //表单数据
    private Map<String, Object> data;
    //系统匹配
    private TemplateJsonModel templateJson;
    //操作对象
    private FlowTaskOperatorRecordEntity record;

}
