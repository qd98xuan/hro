package com.linzen.controller;

import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.controller.SuperController;
import com.linzen.base.vo.ListVO;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.constant.MsgCode;
import com.linzen.engine.model.flowengine.FlowPagination;
import com.linzen.entity.AppDataEntity;
import com.linzen.model.AppDataCrForm;
import com.linzen.model.AppDataListAllVO;
import com.linzen.model.AppDataListVO;
import com.linzen.model.AppFlowListAllVO;
import com.linzen.service.AppDataService;
import com.linzen.util.JsonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * app常用数据
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "app常用数据", description = "data")
@RestController
@RequestMapping("/api/app/Data")
public class AppDataController extends SuperController<AppDataService, AppDataEntity> {

    @Autowired
    private AppDataService appDataService;

    /**
     * 常用数据
     *
     * @param type 类型
     * @return
     */
    @Operation(summary = "常用数据")
    @GetMapping
    @Parameters({
            @Parameter(name = "type", description = "类型"),
    })
    public ServiceResult<ListVO<AppDataListVO>> list(@RequestParam("type") String type) {
        List<AppDataEntity> list = appDataService.getList(type);
        List<AppDataListVO> data = JsonUtil.createJsonToList(list, AppDataListVO.class);
        ListVO listVO = new ListVO();
        listVO.setList(data);
        return ServiceResult.success(listVO);
    }

    /**
     * 新建
     *
     * @param appDataCrForm 新建模型
     * @return
     */
    @PostMapping
    @Operation(summary = "新建")
    @Parameters({
            @Parameter(name = "appDataCrForm", description = "常用模型",required = true),
    })
    public ServiceResult create(@RequestBody @Valid AppDataCrForm appDataCrForm) {
        AppDataEntity entity = BeanUtil.toBean(appDataCrForm, AppDataEntity.class);
        if (appDataService.isExistByObjectId(entity.getObjectId(),appDataCrForm.getSystemId())) {
            return ServiceResult.error("常用数据已存在");
        }
        appDataService.create(entity);
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 删除
     *
     * @param objectId 主键
     * @return
     */
    @Operation(summary = "删除")
    @DeleteMapping("/{objectId}")
    @Parameters({
            @Parameter(name = "objectId", description = "主键", required = true),
    })
    public ServiceResult create(@PathVariable("objectId") String objectId) {
        AppDataEntity entity = appDataService.getInfo(objectId);
        if (entity != null) {
            appDataService.delete(entity);
            return ServiceResult.success(MsgCode.SU003.get());
        }
        return ServiceResult.error(MsgCode.FA003.get());
    }

    /**
     * 所有流程
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "所有流程")
    @GetMapping("/getFlowList")
    public ServiceResult<PageListVO<AppFlowListAllVO>> getFlowList(FlowPagination pagination) {
        List<AppFlowListAllVO> list = appDataService.getFlowList(pagination);
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(list, paginationVO);
    }

    /**
     * 所有应用
     *
     * @return
     */
    @Operation(summary = "所有应用")
    @GetMapping("/getDataList")
    public ServiceResult<ListVO<AppDataListAllVO>> getAllList() {
        List<AppDataListAllVO> result = appDataService.getDataList("2");
        ListVO listVO = new ListVO();
        listVO.setList(result);
        return ServiceResult.success(listVO);
    }
}
