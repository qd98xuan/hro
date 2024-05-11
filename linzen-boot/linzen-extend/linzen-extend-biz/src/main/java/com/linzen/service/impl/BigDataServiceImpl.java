package com.linzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.Pagination;
import com.linzen.base.entity.SuperBaseEntity;
import com.linzen.base.service.DbLinkService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.config.ConfigValueUtil;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.util.ConnUtil;
import com.linzen.database.util.DataSourceUtil;
import com.linzen.database.util.DbTypeUtil;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.entity.BigDataEntity;
import com.linzen.exception.WorkFlowException;
import com.linzen.mapper.BigDataMapper;
import com.linzen.service.BigDataService;
import com.linzen.util.DateUtil;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 大数据测试
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Service
public class BigDataServiceImpl extends SuperServiceImpl<BigDataMapper, BigDataEntity> implements BigDataService {

    @Autowired
    private DataSourceUtil dataSourceUtils;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private DbLinkService dbLinkService;


    @Override
    public List<BigDataEntity> getList(Pagination pagination) {
        QueryWrapper<BigDataEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(pagination.getKeyword())) {
            queryWrapper.lambda().and(
                    t -> t.like(BigDataEntity::getFullName, pagination.getKeyword())
                            .or().like(BigDataEntity::getEnCode, pagination.getKeyword())
            );
        }
        //排序
        queryWrapper.lambda().orderByDesc(SuperBaseEntity.SuperCBaseEntity::getCreatorTime, SuperBaseEntity.SuperIBaseEntity::getId);
        Page<BigDataEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<BigDataEntity> iPage = this.page(page, queryWrapper);
        return pagination.setData(iPage.getRecords(), page.getTotal());
    }

    @Override
    public void create(int insertCount) throws WorkFlowException {
        Integer code = this.baseMapper.maxCode();
        if (code == null) {
            code = 0;
        }
        int index = code == 0 ? 10000001 : code;
        if (index > 11500001) {
            throw new WorkFlowException("防止恶意创建过多数据");
        }
        try {
            @Cleanup Connection conn = ConnUtil.getConnOrDefault(dataSourceUtils);
            @Cleanup PreparedStatement pstm = null;
            String sql = "";
            String tenantColumn = TenantDataSourceUtil.getTenantColumn();
            DbLinkEntity dbLinkEntity = dbLinkService.getResource("0");
            if (DbTypeUtil.checkOracle(dbLinkEntity)||DbTypeUtil.checkDM(dbLinkEntity)) {
                sql = "INSERT INTO ext_big_data(F_ID,F_EN_CODE,F_FULL_NAME,F_CREATOR_TIME{column})  VALUES (?,?,?,to_date(?,'yyyy-mm-dd hh24:mi:ss'){value})";
            } else {
                sql = "INSERT INTO ext_big_data(F_ID,F_EN_CODE,F_FULL_NAME,F_CREATOR_TIME{column})  VALUES (?,?,?,?{value})";
            }
            if (StringUtil.isNotEmpty(tenantColumn)) {
                sql = sql.replaceAll("\\{column}", "," + configValueUtil.getMultiTenantColumn());
                sql = sql.replaceAll("\\{value}", ",?");
            } else {
                sql = sql.replaceAll("\\{column}", "");
                sql = sql.replaceAll("\\{value}", "");
            }
            pstm = conn.prepareStatement(sql);
            conn.setAutoCommit(false);
            if (DbTypeUtil.checkPostgre(dbLinkEntity)) {
                for (int i = 0; i < insertCount; i++) {
                    pstm.setString(1, RandomUtil.uuId());
                    pstm.setInt(2, index);
                    pstm.setString(3, "测试大数据" + index);
                    pstm.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                    if (StringUtil.isNotEmpty(tenantColumn)) {
                        pstm.setString(5, tenantColumn);
                    }
                    pstm.addBatch();
                    index++;
                }
            } else {
                Date date=new Date();
                SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time=sf.format(date);
                for (int i = 0; i < insertCount; i++) {
                    pstm.setString(1, RandomUtil.uuId());
                    pstm.setInt(2, index);
                    pstm.setString(3, "测试大数据" + index);
//                    pstm.setString(4, time);
                    pstm.setString(4, DateUtil.daFormatHHMMSSAddEight(System.currentTimeMillis()));
                    if (StringUtil.isNotEmpty(tenantColumn)) {
                        pstm.setString(5, tenantColumn);
                    }
                    pstm.addBatch();
                    index++;
                }
            }
            pstm.executeBatch();
            conn.commit();
            pstm.close();
            conn.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
