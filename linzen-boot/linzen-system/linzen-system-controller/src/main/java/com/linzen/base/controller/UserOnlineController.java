package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.Pagination;
import com.linzen.base.model.online.BatchOnlineModel;
import com.linzen.base.service.UserOnlineService;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.message.model.UserOnlineModel;
import com.linzen.message.model.UserOnlineVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 在线用户
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "在线用户", description = "Online")
@RestController
@RequestMapping("/api/system/OnlineUser")
public class UserOnlineController {

    @Autowired
    private UserOnlineService userOnlineService;

    /**
     * 列表
     *
     * @param page 分页参数
     * @return ignore
     */
    @Operation(summary = "获取在线用户列表")
    @SaCheckPermission("permission.userOnline")
    @GetMapping
    public ServiceResult<PageListVO<UserOnlineVO>> list(Pagination page) {
        List<UserOnlineModel> data = userOnlineService.getList(page);
        List<UserOnlineVO> voList= data.stream().map(online->{
            UserOnlineVO vo = BeanUtil.toBean(online, UserOnlineVO.class);
            vo.setUserId(online.getToken());
            //vo.setUserName(vo.getUserName() + "/" + online.getDevice());
            return vo;
        }).collect(Collectors.toList());
        PaginationVO paginationVO = BeanUtil.toBean(page, PaginationVO.class);
        return ServiceResult.pageList(voList, paginationVO);
    }

    /**
     * 注销
     *
     * @param token 主键值
     * @return
     */
    @Operation(summary = "强制下线")
    @Parameter(name = "token", description = "token", required = true)
    @SaCheckPermission("permission.userOnline")
    @DeleteMapping("/{token}")
    public ServiceResult delete(@PathVariable("token") String token) {
        userOnlineService.delete(token);
        return ServiceResult.success("操作成功");
    }

    /**
     * 批量下线用户
     *
     * @param model 主键值
     * @return ignore
     */
    @Operation(summary = "批量下线用户")
    @Parameter(name = "model", description = "在线用户id集合", required = true)
    @SaCheckPermission("permission.userOnline")
    @DeleteMapping
    public ServiceResult clear(@RequestBody BatchOnlineModel model) {
        userOnlineService.delete(model.getIds());
        return ServiceResult.success("操作成功");
    }
}
