package com.linzen.integrate.model.nodeJson;

import com.linzen.base.UserInfo;
import com.linzen.integrate.entity.IntegrateEntity;
import com.linzen.integrate.entity.IntegrateNodeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP管理员/admin
 * @date 2023-04-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegrateChildNodeModel {
    private Map<String, Object> data = new HashMap<>();
    private List<Map<String, Object>> dataListAll = new ArrayList<>();
    private List<IntegrateNodeEntity> nodeList = new ArrayList<>();
    private String node;
    private IntegrateEntity entity;
    private String retryNodeCode;
    private UserInfo userInfo;

}
