package com.linzen.service;

import com.linzen.base.service.SuperService;
import com.linzen.entity.ProductclassifyEntity;

import java.util.List;

/**
 *
 * 产品分类
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
public interface ProductclassifyService extends SuperService<ProductclassifyEntity> {

    List<ProductclassifyEntity> getList();

    ProductclassifyEntity getInfo(String id);

    void delete(ProductclassifyEntity entity);

    void create(ProductclassifyEntity entity);

    boolean update( String id, ProductclassifyEntity entity);

}
