package com.linzen.base.mapper;

import com.linzen.base.entity.SysConfigEntity;


/**
 * 系统配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface SysConfigMapper extends SuperMapper<SysConfigEntity> {

    void deleteSysConfig();

    void deleteMpConfig();

    void deleteQyhConfig();
}
