package com.linzen.base.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.linzen.base.entity.PortalManageEntity;
import com.linzen.base.model.portalManage.PortalManagePage;
import com.linzen.base.model.portalManage.PortalManagePageDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 虎门管理
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface PortalManageMapper extends SuperMapper<PortalManageEntity> {

    @Select("SELECT f_full_name FROM base_portal WHERE F_Id = #{portalId}")
    String getPortalFullName(String portalId);

    @Select("SELECT f_category FROM base_portal WHERE F_Id = #{portalId}")
    String getPortalCategoryId(String portalId);

    PageDTO<PortalManagePageDO> selectPortalManageDoPage(PageDTO<PortalManagePageDO> page, @Param("pmPage") PortalManagePage pmPage);

    List<PortalManagePageDO> selectPortalManageDoList(@Param("pmPage") PortalManagePage pmPage);

    List<PortalManagePageDO> selectPortalBySystemIds(@Param("systemIds") List<String> systemIds, @Param("collect") List<String> collect);

}
