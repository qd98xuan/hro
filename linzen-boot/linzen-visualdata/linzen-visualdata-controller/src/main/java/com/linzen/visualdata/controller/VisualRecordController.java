package com.linzen.visualdata.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.controller.SuperController;
import com.linzen.util.JsonUtil;
import com.linzen.visualdata.entity.VisualRecordEntity;
import com.linzen.visualdata.model.VisualPageVO;
import com.linzen.visualdata.model.VisualPagination;
import com.linzen.visualdata.model.visualrecord.VisualRecordCrForm;
import com.linzen.visualdata.model.visualrecord.VisualRecordInfoVO;
import com.linzen.visualdata.model.visualrecord.VisualRecordListVO;
import com.linzen.visualdata.model.visualrecord.VisualRecordUpForm;
import com.linzen.visualdata.service.VisualRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 大屏数据源配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@RestController
@Tag(name = "大屏数据集配置", description = "record")
@RequestMapping("/api/blade-visual/record")
public class VisualRecordController extends SuperController<VisualRecordService, VisualRecordEntity> {

    @Autowired
    private VisualRecordService recordService;

    /**
     * 分页
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "分页")
    @GetMapping("/list")
    public ServiceResult<VisualPageVO<VisualRecordListVO>> list(VisualPagination pagination) {
        List<VisualRecordEntity> data = recordService.getList(pagination);
        List<VisualRecordListVO> list = JsonUtil.createJsonToList(data, VisualRecordListVO.class);
        VisualPageVO<VisualRecordListVO> paginationVO = BeanUtil.toBean(pagination, VisualPageVO.class);
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
    public ServiceResult<VisualRecordInfoVO> info(@RequestParam("id") String id) {
        VisualRecordEntity entity = recordService.getInfo(id);
        VisualRecordInfoVO vo = BeanUtil.toBean(entity, VisualRecordInfoVO.class);
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
            @Parameter(name = "recordCrForm", description = "数据模型", required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult create(@RequestBody VisualRecordCrForm recordCrForm) {
        VisualRecordEntity entity = BeanUtil.toBean(recordCrForm, VisualRecordEntity.class);
        recordService.create(entity);
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
            @Parameter(name = "recordUpForm", description = "数据模型", required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult update(@RequestBody VisualRecordUpForm recordUpForm) {
        VisualRecordEntity entity = BeanUtil.toBean(recordUpForm, VisualRecordEntity.class);
        recordService.update(entity.getId(), entity);
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
        VisualRecordEntity entity = recordService.getInfo(ids);
        if (entity != null) {
            recordService.delete(entity);
            return ServiceResult.success("删除成功");
        }
        return ServiceResult.error("删除失败，数据不存在");
    }


}
