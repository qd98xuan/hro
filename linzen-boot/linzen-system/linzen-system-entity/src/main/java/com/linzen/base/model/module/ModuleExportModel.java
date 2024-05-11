package com.linzen.base.model.module;

import com.linzen.base.entity.*;
import lombok.Data;

import java.util.List;

/**
 * 系统菜单导出模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ModuleExportModel {
    private ModuleEntity moduleEntity;
    private List<ModuleButtonEntity> buttonEntityList;
    private List<ModuleColumnEntity> columnEntityList;
    private List<ModuleFormEntity> formEntityList;
    private List<ModuleDataAuthorizeSchemeEntity> schemeEntityList;
    private List<ModuleDataAuthorizeEntity> authorizeEntityList;
}
