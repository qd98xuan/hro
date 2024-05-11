package com.linzen.base.service;

import com.linzen.base.entity.ModuleDataAuthorizeSchemeEntity;

import java.util.List;

/**
 * 数据权限方案
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface ModuleDataAuthorizeSchemeService extends SuperService<ModuleDataAuthorizeSchemeEntity> {

    /**
     * 列表
     *
     * @return ignore
     */
    List<ModuleDataAuthorizeSchemeEntity> getList();

    /**
     * 列表
     *
     * @return ignore
     */
    List<ModuleDataAuthorizeSchemeEntity> getEnabledMarkList(String delFlag);

    /**
     * 列表
     *
     * @param moduleId 功能主键
     * @return ignore
     */
    List<ModuleDataAuthorizeSchemeEntity> getList(String moduleId);

    /**
     * 信息
     *
     * @param id 主键值
     * @return ignore
     */
    ModuleDataAuthorizeSchemeEntity getInfo(String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(ModuleDataAuthorizeSchemeEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return ignore
     */
    boolean update(String id, ModuleDataAuthorizeSchemeEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(ModuleDataAuthorizeSchemeEntity entity);

    /**
     * 判断名称是否重复
     * @param id
     * @param fullName
     * @return
     */
    Boolean isExistByFullName(String id, String fullName, String moduleId);

    /**
     * 判断名称是否重复
     * @param id
     * @param enCode
     * @return
     */
    Boolean isExistByEnCode(String id, String enCode, String moduleId);

    /**
     * 是否存在全部数据
     * @param moduleId
     * @return
     */
    Boolean isExistAllData(String moduleId);

    /**
     * 通过moduleIds获取权限
     *
     * @param ids
     * @return
     */
    List<ModuleDataAuthorizeSchemeEntity> getListByModuleId(List<String> ids);

    /**
     * 通过moduleIds获取权限
     *
     * @param ids
     * @return
     */
    List<ModuleDataAuthorizeSchemeEntity> getListByIds(List<String> ids);
}
