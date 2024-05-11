package com.linzen.integrate.service;

import com.linzen.base.service.SuperService;
import com.linzen.integrate.entity.IntegrateNodeEntity;

import java.util.List;

public interface IntegrateNodeService extends SuperService<IntegrateNodeEntity> {

    List<IntegrateNodeEntity> getList(List<String> id,String nodeCode);

    List<IntegrateNodeEntity> getList(List<String> id,String nodeCode, Integer isRetry);

    IntegrateNodeEntity getInfo(String id);

    void create(IntegrateNodeEntity entity);

    void update(String id,String nodeCode);

    Boolean update(String id, IntegrateNodeEntity entity);

    void delete(IntegrateNodeEntity entity);

    void delete(String id);
}
