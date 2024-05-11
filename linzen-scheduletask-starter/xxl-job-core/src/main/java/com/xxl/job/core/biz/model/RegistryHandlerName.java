package com.xxl.job.core.biz.model;

import java.io.Serializable;
import java.util.List;

/**
 * 注册handler参数
 *
 * @author FHNP
 * @version: V3.1.0
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class RegistryHandlerName implements Serializable {
    /**
     * handler集合
     */
    private List<String> handlerList;
    /**
     * 执行器名称
     */
    private String executorName;

    /**
     * 调用方法
     */
    private String uri;

    public List<String> getHandlerList() {
        return handlerList;
    }

    public void setHandlerList(List<String> handlerList) {
        this.handlerList = handlerList;
    }

    public String getExecutorName() {
        return executorName;
    }

    public void setExecutorName(String executorName) {
        this.executorName = executorName;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
