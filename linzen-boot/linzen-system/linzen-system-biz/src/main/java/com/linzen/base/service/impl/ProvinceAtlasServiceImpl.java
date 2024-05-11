package com.linzen.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.entity.ProvinceAtlasEntity;
import com.linzen.base.mapper.ProvinceAtlasMapper;
import com.linzen.base.service.ProvinceAtlasService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 行政区划
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class ProvinceAtlasServiceImpl extends SuperServiceImpl<ProvinceAtlasMapper, ProvinceAtlasEntity> implements ProvinceAtlasService {

    @Override
    public List<ProvinceAtlasEntity> getList() {
        QueryWrapper<ProvinceAtlasEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProvinceAtlasEntity::getEnabledMark, 1);
        return  this.list(queryWrapper);
    }

    @Override
    public List<ProvinceAtlasEntity> getListByPid(String pid) {
        QueryWrapper<ProvinceAtlasEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(pid)) {
            queryWrapper.lambda().eq(ProvinceAtlasEntity::getParentId, pid);
        }else{
            queryWrapper.lambda().eq(ProvinceAtlasEntity::getParentId, "-1");
        }
        queryWrapper.lambda().eq(ProvinceAtlasEntity::getEnabledMark, 1);
        return  this.list(queryWrapper);
    }

    @Override
    public ProvinceAtlasEntity findOneByCode(String code) {
        QueryWrapper<ProvinceAtlasEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProvinceAtlasEntity::getEnCode, code);
        queryWrapper.lambda().eq(ProvinceAtlasEntity::getEnabledMark, 1);
        return  this.getOne(queryWrapper);
    }
}
