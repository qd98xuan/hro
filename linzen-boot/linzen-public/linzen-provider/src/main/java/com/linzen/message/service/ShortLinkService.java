
package com.linzen.message.service;

import com.linzen.base.service.SuperService;
import com.linzen.message.entity.ShortLinkEntity;

/**
 *
 * 消息模板（新）
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
public interface ShortLinkService extends SuperService<ShortLinkEntity> {

    String shortLink (String link);

    ShortLinkEntity getInfoByLink(String link);
}
