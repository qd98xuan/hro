package com.linzen.integrate.service;

import com.linzen.base.service.SuperService;
import com.linzen.integrate.entity.IntegrateQueueEntity;

import java.util.List;

public interface IntegrateQueueService extends SuperService<IntegrateQueueEntity> {

    List<IntegrateQueueEntity> getList();

    void create(IntegrateQueueEntity entity);

    Boolean update(String id, IntegrateQueueEntity entity);

    void delete(IntegrateQueueEntity entity);

    void delete(String integrateId);

}
