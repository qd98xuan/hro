package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.ProvinceEntity;
import com.linzen.base.entity.SuperBaseEntity;
import com.linzen.base.model.province.*;
import com.linzen.base.service.ProvinceService;
import com.linzen.base.vo.ListVO;
import com.linzen.constant.MsgCode;
import com.linzen.exception.DataBaseException;
import com.linzen.util.JsonUtil;
import com.linzen.util.JsonUtilEx;
import com.linzen.util.StringUtil;
import com.linzen.util.treeutil.ListToTreeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 行政区划
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "行政区划", description = "Area")
@RestController
@RequestMapping("/api/system/Area")
public class AreaController extends SuperController<ProvinceService, ProvinceEntity> {

    @Autowired
    private ProvinceService provinceService;

    /**
     * 列表（异步加载）
     *
     * @param nodeId 节点主键
     * @param page   关键字
     * @return
     */
    @Operation(summary = "列表（异步加载）")
    @Parameters({
            @Parameter(name = "nodeId", description = "节点主键", required = true)
    })
    @SaCheckPermission("system.area")
    @GetMapping("/{nodeId}")
    public ServiceResult<ListVO<ProvinceListVO>> list(@PathVariable("nodeId") String nodeId, PaginationProvince page) {
        List<ProvinceEntity> data = provinceService.getList(nodeId, page);
        List<ProvinceEntity> dataAll = data;
        List<ProvinceEntity> result = JsonUtil.createJsonToList(ListToTreeUtil.treeWhere(data, dataAll), ProvinceEntity.class);
        List<ProvinceListVO> treeList = JsonUtil.createJsonToList(result, ProvinceListVO.class);
        int i = 0;
        for (ProvinceListVO entity : treeList) {
            boolean childNode = provinceService.getList(entity.getId()).size() <= 0;
            ProvinceListVO provinceListVO = BeanUtil.toBean(entity, ProvinceListVO.class);
            provinceListVO.setIsLeaf(childNode);
            provinceListVO.setHasChildren(!childNode);
            treeList.set(i, provinceListVO);
            i++;
        }
        ListVO<ProvinceListVO> vo = new ListVO<>();
        vo.setList(treeList);
        return ServiceResult.success(vo);
    }

