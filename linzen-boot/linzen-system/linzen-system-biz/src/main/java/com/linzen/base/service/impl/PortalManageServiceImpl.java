package com.linzen.base.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.linzen.base.entity.PortalManageEntity;
import com.linzen.base.mapper.PortalManageMapper;
import com.linzen.base.model.portalManage.PortalManagePage;
import com.linzen.base.model.portalManage.PortalManagePageDO;
import com.linzen.base.model.portalManage.PortalManagePrimary;
import com.linzen.base.model.portalManage.PortalManageVO;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.service.PortalManageService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * <p>
 * 门户管理 服务实现类
 * </p>
 *
 * @author FHNP
 * @since 2023-02-16
 */
@Service
public class PortalManageServiceImpl extends SuperServiceImpl<PortalManageMapper, PortalManageEntity> implements PortalManageService {

    @Autowired
    private UserService userService;
    @Autowired
    private PortalManageMapper portalManageMapper;
    @Autowired
    private DictionaryDataService dictionaryDataService;

    @Override
    public void checkCreUp(PortalManageEntity creUpEntity) throws Exception{
        // 当ID为空时，为添加方法
        PortalManagePrimary primary = new PortalManagePrimary(
                creUpEntity.getPlatform(),
                creUpEntity.getPortalId(),
                creUpEntity.getSystemId());
        if(creUpEntity.getId() == null){
            if(count(primary.getQuery()) > 0) throw new Exception("此系统与平台下门户已存在");
        }
//        if(creUpEntity.getHomePageMark() == 1) {
//            primary.getQuery().lambda().eq(PortalManageEntity::getEnabledMark, 1);
//            PortalManageEntity one = getOne(primary.getQuery());
//            if(one != null && !one.getId().equals(creUpEntity.getId())){
//                throw  new Exception("已存在默认首页，不允许被保存");
//            }
//        }
    }

    @Override
    public PortalManageVO convertVO(PortalManageEntity entity){
        PortalManageVO vo = new PortalManageVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setId(entity.getId()); // 父类id声明泛型，会拷贝失败
        // 创建人
        SysUserEntity creatorUser = userService.getInfo(entity.getCreatorUserId());
        vo.setCreatorUser(creatorUser != null ? creatorUser.getRealName() + "/" + creatorUser.getAccount() : entity.getCreatorUserId());
        // 修改人
        SysUserEntity updateUser = userService.getInfo(entity.getCreatorUserId());
        vo.setUpdateUser(updateUser != null ? updateUser.getRealName() + "/" + updateUser.getAccount() : "");
        // 门户名称
        try{
            vo.setFullName(portalManageMapper.getPortalFullName(entity.getPortalId()));
            String categoryId = portalManageMapper.getPortalCategoryId(entity.getPortalId());
            String categoryName = dictionaryDataService.getInfo(portalManageMapper.getPortalCategoryId(entity.getPortalId())).getFullName();
            vo.setCategoryId(categoryId);
            vo.setCategoryName(categoryName);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        vo.setPlatform(entity.getPlatform());
        vo.setSystemId(entity.getSystemId());
        return vo;
    }

    @Override
    public List<PortalManageVO> getList(PortalManagePrimary primary) {
        return list(primary.getQuery()).stream().map(this::convertVO).collect(Collectors.toList());
    }

    @Override
    public List<PortalManageVO> getListByEnable(PortalManagePrimary primary) {
        primary.getQuery().lambda().eq(PortalManageEntity::getEnabledMark, 1);
        return getList(primary);
    }

    @Override
    public PageDTO<PortalManagePageDO> getPage(PortalManagePage pmPage) {
        String keyword = pmPage.getKeyword();
        if (StringUtil.isNotEmpty(keyword)) {
            pmPage.setKeyword("%" + keyword + "%");
        }
        PageDTO<PortalManagePageDO> pageDto = portalManageMapper.selectPortalManageDoPage(pmPage.getPageDto(), pmPage);
        pmPage.setTotal(pageDto.getTotal());
        return pageDto;
    }

    @Override
    public List<PortalManagePageDO> getSelectList(PortalManagePage pmPage) {
        List<PortalManagePageDO> list = portalManageMapper.selectPortalManageDoList(pmPage);
        return list;
    }

    @Override
    public List<PortalManagePageDO> selectPortalBySystemIds(List<String> systemIds, List<String> collect) {
        if (systemIds.size() == 0) {
            systemIds.add("");
        }
        return portalManageMapper.selectPortalBySystemIds(systemIds, collect);
    }

    @Override
    public void createBatch(List<PortalManagePrimary> primaryLit) throws Exception {
        List<PortalManageEntity> list = new ArrayList<>();
        for (PortalManagePrimary primary : primaryLit) {
            if(list(primary.getQuery()).size() < 1){
                list.add(primary.getEntity());
            }
        }
        saveBatch(list);
    }

}
