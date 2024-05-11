package com.linzen.model;

import com.linzen.base.UserInfo;
import com.linzen.base.entity.SysSystemEntity;
import com.linzen.permission.entity.SysUserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuildUserCommonInfoModel implements Serializable {

    private UserInfo userInfo;
    private SysSystemEntity mainSystemEntity;
    private SysSystemEntity workSystemEntity;
    private SysUserEntity userEntity;
    private BaseSystemInfo baseSystemInfo;
    private String systemId;

}
