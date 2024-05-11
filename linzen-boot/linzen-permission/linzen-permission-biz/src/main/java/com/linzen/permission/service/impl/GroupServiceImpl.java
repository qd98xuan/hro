package com.linzen.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.permission.entity.GroupEntity;
import com.linzen.permission.mapper.GroupMapper;
import com.linzen.permission.model.usergroup.PaginationGroup;
import com.linzen.permission.service.GroupService;
import com.linzen.util.DateUtil;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分组管理业务类实现类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class GroupServiceImpl extends SuperServiceImpl<GroupMapper, GroupEntity> implements GroupService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<GroupEntity> getList(PaginationGroup pagination) {
        QueryWrapper<GroupEntity> queryWrapper = new QueryWrapper<>();
        // 定义变量判断是否需要使用修改时间倒序
        boolean flag = false;
        // 判断关键字
        String keyword = pagination.getKeyword();
        if (StringUtil.isNotEmpty(keyword)) {
            flag = true;
            queryWrapper.lambda().and(
                    t -> t.like(GroupEntity::getFullName, keyword)
                            .or().like(GroupEntity::getEnCode, keyword)
                            .or().like(GroupEntity::getDescription, keyword)
            );
        }
        if (pagination.getEnabledMark() != null) {
            queryWrapper.lambda().eq(GroupEntity::getEnabledMark, pagination.getEnabledMark());
        }
        if (StringUtil.isNotEmpty(pagination.getType())) {
            flag = true;
            queryWrapper.lambda().eq(GroupEntity::getType, pagination.getType());
        }
        // 获取列表
        queryWrapper.lambda().orderByAsc(GroupEntity::getSortCode).orderByDesc(GroupEntity::getCreatorTime);
        if (flag) {
            queryWrapper.lambda().orderByDesc(GroupEntity::getUpdateTime);
        }
        Page<GroupEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<GroupEntity> iPage = this.page(page, queryWrapper);
        return pagination.setData(iPage.getRecords(), iPage.getTotal());
    }

    @Override
    public List<GroupEntity> list() {
        QueryWrapper<GroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(GroupEntity::getEnabledMark, 1);
        queryWrapper.lambda().orderByAsc(GroupEntity::getSortCode).orderByDesc(GroupEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public Map<String, Object> getGroupMap() {
        QueryWrapper<GroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(GroupEntity::getId,GroupEntity::getFullName);
        return this.list(queryWrapper).stream().collect(Collectors.toMap(GroupEntity::getId,GroupEntity::getFullName));
    }

    @Override
    public Map<String, Object> getGroupEncodeMap() {
        QueryWrapper<GroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(GroupEntity::getId,GroupEntity::getFullName,GroupEntity::getEnCode);
        return this.list(queryWrapper).stream().collect(Collectors.toMap(group->group.getFullName() + "/" + group.getEnCode(),GroupEntity::getId));
    }

    @Override
    public GroupEntity getInfo(String id) {
        QueryWrapper<GroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(GroupEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public GroupEntity getInfo(String fullName,String enCode) {
        QueryWrapper<GroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(GroupEntity::getFullName, fullName);
        queryWrapper.lambda().eq(GroupEntity::getEnCode, enCode);
        return this.getOne(queryWrapper);
    }

    @Override
    public void crete(GroupEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreatorUserId(userProvider.get().getUserId());
        entity.setCreatorTime(DateUtil.getNowDate());
        this.save(entity);
    }

    @Override
    public Boolean update(String id, GroupEntity entity) {
        entity.setId(id);
        entity.setUpdateUserId(userProvider.get().getUserId());
        entity.setUpdateTime(DateUtil.getNowDate());
        return this.updateById(entity);
    }

    @Override
    public void delete(GroupEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public Boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<GroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(GroupEntity::getFullName, fullName);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(GroupEntity::getId, id);
        }
        return this.count(queryWrapper) > 0;
    }

    @Override
    public Boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<GroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(GroupEntity::getEnCode, enCode);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(GroupEntity::getId, id);
        }
        return this.count(queryWrapper) > 0;
    }

    @Override
    public List<GroupEntity> getListByIds(List<String> list, Boolean filterdelFlag) {
        if (list.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        QueryWrapper<GroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(GroupEntity::getId, list);
        if (filterdelFlag) {
            queryWrapper.lambda().eq(GroupEntity::getEnabledMark, 1);
        }
        return this.list(queryWrapper);
    }

}
