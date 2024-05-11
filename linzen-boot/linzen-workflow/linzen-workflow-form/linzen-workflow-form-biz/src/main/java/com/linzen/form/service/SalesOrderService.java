package com.linzen.form.service;

import com.linzen.base.service.SuperService;
import com.linzen.exception.WorkFlowException;
import com.linzen.form.entity.SalesOrderEntity;
import com.linzen.form.entity.SalesOrderEntryEntity;
import com.linzen.form.model.salesorder.SalesOrderForm;

import java.util.List;

/**
 * 销售订单
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface SalesOrderService extends SuperService<SalesOrderEntity> {

    /**
     * 列表
     *
     * @param id 主键值
     * @return
     */
    List<SalesOrderEntryEntity> getSalesEntryList(String id);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    SalesOrderEntity getInfo(String id);

    /**
     * 保存
     *
     * @param id                        主键值
     * @param entity                    实体对象
     * @param salesOrderEntryEntityList 子表
     * @throws WorkFlowException 异常
     */
    void save(String id, SalesOrderEntity entity, List<SalesOrderEntryEntity> salesOrderEntryEntityList, SalesOrderForm form);

    /**
     * 提交
     *
     * @param id                        主键值
     * @param entity                    实体对象
     * @param salesOrderEntryEntityList 子表
     * @throws WorkFlowException 异常
     */
    void submit(String id, SalesOrderEntity entity, List<SalesOrderEntryEntity> salesOrderEntryEntityList, SalesOrderForm form);

    /**
     * 更改数据
     *
     * @param id   主键值
     * @param data 实体对象
     */
    void data(String id, String data);
}
