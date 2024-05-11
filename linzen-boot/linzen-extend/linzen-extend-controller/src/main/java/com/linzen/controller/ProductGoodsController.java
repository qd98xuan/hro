package com.linzen.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.controller.SuperController;
import com.linzen.base.vo.ListVO;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.entity.ProductGoodsEntity;
import com.linzen.model.productgoods.*;
import com.linzen.service.ProductGoodsService;
import com.linzen.util.JsonUtil;
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
 * 产品商品
 *
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Slf4j
@RestController
@Tag(name = "产品商品", description = "Goods")
@RequestMapping("/api/extend/saleOrder/Goods")
public class ProductGoodsController extends SuperController<ProductGoodsService, ProductGoodsEntity> {

    @Autowired
    private ProductGoodsService productgoodsService;

    /**
     * 列表
     *
     * @param type 类型
     * @return
     */
    @GetMapping("/getGoodList")
    @Operation(summary = "列表")
    @Parameters({
            @Parameter(name = "type", description = "类型"),
    })
    @SaCheckPermission("saleOrder")
    public ServiceResult<ListVO<ProductGoodsListVO>> list(@RequestParam("type")String type) {
        List<ProductGoodsEntity> list = productgoodsService.getGoodList(type);
        List<ProductGoodsListVO> listVO = JsonUtil.createJsonToList(list, ProductGoodsListVO.class);
        ListVO vo = new ListVO();
        vo.setList(listVO);
        return ServiceResult.success(vo);
    }

    /**
     * 列表
     *
     * @param goodsPagination 分页模型
     * @return
     */
    @GetMapping
    @Operation(summary = "列表")
    @SaCheckPermission("saleOrder")
    public ServiceResult<PageListVO<ProductGoodsListVO>> list(ProductGoodsPagination goodsPagination) {
        List<ProductGoodsEntity> list = productgoodsService.getList(goodsPagination);
        List<ProductGoodsListVO> listVO = JsonUtil.createJsonToList(list, ProductGoodsListVO.class);
        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        PaginationVO page = BeanUtil.toBean(goodsPagination, PaginationVO.class);
        vo.setPagination(page);
        return ServiceResult.success(vo);
    }

    /**
     * 创建
     *
     * @param goodsCrForm 商品模型
     * @return
     */
    @PostMapping
    @Operation(summary = "创建")
    @Parameters({
            @Parameter(name = "goodsCrForm", description = "商品模型",required = true),
    })
    @SaCheckPermission("saleOrder")
    public ServiceResult create(@RequestBody @Valid ProductGoodsCrForm goodsCrForm) {
        ProductGoodsEntity entity = BeanUtil.toBean(goodsCrForm, ProductGoodsEntity.class);
        productgoodsService.create(entity);
        return ServiceResult.success("新建成功");
    }

    /**
     * 信息
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "信息")
    @GetMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission("saleOrder")
    public ServiceResult<ProductGoodsInfoVO> info(@PathVariable("id") String id) {
        ProductGoodsEntity entity = productgoodsService.getInfo(id);
        ProductGoodsInfoVO vo = BeanUtil.toBean(entity, ProductGoodsInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 更新
     *
     * @param id                主键
     * @param goodsCrFormUpForm 商品模型
     * @return
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "goodsCrFormUpForm", description = "商品模型",required = true),
    })
    @SaCheckPermission("saleOrder")
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid ProductGoodsUpForm goodsCrFormUpForm) {
        ProductGoodsEntity entity = BeanUtil.toBean(goodsCrFormUpForm, ProductGoodsEntity.class);
        boolean ok = productgoodsService.update(id, entity);
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
        ProductGoodsEntity entity = productgoodsService.getInfo(id);
        if (entity != null) {
            productgoodsService.delete(entity);
        }
        return ServiceResult.success("删除成功");
    }

    /**
     * 下拉
     *
     * @param goodsPagination 下拉模型
     * @return
     */
    @GetMapping("/Selector")
    @Operation(summary = "下拉")
    @SaCheckPermission("saleOrder")
    public ServiceResult<ListVO<ProductGoodsListVO>> listSelect(ProductGoodsPagination goodsPagination) {
        goodsPagination.setCurrentPage(1);
        goodsPagination.setPageSize(50);
        List<ProductGoodsEntity> list = productgoodsService.getList(goodsPagination);
        List<ProductGoodsListVO> listVO = JsonUtil.createJsonToList(list, ProductGoodsListVO.class);
        ListVO vo = new ListVO();
        vo.setList(listVO);
        return ServiceResult.success(vo);
    }

}
