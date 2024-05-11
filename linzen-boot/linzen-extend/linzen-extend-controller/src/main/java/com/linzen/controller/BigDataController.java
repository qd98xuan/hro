package com.linzen.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.Pagination;
import com.linzen.base.controller.SuperController;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.entity.BigDataEntity;
import com.linzen.exception.WorkFlowException;
import com.linzen.model.bidata.BigBigDataListVO;
import com.linzen.service.BigDataService;
import com.linzen.util.JsonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 大数据测试
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "大数据测试", description = "BigData")
@RestController
@RequestMapping("/api/extend/BigData")
public class BigDataController extends SuperController<BigDataService, BigDataEntity> {

    @Autowired
    private BigDataService bigDataService;

    /**
     * 列表
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "列表")
    @GetMapping
    @SaCheckPermission("extend.bigData")
    public ServiceResult<PageListVO<BigBigDataListVO>> list(Pagination pagination) {
        List<BigDataEntity> data = bigDataService.getList(pagination);
        List<BigBigDataListVO> list= JsonUtil.createJsonToList(data, BigBigDataListVO.class);
        PaginationVO paginationVO  = BeanUtil.toBean(pagination,PaginationVO.class);
        return ServiceResult.pageList(list,paginationVO);
    }

    /**
     * 新建
     *
     * @return
     */
    @Operation(summary = "添加大数据测试")
    @PostMapping
    @SaCheckPermission("extend.bigData")
    public ServiceResult create() throws WorkFlowException {
        bigDataService.create(10000);
        return ServiceResult.success("新建成功10000条数据");
    }
}
