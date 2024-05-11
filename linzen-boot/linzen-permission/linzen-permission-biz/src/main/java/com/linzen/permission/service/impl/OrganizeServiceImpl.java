package com.linzen.permission.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.google.common.collect.ImmutableMap;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.constant.MsgCode;
import com.linzen.constant.PermissionConst;
import com.linzen.message.service.SynThirdDingTalkService;
import com.linzen.message.service.SynThirdQyService;
import com.linzen.permission.entity.SysOrganizeAdministratorEntity;
import com.linzen.permission.entity.SysOrganizeEntity;
import com.linzen.permission.entity.SysPositionEntity;
import com.linzen.permission.mapper.OrganizeMapper;
import com.linzen.permission.model.organize.OrganizeConditionModel;
import com.linzen.permission.model.organize.OrganizeModel;
import com.linzen.permission.service.*;
import com.linzen.util.*;
import com.linzen.util.treeutil.SumTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * 组织机构
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class OrganizeServiceImpl extends SuperServiceImpl<OrganizeMapper, SysOrganizeEntity> implements OrganizeService {

    @Autowired
    private PositionService positionService;
    @Autowired
    private CacheKeyUtil cacheKeyUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private OrganizeRelationService organizeRelationService;
    @Autowired
    private SynThirdQyService synThirdQyService;
    @Autowired
    private SynThirdDingTalkService synThirdDingTalkService;
    @Autowired
    private Executor threadPoolExecutor;
    @Autowired
    private OrganizeAdministratorService organizeAdministratorService;


    @Override
    public List<SysOrganizeEntity> getListAll(List<String> idAll, String keyWord) {
        // 定义变量判断是否需要使用修改时间倒序
        boolean flag = false;
        List<SysOrganizeEntity> list = new ArrayList<>();
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(keyWord)) {
            flag = true;
            queryWrapper.lambda().and(
                    t -> t.like(SysOrganizeEntity::getFullName, keyWord)
                            .or().like(SysOrganizeEntity::getEnCode, keyWord)
            );
        }
        // 排序
        queryWrapper.lambda().orderByAsc(SysOrganizeEntity::getSortCode)
                .orderByDesc(SysOrganizeEntity::getCreatorTime);
        if (flag) {
            queryWrapper.lambda().orderByDesc(SysOrganizeEntity::getUpdateTime);
        }
        if (idAll.size() > 0) {
            queryWrapper.lambda().in(SysOrganizeEntity::getId, idAll);
            list = this.list(queryWrapper);
        }
        return list;
    }

    @Override
    public List<SysOrganizeEntity> getParentIdList(String id) {
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysOrganizeEntity::getParentId, id);
        queryWrapper.lambda().eq(SysOrganizeEntity::getCategory, PermissionConst.DEPARTMENT);
        queryWrapper.lambda().orderByAsc(SysOrganizeEntity::getSortCode)
                .orderByDesc(SysOrganizeEntity::getCreatorTime);
        List<SysOrganizeEntity> list = this.list(queryWrapper);
        return list;
    }

    @Override
    public List<SysOrganizeEntity> getList(boolean filterdelFlag) {
        return getList(null, filterdelFlag);
    }

    @Override
    public List<SysOrganizeEntity> getListBydelFlag(Boolean enable) {
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        if (enable) {
            queryWrapper.lambda().eq(SysOrganizeEntity::getEnabledMark, 1);
        }
        Map<String, SysOrganizeEntity> orgMaps = getBaseOrgMaps(queryWrapper, ImmutableMap.of(
                SysOrganizeEntity::getSortCode, true,
                SysOrganizeEntity::getCreatorTime, false)
                , null);

//        Map<String, OrganizeEntity> entityList = new LinkedHashMap<>();
//        if (StringUtil.isNotEmpty(keyword)) {
//            getParentOrganize(orgMaps, orgMaps, entityList);
//            orgMaps.clear();
//            orgMaps = entityList;
//        }
        return new LinkedList<>(orgMaps.values());
    }

    @Override
    public SysOrganizeEntity getInfoByFullName(String fullName) {
        if (StringUtil.isEmpty(fullName)) {
            return null;
        }
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysOrganizeEntity::getFullName, fullName);
        List<SysOrganizeEntity> list = this.list(queryWrapper);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<SysOrganizeEntity> getList(String keyword, boolean filterdelFlag) {
        return new LinkedList<>(getOrgMaps(keyword, filterdelFlag, null).values());
    }


    /**
     * 获取组织信息
     * @return OrgId, OrgEntity
     */
    @Override
    public Map<String, SysOrganizeEntity> getOrgMapsAll(SFunction<SysOrganizeEntity, ?>... columns) {
        return getOrgMaps(null, false, null, columns);
    }

    /**
     * 获取组织信息
     * @param keyword
     * @param filterdelFlag
     * @param type
     * @return OrgId, OrgEntity
     */
    @Override
    public Map<String, SysOrganizeEntity> getOrgMaps(String keyword, boolean filterdelFlag, String type, SFunction<SysOrganizeEntity, ?>... columns) {
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(keyword)) {
            queryWrapper.lambda().and(
                    t -> t.like(SysOrganizeEntity::getFullName, keyword)
                            .or().like(SysOrganizeEntity::getEnCode, keyword.toLowerCase())
            );
        }
        if (filterdelFlag) {
            queryWrapper.lambda().eq(SysOrganizeEntity::getEnabledMark, 1);
        }
        if (StringUtil.isNotEmpty(type)) {
            queryWrapper.lambda().eq(SysOrganizeEntity::getCategory, type);
        }
        Map<String, SysOrganizeEntity> orgMaps = getBaseOrgMaps(queryWrapper, ImmutableMap.of(
                SysOrganizeEntity::getSortCode, true,
                SysOrganizeEntity::getCreatorTime, false)
        , null);

        Map<String, SysOrganizeEntity> entityList = new LinkedHashMap<>();
        if (StringUtil.isNotEmpty(keyword)) {
            getParentOrganize(orgMaps, orgMaps, entityList);
            orgMaps.clear();
            orgMaps = entityList;
        }
        return orgMaps;
    }


    /**
     * 组织基础过滤
     * @param queryWrapper
     * @param orderBy Map<Column, isAsc>
     * @param groupBy Column
     * @param columns query
     * @return
     */
    public Map<String, SysOrganizeEntity> getBaseOrgMaps(QueryWrapper<SysOrganizeEntity> queryWrapper, Map<SFunction<SysOrganizeEntity, ?>, Boolean> orderBy, List<SFunction<SysOrganizeEntity, ?>> groupBy, SFunction<SysOrganizeEntity, ?>... columns) {
        if(queryWrapper == null){
            queryWrapper = new QueryWrapper<>();
        }
        LambdaQueryWrapper<SysOrganizeEntity> lambdaQueryWrapper = queryWrapper.lambda();

        List<SFunction<SysOrganizeEntity, ?>> columnList;
        List<SFunction<SysOrganizeEntity, ?>> bigColumnList = null;
        //没有指定查询字段就返回全部字段
        if(columns == null || columns.length == 0){
            columnList = Arrays.asList(SysOrganizeEntity::getId
                    , SysOrganizeEntity::getParentId
                    , SysOrganizeEntity::getCategory
                    , SysOrganizeEntity::getEnCode
                    , SysOrganizeEntity::getFullName
                    , SysOrganizeEntity::getManagerId
                    , SysOrganizeEntity::getSortCode
                    , SysOrganizeEntity::getEnabledMark
                    , SysOrganizeEntity::getCreatorTime
                    , SysOrganizeEntity::getCreatorUserId
                    , SysOrganizeEntity::getUpdateTime
                    , SysOrganizeEntity::getUpdateUserId
                    , SysOrganizeEntity::getEnabledMark
                    , SysOrganizeEntity::getDeleteTime
                    , SysOrganizeEntity::getDeleteUserId
                    , SysOrganizeEntity::getTenantId);
            //把长文本字段分开查询, 默认带有排序， 数据量大的情况长文本字段参与排序速度非常慢
            bigColumnList = Arrays.asList(SysOrganizeEntity::getDescription
                    , SysOrganizeEntity::getPropertyJson
                    , SysOrganizeEntity::getOrganizeIdTree);
        }else{
            columnList = new ArrayList<>(Arrays.asList(columns));
            //指定字段中没有ID， 强制添加ID字段
            if(!columnList.contains((SFunction<SysOrganizeEntity, ?>) SysOrganizeEntity::getId)){
                columnList.add(SysOrganizeEntity::getId);
            }
        }
        lambdaQueryWrapper.select(columnList);
        QueryWrapper<SysOrganizeEntity> bigColumnQuery = null;
        if(bigColumnList != null){
            //获取大字段不参与排序
            bigColumnQuery = queryWrapper.clone();
        }
        //排序
        if(orderBy != null && !orderBy.isEmpty()){
            orderBy.forEach((k,v)->{
                lambdaQueryWrapper.orderBy(true, v, k);
            });
        }
        //分组
        if(groupBy != null && !groupBy.isEmpty()){
            lambdaQueryWrapper.groupBy(groupBy);
        }
        List<SysOrganizeEntity> list = this.list(queryWrapper);

        Map<String, SysOrganizeEntity> orgMaps = new LinkedHashMap<>(list.size(), 1);
        list.forEach(t->orgMaps.put(t.getId(), t));

        if(bigColumnList != null) {
            //获取大字段数据
            bigColumnQuery.lambda().select(SysOrganizeEntity::getId, SysOrganizeEntity::getOrganizeIdTree);
            List<SysOrganizeEntity> listBigFields = this.list(bigColumnQuery);
            listBigFields.forEach(t -> {
                SysOrganizeEntity organizeEntity = orgMaps.get(t.getId());
                if (organizeEntity != null) {
                    organizeEntity.setOrganizeIdTree(t.getOrganizeIdTree());
                }
            });
        }
        return orgMaps;
    }

    /**
     * 获取父级集合
     *
     * @param list       需要遍历的集合
     * @param entityList 结果集
     */
    private void getParentOrganize(Map<String, SysOrganizeEntity> list, Map<String, SysOrganizeEntity> searchList, Map<String, SysOrganizeEntity> entityList) {
        Map<String, SysOrganizeEntity> list1 = new LinkedHashMap<>();
        searchList.forEach((id, entity) -> {
            entityList.put(id, entity);
            SysOrganizeEntity info = list.get(id);
            if(info == null){
                info = getInfo(id);
            }
            if (Objects.nonNull(info)) {
                if (StringUtil.isNotEmpty(info.getParentId()) && !"-1".equals(info.getParentId())) {
                    SysOrganizeEntity organizeEntity = list.get(info.getParentId());
                    if(organizeEntity == null){
                        organizeEntity = getInfo(info.getParentId());
                    }
                    if (organizeEntity != null) {
                        list1.put(organizeEntity.getId(), organizeEntity);
                        getParentOrganize(list, list1, entityList);
                    }
                } else if (StringUtil.isNotEmpty(info.getParentId()) && "-1".equals(info.getParentId())) {
                    entityList.put(id, info);
                }
            }
        });
    }

    @Override
    public List<SysOrganizeEntity> getOrgEntityList(List<String> idList, Boolean enable) {
        if (!idList.isEmpty()) {
            QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(SysOrganizeEntity::getId, idList);
            if (enable) {
                queryWrapper.lambda().eq(SysOrganizeEntity::getEnabledMark, 1);
            }
//            queryWrapper.lambda().select(OrganizeEntity::getId, OrganizeEntity::getFullName);
            return this.list(queryWrapper);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<SysOrganizeEntity> getOrgEntityList(Set<String> idList) {
        if (idList.size() > 0) {
            QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().select(SysOrganizeEntity::getId, SysOrganizeEntity::getFullName).in(SysOrganizeEntity::getId, idList);
            List<SysOrganizeEntity> list = this.list(queryWrapper);
            return list;
        }
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getOrgMap() {
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(SysOrganizeEntity::getId, SysOrganizeEntity::getFullName);
        List<SysOrganizeEntity> list = this.list(queryWrapper);
        return list.stream().collect(Collectors.toMap(SysOrganizeEntity::getId, SysOrganizeEntity::getFullName));
    }

    @Override
    public Map<String, Object> getOrgEncodeAndName(String type) {
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(SysOrganizeEntity::getId, SysOrganizeEntity::getFullName , SysOrganizeEntity::getEnCode);
        queryWrapper.lambda().eq(SysOrganizeEntity::getCategory, type);
        List<SysOrganizeEntity> list = this.list(queryWrapper);
        return list.stream().collect(Collectors.toMap(o->o.getFullName()+ "/"+o.getEnCode(), SysOrganizeEntity::getId, (v1, v2)->v2));
    }
    @Override
    public Map<String, Object> getOrgNameAndId(String type) {
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(SysOrganizeEntity::getId, SysOrganizeEntity::getFullName);
        if (StringUtil.isNotEmpty(type)){
            queryWrapper.lambda().eq(SysOrganizeEntity::getCategory, type);
        }
        List<SysOrganizeEntity> list = this.list(queryWrapper);
        Map<String,Object> allOrgMap = new HashMap<>();
        for (SysOrganizeEntity entity : list){
            allOrgMap.put(entity.getFullName(),entity.getId());
        }
        return allOrgMap;
    }


    @Override
    public List<SysOrganizeEntity> getOrgRedisList() {
        if (redisUtil.exists(cacheKeyUtil.getOrganizeList())) {
            return JsonUtil.createJsonToList(redisUtil.getString(cacheKeyUtil.getOrganizeList()).toString(), SysOrganizeEntity.class);
        }
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysOrganizeEntity::getEnabledMark, 1);

        List<SysOrganizeEntity> list = this.list(queryWrapper);
        if (list.size() > 0) {
            redisUtil.insert(cacheKeyUtil.getOrganizeList(), JsonUtil.createObjectToString(list), 300);
        }
        return list;
    }

    @Override
    public SysOrganizeEntity getInfo(String id) {
        return this.getById(id);
    }

    @Override
    public SysOrganizeEntity getByFullName(String fullName) {
        SysOrganizeEntity organizeEntity = new SysOrganizeEntity();
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysOrganizeEntity::getFullName, fullName);
        queryWrapper.lambda().select(SysOrganizeEntity::getId);
        List<SysOrganizeEntity> list = this.list(queryWrapper);
        if (list.size() > 0) {
            organizeEntity = list.get(0);
        }
        return organizeEntity;
    }

    @Override
    public SysOrganizeEntity getByFullName(String fullName, String category, String enCode) {
        SysOrganizeEntity organizeEntity = new SysOrganizeEntity();
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysOrganizeEntity::getFullName, fullName);
        queryWrapper.lambda().eq(SysOrganizeEntity::getCategory, category);
        queryWrapper.lambda().eq(SysOrganizeEntity::getEnCode, enCode);
        queryWrapper.lambda().select(SysOrganizeEntity::getId);
        List<SysOrganizeEntity> list = this.list(queryWrapper);
        if (list.size() > 0) {
            organizeEntity = list.get(0);
        }
        return organizeEntity;
    }

    @Override
    public boolean isExistByFullName(SysOrganizeEntity entity, boolean isCheck, boolean isFilter) {
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysOrganizeEntity::getFullName, entity.getFullName());
        if (!isCheck) {
            if (isFilter) {
                queryWrapper.lambda().ne(SysOrganizeEntity::getId, entity.getId());
            }
            List<SysOrganizeEntity> entityList = this.list(queryWrapper);
            if (entityList.size() > 0) {
                for (SysOrganizeEntity organizeEntity : entityList) {
                    if (organizeEntity != null && organizeEntity.getParentId().equals(entity.getParentId()) && organizeEntity.getCategory().equals(entity.getCategory())) {
                        return true;
                    }
                }
            }
            return false;
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public void getOrganizeIdTree(String organizeId, List<String> organizeParentIdList) {
        SysOrganizeEntity entity = getInfo(organizeId);
        if (entity != null) {
            organizeParentIdList.add(entity.getId());
            if (StringUtil.isNotEmpty(entity.getParentId())) {
                getOrganizeIdTree(entity.getParentId(), organizeParentIdList);
            }
        }
    }

    @Override
    public void getOrganizeId(String organizeId, List<SysOrganizeEntity> organizeList) {
        SysOrganizeEntity entity = getInfo(organizeId);
        if (entity != null) {
            organizeList.add(entity);
            if (StringUtil.isNotEmpty(entity.getParentId())) {
                getOrganizeId(entity.getParentId(), organizeList);
            }
        }
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysOrganizeEntity::getEnCode, enCode);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(SysOrganizeEntity::getId, id);
        }
        return this.count(queryWrapper) > 0;
    }

    @Override
    public void create(SysOrganizeEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreatorUserId(userProvider.get().getUserId());
        // 拼上当前组织id
        String organizeIdTree = StringUtil.isNotEmpty(entity.getOrganizeIdTree()) ? entity.getOrganizeIdTree() + "," : "";
        entity.setOrganizeIdTree(organizeIdTree + entity.getId());
        if (!userProvider.get().getIsAdministrator()) {
            // 当前用户创建的组织要赋予权限
            SysOrganizeAdministratorEntity organizeAdministratorEntity = new SysOrganizeAdministratorEntity();
            organizeAdministratorEntity.setUserId(userProvider.get().getUserId());
            organizeAdministratorEntity.setOrganizeId(entity.getId());
            organizeAdministratorEntity.setThisLayerAdd(1);
            organizeAdministratorEntity.setThisLayerEdit(1);
            organizeAdministratorEntity.setThisLayerDelete(1);
            organizeAdministratorEntity.setThisLayerSelect(1);
            organizeAdministratorEntity.setSubLayerAdd(0);
            organizeAdministratorEntity.setSubLayerEdit(0);
            organizeAdministratorEntity.setSubLayerDelete(0);
            organizeAdministratorEntity.setSubLayerSelect(0);
            organizeAdministratorService.create(organizeAdministratorEntity);
        }
        this.save(entity);
        redisUtil.remove(cacheKeyUtil.getOrganizeInfoList());
    }

    @Override
    public boolean update(String id, SysOrganizeEntity entity) {
        entity.setId(id);
        entity.setUpdateTime(DateUtil.getNowDate());
        entity.setUpdateUserId(userProvider.get().getUserId());
        // 拼上当前组织id
        String organizeIdTree = StringUtil.isNotEmpty(entity.getOrganizeIdTree()) ? entity.getOrganizeIdTree() + "," : "";
        entity.setOrganizeIdTree(organizeIdTree + entity.getId());
        // 判断父级是否变化
        SysOrganizeEntity info = getInfo(id);
        boolean updateById = this.updateById(entity);
        if (info != null && !entity.getParentId().equals(info.getParentId())) {
            // 子集和父级都需要修改父级树
            update(entity, info.getCategory());
        }
        redisUtil.remove(cacheKeyUtil.getOrganizeInfoList());
        return updateById;
    }

    @Override
    public void update(SysOrganizeEntity entity, String category) {
        // 查询子级
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysOrganizeEntity::getParentId, entity.getId());
        if (PermissionConst.COMPANY.equals(category)) {
            queryWrapper.lambda().eq(SysOrganizeEntity::getCategory, PermissionConst.COMPANY);
        } else {
            queryWrapper.lambda().eq(SysOrganizeEntity::getCategory, PermissionConst.DEPARTMENT);
        }
        List<SysOrganizeEntity> list = this.list(queryWrapper);
        // 递归修改子组织的父级id字段
        for (SysOrganizeEntity organizeEntity : list) {
            List<String> list1 = new ArrayList<>();
            getOrganizeIdTree(organizeEntity.getId(), list1);
            // 倒叙排放
            Collections.reverse(list1);
            StringBuilder organizeIdTree = new StringBuilder();
            for (String organizeParentId : list1) {
                organizeIdTree.append("," + organizeParentId);
            }
            String organizeParentIdTree = organizeIdTree.toString();
            if (StringUtil.isNotEmpty(organizeParentIdTree)) {
                organizeParentIdTree = organizeParentIdTree.replaceFirst(",", "");
            }
            organizeEntity.setOrganizeIdTree(organizeParentIdTree);
            this.updateById(organizeEntity);
            redisUtil.remove(cacheKeyUtil.getOrganizeInfoList());
        }
    }

    @Override
    public ServiceResult<String> delete(String orgId) {
        String flag = this.allowDelete(orgId);
        if (flag == null) {
            SysOrganizeEntity organizeEntity = this.getInfo(orgId);
            if (organizeEntity != null) {
                this.removeById(orgId);
                redisUtil.remove(cacheKeyUtil.getOrganizeInfoList());
                threadPoolExecutor.execute(() -> {
                    try {
                        //删除部门后判断是否需要同步到企业微信
                        synThirdQyService.deleteDepartmentSysToQy(false, orgId, "");
                        //删除部门后判断是否需要同步到钉钉
                        synThirdDingTalkService.deleteDepartmentSysToDing(false, orgId, "");
                    } catch (Exception e) {
                        log.error("删除部门后同步失败到企业微信或钉钉失败，异常：" + e.getMessage());
                    }
                });
                return ServiceResult.success(MsgCode.SU003.get());
            }
            return ServiceResult.error(MsgCode.FA003.get());
        } else {
            return ServiceResult.error("此记录与\"" + flag + "\"关联引用，不允许被删除");
        }
    }

    @Override
    @DSTransactional
    public boolean first(String id) {
        boolean isOk = false;
        //获取要上移的那条数据的信息
        SysOrganizeEntity upEntity = this.getById(id);
        Long upSortCode = upEntity.getSortCode() == null ? 0 : upEntity.getSortCode();
        //查询上几条记录
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .lt(SysOrganizeEntity::getSortCode, upSortCode)
                .eq(SysOrganizeEntity::getParentId, upEntity.getParentId())
                .orderByDesc(SysOrganizeEntity::getSortCode);
        List<SysOrganizeEntity> downEntity = this.list(queryWrapper);
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
    @DSTransactional
    public boolean next(String id) {
        boolean isOk = false;
        //获取要下移的那条数据的信息
        SysOrganizeEntity downEntity = this.getById(id);
        Long upSortCode = downEntity.getSortCode() == null ? 0 : downEntity.getSortCode();
        //查询下几条记录
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .gt(SysOrganizeEntity::getSortCode, upSortCode)
                .eq(SysOrganizeEntity::getParentId, downEntity.getParentId())
                .orderByAsc(SysOrganizeEntity::getSortCode);
        List<SysOrganizeEntity> upEntity = this.list(queryWrapper);
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

    @Override
    public String allowDelete(String orgId) {
        // 组织底下是否有组织
        List<SysOrganizeEntity> list = getListByParentId(orgId);
        if (Objects.nonNull(list) && list.size() > 0) {
            return "组织";
        }
        // 组织底下是否有岗位
        List<SysPositionEntity> list1 = positionService.getListByOrganizeId(Collections.singletonList(orgId), false);
        if (Objects.nonNull(list1) && list1.size() > 0) {
            return "岗位";
        }
        // 组织底下是否有用户
        if (userRelationService.existByObj(PermissionConst.ORGANIZE, orgId)) {
            return "用户";
        }
        // 组织底下是否有角色
        if (organizeRelationService.existByObjTypeAndOrgId(PermissionConst.ROLE, orgId)) {
            return "角色";
        }
        return null;
    }

    @Override
    public List<SysOrganizeEntity> getOrganizeName(List<String> id) {
        List<SysOrganizeEntity> list = new ArrayList<>();
        if (id.size() > 0) {
            QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(SysOrganizeEntity::getId, id);
            queryWrapper.lambda().orderByAsc(SysOrganizeEntity::getSortCode).orderByDesc(SysOrganizeEntity::getCreatorTime);
            list = this.list(queryWrapper);
        }
        return list;
    }

    @Override
    public Map<String, SysOrganizeEntity> getOrganizeName(List<String> id, String keyword, boolean filterdelFlag, String type) {
        Map<String, SysOrganizeEntity> list = Collections.EMPTY_MAP;
        if (id.size() > 0) {
            QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(SysOrganizeEntity::getId, id);
            if (StringUtil.isNotEmpty(keyword)) {
                queryWrapper.lambda().and(
                        t -> t.like(SysOrganizeEntity::getFullName, keyword)
                                .or().like(SysOrganizeEntity::getEnCode, keyword)
                );
            }
            if (StringUtil.isNotEmpty(type)) {
                queryWrapper.lambda().eq(SysOrganizeEntity::getCategory, type);
            }
            if (filterdelFlag) {
                queryWrapper.lambda().eq(SysOrganizeEntity::getEnabledMark, 1);
            }
            list = getBaseOrgMaps(queryWrapper, ImmutableMap.of(
                    SysOrganizeEntity::getSortCode, true,
                    SysOrganizeEntity::getCreatorTime, false
            ), null);
//            Map<String, OrganizeEntity> orgList = new LinkedHashMap<>(id.size(), 1);
//            orgMaps.values().forEach(t -> {
//                if (StringUtil.isNotEmpty(t.getOrganizeIdTree())) {
//                    String[] split = t.getOrganizeIdTree().split(",");
//                    for (String orgId : split) {
//                        if (id.contains(orgId) && !orgList.containsKey(orgId)) {
//                            OrganizeEntity entity = orgMaps.get(orgId);
//                            if(entity == null){
//                                entity = getInfo(orgId);
//                            }
//                            if (entity != null) {
//                                orgList.put(orgId, entity);
//                            }
//                        }
//                    }
//                }
//            });
//            list = orgList;
        }
        return list;
    }

    @Override
    public List<SysOrganizeEntity> getOrganizeNameSort(List<String> id) {
        List<SysOrganizeEntity> list = new ArrayList<>();
        for (String orgId : id) {
            QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(SysOrganizeEntity::getId, orgId);
            queryWrapper.lambda().select(SysOrganizeEntity::getFullName);
            SysOrganizeEntity entity = this.getOne(queryWrapper);
            if (entity != null) {
                list.add(entity);
            }
        }
        return list;
    }

    @Override
    public List<String> getOrganize(String organizeParentId) {
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysOrganizeEntity::getParentId, organizeParentId);
        queryWrapper.lambda().select(SysOrganizeEntity::getId);
        List<String> list = this.list(queryWrapper).stream().map(t -> t.getId()).collect(Collectors.toList());
        return list;
    }

    @Override
    public List<String> getOrganizeByOraParentId(String organizeParentId) {
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysOrganizeEntity::getParentId, organizeParentId);
//        queryWrapper.lambda().select(OrganizeEntity::getId);
        List<SysOrganizeEntity> list = this.list(queryWrapper);
        return list.stream().map(t -> t.getId()).collect(Collectors.toList());
    }

    @Override
    public List<String> getUnderOrganizations(String organizeId, boolean filterdelFlag) {
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        if (filterdelFlag) {
            queryWrapper.lambda().eq(SysOrganizeEntity::getEnabledMark, 1);
        }
        queryWrapper.lambda().ne(SysOrganizeEntity::getId, organizeId);
        queryWrapper.lambda().like(SysOrganizeEntity::getOrganizeIdTree, organizeId);
        queryWrapper.lambda().select(SysOrganizeEntity::getId);
        return this.list(queryWrapper).stream().map(SysOrganizeEntity::getId).collect(Collectors.toList());
    }

    @Override
    public List<String> getUnderOrganizationss(String organizeId) {
        List<String> totalIds = new ArrayList<>();
        if (!userProvider.get().getIsAdministrator()) {
            // 得到有权限的组织
            List<String> collect = organizeAdministratorService.getListByAuthorize().stream().map(SysOrganizeEntity::getId).collect(Collectors.toList());
            totalIds = totalIds.stream().filter(t -> collect.contains(t)).collect(Collectors.toList());
        }else{
            totalIds = getUnderOrganizations(organizeId, false);
        }
        return totalIds;
    }

    @Override
    public List<SysOrganizeEntity> getListByFullName(String fullName) {
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysOrganizeEntity::getFullName, fullName);
        return this.list(queryWrapper);
    }

    @Override
    public List<SysOrganizeEntity> getListByParentId(String id) {
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysOrganizeEntity::getParentId, id);
        return this.list(queryWrapper);
    }

    @Override
    public List<SysOrganizeEntity> getAllOrgByUserId(String userId) {
        List<String> ids = new ArrayList<>();
        userRelationService.getAllOrgRelationByUserId(userId).forEach(r -> {
            ids.add(r.getObjectId());
        });
        return this.listByIds(ids);
    }

    @Override
    public String getFullNameByOrgIdTree(Map<String, String> idNameMaps, String orgIdTree, String regex) {
        if(idNameMaps == null || idNameMaps.isEmpty()){
            idNameMaps = this.getInfoList();
        }
        String fullName = "";
        if (StringUtil.isNotEmpty(orgIdTree)) {
            String[] split = orgIdTree.split(",");
            StringBuilder orgName = new StringBuilder();
            String tmpName;
            for (String orgId : split) {
                if (StringUtil.isEmpty(orgIdTree)) {
                    continue;
                }
                if((tmpName = idNameMaps.get(orgId)) != null){
                    orgName.append(regex).append(tmpName);
                }
            }
            if (orgName.length() > 0) {
                fullName = orgName.toString().replaceFirst(regex, "");
            }
        }
        return fullName;
    }

    @Override
    public String getOrganizeIdTree(SysOrganizeEntity entity) {
        List<String> list = new ArrayList<>();
        this.getOrganizeIdTree(entity.getParentId(), list);
        // 倒叙排放
        Collections.reverse(list);
        StringBuilder organizeIdTree = new StringBuilder();
        for (String organizeParentId : list) {
            organizeIdTree.append("," + organizeParentId);
        }
        String organizeParentIdTree = organizeIdTree.toString();
        if (StringUtil.isNotEmpty(organizeParentIdTree)) {
            organizeParentIdTree = organizeParentIdTree.replaceFirst(",", "");
        }
        return organizeParentIdTree;
    }

    @Override
    public List<SysOrganizeEntity> getOrganizeByParentId(String parentId) {
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysOrganizeEntity::getParentId, parentId);
        return this.list(queryWrapper);
    }

    @Override
    public List<SysOrganizeEntity> getDepartmentAll(String organizeId) {
        SysOrganizeEntity organizeCompany = getOrganizeCompany(organizeId);
        List<SysOrganizeEntity> organizeList = new ArrayList<>();
        if (organizeCompany != null) {
            getOrganizeDepartmentAll(organizeCompany.getId(), organizeList);
            organizeList.add(organizeCompany);
        }
        return organizeList;
    }

    @Override
    public SysOrganizeEntity getOrganizeCompany(String organizeId) {
        SysOrganizeEntity entity = getInfo(organizeId);
        return (entity != null && !PermissionConst.COMPANY.equals(entity.getCategory())) ? getOrganizeCompany(entity.getParentId()) : entity;
    }

    @Override
    public void getOrganizeDepartmentAll(String organizeId, List<SysOrganizeEntity> organizeList) {
        List<SysOrganizeEntity> organizeEntityList = getListByParentId(organizeId);
        for (SysOrganizeEntity entity : organizeEntityList) {
            if (!PermissionConst.COMPANY.equals(entity.getCategory())) {
                organizeList.add(entity);
                getOrganizeDepartmentAll(entity.getId(), organizeList);
            }
        }
    }

    @Override
    public List<String> getOrgIdTree(SysOrganizeEntity entity) {
        List<String> orgIds= new ArrayList<>();
        if (entity != null) {
            String organizeIdTree = entity.getOrganizeIdTree();
            if (StringUtil.isNotEmpty(organizeIdTree)) {
                String[] split = organizeIdTree.split(",");
                for (String orgId : split) {
                    orgIds.add(orgId);
                }
            }
        }
        return orgIds;
    }

    @Override
    public List<String> upWardRecursion(List<String> orgIDs, String orgID) {
        this.getOrgIDs(orgIDs,orgID);
        return orgIDs;
    }

    @Override
    public Map<String, String> getInfoList() {
        if (redisUtil.exists(cacheKeyUtil.getOrganizeInfoList())) {
            return new HashMap<>(redisUtil.getMap(cacheKeyUtil.getOrganizeInfoList()));
        } else {
            Map<String, SysOrganizeEntity> orgs = getOrgMaps(null, false, null, SysOrganizeEntity::getFullName);
            Map<String, String> infoMap = new LinkedHashMap<>(orgs.size(), 1);
            orgs.forEach((k,v) -> infoMap.put(k, v.getFullName()));
            redisUtil.insert(cacheKeyUtil.getOrganizeInfoList(), infoMap);
            return infoMap;
        }
    }

    @Override
    public List<SysOrganizeEntity> getListById(Boolean enable) {
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        if (enable) {
            queryWrapper.lambda().eq(SysOrganizeEntity::getEnabledMark, 1);
        }
        return this.list(queryWrapper);
    }

    @Override
    public SysOrganizeEntity getInfoByParentId(String parentId) {
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysOrganizeEntity::getParentId, parentId);
        return this.getOne(queryWrapper);
    }


    private void getOrgIDs(List<String> orgIDs, String orgID) {
        SysOrganizeEntity info = this.getInfo(orgID);
        if (info != null){
            this.getOrgIDs(orgIDs,info.getParentId());
            orgIDs.add(info.getId());
        }
    }

    /**
     * 查询给定的条件是否有默认当前登录者的默认部门值
     * @param organizeConditionModel
     * @return
     */
    @Override
    public String getDefaultCurrentValueDepartmentId(OrganizeConditionModel organizeConditionModel) {
        UserInfo userInfo = UserProvider.getUser();
        int currentFinded = 0;
        if(organizeConditionModel.getDepartIds() != null && !organizeConditionModel.getDepartIds().isEmpty() && organizeConditionModel.getDepartIds().contains(userInfo.getOrganizeId())) {
            currentFinded = 1;
        }
        if(currentFinded == 0 && organizeConditionModel.getDepartIds() != null && !organizeConditionModel.getDepartIds().isEmpty()) {
            List<String> idList = new ArrayList<>(16);
            // 获取所有组织
            if (organizeConditionModel.getDepartIds().size() > 0) {
                idList.addAll(organizeConditionModel.getDepartIds());
                organizeConditionModel.getDepartIds().forEach(t -> {
                    List<String> underOrganizations = getUnderOrganizations(t, false);
                    if (underOrganizations.size() > 0) {
                        idList.addAll(underOrganizations);
                    }
                });
            }
            Map<String, String> orgIdNameMaps = this.getInfoList();
            List<SysOrganizeEntity> listAll = getListAll(idList, organizeConditionModel.getKeyword());
            List<OrganizeModel> organizeList = JsonUtil.createJsonToList(listAll, OrganizeModel.class);
            List<String> collect = organizeList.stream().map(SumTree::getParentId).collect(Collectors.toList());
            List<OrganizeModel> noParentId = organizeList.stream().filter(t->!collect.contains(t.getId()) && !"-1".equals(t.getParentId())).collect(Collectors.toList());
            noParentId.forEach(t->{
                if (StringUtil.isNotEmpty(t.getOrganizeIdTree())) {
                    String[] split = t.getOrganizeIdTree().split(",");
                    List<String> list = Arrays.asList(split);
                    Collections.reverse(list);
                    for (int i = 1; i < list.size(); i++) {
                        String orgId = list.get(i);
                        List<OrganizeModel> collect1 = organizeList.stream().filter(tt -> orgId.equals(tt.getId())).collect(Collectors.toList());
                        if (collect1.size() > 0) {
                            String[] split1 = StringUtil.isNotEmpty(t.getOrganizeIdTree()) ? t.getOrganizeIdTree().split(orgId) : new String[0];
                            if (split1.length > 0) {
                                t.setFullName(getFullNameByOrgIdTree(orgIdNameMaps, split1[1], "/"));
                            }
                            t.setParentId(orgId);
                            break;
                        }
                    }
                }
            });

            List<String> orgLIdList = organizeList.stream().map(OrganizeModel::getId).collect(Collectors.toList());
            if(orgLIdList != null && !orgLIdList.isEmpty() && orgLIdList.contains(userInfo.getOrganizeId())) {
                currentFinded = 1;
            }
        }
        return (currentFinded == 1)?userInfo.getOrganizeId():"";
    }

    @Override
    public Map<String, Object> getAllOrgsTreeName() {
        Map<String, Object> map = new HashMap<>();
        QueryWrapper<SysOrganizeEntity> queryWrapper = new QueryWrapper<>();
        List<SysOrganizeEntity> list = this.list(queryWrapper);
        Map<String, String> collect = list.stream().collect(Collectors.toMap(SysOrganizeEntity::getId, SysOrganizeEntity::getFullName));
        for (SysOrganizeEntity org : list) {
            String[] split = org.getOrganizeIdTree().split(",");
            StringJoiner names = new StringJoiner("/");
            for (String id : split) {
                names.add(collect.get(id));
            }
            map.put(org.getOrganizeIdTree(), names.toString());
        }
        return map;
    }
}
