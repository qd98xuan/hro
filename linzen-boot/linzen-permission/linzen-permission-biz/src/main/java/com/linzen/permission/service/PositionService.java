package com.linzen.permission.service;


import com.linzen.base.service.SuperService;
import com.linzen.permission.entity.SysPositionEntity;
import com.linzen.permission.model.position.PaginationPosition;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 岗位信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface PositionService extends SuperService<SysPositionEntity> {

    /**
     * 列表
     *
     * @return
     * @param filterdelFlag
     */
    List<SysPositionEntity> getList(boolean filterdelFlag);

    /**
     * 岗位名列表（在线开发）
     *
     * @param idList
     * @return
     */
    List<SysPositionEntity> getPosList(List<String> idList);


    /**
     * 岗位名列表（在线开发）
     *
     * @param idList
     * @return
     */
    List<SysPositionEntity> getPosList(Set<String> idList);

    Map<String,Object> getPosMap();

    Map<String,Object> getPosEncodeAndName();

    /**
     * 获取redis存储的岗位信息
     *
     * @return
     */
    List<SysPositionEntity> getPosRedisList();

    /**
     * 列表
     *
     * @param paginationPosition 条件
     * @return
     */
    List<SysPositionEntity> getList(PaginationPosition paginationPosition);

    /**
     * 列表
     *
     * @param userId 用户主键
     * @return
     */
    List<SysPositionEntity> getListByUserId(String userId);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    SysPositionEntity getInfo(String id);

    /**
     * 通过名称查询id
     *
     * @param fullName 名称
     * @return
     */
    SysPositionEntity getByFullName(String fullName);

    /**
     * 通过名称查询id
     *
     * @param fullName 名称
     * @return
     */
    SysPositionEntity getByFullName(String fullName, String encode);

    /**
     * 验证名称
     *
     * @param entity
     * @param isFilter 是否过滤
     * @return
     */
    boolean isExistByFullName(SysPositionEntity entity, boolean isFilter);

    /**
     * 验证编码
     *
     * @param entity
     * @param isFilter 是否过滤
     * @return
     */
    boolean isExistByEnCode(SysPositionEntity entity, boolean isFilter);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(SysPositionEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     */
    boolean update(String id, SysPositionEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(SysPositionEntity entity);

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
     * 获取名称
     *
     * @return
     */
    List<SysPositionEntity> getPositionName(List<String> id, boolean filterdelFlag);

    /**
     * 获取名称
     *
     * @return
     */
    List<SysPositionEntity> getPositionName(List<String> id, String keyword);

    /**
     * 获取岗位列表
     *
     * @param organizeIds 组织id
     * @param delFlag
     * @return
     */
    List<SysPositionEntity> getListByOrganizeId(List<String> organizeIds, boolean enabledMark);

    /**
     * 获取用户组织底下所有的岗位
     * @param organizeId
     * @param userId
     * @return
     */
    List<SysPositionEntity> getListByOrgIdAndUserId(String organizeId, String userId);

    /**
     * 通过名称获取岗位列表
     *
     * @param fullName  岗位名称
     * @param enCode    编码
     * @return
     */
    List<SysPositionEntity> getListByFullName(String fullName, String enCode);

    List<SysPositionEntity> getCurPositionsByOrgId(String orgId);

}
