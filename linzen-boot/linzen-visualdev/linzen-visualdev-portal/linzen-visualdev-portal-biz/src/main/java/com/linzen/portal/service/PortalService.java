package com.linzen.portal.service;

import com.linzen.base.model.VisualFunctionModel;
import com.linzen.base.service.SuperService;
import com.linzen.portal.entity.PortalEntity;
import com.linzen.portal.model.PortalPagination;
import com.linzen.portal.model.PortalSelectModel;
import com.linzen.portal.model.PortalSelectVO;
import com.linzen.portal.model.PortalViewPrimary;

import java.util.List;


/**
 * base_portal
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP管理员/admin
 * @date 2023-04-01
 */

public interface PortalService extends SuperService<PortalEntity> {

    PortalEntity getInfo(String id);

    /**
     * 是否重名
     */
    Boolean isExistByFullName(String fullName, String id);

    /**
     * 是否重码
     */
    Boolean isExistByEnCode(String encode, String id);

    void create(PortalEntity entity);

    Boolean update(String id, PortalEntity entity);

    void delete(PortalEntity entity) throws Exception;

    List<PortalEntity> getList(PortalPagination pagination);

    String getModListFirstId(PortalViewPrimary primary);

    List<PortalSelectModel> getModList(PortalViewPrimary primary);

    List<PortalSelectModel> getModSelectList();

    /**
     * 获取门户模型集合
     *
     * @param pagination 分页信息
     * @return 模型集合
     */
    List<VisualFunctionModel> getModelList(PortalPagination pagination);


    /**
     * 获取门户管理下拉
     *
     * @param pagination 分页信息
     * @param systemId   系统ID
     * @return 分页结婚
     */
    List<PortalSelectVO> getManageSelectorPage(PortalPagination pagination, String systemId);

}
