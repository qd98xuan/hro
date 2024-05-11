package com.linzen.integrate.model.childnode;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP管理员/admin
 * @date 2023-04-01
 */
@Data
public class IntegrateTemplateModel {
    //远端接口
    private String field;
    private Boolean required = false;
    private String sourceType;
    private String relationField;


    //发送配置
    private String templateId;
    private String sendConfigId;
    private String msgTemplateName;
    private List<IntegrateParamModel> paramJson = new ArrayList<>();
}
