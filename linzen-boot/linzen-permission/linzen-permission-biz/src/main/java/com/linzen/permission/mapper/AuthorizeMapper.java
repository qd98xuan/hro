package com.linzen.permission.mapper;


import com.linzen.base.mapper.SuperMapper;
import com.linzen.base.model.base.SystemBaeModel;
import com.linzen.base.model.button.ButtonModel;
import com.linzen.base.model.column.ColumnModel;
import com.linzen.base.model.form.ModuleFormModel;
import com.linzen.base.model.module.ModuleModel;
import com.linzen.base.model.resource.ResourceModel;
import com.linzen.permission.entity.SysAuthorizeEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface AuthorizeMapper extends SuperMapper<SysAuthorizeEntity> {


    List<ModuleModel> findModule(@Param("objectId") String objectId, @Param("id") String id, @Param("moduleAuthorize") List<String> moduleAuthorize, @Param("moduleUrlAddressAuthorize") List<String> moduleUrlAddressAuthorize, @Param("mark") Integer mark);

    List<ButtonModel> findButton(@Param("objectId") String objectId);

    List<ColumnModel> findColumn(@Param("objectId") String objectId);

    List<ResourceModel> findResource(@Param("objectId") String objectId);

    List<ModuleFormModel> findForms(@Param("objectId") String objectId);

    List<SystemBaeModel> findSystem(@Param("objectId") String objectId, @Param("enCode") String enCode, @Param("moduleAuthorize") List<String> moduleAuthorize, @Param("mark") Integer mark);

    List<ButtonModel> findButtonAdmin(@Param("mark") Integer mark);

    List<ColumnModel> findColumnAdmin(@Param("mark") Integer mark);

    List<ResourceModel> findResourceAdmin(@Param("mark") Integer mark);

    List<ModuleFormModel> findFormsAdmin(@Param("mark") Integer mark);

    void saveBatch(@Param("values") String values);

    void savaAuth(SysAuthorizeEntity authorizeEntity);

}
