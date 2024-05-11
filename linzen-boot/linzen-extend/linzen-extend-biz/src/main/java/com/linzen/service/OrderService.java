package com.linzen.service;

import com.linzen.base.service.SuperService;
import com.linzen.entity.OrderEntity;
import com.linzen.entity.OrderEntryEntity;
import com.linzen.entity.OrderReceivableEntity;
import com.linzen.exception.DataBaseException;
import com.linzen.exception.WorkFlowException;
import com.linzen.model.order.OrderInfoVO;
import com.linzen.model.order.PaginationOrder;

import java.util.List;

/**
 * 订单信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface OrderService extends SuperService<OrderEntity> {

    /**
     * 列表
     *
     * @param paginationOrder 分页
     * @return
     */
    List<OrderEntity> getList(PaginationOrder paginationOrder);

    /**
     * 子列表（订单明细）
     *
     * @param id 主表Id
     * @return
     */
    List<OrderEntryEntity> getOrderEntryList(String id);

    /**
     * 子列表（订单收款）
     *
     * @param id 主表Id
     * @return
     */
    List<OrderReceivableEntity> getOrderReceivableList(String id);

    /**
     * 信息（前单、后单）
     *
     * @param id     主键值
     * @param method 方法:prev、next
     * @return
     */
    OrderEntity getPrevOrNextInfo(String id, String method);

    /**
     * 信息（前单、后单）
     *
     * @param id     主键值
     * @param method 方法:prev、next
     * @return
     * @throws DataBaseException 异常
     */
    OrderInfoVO getInfoVo(String id, String method) throws DataBaseException;

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    OrderEntity getInfo(String id);

    /**
     * 删除
     *
     * @param entity 订单信息
     */
    void delete(OrderEntity entity);

    /**
     * 新增
     *
     * @param entity              订单信息
     * @param orderEntryList      订单明细
     * @param orderReceivableList 订单收款
     * @throws WorkFlowException 异常
     */
    void create(OrderEntity entity, List<OrderEntryEntity> orderEntryList, List<OrderReceivableEntity> orderReceivableList);

    /**
     * 更新
     *
     * @param id                  主键值
     * @param entity              订单信息
     * @param orderEntryList      订单明细
     * @param orderReceivableList 订单收款
     * @return
     * @throws WorkFlowException 异常
     */
    boolean update(String id, OrderEntity entity, List<OrderEntryEntity> orderEntryList, List<OrderReceivableEntity> orderReceivableList);

    /**
     * 更改数据
     *
     * @param id   主键值
     * @param data 实体对象
     */
    void data(String id, String data);

}
