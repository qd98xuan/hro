package com.linzen.portal.model;

import com.linzen.base.MyBatisPrimaryBase;
import com.linzen.base.UserInfo;
import com.linzen.portal.constant.PortalConst;
import com.linzen.portal.entity.PortalEntity;
import com.linzen.util.UserProvider;
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
public class PortalViewPrimary extends MyBatisPrimaryBase<PortalEntity> {

    private String creatorId;

    private String portalId;

    private String platForm = PortalConst.WEB;

    private String systemId;

    public PortalViewPrimary(String platForm, String portalId){
        if(platForm != null) this.platForm = platForm;
        this.portalId = portalId;
        UserInfo userInfo = UserProvider.getUser();
        this.systemId = PortalConst.WEB.equals(platForm) ? userInfo.getSystemId() : userInfo.getAppSystemId();
        this.creatorId = userInfo.getId();
    }

}
