
package com.linzen.message.service;

import com.linzen.base.service.SuperService;
import com.linzen.message.entity.WechatUserEntity;

/**
 *
 * 消息模板（新）
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
public interface WechatUserService extends SuperService<WechatUserEntity> {

    WechatUserEntity getInfoByGzhId(String userId,String gzhId);

    void create(WechatUserEntity entity);

    boolean update(String id, WechatUserEntity entity);

    void delete(WechatUserEntity entity);

}
