package com.linzen.visualdata.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.controller.SuperController;
import com.linzen.util.JsonUtil;
import com.linzen.visualdata.entity.VisualCategoryEntity;
import com.linzen.visualdata.model.VisualPageVO;
import com.linzen.visualdata.model.VisualPagination;
import com.linzen.visualdata.model.visualcategory.VisualCategoryCrForm;
import com.linzen.visualdata.model.visualcategory.VisualCategoryInfoVO;
import com.linzen.visualdata.model.visualcategory.VisualCategoryListVO;
import com.linzen.visualdata.model.visualcategory.VisualCategoryUpForm;
import com.linzen.visualdata.service.VisualCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 大屏分类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@RestController
@Tag(name = "大屏分类", description = "category")
@RequestMapping("/api/blade-visual/category")
public class VisualCategoryController extends SuperController<VisualCategoryService, VisualCategoryEntity> {

    @Autowired
    private VisualCategoryService categoryService;

    /**
     * 列表
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "列表")
    @GetMapping("/page")
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult<VisualPageVO<VisualCategoryListVO>> list(VisualPagination pagination) {
        List<VisualCategoryEntity> data = categoryService.getList(pagination);
        List<VisualCategoryListVO> list = BeanUtil.copyToList(data, VisualCategoryListVO.class);
        VisualPageVO<VisualCategoryListVO> paginationVO = BeanUtil.toBean(pagination, VisualPageVO.class);
        paginationVO.setRecords(list);
        return ServiceResult.success(paginationVO);
    }

    /**
     * 列表
     *
     * @return
     */
    @Operation(summary = "列表")
    @GetMapping("/list")
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult<List<VisualCategoryListVO>> list() {
        List<VisualCategoryEntity> data = categoryService.getList();
        List<VisualCategoryListVO> list = BeanUtil.copyToList(data, VisualCategoryListVO.class);
        return ServiceResult.success(list);
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
    public ServiceResult<VisualCategoryInfoVO> info(@RequestParam("id") String id) {
        VisualCategoryEntity entity = categoryService.getInfo(id);
        VisualCategoryInfoVO vo = BeanUtil.toBean(entity, VisualCategoryInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 新增
     *
     * @param categoryCrForm 大屏分类模型
     * @return
     */
    @Operation(summary = "新增")
    @PostMapping("/save")
    @Parameters({
            @Parameter(name = "categoryCrForm", description = "大屏分类模型",required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult create(@RequestBody @Valid VisualCategoryCrForm categoryCrForm) {
        VisualCategoryEntity entity = BeanUtil.toBean(categoryCrForm, VisualCategoryEntity.class);
        if (categoryService.isExistByValue(entity.getCategoryValue(), entity.getId())) {
            return ServiceResult.error("模块键值已存在");
        }
        categoryService.create(entity);
        return ServiceResult.success("新建成功");
    }

    /**
     * 修改
     *
     * @param categoryUpForm 大屏分类模型
     * @return
     */
    @Operation(summary = "修改")
    @PostMapping("/update")
    @Parameters({
            @Parameter(name = "categoryUpForm", description = "大屏分类模型",required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult update(@RequestBody VisualCategoryUpForm categoryUpForm) {
        VisualCategoryEntity entity = BeanUtil.toBean(categoryUpForm, VisualCategoryEntity.class);
        if (categoryService.isExistByValue(entity.getCategoryValue(), entity.getId())) {
            return ServiceResult.error("模块键值已存在");
        }
        boolean flag = categoryService.update(categoryUpForm.getId(), entity);
        if (!flag) {
            return ServiceResult.error("更新失败，数据不存在");
        }
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
        VisualCategoryEntity entity = categoryService.getInfo(ids);
        if (entity != null) {
            categoryService.delete(entity);
            return ServiceResult.success("删除成功");
        }
        return ServiceResult.error("删除失败，数据不存在");
    }

}
