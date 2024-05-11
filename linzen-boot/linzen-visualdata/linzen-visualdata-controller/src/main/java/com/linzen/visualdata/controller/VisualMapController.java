package com.linzen.visualdata.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.controller.SuperController;
import com.linzen.util.JsonUtil;
import com.linzen.visualdata.entity.VisualMapEntity;
import com.linzen.visualdata.model.VisualPageVO;
import com.linzen.visualdata.model.VisualPagination;
import com.linzen.visualdata.model.visualmap.VisualMapCrForm;
import com.linzen.visualdata.model.visualmap.VisualMapInfoVO;
import com.linzen.visualdata.model.visualmap.VisualMapListVO;
import com.linzen.visualdata.model.visualmap.VisualMapUpForm;
import com.linzen.visualdata.service.VisualMapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * 大屏地图
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@RestController
@Tag(name = "大屏地图", description = "map")
@RequestMapping("/api/blade-visual/map")
public class VisualMapController extends SuperController<VisualMapService, VisualMapEntity> {

    @Autowired
    private VisualMapService mapService;

    /**
     * 分页
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "分页")
    @GetMapping("/list")
    public ServiceResult<VisualPageVO<VisualMapListVO>> list(VisualPagination pagination) {
        List<VisualMapEntity> data = mapService.getListWithColnums(pagination, VisualMapEntity::getId, VisualMapEntity::getName);
        List<VisualMapListVO> list = JsonUtil.createJsonToList(data, VisualMapListVO.class);
        VisualPageVO<VisualMapListVO> paginationVO = BeanUtil.toBean(pagination, VisualPageVO.class);
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
    public ServiceResult<VisualMapInfoVO> info(@RequestParam("id") String id) {
        VisualMapEntity entity = mapService.getInfo(id);
        VisualMapInfoVO vo = BeanUtil.toBean(entity, VisualMapInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 新增
     *
     * @param mapCrForm 地图模型
     * @return
     */
    @Operation(summary = "新增")
    @PostMapping("/save")
    @Parameters({
            @Parameter(name = "mapCrForm", description = "地图模型", required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult create(@RequestBody VisualMapCrForm mapCrForm) {
        VisualMapEntity entity = BeanUtil.toBean(mapCrForm, VisualMapEntity.class);
        mapService.create(entity);
        return ServiceResult.success("新建成功");
    }

    /**
     * 修改
     *
     * @param mapUpForm 地图模型
     * @return
     */
    @Operation(summary = "修改")
    @PostMapping("/update")
    @Parameters({
            @Parameter(name = "mapUpForm", description = "地图模型", required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult update(@RequestBody VisualMapUpForm mapUpForm) {
        VisualMapEntity entity = BeanUtil.toBean(mapUpForm, VisualMapEntity.class);
        boolean flag = mapService.update(mapUpForm.getId(), entity);
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
    public ServiceResult delete(@RequestParam("ids") String ids) {
        VisualMapEntity entity = mapService.getInfo(ids);
        if (entity != null) {
            mapService.delete(entity);
            return ServiceResult.success("删除成功");
        }
        return ServiceResult.error("删除失败，数据不存在");
    }

    /**
     * 数据详情
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "数据详情")
    @GetMapping("/data")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public Map<String, Object> dataInfo(@RequestParam("id") String id) {
        VisualMapEntity entity = mapService.getInfo(id);
        Map<String, Object> data = JsonUtil.stringToMap(entity.getData());
        return data;
    }

}
