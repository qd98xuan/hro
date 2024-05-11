package com.linzen.engine.model.flowengine.shuntjson.nodejson;

import com.linzen.engine.model.flowengine.shuntjson.childnode.Properties;
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
public class ChildNodeList {
    /**
     * 节点属性
     **/
    private Properties properties = new Properties();
    /**
     * 自定义属性
     **/
    private Custom custom = new Custom();
    /**
     * 流程节点id
     **/
    private String taskNodeId;
    /**
     * 流程任务id
     **/
    private String taskId;
    /**
     * 下一级定时器属性
     **/
    private DateProperties timer = new DateProperties();
    /**
     * 分流合流
     **/
    private String conditionType;
}
