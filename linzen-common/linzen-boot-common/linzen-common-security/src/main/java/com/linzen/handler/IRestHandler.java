package com.linzen.handler;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.util.Map;

public interface IRestHandler {

    /**
     * 是否处理Header
     */
    default boolean supportHeader(){
        return false;
    }

    /**
     * 是否处理Form表单数据
     */
    default boolean supportParameter(){
        return false;
    }

    /**
     * 是否处理Body JSON
     */
    default boolean supportBodyJson(){
        return false;
    }

    /**
     * 是否处理返回结果
     */
    default boolean supportResponse(){
        return false;
    }

    /**
     * 初始化Body JSON
     */
    default JSONObject initBodyJson(JSONObject jsonContent){
        return jsonContent;
    }

    /**
     * 初始化Form表单数据
     */
    default Map<String, String[]> initParameter(Map<String, String[]> parameter){
        return parameter;
    }

    /**
     * 处理返回结果
     */
    default String processResponse(String data) {
        return data;
    }

}
