package com.linzen.integrate.service;

import com.linzen.base.ServiceResult;
import com.linzen.base.service.SuperService;
import com.linzen.exception.WorkFlowException;
import com.linzen.integrate.entity.IntegrateEntity;
import com.linzen.integrate.model.integrate.IntegratePagination;

import java.util.List;

public interface IntegrateService extends SuperService<IntegrateEntity> {

    List<IntegrateEntity> getList(IntegratePagination pagination);

    List<IntegrateEntity> getList(IntegratePagination pagination, boolean isPage);

    IntegrateEntity getInfo(String id);

    Boolean isExistByFullName(String fullName, String id);

    Boolean isExistByEnCode(String encode, String id);

    void create(IntegrateEntity entity);

    ServiceResult ImportData(IntegrateEntity entity, Integer type) throws WorkFlowException;

    Boolean update(String id, IntegrateEntity entity,boolean state);

    void delete(IntegrateEntity entity);

}
