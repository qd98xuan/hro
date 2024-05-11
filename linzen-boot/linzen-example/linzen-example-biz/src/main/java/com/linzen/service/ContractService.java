package com.linzen.service;

import com.linzen.base.Pagination;
import com.linzen.base.service.SuperService;
import com.linzen.entity.ContractEntity;

import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface ContractService extends SuperService<ContractEntity> {

    List<ContractEntity> getlist(Pagination pagination);

    ContractEntity getInfo(String id);

    void create(ContractEntity entity);

    void update(String id, ContractEntity entity);

    void delete(ContractEntity entity);
}
