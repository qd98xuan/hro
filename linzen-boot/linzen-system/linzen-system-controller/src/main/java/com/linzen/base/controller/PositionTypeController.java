package com.linzen.base.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.DictionaryTypeEntity;
import com.linzen.base.model.dictionarytype.*;
import com.linzen.base.service.DictionaryTypeService;
import com.linzen.base.vo.ListVO;
import com.linzen.exception.DataBaseException;
import com.linzen.util.JsonUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.treeutil.SumTree;
import com.linzen.util.treeutil.TreeDotUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 字典分类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "PositionType", description = "PositionType")
@RestController
@RequestMapping("/api/system/PositionType")
public class PositionTypeController extends SuperController<DictionaryTypeService, DictionaryTypeEntity> {

    @Autowired
    private DictionaryTypeService dictionaryTypeService;

    /**
     * 获取字典分类
     *
     * @return
     */
    @Operation(summary = "获取字典分类")
    @GetMapping
    public ServiceResult<ListVO<DictionaryTypeListVO>> list() {

        List<DictionaryTypeEntity> data = dictionaryTypeService.getList();
        List<DictionaryTypeModel> voListVO = JsonUtil.createJsonToList(data, DictionaryTypeModel.class);
        voListVO.forEach(vo -> {
            if (StringUtil.isNotEmpty(vo.getCategory()) && "1".equals(vo.getCategory()) && "-1".equals(vo.getParentId())) {
                vo.setCategory("系统");
                vo.setParentId("1");
            } else if (StringUtil.isNotEmpty(vo.getCategory()) && "0".equals(vo.getCategory()) && "-1".equals(vo.getParentId())) {
                vo.setCategory("业务");
                vo.setParentId("0");
            } else if (StringUtil.isNotEmpty(vo.getCategory()) && "2".equals(vo.getCategory()) && "-1".equals(vo.getParentId())) {
                vo.setCategory("物联");
                vo.setParentId("0");
            }
        });
        List<SumTree<DictionaryTypeModel>> sumTrees = TreeDotUtils.convertListToTreeDot(voListVO);
        List<DictionaryTypeListVO> list = JsonUtil.createJsonToList(sumTrees, DictionaryTypeListVO.class);

        DictionaryTypeListVO parentVO = new DictionaryTypeListVO();
        parentVO.setFullName("系统字典");
        parentVO.setChildren(new ArrayList<>());
        parentVO.setId("1");
        DictionaryTypeListVO parentVO1 = new DictionaryTypeListVO();
        parentVO1.setFullName("业务字典");
        parentVO1.setChildren(new ArrayList<>());
        parentVO1.setId("0");

        list.forEach(vo -> {
            if ("系统".equals(vo.getCategory())) {
                List<DictionaryTypeListVO> children = parentVO.getChildren();
                children.add(vo);
                parentVO.setHasChildren(true);
            } else if ("业务".equals(vo.getCategory())) {
                List<DictionaryTypeListVO> children = parentVO1.getChildren();
                children.add(vo);
                parentVO1.setHasChildren(true);
            }
        });
        List<DictionaryTypeListVO> listVo = new ArrayList<>();
        listVo.add(parentVO1);
        listVo.add(parentVO);

        ListVO<DictionaryTypeListVO> vo = new ListVO<>();
        vo.setList(listVo);
        return ServiceResult.success(vo);
    }


