package com.bstek.ureport.console.ureport.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bstek.ureport.console.ureport.entity.ReportEntity;
import com.bstek.ureport.console.ureport.mapper.ReportMapper;
import com.bstek.ureport.console.ureport.model.PaginationReport;
import com.bstek.ureport.console.ureport.service.ReportService;
import com.bstek.ureport.utils.DateUtil;
import com.bstek.ureport.console.util.RandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, ReportEntity> implements ReportService {

    @Override
    public List<ReportEntity> GetList(PaginationReport paginationReport) {
        QueryWrapper<ReportEntity> queryWrapper = new QueryWrapper<>();
        //支持encode和fullName
        if (Strings.isNotEmpty(paginationReport.getKeyword())){
            queryWrapper.lambda().and(t->t.like(ReportEntity::getFullName,paginationReport.getKeyword())
                    .or().like(ReportEntity::getEnCode,paginationReport.getKeyword())
            );
        }
        if (Strings.isNotEmpty(paginationReport.getCategory())){
            queryWrapper.lambda().eq(ReportEntity::getCategoryId, paginationReport.getCategory());
        }
        if (paginationReport.getDelFlag()!=null){
            queryWrapper.lambda().eq(ReportEntity::getDelFlag, paginationReport.getDelFlag());
        }
        queryWrapper.lambda().orderByAsc(ReportEntity::getSortCode).orderByDesc(ReportEntity::getCreatorTime);
        Page<ReportEntity> page = new Page<>(paginationReport.getCurrentPage(), paginationReport.getPageSize());
        IPage<ReportEntity> userPage = this.page(page, queryWrapper);
        return paginationReport.setData(userPage.getRecords(), userPage.getTotal());
    }

    @Override
    public List<ReportEntity> GetList() {
        QueryWrapper<ReportEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(ReportEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<ReportEntity> Selector(String categoryId) {
        QueryWrapper<ReportEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ReportEntity::getCategoryId, categoryId);
        List<ReportEntity> list = this.list(queryWrapper);
        return list;
    }

    @Override
    public ReportEntity GetInfo(String id) {
        QueryWrapper<ReportEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ReportEntity::getId,id);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean IsExistByFullName(String fullName,String id) {
        QueryWrapper<ReportEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ReportEntity::getFullName, fullName);
        if (!StringUtils.isEmpty(id)) {
            queryWrapper.lambda().ne(ReportEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean Delete(ReportEntity entity) {
        return this.removeById(entity.getId());
    }

    @Override
    public boolean Copy(ReportEntity entity) {
        String copyNum = UUID.randomUUID().toString().substring(0, 5);
        entity.setId(null);
        entity.setUpdateTime(null);
        entity.setUpdateUser(null);
        entity.setFullName(entity.getFullName() + ".副本" + copyNum);
        entity.setEnCode(entity.getEnCode() + copyNum);
        entity.setDelFlag(0);
        return Create(entity);
    }

    @Override
    public boolean Create(ReportEntity entity) {
        if (entity.getId() == null || "".equals(entity.getId())){
            entity.setId(RandomUtil.uuId());
            entity.setCreatorTime(DateUtil.getNowDate());
        }
        return this.save(entity);
    }

    @Override
    public boolean Update(String id, ReportEntity entity) {
        entity.setId(id);
        entity.setUpdateTime(DateUtil.getNowDate());
        return this.updateById(entity);
    }

}
