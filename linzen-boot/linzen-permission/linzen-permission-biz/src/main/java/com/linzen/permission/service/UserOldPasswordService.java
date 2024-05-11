package com.linzen.permission.service;

import com.linzen.base.service.SuperService;
import com.linzen.permission.entity.SysUserOldPasswordEntity;

import java.util.List;

/**
 * 用户信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface UserOldPasswordService extends SuperService<SysUserOldPasswordEntity> {

    /**
     * 列表
     *
     * @return
     */
    List<SysUserOldPasswordEntity>  getList(String userId);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    Boolean create(SysUserOldPasswordEntity entity);

}
