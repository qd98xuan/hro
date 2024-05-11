package com.linzen.engine.model.flowtime;

import com.linzen.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import lombok.Data;

import java.util.Date;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FlowTimeModel {
    /**
     * 是否开启限时设置
     */
    private Boolean on = false;
    /**
     * 开始时间
     */
    private Date date = new Date();
    /**
     * 通知
     */
    private ChildNodeList childNode = new ChildNodeList();
    /**
     * 事件
     */
    private ChildNodeList childNodeEvnet = new ChildNodeList();
    /**
     * 节点对象
     */
    private ChildNodeList childNodeList = new ChildNodeList();
}
