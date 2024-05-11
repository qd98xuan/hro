package com.linzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.Pagination;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.entity.WorkLogEntity;
import com.linzen.entity.WorkLogShareEntity;
import com.linzen.mapper.WorkLogMapper;
import com.linzen.service.WorkLogService;
import com.linzen.service.WorkLogShareService;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 工作日志
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class WorkLogServiceImpl extends SuperServiceImpl<WorkLogMapper, WorkLogEntity> implements WorkLogService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private WorkLogShareService workLogShareService;

    @Override
    public List<WorkLogEntity> getSendList(Pagination pageModel) {
        QueryWrapper<WorkLogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(WorkLogEntity::getCreatorUserId, userProvider.get().getUserId());
        //app搜索
        if(StringUtil.isNotEmpty(pageModel.getKeyword())){
            queryWrapper.lambda().like(WorkLogEntity::getTitle,pageModel.getKeyword());
        }
        //排序
        if (StringUtils.isEmpty(pageModel.getSidx())) {
            queryWrapper.lambda().orderByDesc(WorkLogEntity::getCreatorTime);
        } else {
            queryWrapper = "asc".equals(pageModel.getSort().toLowerCase()) ? queryWrapper.orderByAsc(pageModel.getSidx()) : queryWrapper.orderByDesc(pageModel.getSidx());
        }
        Page page = new Page(pageModel.getCurrentPage(), pageModel.getPageSize());
        IPage<WorkLogEntity> IPages = this.page(page, queryWrapper);
        return pageModel.setData(IPages.getRecords(), page.getTotal());
    }

    @Override
    public List<WorkLogEntity> getReceiveList(Pagination pageModel) {
        QueryWrapper<WorkLogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().like(WorkLogEntity::getToUserId, userProvider.get().getUserId());
        //app搜索
        if(StringUtil.isNotEmpty(pageModel.getKeyword())){
            queryWrapper.lambda().like(WorkLogEntity::getTitle,pageModel.getKeyword());
        }
        //排序
        if (StringUtils.isEmpty(pageModel.getSidx())) {
            queryWrapper.lambda().orderByDesc(WorkLogEntity::getCreatorTime);
        } else {
            queryWrapper = "asc".equals(pageModel.getSort().toLowerCase()) ? queryWrapper.orderByAsc(pageModel.getSidx()) : queryWrapper.orderByDesc(pageModel.getSidx());
        }
        Page page = new Page(pageModel.getCurrentPage(), pageModel.getPageSize());
        IPage<WorkLogEntity> IPages = this.page(page, queryWrapper);
        return pageModel.setData(IPages.getRecords(), page.getTotal());
    }

    @Override
    public WorkLogEntity getInfo(String id) {
        QueryWrapper<WorkLogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(WorkLogEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional
    public void create(WorkLogEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setSortCode(RandomUtil.parses());
        entity.setCreatorTime(new Date());
        entity.setCreatorUserId(userProvider.get().getUserId());
        this.save(entity);
        String[] toUserIds = entity.getToUserId().split(",");
        for (String userIds : toUserIds) {
            WorkLogShareEntity workLogShare = new WorkLogShareEntity();
            workLogShare.setId(RandomUtil.uuId());
            workLogShare.setShareTime(new Date());
            workLogShare.setWorkLogId(entity.getId());
            workLogShare.setShareUserId(userIds);
            workLogShareService.save(workLogShare);
        }
    }

    @Override
    @Transactional
    public boolean update(String id, WorkLogEntity entity) {
        boolean flag = false;
        entity.setId(id);
        entity.setUpdateTime(new Date());
        entity.setUpdateUserId(userProvider.get().getUserId());
        flag = this.updateById(entity);
        QueryWrapper<WorkLogShareEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(WorkLogShareEntity::getWorkLogId,entity.getId());
        workLogShareService.remove(queryWrapper);
        String[] toUserIds = entity.getToUserId().split(",");
        for (String userIds : toUserIds) {
            WorkLogShareEntity workLogShare = new WorkLogShareEntity();
            workLogShare.setId(RandomUtil.uuId());
            workLogShare.setShareTime(new Date());
            workLogShare.setWorkLogId(entity.getId());
            workLogShare.setShareUserId(userIds);
            workLogShareService.save(workLogShare);
        }
        return flag;
    }

    @Override
    @Transactional
    public void delete(WorkLogEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
        QueryWrapper<WorkLogShareEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(WorkLogShareEntity::getWorkLogId,entity.getId());
        workLogShareService.remove(queryWrapper);
    }

}
