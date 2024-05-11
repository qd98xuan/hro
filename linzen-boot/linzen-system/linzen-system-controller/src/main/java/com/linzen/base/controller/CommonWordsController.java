package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.CommonWordsEntity;
import com.linzen.base.entity.SysSystemEntity;
import com.linzen.base.model.commonword.ComWordsPagination;
import com.linzen.base.model.commonword.CommonWordsForm;
import com.linzen.base.model.commonword.CommonWordsVO;
import com.linzen.base.service.CommonWordsService;
import com.linzen.base.service.SystemService;
import com.linzen.base.vo.ListVO;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.constant.MsgCode;
import com.linzen.util.JsonUtil;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 常用语控制类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息
 * @date 2023-04-01
 */
@Tag(name = "审批常用语", description = "commonWords")
@RestController
@RequestMapping("/api/system/CommonWords")
public class CommonWordsController extends SuperController<CommonWordsService, CommonWordsEntity> {

    @Autowired
    private CommonWordsService commonWordsService;
    @Autowired
    private SystemService systemService;


    /**
     * 列表
     *
     * @param comWordsPagination 页面参数对象
     * @return 列表结果集
     */
    @Operation(summary = "当前系统应用列表")
    @GetMapping()
    public ServiceResult<PageListVO<CommonWordsVO>> getList(ComWordsPagination comWordsPagination) {
        List<CommonWordsEntity> entityList = commonWordsService.getSysList(comWordsPagination, false);
        List<CommonWordsVO> voList = JsonUtil.createJsonToList(entityList, CommonWordsVO.class);
        formatSystemNames(voList);
        return ServiceResult.pageList(voList, BeanUtil.toBean(comWordsPagination, PaginationVO.class));
    }

    /**
     * 获取信息
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "获取信息")
    @Parameter(name = "id", description = "主键", required = true)
    @SaCheckPermission(value = {"commonWords", "workFlow.flowTodo"}, mode = SaMode.OR)
    @GetMapping("/{id}")
    public ServiceResult<CommonWordsVO> getInfo(@PathVariable String id) {
        CommonWordsEntity entity = commonWordsService.getById(id);
        if(StringUtil.isNotEmpty(entity.getSystemIds())){
            String[] sysIds = entity.getSystemIds().split(",");
            List<String> ids = new ArrayList<>();
            for(String sysId : sysIds){
                if(!StringUtil.isEmpty(sysId)) {
                    SysSystemEntity systemEntity = systemService.getInfo(sysId);
                    if (systemEntity != null && systemEntity.getEnabledMark() == 1) {
                        ids.add(sysId);
                    }
                }
            }
            if(ids.size() > 0){
                entity.setSystemIds(StringUtil.join(ids, ","));
            }else {
                entity.setSystemIds(null);
            }
        }
        CommonWordsVO vo = BeanUtil.toBean(entity, CommonWordsVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 下拉列表
     *
     * @return
     */
    @Operation(summary = "下拉列表")
    @SaCheckPermission(value = {"commonWords", "workFlow.flowTodo"}, mode = SaMode.OR)
    @GetMapping("/Selector")
    public ServiceResult<ListVO<CommonWordsVO>> getSelect(String type) {
        List<CommonWordsVO> voList = JsonUtil.createJsonToList(commonWordsService.getListModel(type), CommonWordsVO.class);
        formatSystemNames(voList);
        return ServiceResult.success(new ListVO<>(voList));
    }

    /**
     * 新建
     *
     * @param commonWordsForm 实体模型
     * @return
     */
    @Operation(summary = "新建")
    @SaCheckPermission(value = {"commonWords", "workFlow.flowTodo"}, mode = SaMode.OR)
    @Parameter(name = "commonWordsForm", description = "实体模型", required = true)
    @PostMapping("")
    public ServiceResult<CommonWordsForm> create(@RequestBody CommonWordsForm commonWordsForm) {
        CommonWordsEntity entity = BeanUtil.toBean(commonWordsForm, CommonWordsEntity.class);
        entity.setId(RandomUtil.uuId());
        commonWordsService.save(entity);
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 修改
     * @param commonWordsForm 实体模型
     * @return
     */
    @Operation(summary = "修改")
    @SaCheckPermission(value = {"commonWords", "workFlow.flowTodo"}, mode = SaMode.OR)
    @Parameters({
            @Parameter(name = "commonWordsForm", description = "实体模型", required = true)
    })
    @PutMapping("/{id}")
    public ServiceResult<CommonWordsForm> update(@RequestBody CommonWordsForm commonWordsForm) {
        CommonWordsEntity entity = BeanUtil.toBean(commonWordsForm, CommonWordsEntity.class);
        entity.setId(commonWordsForm.getId());
        commonWordsService.updateById(entity);
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "删除")
    @SaCheckPermission("commonWords")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @DeleteMapping("/{id}")
    public ServiceResult<CommonWordsForm> delete(@PathVariable String id) {
        //对象存在判断
        if (commonWordsService.getById(id) != null) {
            commonWordsService.removeById(id);
            return ServiceResult.success(MsgCode.SU003.get());
        } else {
            return ServiceResult.error(MsgCode.FA003.get());
        }
    }

    private void formatSystemNames(List<CommonWordsVO> voList){
        voList.forEach(vo->{
            if(StringUtil.isNotEmpty(vo.getSystemIds())){
                List<String> sysNameList = systemService.getListByIds(vo.getSystemIds(), null).stream()
                        .map(SysSystemEntity::getFullName).collect(Collectors.toList());
                vo.setSystemNames(StringUtil.join(sysNameList, ","));
            }
        });
    }

}
