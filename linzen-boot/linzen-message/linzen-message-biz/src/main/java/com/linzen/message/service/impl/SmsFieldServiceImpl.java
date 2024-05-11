package com.linzen.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.message.entity.SmsFieldEntity;
import com.linzen.message.mapper.SmsFieldMapper;
import com.linzen.message.model.messagetemplateconfig.MessageTemplateConfigPagination;
import com.linzen.message.service.SmsFieldService;
import com.linzen.permission.service.AuthorizeService;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息模板（新）
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Service
public class SmsFieldServiceImpl extends SuperServiceImpl<SmsFieldMapper, SmsFieldEntity> implements SmsFieldService {



    @Autowired
    private UserProvider userProvider;

    @Autowired
    private AuthorizeService authorizeService;

    @Override
    public QueryWrapper<SmsFieldEntity> getChild(MessageTemplateConfigPagination pagination, QueryWrapper<SmsFieldEntity> smsFieldQueryWrapper) {
//        boolean pcPermission = false;
//        boolean appPermission = false;
//        boolean isPc = ServletUtil.getHeader("linzen-origin").equals("pc");
//        if (isPc) {
//        }

        return smsFieldQueryWrapper;
    }
    @Override
    public SmsFieldEntity getInfo(String id) {
        QueryWrapper<SmsFieldEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SmsFieldEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<SmsFieldEntity> getDetailListByParentId(String id) {
        QueryWrapper<SmsFieldEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SmsFieldEntity::getTemplateId, id);
        return this.list(queryWrapper);
    }

    @Override
    public List<SmsFieldEntity> getParamList(String id,List<String> params) {
        QueryWrapper<SmsFieldEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SmsFieldEntity::getTemplateId, id);
        queryWrapper.lambda().in(SmsFieldEntity::getField,params);
        return this.list(queryWrapper);
    }

    @Override
    public Map<String,Object> getParamMap(String templateId,Map<String,Object> map) {
        Map<String,Object> paramMap = new HashMap<>();
        List<SmsFieldEntity> list = this.getDetailListByParentId(templateId);
        if (list != null && list.size() > 0) {
            for (SmsFieldEntity entity : list) {
                if (map.containsKey(entity.getField())) {
                    for (String key : map.keySet()) {
                        if (key.equals(entity.getField())) {
                            paramMap.put(entity.getSmsField(), map.get(key));
                            if(StringUtil.isNotEmpty(String.valueOf(entity.getIsTitle())) &&!"null".equals(String.valueOf(entity.getIsTitle())) &&  entity.getIsTitle()==1){
                                paramMap.put("title",map.get(key));
                            }
                        }
                    }
                    if(entity.getField().equals("@FlowLink")){
                        paramMap.put(entity.getSmsField(),"@FlowLink");
                    }
                }
            }
        }
        return paramMap;
    }
}
