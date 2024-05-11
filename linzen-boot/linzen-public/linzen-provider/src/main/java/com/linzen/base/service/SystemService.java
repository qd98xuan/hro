package com.linzen.base.service;

import com.linzen.base.entity.SysSystemEntity;

import java.util.List;

/**
 * 系统
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface SystemService extends SuperService<SysSystemEntity> {

    /**
     * 获取列表
     * @return
     */
    List<SysSystemEntity> getList();

    /**
     * 获取系统列表
     *
     * @param keyword
     * @param filterMain
     * @param isList
     * @param moduleAuthorize
     * @return
     */
    List<SysSystemEntity> getList(String keyword, Boolean filterEnableMark, boolean verifyAuth, Boolean filterMain, boolean isList, List<String> moduleAuthorize);

    /**
     * 获取详情
     *
     * @param id
     * @return
     */
    SysSystemEntity getInfo(String id);

    /**
     * 判断系统名称是否重复
     *
     * @param id
     * @param fullName
     * @return
     */
    Boolean isExistFullName(String id, String fullName);

    /**
     * 判断系统编码是否重复
     *
     * @param id
     * @param enCode
     * @return
     */
    Boolean isExistEnCode(String id, String enCode);

    /**
     * 新建
     *
     * @param entity
     * @return
     */
    Boolean create(SysSystemEntity entity);

    /**
     * 新建
     *
     * @param entity
     * @return
     */
    Boolean update(String id, SysSystemEntity entity);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    Boolean delete(String id);

    /**
     *
     * 通过id获取系统列表
     *
     * @param list
     * @param moduleAuthorize
     * @return
     */
    List<SysSystemEntity> getListByIds(List<String> list, List<String> moduleAuthorize);

    /**
     * 通过编码获取系统信息
     *
     * @param enCode
     * @return
     */
    SysSystemEntity getInfoByEnCode(String enCode);

    /**
     * 获取
     *
     * @param mark
     * @param mainSystemCode
     * @param moduleAuthorize
     * @return
     */
    List<SysSystemEntity> findSystemAdmin(int mark, String mainSystemCode, List<String> moduleAuthorize);

    /**
     * 获取
     *
     * @param mark
     * @param mainSystemCode
     * @return
     */
    List<SysSystemEntity> findSystemAdmin(int mark, String mainSystemCode);
}
