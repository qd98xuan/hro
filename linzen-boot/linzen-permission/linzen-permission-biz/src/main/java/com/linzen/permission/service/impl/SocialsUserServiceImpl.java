package com.linzen.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.permission.entity.SocialsUserEntity;
import com.linzen.permission.mapper.SocialsUserMapper;
import com.linzen.permission.service.SocialsUserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 流程设计
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class SocialsUserServiceImpl extends SuperServiceImpl<SocialsUserMapper, SocialsUserEntity> implements SocialsUserService {
    @Override
    public List<SocialsUserEntity> getListByUserId(String userId) {
        QueryWrapper<SocialsUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SocialsUserEntity::getUserId,userId);
        return this.list(queryWrapper);
    }

    @Override
    public List<SocialsUserEntity> getUserIfnoBySocialIdAndType(String socialId, String socialType) {
        QueryWrapper<SocialsUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SocialsUserEntity::getSocialId,socialId);
        queryWrapper.lambda().eq(SocialsUserEntity::getSocialType,socialType);
        return this.list(queryWrapper);
    }

    @Override
    public List<SocialsUserEntity> getListByUserIdAndSource(String userId, String socialType) {
        QueryWrapper<SocialsUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SocialsUserEntity::getUserId,userId);
        queryWrapper.lambda().eq(SocialsUserEntity::getSocialType,socialType);
        return this.list(queryWrapper);
    }

    @Override
    public SocialsUserEntity getInfoBySocialId(String socialId,String socialType){
        QueryWrapper<SocialsUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SocialsUserEntity::getSocialId,socialId);
        queryWrapper.lambda().eq(SocialsUserEntity::getSocialType,socialType);
        return this.getOne(queryWrapper);
    }

}
