package com.linzen.portal.model;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.MyBatisPrimaryBase;
import com.linzen.portal.constant.PortalConst;
import com.linzen.portal.entity.PortalDataEntity;
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
public class PortalModPrimary extends MyBatisPrimaryBase<PortalDataEntity> {

    /**
     * 门户ID
     */
    private String portalId;

    /**
     * 类型（model：模型、custom：自定义）
     */
    private String type = PortalConst.MODEL;

    public PortalModPrimary(String portalId){
        this.portalId = portalId;
    }

    public QueryWrapper<PortalDataEntity> getQuery(){
        queryWrapper.lambda().eq(PortalDataEntity::getType, type);
        if(this.portalId != null) queryWrapper.lambda().eq(PortalDataEntity::getPortalId, portalId);
        return queryWrapper;
    }

}
