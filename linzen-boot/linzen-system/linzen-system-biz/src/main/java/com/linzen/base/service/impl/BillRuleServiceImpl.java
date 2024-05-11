package com.linzen.base.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.ServiceResult;
import com.linzen.base.Pagination;
import com.linzen.base.entity.BillRuleEntity;
import com.linzen.base.mapper.BillRuleMapper;
import com.linzen.base.model.billrule.BillRulePagination;
import com.linzen.base.service.BillRuleService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.constant.MsgCode;
import com.linzen.exception.DataBaseException;
import com.linzen.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * 单据规则
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class BillRuleServiceImpl extends SuperServiceImpl<BillRuleMapper, BillRuleEntity> implements BillRuleService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<BillRuleEntity> getList(BillRulePagination pagination) {
        // 定义变量判断是否需要使用修改时间倒序
        boolean flag = false;
        QueryWrapper<BillRuleEntity> queryWrapper = new QueryWrapper<>();
        if (!StringUtil.isEmpty(pagination.getKeyword())) {
            flag = true;
            queryWrapper.lambda().and(
                    t -> t.like(BillRuleEntity::getFullName, pagination.getKeyword())
                            .or().like(BillRuleEntity::getEnCode, pagination.getKeyword())
            );
        }
        if (!StringUtil.isEmpty(pagination.getCategoryId())) {
            flag = true;
            queryWrapper.lambda().and(
                    t -> t.like(BillRuleEntity::getCategory, pagination.getCategoryId())
            );
        }
        if (pagination.getEnabledMark() != null) {
            flag = true;
            queryWrapper.lambda().eq(BillRuleEntity::getEnabledMark, pagination.getEnabledMark());
        }
        // 排序
        queryWrapper.lambda().orderByAsc(BillRuleEntity::getSortCode).orderByDesc(BillRuleEntity::getCreatorTime);
        if (flag) {
            queryWrapper.lambda().orderByDesc(BillRuleEntity::getUpdateTime);
        }
        Page<BillRuleEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<BillRuleEntity> userPage = this.page(page, queryWrapper);
        return pagination.setData(userPage.getRecords(), page.getTotal());
    }

    @Override
    public List<BillRuleEntity> getList() {
        QueryWrapper<BillRuleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BillRuleEntity::getEnabledMark, 1);
        // 排序
        queryWrapper.lambda().orderByAsc(BillRuleEntity::getSortCode).orderByDesc(BillRuleEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public BillRuleEntity getInfo(String id) {
        QueryWrapper<BillRuleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BillRuleEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<BillRuleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BillRuleEntity::getFullName, fullName);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(BillRuleEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<BillRuleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BillRuleEntity::getEnCode, enCode);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(BillRuleEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    @DSTransactional
    public String getNumber(String enCode) throws DataBaseException {
        StringBuilder strNumber = new StringBuilder();
        QueryWrapper<BillRuleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BillRuleEntity::getEnCode, enCode);
        BillRuleEntity entity = this.getOne(queryWrapper);
        if (entity != null) {
            Integer startNumber = Integer.parseInt(entity.getStartNumber());
            String dateFor = entity.getDateFormat();
            //处理隔天流水号归0
            if (entity.getOutputNumber() != null) {
                String serialDate;
                entity.setThisNumber(entity.getThisNumber() + 1);
                if (!"no".equals(dateFor)) {
                    String thisDate = DateUtil.dateNow(entity.getDateFormat());
                    serialDate = entity.getOutputNumber().substring((entity.getOutputNumber().length() - dateFor.length() - entity.getDigit()), (entity.getOutputNumber().length() - entity.getDigit()));
                    if (!serialDate.equals(thisDate)) {
                        entity.setThisNumber(0);
                    }
                }
            } else {
                entity.setThisNumber(0);
            }
            //拼接单据编码
            strNumber.append(entity.getPrefix());
            if (!"no".equals(dateFor)) {
                strNumber.append(DateUtil.dateNow(entity.getDateFormat()));
            }
            strNumber.append(PadUtil.padRight(String.valueOf((startNumber) + entity.getThisNumber()), entity.getDigit(), '0'));
            //更新流水号
            entity.setOutputNumber(strNumber.toString());
            this.updateById(entity);
        } else {
            throw new DataBaseException("单据规则不存在");
        }
        return strNumber.toString();
    }

    @Override
    public void create(BillRuleEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreatorUserId(userProvider.get().getUserId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, BillRuleEntity entity) {
        entity.setId(id);
        entity.setUpdateTime(new Date());
        entity.setUpdateUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    public void delete(BillRuleEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    @DSTransactional
    public boolean first(String id) {
        boolean isOk = false;
        //获取要上移的那条数据的信息
        BillRuleEntity upEntity = this.getById(id);
        Long upSortCode = upEntity.getSortCode() == null ? 0 : upEntity.getSortCode();
        //查询上几条记录
        QueryWrapper<BillRuleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .lt(BillRuleEntity::getSortCode, upSortCode)
                .orderByDesc(BillRuleEntity::getSortCode);
        List<BillRuleEntity> downEntity = this.list(queryWrapper);
        if (downEntity.size() > 0) {
            //交换两条记录的sort值
            Long temp = upEntity.getSortCode();
            upEntity.setSortCode(downEntity.get(0).getSortCode());
            downEntity.get(0).setSortCode(temp);
            this.updateById(downEntity.get(0));
            this.updateById(upEntity);
            isOk = true;
        }
        return isOk;
    }

    @Override
    @DSTransactional
    public boolean next(String id) {
        boolean isOk = false;
        //获取要下移的那条数据的信息
        BillRuleEntity downEntity = this.getById(id);
        Long upSortCode = downEntity.getSortCode() == null ? 0 : downEntity.getSortCode();
        //查询下几条记录
        QueryWrapper<BillRuleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .gt(BillRuleEntity::getSortCode, upSortCode)
                .orderByAsc(BillRuleEntity::getSortCode);
        List<BillRuleEntity> upEntity = this.list(queryWrapper);
        if (upEntity.size() > 0) {
            //交换两条记录的sort值
            Long temp = downEntity.getSortCode();
            downEntity.setSortCode(upEntity.get(0).getSortCode());
            upEntity.get(0).setSortCode(temp);
            this.updateById(upEntity.get(0));
            this.updateById(downEntity);
            isOk = true;
        }
        return isOk;
    }

    @Override
    public String getBillNumber(String enCode, boolean isCache) throws DataBaseException {
        String strNumber;
        String tenantId = !StringUtil.isEmpty(userProvider.get().getTenantId()) ? userProvider.get().getTenantId() : "";
        if (isCache) {
            String cacheKey = tenantId + userProvider.get().getUserId() + enCode;
            if (!redisUtil.exists(cacheKey)) {
                strNumber = this.getNumber(enCode);
                redisUtil.insert(cacheKey, strNumber);
            } else {
                strNumber = String.valueOf(redisUtil.getString(cacheKey));
            }
        } else {
            strNumber = this.getNumber(enCode);
        }
        return strNumber;
    }

    @Override
    public void useBillNumber(String enCode) {
        String cacheKey = userProvider.get().getTenantId() + userProvider.get().getUserId() + enCode;
        redisUtil.remove(cacheKey);
    }

    @Override
    @Transactional
    public ServiceResult ImportData(BillRuleEntity entity, Integer type) throws DataBaseException {
        if (entity != null) {
            StringJoiner stringJoiner = new StringJoiner("、");
            QueryWrapper<BillRuleEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(BillRuleEntity::getId, entity.getId());
            if (this.count(queryWrapper) > 0) {
                stringJoiner.add("ID");
            }
            queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(BillRuleEntity::getEnCode, entity.getEnCode());
            if (this.count(queryWrapper) > 0) {
                stringJoiner.add("编码");
            }
            queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(BillRuleEntity::getFullName, entity.getFullName());
            if (this.count(queryWrapper) > 0) {
                stringJoiner.add("名称");
            }
            if (stringJoiner.length() > 0 && ObjectUtil.equal(type, 1)) {
                String copyNum = UUID.randomUUID().toString().substring(0, 5);
                entity.setFullName(entity.getFullName() + ".副本" + copyNum);
                entity.setEnCode(entity.getEnCode() + copyNum);
            } else if (ObjectUtil.equal(type, 0) && stringJoiner.length() > 0) {
                return ServiceResult.error(stringJoiner.toString() + "重复");
            }
            entity.setCreatorTime(new Date());
            entity.setCreatorUserId(UserProvider.getLoginUserId());
            entity.setUpdateTime(null);
            entity.setUpdateUserId(null);
            entity.setId(RandomUtil.uuId());
            try {
                this.setIgnoreLogicDelete().removeById(entity);
                entity.setEnabledMark(0);
                entity.setThisNumber(null);
                entity.setOutputNumber(null);
                this.setIgnoreLogicDelete().saveOrUpdate(entity);
            } catch (Exception e) {
                throw new DataBaseException(MsgCode.IMP003.get());
            }finally {
                this.clearIgnoreLogicDelete();
            }
            return ServiceResult.success(MsgCode.IMP001.get());
        }
        return ServiceResult.error(MsgCode.IMP004.get());
    }
    @Override
    public List<BillRuleEntity> getListByCategory(String id,Pagination pagination) {
        // 定义变量判断是否需要使用修改时间倒序
        boolean flag = false;
        QueryWrapper<BillRuleEntity> queryWrapper = new QueryWrapper<>();
        if (!StringUtil.isEmpty(pagination.getKeyword())) {
            flag = true;
            queryWrapper.lambda().and(
                    t -> t.like(BillRuleEntity::getFullName, pagination.getKeyword())
                            .or().like(BillRuleEntity::getEnCode, pagination.getKeyword())
            );
        }
        if (!StringUtil.isEmpty(id)) {
            flag = true;
            queryWrapper.lambda().eq(BillRuleEntity::getCategory, id);
        }
        queryWrapper.lambda().eq(BillRuleEntity::getEnabledMark, 1);
        // 排序
        queryWrapper.lambda().orderByAsc(BillRuleEntity::getSortCode).orderByDesc(BillRuleEntity::getCreatorTime);
        if (flag) {
            queryWrapper.lambda().orderByDesc(BillRuleEntity::getUpdateTime);
        }
        Page<BillRuleEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<BillRuleEntity> userPage = this.page(page, queryWrapper);
        return pagination.setData(userPage.getRecords(), page.getTotal());
    }
}