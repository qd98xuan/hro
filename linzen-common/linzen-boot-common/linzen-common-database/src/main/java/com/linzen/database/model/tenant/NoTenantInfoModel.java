package com.linzen.database.model.tenant;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@AllArgsConstructor
public class NoTenantInfoModel {
    private String token;
    private String url;
    private String stack;
    private String userId;
}