
package com.linzen.message.service;

import com.linzen.base.service.SuperService;
import com.linzen.message.entity.UserDeviceEntity;

import java.util.List;

/**
 *
 * 消息模板（新）
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
public interface UserDeviceService extends SuperService<UserDeviceEntity> {

    UserDeviceEntity getInfoByUserId(String userId);

    List<String> getCidList(String userId);

    UserDeviceEntity getInfoByClientId(String clientId);

    void create(UserDeviceEntity entity);

    boolean update(String id, UserDeviceEntity entity);

    void delete(UserDeviceEntity entity);

}
