package com.linzen.base.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.Pagination;
import com.linzen.base.entity.MessageTemplateEntity;
import com.linzen.base.mapper.MessageTemplateMapper;
import com.linzen.base.service.MessageTemplateService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 消息模板
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class MessageTemplateServiceImpl extends SuperServiceImpl<MessageTemplateMapper, MessageTemplateEntity> implements MessageTemplateService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<MessageTemplateEntity> getList() {
        QueryWrapper<MessageTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageTemplateEntity::getEnabledMark, 1);
        queryWrapper.lambda().orderByDesc(MessageTemplateEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<MessageTemplateEntity> getList(Pagination pagination, Boolean filter) {
        QueryWrapper<MessageTemplateEntity> queryWrapper = new QueryWrapper<>();
        if (!StringUtil.isEmpty(pagination.getKeyword())) {
            queryWrapper.lambda().and(
                    t -> t.like(MessageTemplateEntity::getFullName, pagination.getKeyword())
                        .or().like(MessageTemplateEntity::getTitle, pagination.getKeyword())
                        .or().like(MessageTemplateEntity::getEnCode, pagination.getKeyword())
            );
        }
        if (filter) {
            queryWrapper.lambda().eq(MessageTemplateEntity::getEnabledMark, 1);
        }
        queryWrapper.lambda().orderByDesc(MessageTemplateEntity::getCreatorTime);
        Page<MessageTemplateEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<MessageTemplateEntity> userPage = this.page(page, queryWrapper);
        return pagination.setData(userPage.getRecords(), page.getTotal());
    }

    @Override
    public MessageTemplateEntity getInfo(String id) {
        QueryWrapper<MessageTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageTemplateEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    @DSTransactional
    public void create(MessageTemplateEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreatorUserId(userProvider.get().getUserId());
        this.save(entity);
    }

    @Override
    @DSTransactional
    public boolean update(String id, MessageTemplateEntity entity) {
        entity.setId(id);
        entity.setUpdateTime(new Date());
        entity.setUpdateUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    public void delete(MessageTemplateEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<MessageTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageTemplateEntity::getFullName, fullName);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(MessageTemplateEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<MessageTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageTemplateEntity::getEnCode, enCode);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(MessageTemplateEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

}