    /**
     * 获取行政区划下拉框数据
     *
     * @param id  主键
     * @param ids 主键集合
     * @return
     */
    @Operation(summary = "获取行政区划下拉框数据")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "ids", description = "主键集合", required = true)
    })
    @GetMapping("/{id}/Selector/{ids}")
    public ServiceResult<ListVO<ProvinceSelectListVO>> selectList(@PathVariable("id") String id, @PathVariable("ids") String ids) {
        List<ProvinceEntity> data = provinceService.getList(id);
        data = data.stream().filter(t -> t.getEnabledMark() == 1).collect(Collectors.toList());
        if (!"0".equals(ids)) {
            //排除子集
            filterData(data, new ArrayList<>(Arrays.asList(new String[]{ids})));
        }
        List<ProvinceSelectListVO> treeList = JsonUtil.createJsonToList(data, ProvinceSelectListVO.class);
        int i = 0;
        for (ProvinceSelectListVO entity : treeList) {
//            boolean childNode = provinceService.getList(entity.getId()).size() <= 0;
            ProvinceSelectListVO provinceListVO = BeanUtil.toBean(entity, ProvinceSelectListVO.class);
            provinceListVO.setIsLeaf(false);
            treeList.set(i, provinceListVO);
            i++;
        }
        ListVO<ProvinceSelectListVO> vo = new ListVO<>();
        vo.setList(treeList);
        return ServiceResult.success(vo);
    }

    /**
     * 递归排除子集
     *
     * @param data 普通列表
     * @param id   ignore
     */
    private void filterData(List<ProvinceEntity> data, List<String> id) {
        List<ProvinceEntity> collect = null;
        //获取子集信息
        for (String ids : id) {
            collect = data.stream().filter(t -> ids.equals(t.getParentId())).collect(Collectors.toList());
            data.removeAll(collect);
        }
        //递归移除子集的子集
        if (collect != null && !collect.isEmpty()) {
            filterData(data, collect.stream().map(SuperBaseEntity.SuperIBaseEntity::getId).collect(Collectors.toList()));
        }
    }

    /**
     * 信息
     *
     * @param id 主键值
     * @return ignore
     */
    @Operation(summary = "获取行政区划信息")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("system.area")
    @GetMapping("/{id}/Info")
    public ServiceResult<ProvinceInfoVO> info(@PathVariable("id") String id) throws DataBaseException {
        ProvinceEntity entity = provinceService.getInfo(id);
        ProvinceInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, ProvinceInfoVO.class);
        if (!"-1".equals(entity.getParentId())) {
            ProvinceEntity parent = provinceService.getInfo(entity.getParentId());
            vo.setParentName(parent.getFullName());
        }
        return ServiceResult.success(vo);
    }

    /**
     * 新建
     *
     * @param provinceCrForm 实体对象
     * @return ignore
     */
    @Operation(summary = "添加行政区划")
    @Parameters({
            @Parameter(name = "provinceCrForm", description = "实体对象", required = true)
    })
    @SaCheckPermission("system.area")
    @PostMapping
    public ServiceResult create(@RequestBody @Valid ProvinceCrForm provinceCrForm) {
        ProvinceEntity entity = BeanUtil.toBean(provinceCrForm, ProvinceEntity.class);
        if (provinceService.isExistByEnCode(provinceCrForm.getEnCode(), entity.getId())) {
            return ServiceResult.error("区域编码不能重复");
        }
        if (StringUtil.isEmpty(provinceCrForm.getParentId())) {
            entity.setParentId("-1");
        }
        if (entity.getParentId().equals("-1")) {
            entity.setType("1");
        } else {
            ProvinceEntity info = provinceService.getInfo(entity.getParentId());
            int type = info != null ? Integer.valueOf(info.getType()) + 1 : 1;
            entity.setType(String.valueOf(type));
        }
        provinceService.create(entity);
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 更新
     *
     * @param id             主键值
     * @param provinceUpForm ignore
     * @return ignore
     */
    @Operation(summary = "修改行政区划")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true),
            @Parameter(name = "provinceUpForm", description = "实体对象", required = true)
    })
    @SaCheckPermission("system.area")
    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid ProvinceUpForm provinceUpForm) {
        ProvinceEntity entity = BeanUtil.toBean(provinceUpForm, ProvinceEntity.class);
        if (provinceService.isExistByEnCode(provinceUpForm.getEnCode(), id)) {
            return ServiceResult.error("区域编码不能重复");
        }
        boolean flag = provinceService.update(id, entity);
        if (!flag) {
            return ServiceResult.error(MsgCode.FA002.get());
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 删除
     *
     * @param id 主键值
     * @return ignore
     */
    @Operation(summary = "删除")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("system.area")
    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable("id") String id) {
        if (provinceService.getList(id).size() == 0) {
            ProvinceEntity entity = provinceService.getInfo(id);
            if (entity != null) {
                provinceService.delete(entity);
                return ServiceResult.success(MsgCode.SU003.get());
            }
            return ServiceResult.error(MsgCode.FA003.get());
        } else {
            return ServiceResult.error("删除失败，当前有子节点数据");
        }
    }

    /**
     * 更新行政区划状态
     *
     * @param id 主键值
     * @return ignore
     */
    @Operation(summary = "更新行政区划状态")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("system.area")
    @PutMapping("/{id}/Actions/State")
    public ServiceResult upState(@PathVariable("id") String id) {
        ProvinceEntity entity = provinceService.getInfo(id);
        if (entity.getEnabledMark() == null || "1".equals(String.valueOf(entity.getEnabledMark()))) {
            entity.setEnabledMark(0);
        } else {
            entity.setEnabledMark(1);
        }
        boolean flag = provinceService.update(id, entity);
        if (!flag) {
            return ServiceResult.error(MsgCode.FA002.get());
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 行政区划id转名称
     *
     * @param model 二维数组
     * @return ignore
     */
    @Operation(summary = "行政区划id转名称")
    @Parameters({
            @Parameter(name = "model", description = "二维数组", required = true)
    })
    @PostMapping("/GetAreaByIds")
    public ServiceResult getAreaByIds(@RequestBody AreaModel model) {
        // 返回给前端的list
        List<List<String>> list = new LinkedList<>();
        for (List<String> idList : model.getIdsList()) {
            List<ProvinceEntity> proList = provinceService.getProList(idList);
            List<String> collect = proList.stream().map(ProvinceEntity::getFullName).collect(Collectors.toList());
            list.add(collect);
        }
        return ServiceResult.success(list);
    }

}
