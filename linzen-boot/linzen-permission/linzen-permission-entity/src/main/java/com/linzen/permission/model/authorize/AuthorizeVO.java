package com.linzen.permission.model.authorize;

import com.linzen.base.model.base.SystemBaeModel;
import com.linzen.base.model.button.ButtonModel;
import com.linzen.base.model.column.ColumnModel;
import com.linzen.base.model.form.ModuleFormModel;
import com.linzen.base.model.module.ModuleModel;
import com.linzen.base.model.resource.ResourceModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class AuthorizeVO {
    // 菜单
//    private List<MenuModel> menuList;

    /**
     * 功能
     */
    private List<ModuleModel> moduleList = new ArrayList<>();

    /**
     * 按钮
     */
    private List<ButtonModel> buttonList = new ArrayList<>();

    /**
     * 视图
     */
    private List<ColumnModel> columnList = new ArrayList<>();

    /**
     * 资源
     */
    private List<ResourceModel> resourceList = new ArrayList<>();

    /**
     * 表单
     */
    private List<ModuleFormModel> formsList = new ArrayList<>();

    /**
     * 系统
     */
    private List<SystemBaeModel> systemList = new ArrayList<>();

    public AuthorizeVO(List<ModuleModel> moduleList, List<ButtonModel> buttonList, List<ColumnModel> columnList, List<ResourceModel> resourceList, List<ModuleFormModel> formsList, List<SystemBaeModel> systemList) {
//        this.menuList = menuList;
        this.moduleList = moduleList;
        this.buttonList = buttonList;
        this.columnList = columnList;
        this.resourceList = resourceList;
        this.formsList = formsList;
        this.systemList = systemList;
    }

    public AuthorizeVO() {
    }
}
