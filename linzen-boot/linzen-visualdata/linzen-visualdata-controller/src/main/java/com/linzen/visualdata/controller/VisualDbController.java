package com.linzen.visualdata.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.controller.SuperController;
import com.linzen.constant.MsgCode;
import com.linzen.util.JsonUtil;
import com.linzen.util.StringUtil;
import com.linzen.visualdata.entity.VisualDbEntity;
import com.linzen.visualdata.model.VisualPageVO;
import com.linzen.visualdata.model.VisualPagination;
import com.linzen.visualdata.model.visualdb.*;
import com.linzen.visualdata.service.VisualDbService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 大屏数据源配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@RestController
@Tag(name = "大屏数据源配置", description = "db")
@RequestMapping("/api/blade-visual/db")
public class VisualDbController extends SuperController<VisualDbService, VisualDbEntity> {

    @Autowired
    private VisualDbService dbService;

    /**
     * 分页
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "分页")
    @GetMapping("/list")
    public ServiceResult<VisualPageVO<VisualDbListVO>> list(VisualPagination pagination) {
        List<VisualDbEntity> data = dbService.getList(pagination);
        List<VisualDbListVO> list = JsonUtil.createJsonToList(data, VisualDbListVO.class);
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
    public ServiceResult<VisualDbInfoVO> info(@RequestParam("id")String id) {
        VisualDbEntity entity = dbService.getInfo(id);
        VisualDbInfoVO vo = BeanUtil.toBean(entity, VisualDbInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 新增或修改
     *
     * @param dbUpForm 数据模型
     * @return
     */
    @Operation(summary = "新增或修改")
    @PostMapping("/submit")
    @Parameters({
            @Parameter(name = "dbUpForm", description = "数据模型",required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult submit(@RequestBody VisualDbUpForm dbUpForm) {
        VisualDbEntity entity = BeanUtil.toBean(dbUpForm, VisualDbEntity.class);
        if (StringUtil.isEmpty(entity.getId())) {
            dbService.create(entity);
            return ServiceResult.success("新建成功");
        } else {
            dbService.update(entity.getId(), entity);
            return ServiceResult.success("更新成功");
        }
    }

    /**
     * 新增
     *
     * @param dbCrForm 数据模型
     * @return
     */
    @Operation(summary = "新增")
    @PostMapping("/save")
    @Parameters({
            @Parameter(name = "dbCrForm", description = "数据模型",required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult create(@RequestBody VisualDbCrForm dbCrForm) {
        VisualDbEntity entity = BeanUtil.toBean(dbCrForm, VisualDbEntity.class);
        dbService.create(entity);
        return ServiceResult.success("新建成功");
    }

    /**
     * 修改
     *
     * @param dbUpForm 数据模型
     * @return
     */
    @Operation(summary = "修改")
    @PostMapping("/update")
    @Parameters({
            @Parameter(name = "dbUpForm", description = "数据模型",required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult update(@RequestBody VisualDbUpForm dbUpForm) {
        VisualDbEntity entity = BeanUtil.toBean(dbUpForm, VisualDbEntity.class);
        dbService.update(entity.getId(), entity);
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
        VisualDbEntity entity = dbService.getInfo(ids);
        if (entity != null) {
            dbService.delete(entity);
            return ServiceResult.success("删除成功");
        }
        return ServiceResult.error("删除失败，数据不存在");
    }

    /**
     * 下拉数据源
     *
     * @return
     */
    @Operation(summary = "下拉数据源")
    @GetMapping("/db-list")
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult<List<VisualDbSelectVO>> list() {
        List<VisualDbEntity> data = dbService.getList();
        List<VisualDbSelectVO> list = JsonUtil.createJsonToList(data, VisualDbSelectVO.class);
        return ServiceResult.success(list);
    }

    /**
     * 数据源测试连接
     *
     * @param dbCrForm 数据源模型
     * @return
     */
    @Operation(summary = "数据源测试连接")
    @PostMapping("/db-test")
    @Parameters({
            @Parameter(name = "dbCrForm", description = "数据源模型",required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult test(@RequestBody VisualDbCrForm dbCrForm) {
        VisualDbEntity entity = BeanUtil.toBean(dbCrForm, VisualDbEntity.class);
        boolean flag = dbService.dbTest(entity);
        if (flag) {
            return ServiceResult.success(MsgCode.DB301.get());
        }
        return ServiceResult.error(MsgCode.DB302.get());
    }

    /**
     * 动态执行SQL
     *
     * @param queryForm 数据模型
     * @return
     */
    @Operation(summary = "动态执行SQL")
    @PostMapping("/dynamic-query")
    @Parameters({
            @Parameter(name = "queryForm", description = "数据模型",required = true),
    })
    public ServiceResult query(@RequestBody VisualDbQueryForm queryForm) {
        VisualDbEntity entity = dbService.getInfo(queryForm.getId());
        List<Map<String, Object>> data = new ArrayList<>();
        if (entity != null) {
            data = dbService.query(entity, queryForm.getSql());
        }
        return ServiceResult.success(data);
    }

}
