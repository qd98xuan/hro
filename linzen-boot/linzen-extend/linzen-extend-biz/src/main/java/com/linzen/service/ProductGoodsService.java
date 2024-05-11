package com.linzen.service;

import com.linzen.base.service.SuperService;
import com.linzen.entity.ProductGoodsEntity;
import com.linzen.model.productgoods.ProductGoodsPagination;

import java.util.List;

/**
 *
 * 产品商品
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
public interface ProductGoodsService extends SuperService<ProductGoodsEntity> {

    List<ProductGoodsEntity> getGoodList(String type);

    List<ProductGoodsEntity> getList(ProductGoodsPagination productgoodsPagination);

    ProductGoodsEntity getInfo(String id);

    void delete(ProductGoodsEntity entity);

    void create(ProductGoodsEntity entity);

    boolean update( String id, ProductGoodsEntity entity);

}
