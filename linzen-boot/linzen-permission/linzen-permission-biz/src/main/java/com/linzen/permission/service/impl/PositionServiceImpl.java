package com.linzen.permission.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.constant.PermissionConst;
import com.linzen.permission.entity.*;
import com.linzen.permission.mapper.PositionMapper;
import com.linzen.permission.model.position.PaginationPosition;
import com.linzen.permission.service.*;
import com.linzen.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 岗位信息
 *
 * @copyright 领致信息技术有限公司
 * @author FHNP
 * @version V0.0.1
 * @date 2023-04-01
 */
@Service
public class PositionServiceImpl extends SuperServiceImpl<PositionMapper, SysPositionEntity> implements PositionService {

    @Autowired
    private AuthorizeService authorizeService;
    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CacheKeyUtil cacheKeyUtil;
    @Autowired
    private OrganizeRelationService organizeRelationService;
    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private OrganizeAdministratorService organizeAdministratorService;
    @Autowired
    private DictionaryDataService dictionaryDataService;

    @Override
    public List<SysPositionEntity> getList(boolean filterdelFlag) {
        QueryWrapper<SysPositionEntity> queryWrapper = new QueryWrapper<>();
        if (filterdelFlag) {
            queryWrapper.lambda().eq(SysPositionEntity::getEnabledMark, 1);
        }
        queryWrapper.lambda().orderByAsc(SysPositionEntity::getSortCode).orderByDesc(SysPositionEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<SysPositionEntity> getPosList(List<String> idList) {
        if (idList.size()>0){
            QueryWrapper<SysPositionEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(SysPositionEntity::getId,idList).select(SysPositionEntity::getId, SysPositionEntity::getFullName, SysPositionEntity::getEnabledMark);
            return this.list(queryWrapper);
        }
        return new ArrayList<>();
    }

    @Override
    public List<SysPositionEntity> getPosList(Set<String> idList) {
        if (idList.size()>0){
            QueryWrapper<SysPositionEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().orderByAsc(SysPositionEntity::getSortCode).orderByDesc(SysPositionEntity::getCreatorTime);
            queryWrapper.lambda().select(SysPositionEntity::getId, SysPositionEntity::getFullName).in(SysPositionEntity::getId,idList);
            return this.list(queryWrapper);
        }
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getPosMap() {
        QueryWrapper<SysPositionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(SysPositionEntity::getId, SysPositionEntity::getFullName);
        return this.list(queryWrapper).stream().collect(Collectors.toMap(SysPositionEntity::getId, SysPositionEntity::getFullName));
    }

    @Override
    public Map<String, Object> getPosEncodeAndName() {
        QueryWrapper<SysPositionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(SysPositionEntity::getId, SysPositionEntity::getFullName, SysPositionEntity::getEnCode);
        return this.list(queryWrapper).stream().collect(Collectors.toMap(p->p.getFullName() + "/" + p.getEnCode(), SysPositionEntity::getId));
    }


    @Override
    public List<SysPositionEntity> getPosRedisList() {
        if(redisUtil.exists(cacheKeyUtil.getPositionList())){
            return JsonUtil.createJsonToList(redisUtil.getString(cacheKeyUtil.getPositionList()).toString(), SysPositionEntity.class);
        }
        QueryWrapper<SysPositionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysPositionEntity::getEnabledMark,1);

        List<SysPositionEntity> list=this.list(queryWrapper);
        if(list.size()>0){
            redisUtil.insert(cacheKeyUtil.getPositionList(), JsonUtil.createObjectToString(list),300);
        }
        return list;
    }

    @Override
    public List<SysPositionEntity> getList(PaginationPosition paginationPosition) {
        // 需要查询哪些组织
        List<String> orgIds = new ArrayList<>();
        // 所有有权限的组织
        Set<String> orgId = new HashSet<>(16);
        if (!userProvider.get().getIsAdministrator()) {
            // 通过权限转树
            List<SysOrganizeAdministratorEntity> listss = organizeAdministratorService.getOrganizeAdministratorEntity(userProvider.get().getUserId());
            // 判断自己是哪些组织的管理员
            listss.forEach(t -> {
                if (t != null) {
                    if (t.getThisLayerSelect() != null && t.getThisLayerSelect() == 1) {
                        orgId.add(t.getOrganizeId());
                    }
                    if (t.getSubLayerSelect() != null && t.getSubLayerSelect() == 1) {
                        List<String> underOrganizations = organizeService.getUnderOrganizations(t.getOrganizeId(), false);
                        orgId.addAll(underOrganizations);
                    }
                }
            });
        } else {
            orgId.addAll(organizeService.getOrgMapsAll(SysOrganizeEntity::getId).keySet());
        }
        if (orgId.size() == 0) {
            return new ArrayList<>();
        }
        QueryWrapper<SysPositionEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(paginationPosition.getKeyword())) {
            queryWrapper.lambda().and(
                    t -> t.like(SysPositionEntity::getFullName, paginationPosition.getKeyword())
                            .or().like(SysPositionEntity::getEnCode, paginationPosition.getKeyword())
            );
        }
        if (paginationPosition.getEnabledMark() != null) {
            queryWrapper.lambda().eq(SysPositionEntity::getEnabledMark, paginationPosition.getEnabledMark());
        }
        if (StringUtil.isNotEmpty(paginationPosition.getEnCode())) {
            queryWrapper.lambda().eq(SysPositionEntity::getType, paginationPosition.getEnCode());
        }
        if (StringUtil.isNotEmpty(paginationPosition.getOrganizeId())) {
            List<String> underOrganizations = organizeService.getUnderOrganizations(paginationPosition.getOrganizeId(), false);
            // 判断哪些组织时有权限的
            List<String> collect = underOrganizations.stream().filter(orgId::contains).collect(Collectors.toList());
            orgIds.add(paginationPosition.getOrganizeId());
            orgIds.addAll(collect);
            orgIds.add(paginationPosition.getOrganizeId());
            queryWrapper.lambda().in(SysPositionEntity::getOrganizeId, orgIds);
        } else {
            queryWrapper.lambda().in(SysPositionEntity::getOrganizeId, orgId);
        }
        long count = this.count(queryWrapper);
        queryWrapper.lambda().select(SysPositionEntity::getId, SysPositionEntity::getEnCode, SysPositionEntity::getCreatorTime,
                SysPositionEntity::getOrganizeId, SysPositionEntity::getEnabledMark, SysPositionEntity::getFullName,
                SysPositionEntity::getSortCode, SysPositionEntity::getType);
        queryWrapper.lambda().orderByAsc(SysPositionEntity::getSortCode).orderByDesc(SysPositionEntity::getCreatorTime);
        Page<SysPositionEntity> page = new Page<>(paginationPosition.getCurrentPage(), paginationPosition.getPageSize(), count, false);
        page.setOptimizeCountSql(false);
        IPage<SysPositionEntity> iPage = this.page(page, queryWrapper);
        return paginationPosition.setData(iPage.getRecords(), page.getTotal());
    }

    @Override
    public List<SysPositionEntity> getListByUserId(String userId) {
        QueryWrapper<SysPositionEntity> query = new QueryWrapper<>();
        List<String> ids = new ArrayList<>();
        userRelationService.getListByObjectType(userId, PermissionConst.POSITION).forEach(r->{
            ids.add(r.getObjectId());
        });
        if(ids.size() > 0){
            query.lambda().in(SysPositionEntity::getId, ids);
            return this.list(query);
        }else {
            return new ArrayList<>();
        }
    }

    @Override
    public SysPositionEntity getInfo(String id) {
        QueryWrapper<SysPositionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysPositionEntity::getId,id);
        return this.getOne(queryWrapper);
    }

