package com.linzen.base.service;

import com.linzen.base.entity.ModuleDataAuthorizeEntity;

import java.util.List;

/**
 * 数据权限配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface ModuleDataAuthorizeService extends SuperService<ModuleDataAuthorizeEntity> {

    /**
     * 列表
     *
     * @return ignore
     */
    List<ModuleDataAuthorizeEntity> getList();

    /**
     * 列表
     *
     * @param moduleId 功能主键
     * @return ignore
     */
    List<ModuleDataAuthorizeEntity> getList(String moduleId);

    /**
     * 信息
     *
     * @param id 主键值
     * @return ignore
     */
    ModuleDataAuthorizeEntity getInfo(String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(ModuleDataAuthorizeEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return ignore
     */
    boolean update(String id, ModuleDataAuthorizeEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(ModuleDataAuthorizeEntity entity);

    /**
     * 验证编码是否重复
     *
     * @param moduleId
     * @param enCode
     * @param id
     * @return
     */
    boolean isExistByEnCode(String moduleId, String enCode, String id);

    /**
     * 验证名称是否重复
     *
     * @param moduleId
     * @param fullName
     * @param id
     * @return
     */
    boolean isExistByFullName(String moduleId, String fullName, String id);
}
