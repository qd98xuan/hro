package com.linzen.integrate.service;

import com.linzen.base.service.SuperService;
import com.linzen.integrate.entity.IntegrateTaskEntity;
import com.linzen.integrate.model.integrate.IntegratePageModel;

import java.util.List;

public interface IntegrateTaskService extends SuperService<IntegrateTaskEntity> {

    List<IntegrateTaskEntity> getList(IntegratePageModel pagination);

    List<IntegrateTaskEntity> getList(List<String> id);

    IntegrateTaskEntity getInfo(String id);

    void create(IntegrateTaskEntity entity);

    Boolean update(String id, IntegrateTaskEntity entity);

    void delete(IntegrateTaskEntity entity) ;
}
