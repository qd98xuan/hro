package com.linzen.controller;

import com.linzen.base.ServiceResult;
import com.linzen.base.Page;
import com.linzen.base.model.module.ModuleModel;
import com.linzen.base.vo.ListVO;
import com.linzen.model.AppMenuListVO;
import com.linzen.model.UserMenuModel;
import com.linzen.permission.model.authorize.AuthorizeVO;
import com.linzen.permission.service.AuthorizeService;
import com.linzen.util.JsonUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import com.linzen.util.treeutil.ListToTreeUtil;
import com.linzen.util.treeutil.SumTree;
import com.linzen.util.treeutil.newtreeutil.TreeDotUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * app应用
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "app应用", description = "Menu")
@RestController
@RequestMapping("/api/app/Menu")
public class AppMenuController {
    @Autowired
    private AuthorizeService authorizeService;
    @Autowired
    private UserProvider userProvider;

    /**
     * 获取菜单列表
     *
     * @param page 分页模型
     * @return
     */
    @Operation(summary = "获取菜单列表")
    @GetMapping
    public ServiceResult<ListVO<AppMenuListVO>> list(Page page) {
        AuthorizeVO authorizeModel = authorizeService.getAuthorize(true, false);
        List<ModuleModel> buttonListAll = authorizeModel.getModuleList().stream().filter(t -> "App".equals(t.getCategory())).collect(Collectors.toList());
        // 通过系统id捞取相应的菜单
        buttonListAll = buttonListAll.stream().filter(t -> userProvider.get().getAppSystemId() != null && userProvider.get().getAppSystemId().equals(t.getSystemId())).collect(Collectors.toList());
        List<ModuleModel> buttonList = buttonListAll;
        if (StringUtil.isNotEmpty(page.getKeyword())) {
            buttonList = buttonListAll.stream().filter(t -> t.getFullName().contains(page.getKeyword())).collect(Collectors.toList());
        }
        List<UserMenuModel> list = JsonUtil.createJsonToList(ListToTreeUtil.treeWhere(buttonList, buttonListAll), UserMenuModel.class);
        List<SumTree<UserMenuModel>> menuAll = TreeDotUtils.convertListToTreeDot(list, "-1");
        List<AppMenuListVO> data = JsonUtil.createJsonToList(menuAll, AppMenuListVO.class);
        ListVO listVO = new ListVO();
        listVO.setList(data);
        return ServiceResult.success(listVO);
    }

}
