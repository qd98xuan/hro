package com.linzen.message.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.PageModel;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.message.entity.ImContentEntity;
import com.linzen.message.entity.ImReplyEntity;
import com.linzen.message.mapper.ImContentMapper;
import com.linzen.message.model.ImReplySavaModel;
import com.linzen.message.model.ImUnreadNumModel;
import com.linzen.message.service.ImContentService;
import com.linzen.message.service.ImReplyService;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 聊天内容
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class ImContentServiceImpl extends SuperServiceImpl<ImContentMapper, ImContentEntity> implements ImContentService {

    @Autowired
    private ImReplyService imReplyService;
    @Autowired
    private UserProvider userProvider;
    @Override
    public List<ImContentEntity> getMessageList(String sendUserId, String receiveUserId, PageModel pageModel) {
        QueryWrapper<ImContentEntity> queryWrapper = new QueryWrapper<>();
        //发件人、收件人
        if (!StringUtil.isEmpty(sendUserId) && !StringUtil.isEmpty(receiveUserId)) {

            queryWrapper.lambda().and(wrapper -> {
                wrapper.eq(ImContentEntity::getSendUserId, sendUserId);
                wrapper.eq(ImContentEntity::getReceiveUserId, receiveUserId);
                wrapper.or().eq(ImContentEntity::getSendUserId, receiveUserId);
                wrapper.eq(ImContentEntity::getReceiveUserId, sendUserId);
            });
            queryWrapper.lambda().and(wrapper -> {
                wrapper.isNull(ImContentEntity::getDeleteUserId);
                wrapper.or(). ne(ImContentEntity::getDeleteUserId,receiveUserId);
//                wrapper.ne(ImContentEntity::getEnabledMark, 1);
            });

        }
        //关键字查询
        if (pageModel != null && pageModel.getKeyword() != null) {
            queryWrapper.lambda().like(ImContentEntity::getContent, pageModel.getKeyword());
            //排序
            pageModel.setSidx("f_send_time");
        }

        if (StringUtil.isEmpty(pageModel.getSidx())) {
            queryWrapper.lambda().orderByDesc(ImContentEntity::getSendTime);
        } else {
            queryWrapper = "asc".equals(pageModel.getSord().toLowerCase()) ? queryWrapper.orderByAsc(pageModel.getSidx()) : queryWrapper.orderByDesc(pageModel.getSidx());
        }
        Page<ImContentEntity> page = new Page<>(pageModel.getPage(), pageModel.getRows());
        IPage<ImContentEntity> iPage = this.page(page, queryWrapper);
        return pageModel.setData(iPage.getRecords(), page.getTotal());
    }

    @Override
    public List<ImUnreadNumModel> getUnreadList(String receiveUserId) {
        List<ImUnreadNumModel> list = this.baseMapper.getUnreadList(receiveUserId);
        List<ImUnreadNumModel> list1 = this.baseMapper.getUnreadLists(receiveUserId);
        for (ImUnreadNumModel item : list) {
            Optional<ImUnreadNumModel> first = list1.stream().filter(q -> q.getSendUserId().equals(item.getSendUserId())).findFirst();
            if(first.isPresent()){
                ImUnreadNumModel defaultItem = first.get();
                item.setDefaultMessage(defaultItem.getDefaultMessage());
                item.setDefaultMessageType(defaultItem.getDefaultMessageType());
                item.setDefaultMessageTime(defaultItem.getDefaultMessageTime());
            }
        }
        return list;
    }

    @Override
    public int getUnreadCount(String sendUserId, String receiveUserId) {
        QueryWrapper<ImContentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ImContentEntity::getSendUserId, sendUserId).eq(ImContentEntity::getReceiveUserId, receiveUserId).eq(ImContentEntity::getEnabledMark, 0);
        return (int) this.count(queryWrapper);
    }

    @Override
    @DSTransactional
    public void sendMessage(String sendUserId, String receiveUserId, String message, String messageType) {
        ImContentEntity entity = new ImContentEntity();
        entity.setId(RandomUtil.uuId());
        entity.setSendUserId(sendUserId);
        entity.setSendTime(new Date());
        entity.setReceiveUserId(receiveUserId);
        entity.setEnabledMark(0);
        entity.setContent(message);
        entity.setContentType(messageType);
        this.save(entity);

        //写入到会话表中
        ImReplySavaModel imReplySavaModel = new ImReplySavaModel(sendUserId, receiveUserId, entity.getSendTime());
        ImReplyEntity imReplyEntity = BeanUtil.toBean(imReplySavaModel, ImReplyEntity.class);
        imReplyService.savaImReply(imReplyEntity);
    }

    @Override
    public void readMessage(String sendUserId, String receiveUserId) {
        QueryWrapper<ImContentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ImContentEntity::getSendUserId, sendUserId);
        queryWrapper.lambda().eq(ImContentEntity::getReceiveUserId, receiveUserId);
        queryWrapper.lambda().eq(ImContentEntity::getEnabledMark, 0);
        List<ImContentEntity> list = this.list(queryWrapper);
        for (ImContentEntity entity : list) {
            entity.setEnabledMark(1);
            entity.setReceiveTime(new Date());
            this.updateById(entity);
        }
    }

//    @Override
//    public ImContentEntity getList(String userId, String receiveUserId) {
//        QueryWrapper<ImContentEntity> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lambda().eq(ImContentEntity::getSendUserId, userId)
//                .and(t -> t.eq(ImContentEntity::getReceiveUserId, receiveUserId)).orderByDesc(ImContentEntity::getReceiveTime);
//        List<ImContentEntity> list = this.list(queryWrapper);
//        return list.size() > 0 ? list.get(0) : null;
//    }


    @Override
    public boolean deleteChatRecord(String sendUserId, String receiveUserId) {
        QueryWrapper<ImContentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().and(t-> {
            t.eq(ImContentEntity::getSendUserId, receiveUserId)
                    .eq(ImContentEntity::getReceiveUserId, sendUserId).or();
            t.eq(ImContentEntity::getReceiveUserId, receiveUserId)
                    .eq(ImContentEntity::getSendUserId, sendUserId);
        });
        List<ImContentEntity> list = this.list(queryWrapper);
        for (ImContentEntity entity : list) {
            if(entity.getDeleteUserId()!=null){
                if(!entity.getDeleteUserId().equals(sendUserId)) {
                    entity.setEnabledMark(1);
                    this.updateById(entity);
                }
            }
            entity.setDeleteUserId(sendUserId);
            this.updateById(entity);
        }
        QueryWrapper<ImContentEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ImContentEntity::getEnabledMark,1);
        this.remove(wrapper);
        return false;
    }
}
