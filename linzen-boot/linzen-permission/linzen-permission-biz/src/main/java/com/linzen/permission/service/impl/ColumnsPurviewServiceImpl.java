package com.linzen.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.permission.entity.SysColumnsPurviewEntity;
import com.linzen.permission.mapper.ColumnsPurviewMapper;
import com.linzen.permission.service.ColumnsPurviewService;
import com.linzen.util.DateUtil;
import com.linzen.util.RandomUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 模块列表权限业务实现类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class ColumnsPurviewServiceImpl extends SuperServiceImpl<ColumnsPurviewMapper, SysColumnsPurviewEntity> implements ColumnsPurviewService {
    @Autowired
    private UserProvider userProvider;

    @Override
    public SysColumnsPurviewEntity getInfo(String moduleId) {
        QueryWrapper<SysColumnsPurviewEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysColumnsPurviewEntity::getModuleId, moduleId);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean update(String moduleId, SysColumnsPurviewEntity entity) {
        SysColumnsPurviewEntity entitys = getInfo(moduleId);
        // id不存在则是保存
        if (entitys == null) {
            entity.setId(RandomUtil.uuId());
            entity.setCreatorUserId(userProvider.get().getUserId());
            return this.save(entity);
        } else {
            // 修改
            entity.setId(entitys.getId());
            entity.setUpdateUserId(userProvider.get().getUserId());
            entity.setUpdateTime(DateUtil.getNowDate());
        }
        return this.saveOrUpdate(entity);
    }

}
