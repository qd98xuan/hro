package com.linzen.permission.service;


import com.linzen.base.Pagination;
import com.linzen.base.service.SuperService;
import com.linzen.permission.entity.SysOrganizeAdministratorEntity;
import com.linzen.permission.entity.SysOrganizeEntity;
import com.linzen.permission.model.organizeadministrator.OrganizeAdministratorListVo;
import com.linzen.permission.model.organizeadministrator.OrganizeAdministratorModel;

import java.util.List;

/**
 *
 * 机构分级管理员
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface OrganizeAdministratorService extends SuperService<SysOrganizeAdministratorEntity> {



    /**
     * 获取 机构分级管理员信息
     * @param userId
     * @param organizeId
     * @return
     */
    SysOrganizeAdministratorEntity getOne(String userId, String organizeId);

    /**
     * 根据userId获取列表
     * @param userId
     * @return
     */
    List<SysOrganizeAdministratorEntity> getOrganizeAdministratorEntity(String userId);

    /**
     * 根据userId获取列表
     * @param userId
     * @param type
     * @param filterMain
     * @return
     */
    List<SysOrganizeAdministratorEntity> getOrganizeAdministratorEntity(String userId, String type, boolean filterMain);

    /**
     * 新建
     * @param entity  实体对象
     */
    void create(SysOrganizeAdministratorEntity entity);

    /**
     * 新建
     * @param list
     */
    void createList(List<SysOrganizeAdministratorEntity> list, String userId);

    /**
     * 更新
     * @param id     主键值
     * @param entity 实体对象
     */
    boolean update(String id, SysOrganizeAdministratorEntity entity);

    /**
     * 删除
     * @param userId 用户id
     */
    boolean deleteByUserId(String userId);

    /**
     * 删除
     * @param entity 实体对象
     */
    void delete(SysOrganizeAdministratorEntity entity);

    /**
     * 获取 OrganizeAdminIsTratorEntity 信息
     * @param userId 主键值
     * @return
     */
    List<SysOrganizeAdministratorEntity> getInfoByUserId(String userId);

    /**
     * 获取 OrganizeAdminIsTratorEntity 信息
     * @param id 主键值
     * @return
     */
    SysOrganizeAdministratorEntity getInfo(String id);

    /**
     * 获取 OrganizeAdminIsTratorEntity 信息
     * @param organizeId 机构主键值
     * @return
     */
    SysOrganizeAdministratorEntity getInfoByOrganizeId(String organizeId);

    /**
     * 获取 OrganizeAdminIsTratorEntity 列表
     * @param organizeIdList 机构主键值
     * @return
     */
    List<SysOrganizeAdministratorEntity> getListByOrganizeId(List<String> organizeIdList);

    /**
     * 获取二级管理员列表
     *
     * @param pagination 分页参数
     * @return
     */
    List<OrganizeAdministratorListVo> getList(Pagination pagination);

    List<String> getOrganizeUserList(String type);

    List<SysOrganizeEntity> getListByAuthorize();

    OrganizeAdministratorModel getOrganizeAdministratorList();
}
