package com.linzen.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.controller.SuperController;
import com.linzen.base.vo.ListVO;
import com.linzen.entity.ProductclassifyEntity;
import com.linzen.model.productclassify.*;
import com.linzen.service.ProductclassifyService;
import com.linzen.util.JsonUtil;
import com.linzen.util.treeutil.SumTree;
import com.linzen.util.treeutil.TreeDotUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 产品分类
 *
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Slf4j
@RestController
@Tag(name = "产品分类", description = "Classify")
@RequestMapping("/api/extend/saleOrder/Classify")
public class ProductclassifyController extends SuperController<ProductclassifyService, ProductclassifyEntity> {

    @Autowired
    private ProductclassifyService productclassifyService;

    /**
     * 列表
     *
     * @return
     */
    @GetMapping
    @Operation(summary = "列表")
    @SaCheckPermission("saleOrder")
    public ServiceResult<ListVO<ProductclassifyListVO>> list() {
        List<ProductclassifyEntity> data = productclassifyService.getList();
        List<ProductclassifyModel> modelList = JsonUtil.createJsonToList(data, ProductclassifyModel.class);
        List<SumTree<ProductclassifyModel>> sumTrees = TreeDotUtils.convertListToTreeDot(modelList);
        List<ProductclassifyListVO> list = JsonUtil.createJsonToList(sumTrees, ProductclassifyListVO.class);
        ListVO vo = new ListVO();
        vo.setList(list);
        return ServiceResult.success(vo);
    }

    /**
     * 创建
     *
     * @param classifyCrForm 分类模型
     * @return
     */
    @PostMapping
    @Operation(summary = "创建")
    @Parameters({
            @Parameter(name = "classifyCrForm", description = "分类模型", required = true),
    })
    @SaCheckPermission("saleOrder")
    public ServiceResult create(@RequestBody @Valid ProductclassifyCrForm classifyCrForm) {
        ProductclassifyEntity entity = BeanUtil.toBean(classifyCrForm, ProductclassifyEntity.class);
        productclassifyService.create(entity);
        return ServiceResult.success("新建成功");
    }

    /**
     * 信息
     *
     * @param id 主键
     * @return
     */
    @GetMapping("/{id}")
    @Operation(summary = "信息")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission("saleOrder")
    public ServiceResult<ProductclassifyInfoVO> info(@PathVariable("id") String id) {
        ProductclassifyEntity entity = productclassifyService.getInfo(id);
        ProductclassifyInfoVO vo = BeanUtil.toBean(entity, ProductclassifyInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 更新
     *
     * @param id             主键
     * @param classifyUpForm 分类模型
     * @return
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "classifyUpForm", description = "分类模型", required = true),
    })
    @SaCheckPermission("saleOrder")
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid ProductclassifyUpForm classifyUpForm) {
        ProductclassifyEntity entity = BeanUtil.toBean(classifyUpForm, ProductclassifyEntity.class);
        boolean ok = productclassifyService.update(id, entity);
        if (ok) {
            return ServiceResult.success("更新成功");
        }
        return ServiceResult.error("更新失败，数据不存在");
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission("saleOrder")
    public ServiceResult delete(@PathVariable("id") String id) {
        ProductclassifyEntity entity = productclassifyService.getInfo(id);
        if (entity != null) {
            productclassifyService.delete(entity);
        }
        return ServiceResult.success("删除成功");
    }

}
