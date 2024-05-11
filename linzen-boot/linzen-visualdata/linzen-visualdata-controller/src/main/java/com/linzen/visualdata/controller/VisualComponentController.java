package com.linzen.visualdata.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.controller.SuperController;
import com.linzen.util.JsonUtil;
import com.linzen.visualdata.entity.VisualComponentEntity;
import com.linzen.visualdata.model.VisualPageVO;
import com.linzen.visualdata.model.visualcomponent.*;
import com.linzen.visualdata.service.VisualComponentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 大屏组件库
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@RestController
@Tag(name = "大屏组件库配置", description = "component")
@RequestMapping("/api/blade-visual/component")
public class VisualComponentController extends SuperController<VisualComponentService, VisualComponentEntity> {

    @Autowired
    private VisualComponentService componentService;

    /**
     * 分页
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "分页")
    @GetMapping("/list")
    public ServiceResult<VisualPageVO<VisualComponentListVO>> list(VisualComponentPaginationModel pagination) {
        List<VisualComponentEntity> data = componentService.getList(pagination);
        List<VisualComponentListVO> list = JsonUtil.createJsonToList(data, VisualComponentListVO.class);
        VisualPageVO paginationVO = BeanUtil.toBean(pagination, VisualPageVO.class);
        paginationVO.setRecords(list);
        return ServiceResult.success(paginationVO);
    }

    /**
     * 详情
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "详情")
    @GetMapping("/detail")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult<VisualComponentInfoVO> info(@RequestParam("id")String id) {
        VisualComponentEntity entity = componentService.getInfo(id);
        VisualComponentInfoVO vo = BeanUtil.toBean(entity, VisualComponentInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 新增
     *
     * @param recordCrForm 数据模型
     * @return
     */
    @Operation(summary = "新增")
    @PostMapping("/save")
    @Parameters({
            @Parameter(name = "recordCrForm", description = "数据模型",required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult create(@RequestBody VisualComponentCrForm recordCrForm) {
        VisualComponentEntity entity = BeanUtil.toBean(recordCrForm, VisualComponentEntity.class);
        componentService.create(entity);
        return ServiceResult.success("新建成功");
    }

    /**
     * 修改
     *
     * @param recordUpForm 数据模型
     * @return
     */
    @Operation(summary = "修改")
    @PostMapping("/update")
    @Parameters({
            @Parameter(name = "recordUpForm", description = "数据模型",required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult update(@RequestBody VisualComponentUpForm recordUpForm) {
        VisualComponentEntity entity = BeanUtil.toBean(recordUpForm, VisualComponentEntity.class);
        componentService.update(entity.getId(), entity);
        return ServiceResult.success("更新成功");
    }

    /**
     * 删除
     *
     * @param ids 主键
     * @return
     */
    @Operation(summary = "删除")
    @PostMapping("/remove")
    @Parameters({
            @Parameter(name = "ids", description = "主键", required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult delete(String ids) {
        VisualComponentEntity entity = componentService.getInfo(ids);
        if (entity != null) {
            componentService.delete(entity);
            return ServiceResult.success("删除成功");
        }
        return ServiceResult.error("删除失败，数据不存在");
    }


}
