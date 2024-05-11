package com.linzen.permission.service;

import com.linzen.base.service.SuperService;
import com.linzen.permission.entity.SocialsUserEntity;

import java.util.List;

/**
 * 流程设计
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface SocialsUserService extends SuperService<SocialsUserEntity> {
    /**
     * 查询用户授权列表
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    List<SocialsUserEntity> getListByUserId(String userId);

    /**
     * 查询用户授权列表
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    List<SocialsUserEntity> getUserIfnoBySocialIdAndType(String socialId,String socialType);

    /**
     * 查询用户授权列表
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    List<SocialsUserEntity> getListByUserIdAndSource(String userId,String socialType);

    /**
     *  根据第三方账号账号类型和id获取用户第三方绑定信息
     * @param socialId 第三方账号id
     * @return
     */
    SocialsUserEntity getInfoBySocialId(String socialId,String socialType);
}
