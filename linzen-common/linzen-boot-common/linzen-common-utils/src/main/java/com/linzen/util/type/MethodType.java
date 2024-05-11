package com.linzen.util.type;

/**
 * 请求方法枚举类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public enum MethodType {
    /**
     * GET请求
     */
    GET("GET"),
    /**
     * POST 请求
     */
    POST("POST"),
    /**
     * PUT 请求
     */
    PUT("PUT"),;
    private String method;

    MethodType(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