    /**
     * 获取字典分类
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "获取所有字典分类下拉框列表")
    @Parameter(name = "id", description = "主键", required = true)
    @GetMapping("/Selector/{id}")
    public ServiceResult<ListVO<DictionaryTypeListVO>> selectorTreeView(@PathVariable("id") String id) {
        List<DictionaryTypeEntity> data = dictionaryTypeService.getList();
        if (!"0".equals(id)) {
            data.remove(dictionaryTypeService.getInfo(id));
        }
        List<DictionaryTypeModel> voListVO = JsonUtil.createJsonToList(data, DictionaryTypeModel.class);
        voListVO.forEach(vo -> {
            if (StringUtil.isNotEmpty(vo.getCategory()) && "1".equals(vo.getCategory()) && "-1".equals(vo.getParentId())) {
                vo.setCategory("系统");
                vo.setParentId("1");
            } else if (StringUtil.isNotEmpty(vo.getCategory()) && "0".equals(vo.getCategory()) && "-1".equals(vo.getParentId())) {
                vo.setCategory("业务");
                vo.setParentId("0");
            } else if (StringUtil.isNotEmpty(vo.getCategory()) && "2".equals(vo.getCategory()) && "-1".equals(vo.getParentId())) {
                vo.setCategory("物联");
                vo.setParentId("0");
            }
        });
        List<SumTree<DictionaryTypeModel>> sumTrees = TreeDotUtils.convertListToTreeDot(voListVO);
        List<DictionaryTypeListVO> list = JsonUtil.createJsonToList(sumTrees, DictionaryTypeListVO.class);

        DictionaryTypeListVO parentVO = new DictionaryTypeListVO();
        parentVO.setFullName("系统字典");
        parentVO.setChildren(new ArrayList<>());
        parentVO.setId("1");
        DictionaryTypeListVO parentVO1 = new DictionaryTypeListVO();
        parentVO1.setFullName("业务字典");
        parentVO1.setChildren(new ArrayList<>());
        parentVO1.setId("0");

        list.forEach(vo -> {
            if ("系统".equals(vo.getCategory())) {
                List<DictionaryTypeListVO> children = parentVO.getChildren();
                children.add(vo);
                parentVO.setHasChildren(true);
            } else if ("业务".equals(vo.getCategory())) {
                List<DictionaryTypeListVO> children = parentVO1.getChildren();
                children.add(vo);
                parentVO1.setHasChildren(true);
            }
        });
        List<DictionaryTypeListVO> listVo = new ArrayList<>();
        listVo.add(parentVO1);
        listVo.add(parentVO);

        ListVO<DictionaryTypeListVO> vo = new ListVO<>();
        vo.setList(listVo);
        return ServiceResult.success(vo);
    }

    /**
     * 获取字典分类信息
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "获取字典分类信息")
    @Parameter(name = "id", description = "主键", required = true)
    @GetMapping("/{id}")
    public ServiceResult<DictionaryTypeInfoVO> info(@PathVariable("id") String id) throws DataBaseException {
        DictionaryTypeEntity entity = dictionaryTypeService.getInfo(id);
        if ("-1".equals(entity.getParentId())) {
            entity.setParentId(String.valueOf(entity.getCategory()));
        }
        DictionaryTypeInfoVO vo = BeanUtil.toBean(entity, DictionaryTypeInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 添加字典分类
     *
     * @param dictionaryTypeCrForm 实体对象
     * @return
     */
    @Operation(summary = "添加字典分类")
    @Parameter(name = "dictionaryTypeCrForm", description = "实体对象", required = true)
    @SaCheckPermission("systemData.dictionary")
    @PostMapping
    public ServiceResult create(@RequestBody @Valid DictionaryTypeCrForm dictionaryTypeCrForm) {
        DictionaryTypeEntity entity = BeanUtil.toBean(dictionaryTypeCrForm, DictionaryTypeEntity.class);
        if ("0".equals(entity.getParentId()) || "1".equals(entity.getParentId()) || "2".equals(entity.getParentId())) {
            entity.setCategory(Integer.parseInt(entity.getParentId()));
            entity.setParentId("-1");
        } else {
            DictionaryTypeEntity entity1 = dictionaryTypeService.getInfo(dictionaryTypeCrForm.getParentId());
            entity.setCategory(entity1.getCategory());
        }
        if (dictionaryTypeService.isExistByFullName(entity.getFullName(), entity.getId())) {
            return ServiceResult.error("名称不能重复");
        }
        if (dictionaryTypeService.isExistByEnCode(entity.getEnCode(), entity.getId())) {
            return ServiceResult.error("编码不能重复");
        }
        dictionaryTypeService.create(entity);
        return ServiceResult.success("新建成功");
    }

    /**
     * 修改字典分类
     *
     * @param dictionaryTypeUpForm 实体对象
     * @param id                   主键值
     * @return
     */
    @Operation(summary = "修改字典分类")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true),
            @Parameter(name = "dictionaryTypeUpForm", description = "实体对象", required = true)
    })
    @SaCheckPermission("systemData.dictionary")
    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid DictionaryTypeUpForm dictionaryTypeUpForm) {
        DictionaryTypeEntity entity = BeanUtil.toBean(dictionaryTypeUpForm, DictionaryTypeEntity.class);
        if ("0".equals(entity.getParentId()) || "1".equals(entity.getParentId()) || "2".equals(entity.getParentId())) {
            entity.setCategory(Integer.parseInt(entity.getParentId()));
            entity.setParentId("-1");
        } else {
            DictionaryTypeEntity entity1 = dictionaryTypeService.getInfo(dictionaryTypeUpForm.getParentId());
            entity.setCategory(entity1.getCategory());
        }
        if (dictionaryTypeService.isExistByFullName(entity.getFullName(), id)) {
            return ServiceResult.error("名称不能重复");
        }
        if (dictionaryTypeService.isExistByEnCode(entity.getEnCode(), id)) {
            return ServiceResult.error("编码不能重复");
        }
        boolean flag = dictionaryTypeService.update(id, entity);
        if (!flag) {
            return ServiceResult.success("更新失败，数据不存在");
        }
        return ServiceResult.success("更新成功");
    }

    /**
     * 删除字典分类
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "删除字典分类")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("systemData.dictionary")
    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable("id") String id) {
        DictionaryTypeEntity entity = dictionaryTypeService.getInfo(id);
        if (entity != null) {
            boolean isOk = dictionaryTypeService.delete(entity);
            if (isOk) {
                return ServiceResult.success("删除成功");
            } else {
                return ServiceResult.error("字典类型下面有字典值禁止删除");
            }
        }
        return ServiceResult.error("删除失败，数据不存在");
    }

}
