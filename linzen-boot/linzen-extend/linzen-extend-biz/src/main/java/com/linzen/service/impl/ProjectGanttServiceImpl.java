package com.linzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.Page;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.entity.ProjectGanttEntity;
import com.linzen.mapper.ProjectGanttMapper;
import com.linzen.service.ProjectGanttService;
import com.linzen.util.RandomUtil;
import com.linzen.util.UserProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 订单明细
 *
 * @author FHNP
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class ProjectGanttServiceImpl extends SuperServiceImpl<ProjectGanttMapper, ProjectGanttEntity> implements ProjectGanttService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<ProjectGanttEntity> getList(Page page) {
        QueryWrapper<ProjectGanttEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProjectGanttEntity::getType, 1).orderByAsc(ProjectGanttEntity::getSortCode)
                .orderByDesc(ProjectGanttEntity::getCreatorTime);
        if (!StringUtils.isEmpty(page.getKeyword())) {
            queryWrapper.lambda().and(
                    t -> t.like(ProjectGanttEntity::getEnCode, page.getKeyword())
                           .or().like(ProjectGanttEntity::getFullName, page.getKeyword())
            );
        }
        return this.list(queryWrapper);
    }

    @Override
    public List<ProjectGanttEntity> getTaskList(String projectId) {
        ProjectGanttEntity entity = this.getInfo(projectId);
        QueryWrapper<ProjectGanttEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProjectGanttEntity::getType, 2).eq(ProjectGanttEntity::getProjectId, projectId).orderByAsc(ProjectGanttEntity::getSortCode);
        List<ProjectGanttEntity> list = this.list(queryWrapper);
        list.add(entity);
        return list;
    }

    @Override
    public ProjectGanttEntity getInfo(String id) {
        QueryWrapper<ProjectGanttEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProjectGanttEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean allowDelete(String id) {
        QueryWrapper<ProjectGanttEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().and(t ->
                t.eq(ProjectGanttEntity::getParentId, id).or().eq(ProjectGanttEntity::getProjectId, id)
        );
        return this.list(queryWrapper).size() < 1;
    }

    @Override
    public void delete(ProjectGanttEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public void create(ProjectGanttEntity entity) {
        entity.setId(RandomUtil.uuId());
        if (entity.getEnabledMark() == null) {
            entity.setEnabledMark(1);
        }
        entity.setSortCode(RandomUtil.parses());
        entity.setCreatorUserId(userProvider.get().getUserId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, ProjectGanttEntity entity) {
        entity.setId(id);
        entity.setUpdateTime(new Date());
        entity.setUpdateUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<ProjectGanttEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProjectGanttEntity::getFullName, fullName);
        if (!StringUtils.isEmpty(id)) {
            queryWrapper.lambda().ne(ProjectGanttEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<ProjectGanttEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProjectGanttEntity::getEnCode, enCode);
        if (!StringUtils.isEmpty(id)) {
            queryWrapper.lambda().ne(ProjectGanttEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    @Transactional
    public boolean first(String id) {
        boolean isOk = false;
        //获取要上移的那条数据的信息
        ProjectGanttEntity upEntity = this.getById(id);
        Long upSortCode = upEntity.getSortCode() == null ? 0 : upEntity.getSortCode();
        //查询上几条记录
        QueryWrapper<ProjectGanttEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .lt(ProjectGanttEntity::getSortCode, upSortCode)
                .eq(ProjectGanttEntity::getParentId, upEntity.getParentId())
                .orderByDesc(ProjectGanttEntity::getSortCode);
        List<ProjectGanttEntity> downEntity = this.list(queryWrapper);
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
    @Transactional
    public boolean next(String id) {
        boolean isOk = false;
        //获取要下移的那条数据的信息
        ProjectGanttEntity downEntity = this.getById(id);
        Long upSortCode = downEntity.getSortCode() == null ? 0 : downEntity.getSortCode();
        //查询下几条记录
        QueryWrapper<ProjectGanttEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .gt(ProjectGanttEntity::getSortCode, upSortCode)
                .eq(ProjectGanttEntity::getParentId,downEntity.getParentId())
                .orderByAsc(ProjectGanttEntity::getSortCode);
        List<ProjectGanttEntity> upEntity = this.list(queryWrapper);
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
}
