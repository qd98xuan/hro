package com.linzen.permission.service;

import com.linzen.base.service.SuperService;
import com.linzen.permission.entity.GroupEntity;
import com.linzen.permission.model.usergroup.PaginationGroup;

import java.util.List;
import java.util.Map;

/**
 * 用户管理业务层
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface GroupService extends SuperService<GroupEntity> {

    /**
     * 获取用户列表
     *
     * @param pagination 关键字
     * @return
     */
    List<GroupEntity> getList(PaginationGroup pagination);

    /**
     * 获取所有格分组信息
     *
     * @return
     */
    List<GroupEntity> list();

    Map<String,Object> getGroupMap();

    /**
     * fullName/encode,id
     * @return
     */
    Map<String,Object> getGroupEncodeMap();

    /**
     * 获取用户详情
     *
     * @param id
     * @return
     */
    GroupEntity getInfo(String id);

    /**
     * 获取分组详情
     *
     * @param fullName
     * @param encode
     * @return
     */
    GroupEntity getInfo(String fullName, String encode);

    /**
     * 添加
     *
     * @param entity
     */
    void crete(GroupEntity entity);

    /**
     * 修改
     *
     * @param id
     * @param entity
     */
    Boolean update(String id, GroupEntity entity);

    /**
     * 删除
     *
     * @param entity
     */
    void delete(GroupEntity entity);

    /**
     * 判断名称是否重复
     *
     * @param fullName
     * @param id
     * @return
     */
    Boolean isExistByFullName(String fullName, String id);

    /**
     * 判断编码是否重复
     *
     * @param enCode
     * @param id
     * @return
     */
    Boolean isExistByEnCode(String enCode, String id);

    /**
     * 通过分组id获取分组集合
     *
     * @param list
     * @return
     */
    List<GroupEntity> getListByIds(List<String> list, Boolean filter);
}