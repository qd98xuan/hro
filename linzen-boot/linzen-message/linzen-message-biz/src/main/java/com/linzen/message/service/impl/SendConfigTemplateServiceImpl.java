package com.linzen.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.message.entity.SendConfigTemplateEntity;
import com.linzen.message.mapper.SendConfigTemplateMapper;
import com.linzen.message.model.sendmessageconfig.SendMessageConfigPagination;
import com.linzen.message.service.SendConfigTemplateService;
import com.linzen.permission.service.AuthorizeService;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 消息发送配置
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Service
public class SendConfigTemplateServiceImpl extends SuperServiceImpl<SendConfigTemplateMapper, SendConfigTemplateEntity> implements SendConfigTemplateService {


    @Autowired
    private UserProvider userProvider;

    @Autowired
    private AuthorizeService authorizeService;

    @Override
    public QueryWrapper<SendConfigTemplateEntity> getChild(SendMessageConfigPagination pagination, QueryWrapper<SendConfigTemplateEntity> sendConfigTemplateQueryWrapper) {
//        boolean pcPermission = false;
//        boolean appPermission = false;
//        boolean isPc = ServletUtil.getHeader("linzen-origin").equals("pc");
//        if (isPc) {
//        }

        return sendConfigTemplateQueryWrapper;
    }

    @Override
    public SendConfigTemplateEntity getInfo(String id) {
        QueryWrapper<SendConfigTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SendConfigTemplateEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<SendConfigTemplateEntity> getDetailListByParentId(String id) {
        QueryWrapper<SendConfigTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SendConfigTemplateEntity::getSendConfigId, id);
        return this.list(queryWrapper);
    }

    @Override
    public List<SendConfigTemplateEntity> getConfigTemplateListByConfigId(String id) {
        QueryWrapper<SendConfigTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SendConfigTemplateEntity::getSendConfigId, id);
        queryWrapper.lambda().eq(SendConfigTemplateEntity::getEnabledMark, 1);
        return this.list(queryWrapper);
    }

    @Override
    public boolean isUsedAccount(String accountId) {
        QueryWrapper<SendConfigTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SendConfigTemplateEntity::getAccountConfigId, accountId);
        if (this.list(queryWrapper) != null && this.list(queryWrapper).size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isUsedTemplate(String templateId) {
        QueryWrapper<SendConfigTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SendConfigTemplateEntity::getTemplateId, templateId);
        if (this.list(queryWrapper) != null && this.list(queryWrapper).size() > 0) {
            return true;
        } else {
            return false;
        }
    }
}