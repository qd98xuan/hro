package com.linzen.base.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.linzen.base.entity.PortalManageEntity;
import com.linzen.base.model.portalManage.PortalManagePage;
import com.linzen.base.model.portalManage.PortalManagePageDO;
import com.linzen.base.model.portalManage.PortalManagePrimary;
import com.linzen.base.model.portalManage.PortalManageVO;

import java.util.List;

/**
 * <p>
 * 门户管理 服务类
 * </p>
 *
 * @author FHNP
 * @since 2023-02-16
 */
public interface PortalManageService extends SuperService<PortalManageEntity> {

    void checkCreUp(PortalManageEntity portalManageEntity) throws Exception;

    PortalManageVO convertVO(PortalManageEntity entity);

    List<PortalManageVO> getList(PortalManagePrimary primary);

    List<PortalManageVO> getListByEnable(PortalManagePrimary primary);

    PageDTO<PortalManagePageDO> getPage(PortalManagePage portalPagination);

    List<PortalManagePageDO> getSelectList(PortalManagePage pmPage);

    List<PortalManagePageDO> selectPortalBySystemIds(List<String> systemIds, List<String> collect);

    void createBatch(List<PortalManagePrimary> primaryLit) throws Exception;

}
