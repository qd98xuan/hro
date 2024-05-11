package com.linzen.permission.model.user.mod;

import com.linzen.permission.entity.SysUserEntity;
import lombok.Data;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class UserUpModel {

    private Integer num;

    private SysUserEntity entity;

    public UserUpModel() {
    }

    public UserUpModel(Integer num, SysUserEntity entity) {
        this.num = num;
        this.entity = entity;
    }
}
