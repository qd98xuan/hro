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
public class IntegrateMsgModel {
    //0.关闭  1.自定义  3.默认
    private Integer on = 0;
    private String msgId;
    private List<IntegrateTemplateModel> templateJson = new ArrayList<>();
}
