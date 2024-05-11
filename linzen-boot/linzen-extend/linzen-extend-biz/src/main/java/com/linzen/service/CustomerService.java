package com.linzen.service;

import com.linzen.base.Pagination;
import com.linzen.base.service.SuperService;
import com.linzen.entity.CustomerEntity;

import java.util.List;

/**
 *
 * 客户信息
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
public interface CustomerService extends SuperService<CustomerEntity> {

    List<CustomerEntity> getList(Pagination pagination);

    CustomerEntity getInfo(String id);

    void delete(CustomerEntity entity);

    void create(CustomerEntity entity);

    boolean update( String id, CustomerEntity entity);

}