    @Override
    public SysPositionEntity getByFullName(String fullName) {
        SysPositionEntity positionEntity = new SysPositionEntity();
        QueryWrapper<SysPositionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysPositionEntity::getFullName, fullName);
        queryWrapper.lambda().select(SysPositionEntity::getId);
        List<SysPositionEntity> list = this.list(queryWrapper);
        if (list.size() > 0) {
            positionEntity = list.get(0);
        }
        return positionEntity;
    }

    @Override
    public SysPositionEntity getByFullName(String fullName, String encode) {
        SysPositionEntity positionEntity = new SysPositionEntity();
        QueryWrapper<SysPositionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysPositionEntity::getFullName, fullName);
        queryWrapper.lambda().eq(SysPositionEntity::getEnCode, encode);
        queryWrapper.lambda().select(SysPositionEntity::getId);
        List<SysPositionEntity> list = this.list(queryWrapper);
        if (list.size() > 0) {
            positionEntity = list.get(0);
        }
        return positionEntity;
    }

    @Override
    public boolean isExistByFullName(SysPositionEntity entity, boolean isFilter) {
        QueryWrapper<SysPositionEntity> queryWrapper = new QueryWrapper<>();
        if(entity != null) {
            queryWrapper.lambda().eq(SysPositionEntity::getFullName, entity.getFullName());
        }
        //是否需要过滤
        if (isFilter) {
            queryWrapper.lambda().ne(SysPositionEntity::getId, entity.getId());
        }
        List<SysPositionEntity> entityList = this.list(queryWrapper);
        for (SysPositionEntity positionEntity : entityList) {
            //如果组织id相同则代表已存在
            if (entity != null && entity.getOrganizeId().equals(positionEntity.getOrganizeId())){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isExistByEnCode(SysPositionEntity entity, boolean isFilter) {
        QueryWrapper<SysPositionEntity> queryWrapper = new QueryWrapper<>();
        if(entity != null){
            queryWrapper.lambda().eq(SysPositionEntity::getEnCode, entity.getEnCode());
            if (isFilter) {
                queryWrapper.lambda().ne(SysPositionEntity::getId, entity.getId());
            }
        }
        List<SysPositionEntity> entityList = this.list(queryWrapper);
//        for (PositionEntity positionEntity : entityList) {
//            //如果组织id相同则代表已存在
//            if (entity != null && entity.getOrganizeId().equals(positionEntity.getOrganizeId())){
//                return true;
//            }
//        }
        return entityList.size() > 0;
    }

    @Override
    public void create(SysPositionEntity entity) {
        if (StringUtil.isEmpty(entity.getId())) {
            entity.setId(RandomUtil.uuId());
        }
        entity.setCreatorUserId(userProvider.get().getUserId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, SysPositionEntity entity) {
        entity.setId(id);
        entity.setUpdateTime(new Date());
        entity.setUpdateUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    @DSTransactional
    public void delete(SysPositionEntity entity) {
        this.removeById(entity.getId());
        QueryWrapper<SysUserRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserRelationEntity::getObjectId,entity.getId());
        userRelationService.remove(queryWrapper);
        QueryWrapper<SysAuthorizeEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysAuthorizeEntity::getObjectId,entity.getId());
        authorizeService.remove(wrapper);
    }

    @Override
    @DSTransactional
    public boolean first(String id) {
        boolean isOk = false;
        //获取要上移的那条数据的信息
        SysPositionEntity upEntity = this.getById(id);
        Long upSortCode = upEntity.getSortCode() == null ? 0 : upEntity.getSortCode();
        //查询上几条记录
        QueryWrapper<SysPositionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .lt(SysPositionEntity::getSortCode, upSortCode)
                .eq(SysPositionEntity::getOrganizeId,upEntity.getOrganizeId())
                .orderByDesc(SysPositionEntity::getSortCode);
        List<SysPositionEntity> downEntity = this.list(queryWrapper);
        if(downEntity.size()>0){
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
    @DSTransactional
    public boolean next(String id) {
        boolean isOk = false;
        //获取要下移的那条数据的信息
        SysPositionEntity downEntity = this.getById(id);
        Long upSortCode = downEntity.getSortCode() == null ? 0 : downEntity.getSortCode();
        //查询下几条记录
        QueryWrapper<SysPositionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .gt(SysPositionEntity::getSortCode, upSortCode)
                .eq(SysPositionEntity::getOrganizeId,downEntity.getOrganizeId())
                .orderByAsc(SysPositionEntity::getSortCode);
        List<SysPositionEntity> upEntity = this.list(queryWrapper);
        if(upEntity.size()>0){
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

    @Override
    public List<SysPositionEntity> getPositionName(List<String> id, boolean filterdelFlag) {
        List<SysPositionEntity> roleList = new ArrayList<>();
        if (id.size() > 0) {
            QueryWrapper<SysPositionEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(SysPositionEntity::getId, id);
            roleList = this.list(queryWrapper);
        }
        return roleList;
    }

    @Override
    public List<SysPositionEntity> getPositionName(List<String> id, String keyword) {
        List<SysPositionEntity> roleList = new ArrayList<>();
        if (id.size() > 0) {
            QueryWrapper<SysPositionEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(SysPositionEntity::getId, id);
            //关键字（名称、编码）
            if (!StringUtil.isEmpty(keyword)) {
                queryWrapper.lambda().and(
                        t->t.like(SysPositionEntity::getFullName,keyword)
                                .or().like(SysPositionEntity::getEnCode,keyword)
                );
            }
            roleList = this.list(queryWrapper);
        }
        return roleList;
    }

    @Override
    public List<SysPositionEntity> getListByOrganizeId(List<String> organizeIds, boolean delFlag) {
        if (organizeIds.size() == 0) {
            return new ArrayList<>();
        }
        QueryWrapper<SysPositionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(SysPositionEntity::getOrganizeId, organizeIds);
        if (delFlag) {
            queryWrapper.lambda().eq(SysPositionEntity::getEnabledMark, 1);
        }
        queryWrapper.lambda().orderByAsc(SysPositionEntity::getSortCode).orderByDesc(SysPositionEntity::getCreatorTime);
//        queryWrapper.lambda().select(PositionEntity::getId, PositionEntity::getFullName);
        return this.list(queryWrapper);
    }

    @Override
    public List<SysPositionEntity> getListByOrgIdAndUserId(String organizeId, String userId) {
        // 用户绑定的所有岗位
        List<String> positionIds = userRelationService.getListByUserIdAndObjType(userId, PermissionConst.POSITION).stream()
                .map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
        if(positionIds.size() > 0){
            List<SysPositionEntity> positionEntities = this.listByIds(positionIds);
            return positionEntities.stream().filter(p-> p.getOrganizeId().equals(organizeId)).collect(Collectors.toList());
        }else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<SysPositionEntity> getListByFullName(String fullName, String enCode) {
        QueryWrapper<SysPositionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysPositionEntity::getFullName, fullName).eq(SysPositionEntity::getEnCode, enCode);
        return this.list(queryWrapper);
    }

    @Override
    public List<SysPositionEntity> getCurPositionsByOrgId(String orgId) {
        String userId = userProvider.get().getUserId();
        List<SysUserRelationEntity> userRelations = userRelationService.getListByObjectType(userId, PermissionConst.POSITION);
        List<SysPositionEntity> positions = new ArrayList<>();
        userRelations.forEach(ur->{
            SysPositionEntity entity = this.getInfo(ur.getObjectId());
            if(entity.getOrganizeId().equals(orgId)){
                positions.add(entity);
            }
        });
        return positions;
    }
}
