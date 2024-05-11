package com.linzen.permission.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.linzen.base.ServiceResult;
import com.linzen.base.service.SuperService;
import com.linzen.permission.entity.SysOrganizeEntity;
import com.linzen.permission.model.organize.OrganizeConditionModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 组织机构
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface OrganizeService extends SuperService<SysOrganizeEntity> {

    /**
     * 列表
     *
     * @return
     */
    List<SysOrganizeEntity> getListAll(List<String> idAll, String keyWord);

    /**
     * 列表
     *
     * @return
     */
    List<SysOrganizeEntity> getParentIdList(String id);

    /**
     * 列表
     *
     * @return
     * @param filterdelFlag
     */
    List<SysOrganizeEntity> getList(boolean filterdelFlag);

    /**
     * 列表
     *
     * @return
     */
    List<SysOrganizeEntity> getList(String keyword, boolean filterdelFlag);

    /**
     * 获取组织信息
     * @param keyword
     * @param filterdelFlag
     * @param type
     * @return OrgId, OrgEntity
     */
    Map<String, SysOrganizeEntity> getOrgMaps(String keyword, boolean filterdelFlag, String type, SFunction<SysOrganizeEntity, ?>... columns);

    /**
     * 获取组织信息
     * @return OrgId, OrgEntity
     */
    Map<String, SysOrganizeEntity> getOrgMapsAll(SFunction<SysOrganizeEntity, ?>... columns);

    /**
     * 列表(有效的组织)
     *
     * @return
     */
    List<SysOrganizeEntity> getListBydelFlag(Boolean enable);

    /**
     * 列表
     *
     * @param fullName 组织名称
     * @return
     */
    SysOrganizeEntity getInfoByFullName(String fullName);

    /**
     * 获取部门名列表
     *
     * @return
     */
    List<SysOrganizeEntity> getOrgEntityList(List<String> idList, Boolean enable);

    /**
     * 获取部门名列表(在线开发转换数据使用)
     *
     * @return
     */
    List<SysOrganizeEntity> getOrgEntityList(Set<String> idList);

    /**
     * 全部组织（id : name）
     * @return
     */
    Map<String, Object> getOrgMap();

    /**
     * 全部组织（Encode/name : id）
     * @return
     * @param type
     */
    Map<String, Object> getOrgEncodeAndName(String type);

    /**
     * 全部组织（name : id）
     * @return
     * @param type
     */
    Map<String, Object> getOrgNameAndId(String type);

    /**
     * 获取redis存储的部门信息
     *
     * @return
     */
    List<SysOrganizeEntity> getOrgRedisList();

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    SysOrganizeEntity getInfo(String id);

    /**
     * 通过名称查询id
     *
     * @param fullName 名称
     * @return
     */
    SysOrganizeEntity getByFullName(String fullName);

    /**
     * 通过名称 组织类型 查询id
     *
     * @param fullName 名称
     * @param category 类别
     * @param enCode 编码
     * @return
     */
    SysOrganizeEntity getByFullName(String fullName, String category, String enCode);

    /**
     * 验证名称
     *
     * @param entity
     * @param isCheck  组织名称是否不分级判断
     * @param isFilter 是否需要过滤id
     * @return
     */
    boolean isExistByFullName(SysOrganizeEntity entity, boolean isCheck, boolean isFilter);

    /**
     * 获取父级id
     *
     * @param organizeId           组织id
     * @param organizeParentIdList 父级id集合
     */
    void getOrganizeIdTree(String organizeId, List<String> organizeParentIdList);

    /**
     * 获取父级id
     *
     * @param organizeId           组织id
     * @param organizeParentIdList 父级id集合
     */
    void getOrganizeId(String organizeId, List<SysOrganizeEntity> organizeParentIdList);

    /**
     * 验证编码
     *
     * @param enCode
     * @param id
     * @return
     */
    boolean isExistByEnCode(String enCode, String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(SysOrganizeEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     */
    boolean update(String id, SysOrganizeEntity entity);

    /**
     * 通过父级id修改父级组织树
     *
     * @param entity
     * @param category
     */
    void update(SysOrganizeEntity entity, String category);

    /**
     * 删除
     *
     * @param orgId 实体对象
     */
    ServiceResult<String> delete(String orgId);

    /**
     * 上移
     *
     * @param id 主键值
     */
    boolean first(String id);

    /**
     * 下移
     *
     * @param id 主键值
     */
    boolean next(String id);

    /**
     * 判断是否允许删除
     *
     * @param orgId 主键值
     * @return
     */
    String allowDelete(String orgId);

    /**
     * 获取名称
     *
     * @return
     */
    List<SysOrganizeEntity> getOrganizeName(List<String> id);

    /**
     * 获取名称
     *
     * @return
     */
    Map<String, SysOrganizeEntity> getOrganizeName(List<String> id, String keyword, boolean filterdelFlag, String type);

    /**
     * 获取名称
     *
     * @return
     */
    List<SysOrganizeEntity> getOrganizeNameSort(List<String> id);

    /**
     * @param organizeParentId 父id
     * @return List<String> 接收子结构
     */
    List<String> getOrganize(String organizeParentId);

    /**
     * @param organizeParentId 父id
     * @return List<String> 接收子结构
     */
    List<String> getOrganizeByOraParentId(String organizeParentId);

    /**
     * 获取所有当前用户的组织及子组织
     *
     * @param organizeId
     * @param filterdelFlag
     * @return
     */
    List<String> getUnderOrganizations(String organizeId, boolean filterdelFlag);

    /**
     * 获取所有当前用户的组织及子组织 (有分级权限验证)
     *
     * @param organizeId
     * @return
     */
    List<String> getUnderOrganizationss(String organizeId);

    /**
     * 通过名称获取组织列表
     *
     * @param fullName
     * @return
     */
    List<SysOrganizeEntity> getListByFullName(String fullName);

    /**
     * 通过id判断是否有子集
     *
     * @param id 主键
     * @return
     */
    List<SysOrganizeEntity> getListByParentId(String id);

    /**
     * 获取用户所有所在组织
     *
     * @return 组织对象集合
     */
    List<SysOrganizeEntity> getAllOrgByUserId(String userId);

    /**
     * 通过组织id树获取名称
     *
     * @param idNameMaps 预先获取的组织ID名称映射
     * @param orgIdTree 组织id树
     * @param regex     分隔符
     * @return 组织对象集合
     */
    String getFullNameByOrgIdTree(Map<String, String> idNameMaps, String orgIdTree, String regex);

    /**
     * 获取父级组织id
     *
     * @param entity
     * @return
     */
    String getOrganizeIdTree(SysOrganizeEntity entity);

    /**
     * 获取顶级组织
     *
     * @return
     * @param parentId
     */
    List<SysOrganizeEntity> getOrganizeByParentId(String parentId);

    /**
     * 查询用户的所属公司下的部门
     *
     * @return
     */
    List<SysOrganizeEntity> getDepartmentAll(String organizeId);

    /**
     * 获取所在公司
     *
     * @param organizeId
     * @return
     */
    SysOrganizeEntity getOrganizeCompany(String organizeId);

    /**
     * 获取所在公司下部门
     *
     * @return
     */
    void getOrganizeDepartmentAll(String organize, List<SysOrganizeEntity> list);

    /**
     * 获取组织id树
     *
     * @param entity
     * @return
     */
    List<String> getOrgIdTree(SysOrganizeEntity entity);

    /**
     * 向上递归取组织id
     * @param orgID
     * @return
     */
    List<String> upWardRecursion(List<String> orgIDs, String orgID);

    /**
     * 查询给定的条件是否有默认当前登录者的默认部门值
     * @param organizeConditionModel
     * @return
     */
    String getDefaultCurrentValueDepartmentId(OrganizeConditionModel organizeConditionModel);

    /**
     * 获取名称及id组成map
     *
     * @return
     */
    Map<String, String> getInfoList();

    /**
     * 列表(有效的组织)
     *
     * @return
     */
    List<SysOrganizeEntity> getListById(Boolean enable);

    /**
     * 获取顶级组织
     *
     * @return
     * @param parentId
     */
    SysOrganizeEntity getInfoByParentId(String parentId);

    /**
     * 获取所有组织全路径名称
     *
     * @return
     */
    Map<String, Object> getAllOrgsTreeName();
}