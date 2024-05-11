package com.linzen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.UserInfo;
import com.linzen.base.service.BillRuleService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.entity.OrderEntity;
import com.linzen.entity.OrderEntryEntity;
import com.linzen.entity.OrderReceivableEntity;
import com.linzen.exception.DataBaseException;
import com.linzen.mapper.OrderMapper;
import com.linzen.model.order.*;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.service.OrderEntryService;
import com.linzen.service.OrderReceivableService;
import com.linzen.service.OrderService;
import com.linzen.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 订单信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class OrderServiceImpl extends SuperServiceImpl<OrderMapper, OrderEntity> implements OrderService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private OrderReceivableService orderReceivableService;
    @Autowired
    private OrderEntryService orderEntryService;
    @Autowired
    private BillRuleService billRuleService;
    @Autowired
    private UserService userService;

    /**
     * 前单
     **/
    private static String PREV = "prev";
    /**
     * 后单
     **/
    private static String NEXT = "next";

    @Override
    public List<OrderEntity> getList(PaginationOrder paginationOrder) {
        UserInfo userInfo = userProvider.get();
        QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<>();
        //关键字（订单编码、客户名称、业务人员）
        String keyWord = paginationOrder.getKeyword() != null ? paginationOrder.getKeyword() : null;
        if (!StringUtils.isEmpty(keyWord)) {
            String word = keyWord;
            queryWrapper.lambda().and(
                    t -> t.like(OrderEntity::getOrderCode, word)
                            .or().like(OrderEntity::getCustomerName, word)
                            .or().like(OrderEntity::getSalesmanName, word)
            );
        }
        //起始日期")
        if (ObjectUtil.isNotEmpty(paginationOrder.getStartTime()) && ObjectUtil.isNotEmpty(paginationOrder.getEndTime())) {
            queryWrapper.lambda().between(OrderEntity::getOrderDate, new Date(paginationOrder.getStartTime()), new Date(paginationOrder.getEndTime()));
        }
        //订单状态
        String mark = paginationOrder.getEnabledMark();
        if (ObjectUtil.isNotEmpty(mark)) {
            queryWrapper.lambda().eq(OrderEntity::getEnabledMark, mark);
        }
        //排序
        if (StringUtils.isEmpty(paginationOrder.getSidx())) {
            queryWrapper.lambda().orderByDesc(OrderEntity::getCreatorTime);
        } else {
            queryWrapper = "asc".equals(paginationOrder.getSort().toLowerCase()) ? queryWrapper.orderByAsc(paginationOrder.getSidx()) : queryWrapper.orderByDesc(paginationOrder.getSidx());
        }
        Page<OrderEntity> page = new Page<>(paginationOrder.getCurrentPage(), paginationOrder.getPageSize());
        IPage<OrderEntity> orderEntityPage = this.page(page, queryWrapper);
        List<OrderEntity> data = orderEntityPage.getRecords();
        return paginationOrder.setData(data, page.getTotal());
    }

    @Override
    public List<OrderEntryEntity> getOrderEntryList(String id) {
        QueryWrapper<OrderEntryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(OrderEntryEntity::getOrderId, id).orderByDesc(OrderEntryEntity::getSortCode);
        return orderEntryService.list(queryWrapper);
    }

    @Override
    public List<OrderReceivableEntity> getOrderReceivableList(String id) {
        QueryWrapper<OrderReceivableEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(OrderReceivableEntity::getOrderId, id).orderByDesc(OrderReceivableEntity::getSortCode);
        return orderReceivableService.list(queryWrapper);
    }

    @Override
    public OrderEntity getPrevOrNextInfo(String id, String method) {
        QueryWrapper<OrderEntity> result = new QueryWrapper<>();
        OrderEntity orderEntity = getInfo(id);
        String orderBy = "desc";
        if (PREV.equals(method)) {
            result.lambda().gt(OrderEntity::getCreatorTime, orderEntity.getCreatorTime());
            orderBy = "";
        } else if (NEXT.equals(method)) {
            result.lambda().lt(OrderEntity::getCreatorTime, orderEntity.getCreatorTime());
        }
        result.lambda().notIn(OrderEntity::getId, orderEntity.getId());
        if (StringUtil.isNotEmpty(orderBy)) {
            result.lambda().orderByDesc(OrderEntity::getCreatorTime);
        }
        List<OrderEntity> data = this.list(result);
        if (data.size() > 0) {
            return data.get(0);
        }
        return null;
    }

    @Override
    public OrderInfoVO getInfoVo(String id, String method) throws DataBaseException {
        OrderInfoVO infoModel = null;
        OrderEntity orderEntity = this.getPrevOrNextInfo(id, method);
        if (orderEntity != null) {
            List<OrderEntryEntity> orderEntryList = this.getOrderEntryList(orderEntity.getId());
            List<OrderReceivableEntity> orderReceivableList = this.getOrderReceivableList(orderEntity.getId());
            infoModel = JsonUtilEx.getJsonToBeanEx(orderEntity, OrderInfoVO.class);
            SysUserEntity createUser = null;
            if (StringUtil.isNotEmpty(infoModel.getCreatorUserId())) {
                createUser = userService.getInfo(infoModel.getCreatorUserId());
            }
            infoModel.setCreatorUserId(createUser != null ? createUser.getRealName() + "/" + createUser.getAccount() : "");
            SysUserEntity lastUser = null;
            if (StringUtil.isNotEmpty(infoModel.getUpdateUserId())) {
                lastUser = userService.getInfo(infoModel.getUpdateUserId());
            }
            infoModel.setUpdateUserId(lastUser != null ? lastUser.getRealName() + "/" + lastUser.getAccount() : "");
            List<OrderInfoOrderEntryModel> orderEntryModels = JsonUtil.createJsonToList(orderEntryList, OrderInfoOrderEntryModel.class);
            infoModel.setGoodsList(orderEntryModels);
            List<OrderInfoOrderReceivableModel> orderReceivableModels = JsonUtil.createJsonToList(orderReceivableList, OrderInfoOrderReceivableModel.class);
            infoModel.setCollectionPlanList(orderReceivableModels);
        }
        return infoModel;
    }

    @Override
    public OrderEntity getInfo(String id) {
        QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(OrderEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void delete(OrderEntity entity) {
        QueryWrapper<OrderEntity> orderWrapper = new QueryWrapper<>();
        orderWrapper.lambda().eq(OrderEntity::getId, entity.getId());
        this.remove(orderWrapper);
        QueryWrapper<OrderEntryEntity> entryWrapper = new QueryWrapper<>();
        entryWrapper.lambda().eq(OrderEntryEntity::getOrderId, entity.getId());
        orderEntryService.remove(entryWrapper);
        QueryWrapper<OrderReceivableEntity> receivableWrapper = new QueryWrapper<>();
        receivableWrapper.lambda().eq(OrderReceivableEntity::getOrderId, entity.getId());
        orderReceivableService.remove(receivableWrapper);
    }

    @Override
    @DSTransactional
    public void create(OrderEntity entity, List<OrderEntryEntity> orderEntryList, List<OrderReceivableEntity> orderReceivableList) {
        entity.setCreatorUserId(userProvider.get().getUserId());
        entity.setEnabledMark(1);
        for (int i = 0; i < orderEntryList.size(); i++) {
            orderEntryList.get(i).setId(RandomUtil.uuId());
            orderEntryList.get(i).setOrderId(entity.getId());
            orderEntryList.get(i).setSortCode(Long.parseLong(i + ""));
            orderEntryService.save(orderEntryList.get(i));
        }
        for (int i = 0; i < orderReceivableList.size(); i++) {
            orderReceivableList.get(i).setId(RandomUtil.uuId());
            orderReceivableList.get(i).setOrderId(entity.getId());
            orderReceivableList.get(i).setSortCode(Long.parseLong(i + ""));
            orderReceivableService.save(orderReceivableList.get(i));
        }
        billRuleService.useBillNumber("OrderNumber");
        this.save(entity);
    }

    @Override
    @DSTransactional
    public boolean update(String id, OrderEntity entity, List<OrderEntryEntity> orderEntryList, List<OrderReceivableEntity> orderReceivableList) {
        entity.setId(id);
        entity.setUpdateTime(new Date());
        entity.setUpdateUserId(userProvider.get().getUserId());
        QueryWrapper<OrderEntryEntity> entryWrapper = new QueryWrapper<>();
        entryWrapper.lambda().eq(OrderEntryEntity::getOrderId, entity.getId());
        orderEntryService.remove(entryWrapper);
        QueryWrapper<OrderReceivableEntity> receivableWrapper = new QueryWrapper<>();
        receivableWrapper.lambda().eq(OrderReceivableEntity::getOrderId, entity.getId());
        orderReceivableService.remove(receivableWrapper);
        for (int i = 0; i < orderEntryList.size(); i++) {
            orderEntryList.get(i).setId(RandomUtil.uuId());
            orderEntryList.get(i).setOrderId(entity.getId());
            orderEntryList.get(i).setSortCode(Long.parseLong(i + ""));
            orderEntryService.save(orderEntryList.get(i));
        }
        for (int i = 0; i < orderReceivableList.size(); i++) {
            orderReceivableList.get(i).setId(RandomUtil.uuId());
            orderReceivableList.get(i).setOrderId(entity.getId());
            orderReceivableList.get(i).setSortCode(Long.parseLong(i + ""));
            orderReceivableService.save(orderReceivableList.get(i));
        }
        boolean flag = this.updateById(entity);
        return flag;
    }

    @Override
    public void data(String id, String data) {
        OrderForm orderForm = JsonUtil.createJsonToBean(data, OrderForm.class);
        OrderEntity entity = BeanUtil.toBean(orderForm, OrderEntity.class);
        List<OrderEntryModel> goodsList = orderForm.getGoodsList() != null ? orderForm.getGoodsList() : new ArrayList<>();
        List<OrderEntryEntity> orderEntryList = JsonUtil.createJsonToList(goodsList, OrderEntryEntity.class);
        List<OrderReceivableModel> collectionPlanList = orderForm.getCollectionPlanList() != null ? orderForm.getCollectionPlanList() : new ArrayList<>();
        List<OrderReceivableEntity> orderReceivableList = JsonUtil.createJsonToList(collectionPlanList, OrderReceivableEntity.class);
        entity.setId(id);
        entity.setUpdateTime(new Date());
        entity.setUpdateUserId(userProvider.get().getUserId());
        QueryWrapper<OrderEntryEntity> entryWrapper = new QueryWrapper<>();
        entryWrapper.lambda().eq(OrderEntryEntity::getOrderId, entity.getId());
        orderEntryService.remove(entryWrapper);
        QueryWrapper<OrderReceivableEntity> receivableWrapper = new QueryWrapper<>();
        receivableWrapper.lambda().eq(OrderReceivableEntity::getOrderId, entity.getId());
        orderReceivableService.remove(receivableWrapper);
        for (int i = 0; i < orderEntryList.size(); i++) {
            orderEntryList.get(i).setId(RandomUtil.uuId());
            orderEntryList.get(i).setOrderId(entity.getId());
            orderEntryList.get(i).setSortCode(Long.parseLong(i + ""));
            orderEntryService.save(orderEntryList.get(i));
        }
        for (int i = 0; i < orderReceivableList.size(); i++) {
            orderReceivableList.get(i).setId(RandomUtil.uuId());
            orderReceivableList.get(i).setOrderId(entity.getId());
            orderReceivableList.get(i).setSortCode(Long.parseLong(i + ""));
            orderReceivableService.save(orderReceivableList.get(i));
        }
        this.saveOrUpdate(entity);
    }


}
