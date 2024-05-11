package com.linzen.base.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.Pagination;
import com.linzen.base.entity.SmsTemplateEntity;
import com.linzen.base.entity.SysConfigEntity;
import com.linzen.base.mapper.SmsTemplateMapper;
import com.linzen.base.model.systemconfig.SmsModel;
import com.linzen.base.service.SmsTemplateService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.base.service.SysconfigService;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author FHNP
* @description 针对表【base_sms_template】的数据库操作Service实现
* @createDate 2021-12-09 10:12:52
*/
@Service
public class SmsTemplateServiceImpl extends SuperServiceImpl<SmsTemplateMapper, SmsTemplateEntity> implements SmsTemplateService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private SysconfigService sysconfigService;

    @Override
    public List<SmsTemplateEntity> getList(String keyword) {
        QueryWrapper<SmsTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SmsTemplateEntity::getEnabledMark, 1);
        if (!StringUtil.isEmpty(keyword)) {
            queryWrapper.lambda().and(
                    t -> t.like(SmsTemplateEntity::getTemplateId, keyword)
                            .or().like(SmsTemplateEntity::getFullName, keyword)
                            .or().like(SmsTemplateEntity::getEnCode, keyword)
            );
        }
        queryWrapper.lambda().orderByDesc(SmsTemplateEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<SmsTemplateEntity> getList(Pagination pagination) {
        QueryWrapper<SmsTemplateEntity> queryWrapper = new QueryWrapper<>();
        if (!StringUtil.isEmpty(pagination.getKeyword())) {
            queryWrapper.lambda().and(
                    t -> t.like(SmsTemplateEntity::getTemplateId, pagination.getKeyword())
                            .or().like(SmsTemplateEntity::getFullName, pagination.getKeyword())
                            .or().like(SmsTemplateEntity::getEnCode, pagination.getKeyword())
            );
        }
        queryWrapper.lambda().orderByDesc(SmsTemplateEntity::getCreatorTime);
        Page<SmsTemplateEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<SmsTemplateEntity> userPage = this.page(page, queryWrapper);
        return pagination.setData(userPage.getRecords(), page.getTotal());
    }

    @Override
    public SmsTemplateEntity getInfo(String id) {
        QueryWrapper<SmsTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SmsTemplateEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(SmsTemplateEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreatorUserId(userProvider.get().getUserId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, SmsTemplateEntity entity) {
        entity.setId(id);
        entity.setUpdateTime(new Date());
        entity.setUpdateUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    public void delete(SmsTemplateEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public boolean isExistByTemplateName(String templateName, String id) {
        QueryWrapper<SmsTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SmsTemplateEntity::getFullName, templateName);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(SmsTemplateEntity::getId, id);
        }
        return this.count(queryWrapper) > 0;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<SmsTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SmsTemplateEntity::getEnCode, enCode);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(SmsTemplateEntity::getId, id);
        }
        return this.count(queryWrapper) > 0;
    }

    @Override
    public SmsModel getSmsConfig() {
        // 得到系统配置
        List<SysConfigEntity> configList = sysconfigService.getList("SysConfig");
        Map<String, String> objModel = new HashMap<>(16);
        for (SysConfigEntity entity : configList) {
            objModel.put(entity.getFkey(), entity.getValue());
        }
        SmsModel smsModel = BeanUtil.toBean(objModel, SmsModel.class);
        return smsModel;
    }

}




