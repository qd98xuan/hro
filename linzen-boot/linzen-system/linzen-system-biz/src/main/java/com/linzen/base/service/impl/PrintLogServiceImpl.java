package com.linzen.base.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.linzen.base.PaginationTime;
import com.linzen.base.entity.PrintLogEntity;
import com.linzen.base.mapper.PrintLogMapper;
import com.linzen.base.model.vo.PrintLogVO;
import com.linzen.base.service.PrintLogService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.permission.entity.SysUserEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PrintLogServiceImpl extends SuperServiceImpl<PrintLogMapper, PrintLogEntity> implements PrintLogService {

    @Override
    public List<PrintLogVO> list(String printId, PaginationTime paginationTime) {
        MPJLambdaWrapper<PrintLogEntity> wrapper = new MPJLambdaWrapper<>(PrintLogEntity.class)
                .leftJoin(SysUserEntity.class, SysUserEntity::getId, PrintLogEntity::getCreatorUserId)
                .selectAll(PrintLogEntity.class)
                .select(SysUserEntity::getAccount, SysUserEntity::getRealName)
                .selectAs(PrintLogEntity::getCreatorTime, PrintLogVO::getCreatorTime);
        if (!ObjectUtil.isEmpty(paginationTime.getStartTime()) && !ObjectUtil.isEmpty(paginationTime.getEndTime())) {
            wrapper.between(PrintLogEntity::getCreatorTime, new Date(paginationTime.getStartTime()), new Date(paginationTime.getEndTime()));
        }
        if (!ObjectUtil.isEmpty(printId)) {
            wrapper.eq(PrintLogEntity::getPrintId, printId);
        }
        if (!ObjectUtil.isEmpty(paginationTime.getKeyword())) {
            wrapper.and(
                    t -> t.like(SysUserEntity::getRealName, paginationTime.getKeyword())
                            .or().like(SysUserEntity::getAccount, paginationTime.getKeyword())
                            .or().like(PrintLogEntity::getPrintTitle, paginationTime.getKeyword())
            );
        }
        Page<PrintLogVO> page = new Page<>(paginationTime.getCurrentPage(), paginationTime.getPageSize());
        IPage<PrintLogVO> userPage = this.selectJoinListPage(page, PrintLogVO.class, wrapper);
        return paginationTime.setData(userPage.getRecords(), page.getTotal());
    }
}