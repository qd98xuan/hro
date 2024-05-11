package com.linzen.visualdata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.util.RandomUtil;
import com.linzen.visualdata.entity.VisualConfigEntity;
import com.linzen.visualdata.mapper.VisualConfigMapper;
import com.linzen.visualdata.service.VisualConfigService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 大屏基本配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class VisualConfigServiceImpl extends SuperServiceImpl<VisualConfigMapper, VisualConfigEntity> implements VisualConfigService {

    @Override
    public List<VisualConfigEntity> getList() {
        QueryWrapper<VisualConfigEntity> queryWrapper = new QueryWrapper<>();
        return this.list(queryWrapper);
    }

    @Override
    public VisualConfigEntity getInfo(String id) {
        QueryWrapper<VisualConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualConfigEntity::getVisualId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(VisualConfigEntity entity) {
        entity.setId(RandomUtil.uuId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, VisualConfigEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(VisualConfigEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }
}
