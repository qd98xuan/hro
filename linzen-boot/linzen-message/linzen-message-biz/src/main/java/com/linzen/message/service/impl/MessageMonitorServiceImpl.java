package com.linzen.message.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.message.entity.MessageMonitorEntity;
import com.linzen.message.mapper.MessageMonitorMapper;
import com.linzen.message.model.messagemonitor.MessageMonitorForm;
import com.linzen.message.model.messagemonitor.MessageMonitorPagination;
import com.linzen.message.service.MessageMonitorService;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.AuthorizeService;
import com.linzen.permission.service.UserService;
import com.linzen.util.JsonUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 消息监控
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Service
public class MessageMonitorServiceImpl extends SuperServiceImpl<MessageMonitorMapper, MessageMonitorEntity> implements MessageMonitorService {


    @Autowired
    private UserProvider userProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorizeService authorizeService;


    @Override
    public List<MessageMonitorEntity> getList(MessageMonitorPagination messageMonitorPagination) {
        return getTypeList(messageMonitorPagination, messageMonitorPagination.getDataType());
    }

    @Override
    public List<MessageMonitorEntity> getTypeList(MessageMonitorPagination messageMonitorPagination, String dataType) {
        String userId = userProvider.get().getUserId();
        int total = 0;
        int messageMonitorNum = 0;
        QueryWrapper<MessageMonitorEntity> messageMonitorQueryWrapper = new QueryWrapper<>();
        //关键字
        if (ObjectUtil.isNotEmpty(messageMonitorPagination.getKeyword())) {
            messageMonitorNum++;
            messageMonitorQueryWrapper.lambda().and(t -> t.like(MessageMonitorEntity::getTitle, messageMonitorPagination.getKeyword()));
        }
        //消息类型
        if (ObjectUtil.isNotEmpty(messageMonitorPagination.getMessageType())) {
            messageMonitorNum++;
            messageMonitorQueryWrapper.lambda().eq(MessageMonitorEntity::getMessageType, messageMonitorPagination.getMessageType());
        }
        //发送时间
        if (ObjectUtil.isNotEmpty(messageMonitorPagination.getStartTime()) && ObjectUtil.isNotEmpty(messageMonitorPagination.getEndTime())) {
            messageMonitorNum++;

            messageMonitorQueryWrapper.lambda().ge(MessageMonitorEntity::getSendTime, new Date(messageMonitorPagination.getStartTime()))
                    .le(MessageMonitorEntity::getSendTime, new Date(messageMonitorPagination.getEndTime()));

        }
        //消息来源
        if (ObjectUtil.isNotEmpty(messageMonitorPagination.getMessageSource())) {
            messageMonitorNum++;
            messageMonitorQueryWrapper.lambda().eq(MessageMonitorEntity::getMessageSource, messageMonitorPagination.getMessageSource());
        }
        //排序
        if (StringUtil.isEmpty(messageMonitorPagination.getSidx())) {
            messageMonitorQueryWrapper.lambda().orderByDesc(MessageMonitorEntity::getSendTime);
        } else {
            try {
                String sidx = messageMonitorPagination.getSidx();
                MessageMonitorEntity messageMonitorEntity = new MessageMonitorEntity();
                Field declaredField = messageMonitorEntity.getClass().getDeclaredField(sidx);
                declaredField.setAccessible(true);
                String value = declaredField.getAnnotation(TableField.class).value();
                messageMonitorQueryWrapper = "asc".equals(messageMonitorPagination.getSort().toLowerCase()) ? messageMonitorQueryWrapper.orderByAsc(value) : messageMonitorQueryWrapper.orderByDesc(value);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        if (!"1".equals(dataType)) {
            if (total > 0 || total == 0) {
                Page<MessageMonitorEntity> page = new Page<>(messageMonitorPagination.getCurrentPage(), messageMonitorPagination.getPageSize());
                IPage<MessageMonitorEntity> userIPage = this.page(page, messageMonitorQueryWrapper);
                return messageMonitorPagination.setData(userIPage.getRecords(), userIPage.getTotal());
            } else {
                List<MessageMonitorEntity> list = new ArrayList();
                return messageMonitorPagination.setData(list, list.size());
            }
        } else {
            return this.list(messageMonitorQueryWrapper);
        }
    }


    @Override
    public MessageMonitorEntity getInfo(String id) {
        QueryWrapper<MessageMonitorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageMonitorEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(MessageMonitorEntity entity) {
        this.save(entity);
    }

    @Override
    public boolean update(String id, MessageMonitorEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(MessageMonitorEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }
    //子表方法

    //列表子表数据方法


    //验证表单唯一字段
    @Override
    public boolean checkForm(MessageMonitorForm form, int i) {
        int total = 0;
        if (total > 0) {
            return true;
        }
        return false;
    }
    @Override
    public void emptyMonitor(){
        QueryWrapper<MessageMonitorEntity> queryWrapper = new QueryWrapper<>();
        this.remove(queryWrapper);
    }


    @Override
    @DSTransactional
    public boolean delete(String[] ids) {
        if (ids.length > 0) {
            QueryWrapper<MessageMonitorEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(MessageMonitorEntity::getId, ids);
            return this.remove(queryWrapper);
        }
        return false;
    }
    /**
     * 用户id转名称(多选)
     *
     * @param ids
     * @return
     */
    @Override
    public String userSelectValues(String ids) {
        if (StringUtil.isEmpty(ids)) {
            return ids;
        }
        if (ids.contains("[")){
            List<String> nameList = new ArrayList<>();
            List<String> jsonToList = JsonUtil.createJsonToList(ids, String.class);
            for (String userId : jsonToList){
                SysUserEntity info = userService.getInfo(userId);
                nameList.add(Objects.nonNull(info) ? info.getRealName()+ "/" + info.getAccount() : userId);
            }
            return String.join(";", nameList);
        }else {
            List<String> userInfoList = new ArrayList<>();
            String[] idList = ids.split(",");
            if (idList.length > 0) {
                for (String id : idList) {
                    SysUserEntity userEntity = userService.getInfo(id);
                    if (ObjectUtil.isNotEmpty(userEntity)) {
                        String info = userEntity.getRealName() + "/" + userEntity.getAccount();
                        userInfoList.add(info);
                    }
                }
            }
            return String.join("-", userInfoList);
        }
    }


}