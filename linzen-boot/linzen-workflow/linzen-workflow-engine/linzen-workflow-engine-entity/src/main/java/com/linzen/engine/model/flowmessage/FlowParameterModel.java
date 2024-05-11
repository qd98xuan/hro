package com.linzen.engine.model.flowmessage;

import lombok.Data;

import java.util.Map;

/**
 * 事件对象
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FlowParameterModel {
    private String interId;
    private Map<String, String> parameterMap;
}
