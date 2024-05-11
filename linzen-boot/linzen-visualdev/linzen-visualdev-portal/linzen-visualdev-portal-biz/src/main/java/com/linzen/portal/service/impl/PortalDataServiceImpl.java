package com.linzen.portal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.MyBatisPrimaryBase;
import com.linzen.base.entity.PortalManageEntity;
import com.linzen.base.model.online.VisualMenuModel;
import com.linzen.base.model.portalManage.PortalManagePrimary;
import com.linzen.base.model.portalManage.PortalManageVO;
import com.linzen.base.service.ModuleService;
import com.linzen.base.service.PortalManageService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.base.util.visualUtil.PubulishUtil;
import com.linzen.exception.WorkFlowException;
import com.linzen.permission.entity.SysAuthorizeEntity;
import com.linzen.permission.entity.SysPermissionGroupEntity;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.AuthorizeService;
import com.linzen.permission.service.PermissionGroupService;
import com.linzen.permission.service.RoleService;
import com.linzen.permission.service.UserService;
import com.linzen.portal.constant.PortalConst;
import com.linzen.portal.entity.PortalDataEntity;
import com.linzen.portal.entity.PortalEntity;
import com.linzen.portal.mapper.PortalDataMapper;
import com.linzen.portal.model.*;
import com.linzen.portal.service.PortalDataService;
import com.linzen.portal.service.PortalService;
import com.linzen.util.JsonUtil;
import com.linzen.util.JsonUtilEx;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author FHNP
 * @since 2023-04-19
 */
@Service
public class PortalDataServiceImpl extends SuperServiceImpl<PortalDataMapper, PortalDataEntity> implements PortalDataService {

    @Autowired
    private UserService userService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private PortalService portalService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private AuthorizeService authorizeService;
    @Autowired
    private PortalManageService portalManageApi;
    @Autowired
    private PermissionGroupService permissionGroupApi;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private PubulishUtil pubulishUtil;

    @Override
    public String getCustomDataForm(PortalCustomPrimary primary) throws Exception {
        PortalDataEntity one = getOne(primary.getQuery());
        if(one != null){
            return one.getFormData();
        }else {
            save(primary.getEntity());
        }
        return "";
    }

    @Override
    public String getModelDataForm(PortalModPrimary primary) throws Exception {
        PortalDataEntity one = getOne(primary.getQuery());
        if(one != null){
            return one.getFormData();
        }else {
            save(primary.getEntity());
        }
        return "";
    }

