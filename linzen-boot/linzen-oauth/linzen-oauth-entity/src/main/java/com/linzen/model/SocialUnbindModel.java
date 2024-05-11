package com.linzen.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 第三方未绑定模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@AllArgsConstructor
public class SocialUnbindModel {
    String socialType;
    String socialUnionid;
    String socialName;
}
