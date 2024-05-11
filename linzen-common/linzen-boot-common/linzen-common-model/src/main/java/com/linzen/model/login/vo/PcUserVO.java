package com.linzen.model.login.vo;


import com.linzen.model.login.PermissionModel;
import com.linzen.model.login.SystemInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PcUserVO {

    @Schema(description = "菜单集合")
    private List menuList;

    @Schema(description = "权限集合")
    private List<PermissionModel> permissionList;

    @Schema(description = "用户信息")
    private UserCommonInfoVO userInfo;

    /**
     * 系统配置
     */
    @Schema(description = "系统配置")
    private SystemInfo sysConfigInfo;

    public PcUserVO() {
    }

    public PcUserVO(List menuList, List<PermissionModel> permissionList, UserCommonInfoVO userInfo, SystemInfo sysConfigInfo) {
        this.menuList = menuList;
        this.permissionList = permissionList;
        this.userInfo = userInfo;
        this.sysConfigInfo = sysConfigInfo;
    }
}
