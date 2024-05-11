package com.linzen.engine.model.flowengine.shuntjson.nodejson;

import lombok.Data;

import java.util.Date;

/**
 * 解析引擎
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DateProperties {

    /**
     * 定时器
     **/
    private String title;
    private Integer day = 0;
    private Integer hour = 0;
    private Integer minute = 0;
    private Integer second = 0;
    /**
     * 判断是否有定时器
     **/
    private Boolean time = false;
    /**
     * 定时器id
     **/
    private String nodeId;
    /**
     * 定时器下一节点
     **/
    private String nextId;
    /**
     * 定时任务结束时间
     **/
    private Date date;

}
