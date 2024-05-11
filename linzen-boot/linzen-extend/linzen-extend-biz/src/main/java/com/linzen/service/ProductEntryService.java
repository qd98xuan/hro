package com.linzen.service;

import com.linzen.base.Pagination;
import com.linzen.base.service.SuperService;
import com.linzen.entity.ProductEntryEntity;

import java.util.List;

/**
 *
 * 销售订单明细
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
public interface ProductEntryService extends SuperService<ProductEntryEntity> {

    List<ProductEntryEntity> getProductentryEntityList(String id);

    List<ProductEntryEntity> getProductentryEntityList(Pagination pagination);
}
