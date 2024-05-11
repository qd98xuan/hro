package com.linzen.integrate.model.integratetask;

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
public class IntegrateTaskInfo {
    private List<IntegrateTaskModel> list = new ArrayList<>();
    private String data;
}