    @Override
    public void release(String platform, String portalId, String systemIdListStr,String releasePlatform) throws Exception {
        List<String> systemIdList;
        if(StringUtils.isNotEmpty(systemIdListStr)){
            systemIdList = Arrays.asList(systemIdListStr.split(","));
            // 系统管理对应添加绑定
            portalManageApi.createBatch(systemIdList.stream().map(systemId->
                    new PortalManagePrimary(releasePlatform, portalId, systemId)).collect(Collectors.toList()));
        }else {
            List<PortalManageVO> voList = portalManageApi.getList(new PortalManagePrimary(platform, portalId, null));
            if (voList.size() == 0) {
                throw new WorkFlowException("未找到同步路径，请刷新界面");
            }
            systemIdList = voList.stream().map(PortalManageVO::getSystemId).collect(Collectors.toList());
        }
        String formData = "";
        try {
            formData = getModelDataForm(new PortalModPrimary(portalId));
            createOrUpdate(new PortalReleasePrimary(portalId, platform), formData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 查询所有相关的自定义数据
        for (String systemId : systemIdList) {
            List<PortalDataEntity> list = list(new PortalCustomPrimary(platform, portalId, systemId, null).getQuery());
            final String finalFormData = formData;
            if(list.size() > 0){
                // 把所有数据进行重置formData
                list.forEach(entity -> entity.setFormData(finalFormData));
                updateBatchById(list);
            }
        }
        PortalEntity info = portalService.getInfo(portalId);
        if(info!=null){
            info.setState(1);
            info.setEnabledMark(1);
            portalService.update(portalId,info);
        }
    }

    @Override
    public void releaseModule(ReleaseModel releaseModel,String portalId) throws Exception {
        PortalEntity info = portalService.getInfo(portalId);
        if(info!=null) {
            VisualMenuModel visual = new VisualMenuModel();
            visual.setApp(releaseModel.getApp());
            visual.setPc(releaseModel.getPc());
            visual.setAppModuleParentId(releaseModel.getAppModuleParentId());
            if (StringUtil.isNotEmpty(releaseModel.getPcModuleParentId()) && StringUtil.isEmpty(releaseModel.getPcSystemId())) {
                visual.setPcModuleParentId("-1");
            } else {
                visual.setPcModuleParentId(releaseModel.getPcModuleParentId());
            }
            if (releaseModel.getPc() == 1) {
                createOrUpdate(new PortalReleasePrimary(portalId, PortalConst.WEB), getModelDataForm(new PortalModPrimary(portalId)));
            } else {
                createOrUpdate(new PortalReleasePrimary(portalId, PortalConst.APP), getModelDataForm(new PortalModPrimary(portalId)));
            }
            visual.setPcSystemId(Optional.ofNullable(releaseModel.getPcSystemId()).orElse(releaseModel.getPcModuleParentId()));
            visual.setAppSystemId(Optional.ofNullable(releaseModel.getAppSystemId()).orElse(releaseModel.getAppModuleParentId()));
            visual.setType(8);
            visual.setFullName(info.getFullName());
            visual.setEncode(info.getEnCode());
            visual.setId(info.getId());
            Integer integer = pubulishUtil.publishMenu(visual);
            if (integer == 2) {
                throw new WorkFlowException("同步失败,检查编码或名称是否重复" );
            }
            if (integer == 3) {
                throw new WorkFlowException("未找到同步路径，请刷新界面" );
            }
            info.setState(1);
            info.setEnabledMark(1);
            portalService.update(portalId,info);
        }
    }

    @Override
    public Boolean isReleaseFlag(PortalReleasePrimary primary) {
        return count(primary.getQuery()) > 0;
    }

    @Override
    public Boolean deleteAll(String portalId) {
        QueryWrapper<PortalDataEntity> query = new QueryWrapper<>();
        query.lambda().eq(PortalDataEntity::getPortalId, portalId);
        return remove(query);
    }

    /**
     * 创建或更新
     *
     *  门户ID ->（平台、系统ID、用户ID）-> 排版信息
     *  基础：门户ID绑定排版信息（一对多）、 条件：平台、系统ID、用户ID
     */
    @Override
    public void createOrUpdate(PortalCustomPrimary primary, String formData) throws Exception {
        creUpCom(primary, formData);
    }

    @Override
    public void createOrUpdate(PortalModPrimary primary, String formData) throws Exception {
        creUpCom(primary, formData);
    }

    @Override
    public void createOrUpdate(PortalReleasePrimary primary, String formData) throws Exception {
        creUpCom(primary, formData);
    }

    private void creUpCom(MyBatisPrimaryBase<PortalDataEntity> primary, String formData) throws Exception {
        // 自定义数据变量条件：0、门户 1、用户 2、系统 3、平台
        List<PortalDataEntity> list = list(primary.getQuery());
        if(list.size() < 1){
            PortalDataEntity creEntity = primary.getEntity();
            creEntity.setFormData(formData);
            save(creEntity);
        }else if(list.size() == 1){
            PortalDataEntity upEntity = list.get(0);
            upEntity.setFormData(formData);
            updateById(upEntity);
        }else {
            throw new Exception("门户数据信息存在重复");
        }
    }

    /**
     * 根据id返回门户信息
     */
    @Override
    public PortalInfoAuthVO getDataFormView(String portalId, String platform) throws Exception{
        PortalEntity entity = portalService.getInfo(portalId);
        if (entity == null) throw new Exception("该门户已删除");
//        if (entity.getEnabledMark() == 0) throw new Exception("门户被禁止");
        PortalInfoAuthVO infoVo = JsonUtil.createJsonToBean(JsonUtilEx.getObjectToStringDateFormat
                (entity, "yyyy-MM-dd HH:mm:ss"), PortalInfoAuthVO.class);
        // 查询自定义设计的门户信息
        infoVo.setFormData(getDataForm(platform, portalId));
        return infoVo;
    }

    private String getDataForm(String platform, String portalId) throws Exception {
        List<PortalDataEntity> dataList = list(new PortalCustomPrimary(platform, portalId).getQuery());
        if(CollectionUtil.isEmpty(dataList)) dataList = list(new PortalReleasePrimary(portalId, platform).getQuery());
        // 当没有自定义的排版信息时，使用已发布模板排版信息
        if(CollectionUtil.isNotEmpty(dataList)){
            PortalDataEntity entity = dataList.get(0);
            if (dataList.size() != 1) {
                List<String> ids = dataList.stream().map(PortalDataEntity::getId).collect(Collectors.toList());
                removeBatchByIds(ids.stream().filter(id -> !id.equals(entity.getId())).collect(Collectors.toList()));
            }
            return entity.getFormData();
        }
        return null;
    }

    /**
     * 设置门户默认主页
     *
     * 用户ID -> (平台、系统ID) -> 门户ID
     * 基础：用户ID绑定门户ID（多对多）、条件：平台、系统ID
     * Map格式：Map <platform:systemId, portalId>
     * @param portalId 门户ID
     * @param platform 平台
     */
    @Override
    public void setCurrentDefault(String platform, String portalId) {
        SysUserEntity userEntity = userService.getInfo(userProvider.get().getUserId());
        Map<String, Object> map = new HashMap<>();
        try{
            map = JSONObject.parseObject(userEntity.getPortalId());
        }catch (Exception ignore){}
        if ("App".equals(platform)) {
            map.put(platform + ":" + userEntity.getAppSystemId(), portalId);
        } else {
            map.put(platform + ":" + userEntity.getSystemId(), portalId);
        }
        SysUserEntity update = new SysUserEntity();
        update.setId(userEntity.getId());
        update.setPortalId(JSONObject.toJSONString(map));
        userService.updateById(update);
    }

    @Override
    public String getCurrentDefault(String platform) throws Exception {
        SysUserEntity userEntity = userService.getInfo(userProvider.get().getUserId());
        String systemId = PortalConst.WEB.equals(platform) ? userProvider.get().getSystemId() : userProvider.get().getAppSystemId();
        String portalId = "";
        try{
            Map<String, Object> map = JSONObject.parseObject(userEntity.getPortalId());
            portalId = map.get(platform + ":" + systemId).toString();
        }catch (Exception ignore){}
        PortalEntity mainPortal = portalService.getById(portalId);
        // 校验门户有效性
        if(mainPortal != null && mainPortal.getEnabledMark().equals(1)){
            // 管理员直接设置默认主页
            List<String> authPortalIds;
            if(userProvider.get().getIsAdministrator()){
                List<PortalManageVO> currentVoList = portalManageApi.getListByEnable(new PortalManagePrimary(platform, null, systemId));
                authPortalIds = currentVoList.stream().map(PortalManageVO::getPortalId).collect(Collectors.toList());
            }else {
                // 获取当前用户的所有权限的门户ID集合
                authPortalIds = getCurrentAuthPortalIds(new PortalViewPrimary(platform, null));
            }
            if(CollectionUtil.isNotEmpty(authPortalIds) && authPortalIds.contains(portalId)) {
                return portalId;
            }
        }
        // 重新设置默认门户
        String updatePortalId = portalService.getModListFirstId(new PortalViewPrimary(platform, null));
        setCurrentDefault(platform, updatePortalId);
        return updatePortalId;
    }

    /**
     * 获取当下所有带权限PortalId集合
     */
    public List<String> getCurrentAuthPortalIds(PortalViewPrimary primary){
        String userId = userProvider.get().getUserId();
        String systemId = primary.getSystemId();

        // 获取用户底下所有权限portalManage
        Supplier<List<String>> authPortalManageIds = ()->{
            List<String> roleIds = permissionGroupApi.getPermissionGroupByUserId(userId, null, false, null).stream().map(SysPermissionGroupEntity::getId).collect(Collectors.toList());
            List<String> portalManageIds = new ArrayList<>();
            for (String roleId : roleIds) {
            /* authorize存储 portalManage->item、role->object，本质：门户管理条目与角色关系
            根据用户Id查询出对应所有的门户管理条目(distinct()进行去重) */
                List<SysAuthorizeEntity> authorizePortalManage = authorizeService.getListByObjectId(roleId, PortalConst.AUTHORIZE_PORTAL_MANAGE);
                portalManageIds.addAll(authorizePortalManage.stream()
                        .map(SysAuthorizeEntity::getItemId).distinct().collect(Collectors.toList()));
            }
            return portalManageIds;
        };

        List<String> portalManageIds = authPortalManageIds.get();

        Map<String, List<String>> map = new HashMap<>();
        // 获取具有权限的所有门户
        if(portalManageIds.size() > 0) {
            QueryWrapper<PortalManageEntity> query = new QueryWrapper<>();
            query.lambda().eq(PortalManageEntity::getEnabledMark, 1)
                          .eq(PortalManageEntity::getPlatform, primary.getPlatForm())
                          .in(PortalManageEntity::getId, portalManageIds);
            List<PortalManageEntity> portalManageList = portalManageApi.list(query);
            Map<String, List<PortalManageEntity>> collect = portalManageList.stream().collect(Collectors.groupingBy(PortalManageEntity::getSystemId));
            // key:systemId 、 value:portalIdList
            collect.forEach((key, description) -> map.put(key, description.stream().map(PortalManageEntity::getPortalId).collect(Collectors.toList())));
            return map.get(systemId);
        }else {
            return new ArrayList<>();
        }
    }

    /* ============== 目前使用懒加载更新主页，故注释 =============== */
//    /**
//     *  校验：当前用户的默认门户首页是否还具备权限
//     *  （当权限被删除，默认首页丢失）
//     */
//    private void checkDefaultPortal(List<String> originRoleIds, List<String> updateRoleIds, String portalManageId){
//        // 若是新增角色不会对默认门户产生影响
//        List<String> delRoleIds = originRoleIds.stream().filter(o -> !updateRoleIds.contains(o))
//                .collect(Collectors.toList());
//        if(CollectionUtil.isNotEmpty(delRoleIds)){
//            // 授权取消所涉及到用户集合
//            List<UserEntity> userList = userService.getListByRoleIds(delRoleIds);
//            for (UserEntity userEntity : userList) {
//                String userId = userEntity.getId();
//                // 获取各系统下带权限的门户ID（这里跨服务会产生数据不同步的问题）
//                Map<String, List<String>> authorizeSysPortalIdsMap = getAuthorizePortalIds(userId);
//                // 获取原始各系统下默认门户ID
//                Map<String, String> sysDefaultPortalIdMap = portalManageService.getDefault(userEntity.getId());
//                for (Map.Entry<String, String> defaultMap : sysDefaultPortalIdMap.entrySet()) {
//                    String systemId = defaultMap.getKey();
//                    // 默认门户ID
//                    String defaultPortalId = defaultMap.getValue();
//                    // 带权限门户ID
//                    List<String> authorizePortalIds = authorizeSysPortalIdsMap.get(systemId);
//                    if(CollectionUtil.isNotEmpty(authorizePortalIds)){
//                        if(!authorizePortalIds.contains(defaultPortalId)){
//                            // 丢失默认门户，顺位首个
//                            portalManageService.setDefault(userId, systemId, authorizePortalIds.get(0));
//                        }
//                    }
//                }
//            }
//        }
//    }



}
