package com.linzen.base.user;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户租户信息
 *
 * @author FHNP
 * @version: V3.1.0
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class UserTenantModel implements Serializable {
    /**
     * 租户手机号
     */
    private String tenantId;

    public UserTenantModel() {
    }

    public UserTenantModel(String tenantId) {
        this.tenantId = tenantId;
    }
}
