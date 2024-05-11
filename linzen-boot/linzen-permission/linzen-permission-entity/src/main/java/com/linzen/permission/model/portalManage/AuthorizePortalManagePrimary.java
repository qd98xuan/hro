package com.linzen.permission.model.portalManage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.MyBatisPrimaryBase;
import com.linzen.permission.entity.SysAuthorizeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 类功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息
 * @date 2023-04-01
 */
@Data
public class AuthorizePortalManagePrimary extends MyBatisPrimaryBase<SysAuthorizeEntity> {

    @Schema(description = "权限类型")
    private final String objectType = "role";

    @Schema(description = "条目类型")
    private final String itemType = "portalManage";

    @Schema(description = "角色Id")
    private String roleId;

    @Schema(description = "门户管理Id")
    private String portalManageId;

    public AuthorizePortalManagePrimary(String roleId, String portalManageId){
        this.roleId = roleId;
        this.portalManageId = portalManageId;
    }

    public QueryWrapper<SysAuthorizeEntity> getQuery(){
        queryWrapper.lambda().eq(SysAuthorizeEntity::getObjectType, objectType);
        queryWrapper.lambda().eq(SysAuthorizeEntity::getItemType, itemType);
        if(this.roleId != null) queryWrapper.lambda().eq(SysAuthorizeEntity::getObjectId, roleId);
        if(this.portalManageId != null) queryWrapper.lambda().eq(SysAuthorizeEntity::getItemId, portalManageId);
        return queryWrapper;
    }

}
