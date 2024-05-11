package com.linzen.form.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.form.entity.SalesOrderEntity;
import com.linzen.form.entity.SalesOrderEntryEntity;
import com.linzen.form.mapper.SalesOrderMapper;
import com.linzen.form.model.salesorder.SalesOrderEntryEntityInfoModel;
import com.linzen.form.model.salesorder.SalesOrderForm;
import com.linzen.form.service.SalesOrderEntryService;
import com.linzen.form.service.SalesOrderService;
import com.linzen.util.JsonUtil;
import com.linzen.util.RandomUtil;
import com.linzen.util.ServiceAllUtil;
import com.linzen.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 销售订单
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class SalesOrderServiceImpl extends SuperServiceImpl<SalesOrderMapper, SalesOrderEntity> implements SalesOrderService {

    @Autowired
    private ServiceAllUtil serviceAllUtil;
    @Autowired
    private SalesOrderEntryService salesOrderEntryService;

    @Override
    public List<SalesOrderEntryEntity> getSalesEntryList(String id) {
        QueryWrapper<SalesOrderEntryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SalesOrderEntryEntity::getSalesOrderId, id).orderByDesc(SalesOrderEntryEntity::getSortCode);
        return salesOrderEntryService.list(queryWrapper);
    }

    @Override
    public SalesOrderEntity getInfo(String id) {
        QueryWrapper<SalesOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SalesOrderEntity::getId, id);
        return getOne(queryWrapper);
    }

    @Override
    @DSTransactional
    public void save(String id, SalesOrderEntity entity, List<SalesOrderEntryEntity> salesOrderEntryEntityList, SalesOrderForm form) {
        //表单信息
        if (StringUtil.isEmpty(entity.getId())) {
            entity.setId(id);
            for (int i = 0; i < salesOrderEntryEntityList.size(); i++) {
                salesOrderEntryEntityList.get(i).setId(RandomUtil.uuId());
                salesOrderEntryEntityList.get(i).setSalesOrderId(entity.getId());
                salesOrderEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                salesOrderEntryService.save(salesOrderEntryEntityList.get(i));
            }
            //创建
            save(entity);
            serviceAllUtil.useBillNumber("WF_SalesOrderNo");
        } else {
            entity.setId(id);
            QueryWrapper<SalesOrderEntryEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(SalesOrderEntryEntity::getSalesOrderId, entity.getId());
            salesOrderEntryService.remove(queryWrapper);
            for (int i = 0; i < salesOrderEntryEntityList.size(); i++) {
                salesOrderEntryEntityList.get(i).setId(RandomUtil.uuId());
                salesOrderEntryEntityList.get(i).setSalesOrderId(entity.getId());
                salesOrderEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                salesOrderEntryService.save(salesOrderEntryEntityList.get(i));
            }
            //编辑
            updateById(entity);
        }
    }

    @Override
    @DSTransactional
    public void submit(String id, SalesOrderEntity entity, List<SalesOrderEntryEntity> salesOrderEntryEntityList, SalesOrderForm form) {
        //表单信息
        if (StringUtil.isEmpty(entity.getId())) {
            entity.setId(id);
            for (int i = 0; i < salesOrderEntryEntityList.size(); i++) {
                salesOrderEntryEntityList.get(i).setId(RandomUtil.uuId());
                salesOrderEntryEntityList.get(i).setSalesOrderId(entity.getId());
                salesOrderEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                salesOrderEntryService.save(salesOrderEntryEntityList.get(i));
            }
            //创建
            save(entity);
            serviceAllUtil.useBillNumber("WF_SalesOrderNo");
        } else {
            entity.setId(id);
            QueryWrapper<SalesOrderEntryEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(SalesOrderEntryEntity::getSalesOrderId, entity.getId());
            salesOrderEntryService.remove(queryWrapper);
            for (int i = 0; i < salesOrderEntryEntityList.size(); i++) {
                salesOrderEntryEntityList.get(i).setId(RandomUtil.uuId());
                salesOrderEntryEntityList.get(i).setSalesOrderId(entity.getId());
                salesOrderEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                salesOrderEntryService.save(salesOrderEntryEntityList.get(i));
            }
            //编辑
            updateById(entity);
        }
    }

    @Override
    public void data(String id, String data) {
        SalesOrderForm salesOrderForm = JsonUtil.createJsonToBean(data, SalesOrderForm.class);
        SalesOrderEntity entity = BeanUtil.toBean(salesOrderForm, SalesOrderEntity.class);
        List<SalesOrderEntryEntityInfoModel> entryList = salesOrderForm.getEntryList() != null ? salesOrderForm.getEntryList() : new ArrayList<>();
        List<SalesOrderEntryEntity> salesOrderEntryEntityList = JsonUtil.createJsonToList(entryList, SalesOrderEntryEntity.class);
        entity.setId(id);
        QueryWrapper<SalesOrderEntryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SalesOrderEntryEntity::getSalesOrderId, entity.getId());
        salesOrderEntryService.remove(queryWrapper);
        for (int i = 0; i < salesOrderEntryEntityList.size(); i++) {
            salesOrderEntryEntityList.get(i).setId(RandomUtil.uuId());
            salesOrderEntryEntityList.get(i).setSalesOrderId(entity.getId());
            salesOrderEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
            salesOrderEntryService.save(salesOrderEntryEntityList.get(i));
        }
        //编辑
        saveOrUpdate(entity);
    }

}
