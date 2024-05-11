package com.linzen.permission.service;

import com.linzen.base.service.SuperService;
import com.linzen.permission.entity.SysColumnsPurviewEntity;

/**
 * 模块列表权限业务类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface ColumnsPurviewService extends SuperService<SysColumnsPurviewEntity> {

    /**
     * 通过moduleId获取列表权限
     *
     * @param moduleId
     * @return
     */
    SysColumnsPurviewEntity getInfo(String moduleId);

    /**
     * 判断是保存还是编辑
     *
     * @param moduleId
     * @param entity
     * @return
     */
    boolean update(String moduleId, SysColumnsPurviewEntity entity);
}
