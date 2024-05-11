package com.linzen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import com.linzen.base.UserInfo;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.model.AppPositionVO;
import com.linzen.model.AppUserInfoVO;
import com.linzen.model.AppUsersVO;
import com.linzen.permission.entity.*;
import com.linzen.permission.service.*;
import com.linzen.service.AppService;
import com.linzen.util.RedisUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UploaderUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * app用户信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class AppServiceImpl implements AppService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private DictionaryDataService dictionaryDataService;

    @Override
    public AppUsersVO userInfo() {
        UserInfo userInfo = userProvider.get();
        SysUserEntity userEntity = userService.getInfo(userInfo.getUserId());
        AppUsersVO usersVO = new AppUsersVO();
        usersVO.setBirthday(userEntity.getBirthday() != null ? userEntity.getBirthday().getTime() : null);
        usersVO.setEmail(userEntity.getEmail());
        List<DictionaryDataEntity> dataServiceList4 = dictionaryDataService.getListByTypeDataCode("sex");
        Map<String, String> dataServiceMap4 = dataServiceList4.stream().filter(t -> ObjectUtil.equal(t.getEnabledMark(), 1)).collect(Collectors.toMap(DictionaryDataEntity::getEnCode, DictionaryDataEntity::getFullName));
        usersVO.setGender(dataServiceMap4.get(userEntity.getGender()));
        usersVO.setMobilePhone(userEntity.getMobilePhone());
        this.data(usersVO, userEntity, userInfo);
        this.userInfo(usersVO, userInfo);
        //岗位
        SysPositionEntity position = positionService.getInfo(userEntity.getPositionId());
        AppPositionVO positionVO = new AppPositionVO();
        if(position != null){
            positionVO.setId(position.getId());
            positionVO.setName(position.getFullName());
            usersVO.setPositionIds(ListUtil.toList(positionVO));
        }
        //直属主管
        if(StringUtil.isNotEmpty(userEntity.getManagerId())){
            SysUserEntity menager = userService.getInfo(userEntity.getManagerId());
            usersVO.setManager(menager != null ? menager.getRealName() + "/" + menager.getAccount() : "");
        }
        //角色
        List<String> roles = roleService.getAllRoleIdsByUserIdAndOrgId(userInfo.getUserId(), usersVO.getOrganizeId());
        List<SysRoleEntity> roleList = roleService.getListByIds(roles, null, false);
        usersVO.setRoleName(String.join("，", roleList.stream().map(SysRoleEntity::getFullName).collect(Collectors.toList())));
        usersVO.setRoleId(String.join(".", roleList.stream().map(SysRoleEntity::getId).collect(Collectors.toList())));
        return usersVO;
    }

    @Override
    public AppUserInfoVO getInfo(String id) {
        AppUserInfoVO userInfoVO = new AppUserInfoVO();
        SysUserEntity entity = userService.getInfo(id);
        if (entity != null) {
            userInfoVO = BeanUtil.toBean(entity, AppUserInfoVO.class);
            List<String> positionIds = StringUtil.isNotEmpty(entity.getPositionId()) ? Arrays.asList(entity.getPositionId().split(",")) : new ArrayList<>();
            List<String> positionName = positionService.getPositionName(positionIds, false).stream().map(t -> t.getFullName()).collect(Collectors.toList());
            userInfoVO.setPositionName(String.join(",", positionName));
            SysOrganizeEntity info = organizeService.getInfo(entity.getOrganizeId());
            userInfoVO.setOrganizeName(info != null ? info.getFullName() : "");
            userInfoVO.setHeadIcon(UploaderUtil.uploaderImg(userInfoVO.getHeadIcon()));
        }
        return userInfoVO;
    }
    /**
     * 赋值
     *
     * @param userInfo
     * @param userId
     * @param isAdmin
     */
    private void userInfo(UserInfo userInfo, String userId, boolean isAdmin, SysUserEntity userEntity) {
        List<String> userIdList = new ArrayList(){{add(userId);}};
        List<SysUserRelationEntity> data = userRelationService.getListByUserIdAll(userIdList);
        //获取一个字段的值
        List<String> positionList = data.stream().filter(m -> "Position".equals(m.getObjectType())).map(t -> t.getObjectId()).collect(Collectors.toList());
        Set<String> id = new LinkedHashSet<>();
        String[] position = StringUtil.isNotEmpty(userEntity.getPositionId()) ? userEntity.getPositionId().split(",") : new String[]{};
        List<String> positions = positionList.stream().filter(t->Arrays.asList(position).contains(t)).collect(Collectors.toList());
        id.addAll(positions);
        id.addAll(positionList);
        userInfo.setPositionIds(id.toArray(new String[id.size()]));
        if (!isAdmin) {
            data = data.stream().filter(m -> "Role".equals(m.getObjectType())).collect(Collectors.toList());
        }
        List<String> roleList = data.stream().map(t -> t.getObjectId()).collect(Collectors.toList());
        userInfo.setRoleIds(roleList);
    }

    private void data(AppUsersVO usersVO, SysUserEntity userEntity, UserInfo userInfo) {
        //组织
        usersVO.setOrganizeId(userEntity.getOrganizeId());
        List<SysOrganizeEntity> organizeIdList = new ArrayList<>();
        organizeService.getOrganizeId(userEntity.getOrganizeId(),organizeIdList);
        Collections.reverse(organizeIdList);
        usersVO.setOrganizeName(organizeIdList.stream().map(SysOrganizeEntity::getFullName).collect(Collectors.joining("/")));
        SysOrganizeEntity organizeEntity = organizeIdList.stream().filter(t->t.getId().equals(userEntity.getOrganizeId())).findFirst().orElse(null);
        if (organizeEntity != null) {
            String[] organizeId = StringUtil.isNotEmpty(organizeEntity.getOrganizeIdTree()) ? organizeEntity.getOrganizeIdTree().split(",") : new String[]{};
            if (organizeId.length > 0) {
                userInfo.setOrganizeId(organizeId[0]);
                userInfo.setDepartmentId(organizeId[organizeId.length - 1]);
            }
        }
        userInfo.setManagerId(userInfo.getManagerId());
        boolean b = userInfo.getIsAdministrator();
        List<String> subordinateIdsList = userService.getListByManagerId(userInfo.getUserId(),null).stream().map(SysUserEntity::getId).collect(Collectors.toList());
        userInfo.setSubordinateIds(subordinateIdsList);
        this.userInfo(userInfo, userInfo.getUserId(), b,userEntity);
//        userInfo.setSubOrganizeIds(new String[]{});
        //redisUtil.insert(userInfo.getId(), userInfo, DateUtil.getTime(userInfo.getOverdueTime()) - DateUtil.getTime(new Date()));
        UserProvider.setLoginUser(userInfo);
        UserProvider.setLocalLoginUser(userInfo);
    }

    /**
     * 登录信息
     *
     * @param appUsersVO 返回对象
     * @param userInfo   回话信息
     * @return
     */
    private void userInfo(AppUsersVO appUsersVO, UserInfo userInfo) {
        appUsersVO.setUserId(userInfo.getUserId());
        appUsersVO.setHeadIcon(UploaderUtil.uploaderImg(userInfo.getUserIcon()));
        appUsersVO.setUserName(userInfo.getUserName());
        appUsersVO.setUserAccount(userInfo.getUserAccount());
    }


}
