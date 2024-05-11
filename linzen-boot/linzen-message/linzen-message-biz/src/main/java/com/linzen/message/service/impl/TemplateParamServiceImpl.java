package com.linzen.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.message.entity.TemplateParamEntity;
import com.linzen.message.mapper.TemplateParamMapper;
import com.linzen.message.model.messagetemplateconfig.MessageTemplateConfigPagination;
import com.linzen.message.service.TemplateParamService;
import com.linzen.permission.service.AuthorizeService;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 消息模板（新）
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Service
public class TemplateParamServiceImpl extends SuperServiceImpl<TemplateParamMapper, TemplateParamEntity> implements TemplateParamService {


    @Autowired
    private UserProvider userProvider;

    @Autowired
    private AuthorizeService authorizeService;

    @Override
    public QueryWrapper<TemplateParamEntity> getChild(MessageTemplateConfigPagination pagination, QueryWrapper<TemplateParamEntity> templateParamQueryWrapper) {
//        boolean pcPermission = false;
//        boolean appPermission = false;
//        boolean isPc = ServletUtil.getHeader("linzen-origin").equals("pc");
//        if (isPc) {
//        }

        return templateParamQueryWrapper;
    }

    @Override
    public TemplateParamEntity getInfo(String id) {
        QueryWrapper<TemplateParamEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TemplateParamEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<TemplateParamEntity> getDetailListByParentId(String id) {
        QueryWrapper<TemplateParamEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TemplateParamEntity::getTemplateId, id);
        return this.list(queryWrapper);
    }

    @Override
    public List<TemplateParamEntity> getParamList(String id,List<String> params) {
        QueryWrapper<TemplateParamEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TemplateParamEntity::getTemplateId, id);
        queryWrapper.lambda().in(TemplateParamEntity::getField,params);
        return this.list(queryWrapper);
    }

}