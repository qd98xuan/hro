package com.linzen.service;

import com.linzen.base.service.SuperService;
import com.linzen.entity.ProductEntity;
import com.linzen.entity.ProductEntryEntity;
import com.linzen.exception.DataBaseException;
import com.linzen.model.product.ProductPagination;

import java.util.List;

/**
 * 销售订单
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
public interface ProductService extends SuperService<ProductEntity> {

    List<ProductEntity> getList(ProductPagination productPagination);

    ProductEntity getInfo(String id);

    void delete(ProductEntity entity);

    void create(ProductEntity entity, List<ProductEntryEntity> productEntryList ) throws DataBaseException;

    boolean update(String id, ProductEntity entity, List<ProductEntryEntity> productEntryList );

}
