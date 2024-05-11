package com.linzen.permission.model.socails;

import com.alibaba.fastjson2.JSONArray;
import com.linzen.base.UserInfo;
import lombok.Data;

/**
 * 流程设计
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class SocialsUserInfo {
    UserInfo userInfo;
    JSONArray tenantUserInfo;
    String socialUnionid;
    String socialName;
}
