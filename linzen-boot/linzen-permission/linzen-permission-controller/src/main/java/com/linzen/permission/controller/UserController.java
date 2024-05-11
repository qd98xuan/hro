package com.linzen.permission.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.xuyanwu.spring.file.storage.FileInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.annotation.UserPermission;
import com.linzen.base.ServiceResult;
import com.linzen.base.Page;
import com.linzen.base.Pagination;
import com.linzen.base.UserInfo;
import com.linzen.base.controller.SuperController;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.vo.DownloadVO;
import com.linzen.base.vo.ListVO;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.MsgCode;
import com.linzen.constant.PermissionConst;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.engine.service.FlowTaskService;
import com.linzen.exception.DataBaseException;
import com.linzen.exception.ImportException;
import com.linzen.message.service.SynThirdDingTalkService;
import com.linzen.message.service.SynThirdQyService;
import com.linzen.model.FlowWorkListVO;
import com.linzen.model.FlowWorkModel;
import com.linzen.model.tenant.AdminInfoVO;
import com.linzen.model.tenant.TenantReSetPasswordForm;
import com.linzen.permission.entity.*;
import com.linzen.permission.model.user.UserIdListVo;
import com.linzen.permission.model.user.WorkHandoverModel;
import com.linzen.permission.model.user.form.UserCreateForm;
import com.linzen.permission.model.user.form.UserResetPasswordForm;
import com.linzen.permission.model.user.form.UserUpdateForm;
import com.linzen.permission.model.user.mod.*;
import com.linzen.permission.model.user.page.PageUser;
import com.linzen.permission.model.user.page.PaginationUser;
import com.linzen.permission.model.user.vo.*;
import com.linzen.permission.rest.PullUserUtil;
import com.linzen.permission.service.*;
import com.linzen.permission.util.PermissionUtil;
import com.linzen.service.AuthService;
import com.linzen.util.*;
import com.linzen.util.treeutil.SumTree;
import com.linzen.util.treeutil.newtreeutil.TreeDotUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static com.linzen.util.Constants.ADMIN_KEY;

/**
 * 用户管理
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "用户管理", description = "Users")
@Slf4j
@RestController
@RequestMapping("/api/permission/Users")
public class UserController extends SuperController<UserService, SysUserEntity> {

    @Autowired
    private CacheKeyUtil cacheKeyUtil;
    @Autowired
    private SynThirdQyService synThirdQyService;
    @Autowired
    private SynThirdDingTalkService synThirdDingTalkService;
    @Autowired
    private ConfigValueUtil configValueUtil;

    /*=== the same ===*/

    @Autowired
    private UserService userService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private OrganizeRelationService organizeRelationService;
    @Autowired
    private PermissionGroupService permissionGroupService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private FlowTaskService flowTaskApi;

    /**
     * 取出线程池
     */
    @Autowired
    private Executor threadPoolExecutor;
    @Autowired
    private AuthService authService;
    @Autowired
    private DictionaryDataService dictionaryDataService;

    /**
     * 获取用户列表
     *
     * @param pagination 分页参数
     * @return ignore
     */
    @Operation(summary = "获取用户列表")
    @GetMapping
    public ServiceResult<PageListVO<UserListVO>> getList(PaginationUser pagination) {
        List<SysUserEntity> userList = userService.getList(pagination, pagination.getOrganizeId(), false, true, pagination.getEnabledMark(), pagination.getGender());
        List<UserListVO> list = new ArrayList<>();
        // 得到性别
        List<DictionaryDataEntity> dataServiceList4 = dictionaryDataService.getListByTypeDataCode("sex");
        Map<String, String> dataServiceMap4 = dataServiceList4.stream().filter(t -> ObjectUtil.equal(t.getEnabledMark(), 1)).collect(Collectors.toMap(DictionaryDataEntity::getEnCode, DictionaryDataEntity::getFullName));
        Map<String, String> orgIdNameMaps = organizeService.getInfoList();
        for (SysUserEntity userEntity : userList) {
            UserListVO userVO = BeanUtil.toBean(userEntity, UserListVO.class);
            userVO.setHandoverMark(userEntity.getHandoverMark() == null ? 0 : userEntity.getHandoverMark());
            userVO.setHeadIcon(UploaderUtil.uploaderImg(userVO.getHeadIcon()));
            // 时间小于当前时间则判断已解锁
            if (userVO.getEnabledMark() != null && userVO.getEnabledMark() != 0) {
                if (Objects.nonNull(userEntity.getUnlockTime()) && userEntity.getUnlockTime().getTime() > System.currentTimeMillis()) {
                    userVO.setEnabledMark(2);
                } else if (Objects.nonNull(userEntity.getUnlockTime()) && userEntity.getUnlockTime().getTime() < System.currentTimeMillis()) {
                    userVO.setEnabledMark(1);
                }
            }
            List<SysUserRelationEntity> orgRelationByUserId = userRelationService.getAllOrgRelationByUserId(userEntity.getId());
            // 储存组织id信息
            StringJoiner stringJoiner = new StringJoiner(",");
            for (SysUserRelationEntity userRelationEntity : orgRelationByUserId) {
                // 获取组织id详情
                SysOrganizeEntity entity = organizeService.getInfo(userRelationEntity.getObjectId());
                if (entity != null) {
                    // 获取到组织树
                    String organizeIdTree = entity.getOrganizeIdTree();
                    if (StringUtil.isNotEmpty(organizeIdTree)) {
                        stringJoiner.add(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, organizeIdTree, "/"));
                    }
                }
            }
            userVO.setGender(dataServiceMap4.get(userEntity.getGender()));
            userVO.setOrganize(stringJoiner.toString());
            list.add(userVO);
        }

        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(list, paginationVO);
    }

    /**
     * 获取用户列表
     *
     * @return ignore
     */
    @Operation(summary = "获取所有用户列表")
    @GetMapping("/All")
    public ServiceResult<ListVO<UserAllVO>> getAllUsers(Pagination pagination) {
        List<SysUserEntity> list = userService.getList(pagination, null, false, false, null, null);
        List<UserAllVO> user = JsonUtil.createJsonToList(list, UserAllVO.class);
        ListVO<UserAllVO> vo = new ListVO<>();
        vo.setList(user);
        return ServiceResult.success(vo);
    }

    /**
     * IM通讯获取用户接口
     *
     * @param pagination 分页参数
     * @return ignore
     */
    @Operation(summary = "IM通讯获取用户")
    @GetMapping("/ImUser")
    public ServiceResult<PageListVO<ImUserListVo>> getAllImUserUsers(Pagination pagination) {
        List<SysUserEntity> data = userService.getList(pagination, true);
        List<ImUserListVo> list = new ArrayList<>();
        Map<String, SysOrganizeEntity> orgMaps = organizeService.getOrganizeName(data.stream().map(t -> t.getOrganizeId()).collect(Collectors.toList()), null, false, null);
        for (SysUserEntity entity : data) {
            ImUserListVo user = BeanUtil.toBean(entity, ImUserListVo.class);
            SysOrganizeEntity organize = orgMaps.get(entity.getOrganizeId());
            user.setDepartment(organize != null ? organize.getFullName() : "");
            user.setHeadIcon(UploaderUtil.uploaderImg(entity.getHeadIcon()));
            list.add(user);
        }
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(list, paginationVO);
    }

    /**
     * 获取用户下拉框列表
     *
     * @return ignore
     */
    @Operation(summary = "获取用户下拉框列表")
    @GetMapping("/Selector")
    public ServiceResult<ListVO<UserSelectorVO>> selector() {
        Map<String, SysOrganizeEntity> orgMaps = organizeService.getOrgMaps(null, true, null);
        List<SysOrganizeEntity> organizeData = new ArrayList<>(orgMaps.values());
        List<SysUserEntity> userData = userService.getList(true);
        List<UserSelectorModel> treeList = JsonUtil.createJsonToList(organizeData, UserSelectorModel.class);
        for (UserSelectorModel entity1 : treeList) {
            if ("department".equals(entity1.getType())) {
                entity1.setIcon("icon-linzen icon-linzen-tree-department1");
            } else if ("company".equals(entity1.getType())) {
                entity1.setIcon("icon-linzen icon-linzen-tree-organization3");
            }
        }
        for (SysUserEntity entity : userData) {
            UserSelectorModel treeModel = new UserSelectorModel();
            treeModel.setId(entity.getId());
            treeModel.setParentId(entity.getOrganizeId());
            treeModel.setFullName(entity.getRealName() + "/" + entity.getAccount());
            treeModel.setType("user");
            treeModel.setIcon("icon-linzen icon-linzen-tree-user2");
            treeList.add(treeModel);
        }
        List<SumTree<UserSelectorModel>> trees = TreeDotUtils.convertListToTreeDot(treeList);
        List<UserSelectorVO> listvo = JsonUtil.createJsonToList(trees, UserSelectorVO.class);
        List<SysOrganizeEntity> entities = organizeData.stream().filter(
                t -> "-1".equals(t.getParentId())
        ).collect(Collectors.toList());
        Iterator<UserSelectorVO> iterator = listvo.iterator();
        while (iterator.hasNext()) {
            UserSelectorVO userSelectorVO = iterator.next();
            for (SysOrganizeEntity entity : entities) {
                if (entity.getId().equals(userSelectorVO.getParentId())) {
                    iterator.remove();//使用迭代器的删除方法删除
                }
            }
        }
        ListVO<UserSelectorVO> vo = new ListVO<>();
        vo.setList(listvo);
        return ServiceResult.success(vo);
    }

    /**
     * 通过部门、岗位、用户、角色、分组id获取用户列表
     *
     * @param userConditionModel 用户选择模型
     * @return
     */
    @Operation(summary = "通过部门、岗位、用户、角色、分组id获取用户列表")
    @Parameters({
            @Parameter(name = "userConditionModel", description = "用户选择模型", required = true)
    })
    @PostMapping("/UserCondition")
    public ServiceResult userCondition(@RequestBody UserConditionModel userConditionModel) {
        List<String> list = new ArrayList<>(16);
        if (userConditionModel.getDepartIds() != null) {
            list.addAll(userConditionModel.getDepartIds());
        }
        if (userConditionModel.getRoleIds() != null) {
            list.addAll(userConditionModel.getRoleIds());
        }
        if (userConditionModel.getPositionIds() != null) {
            list.addAll(userConditionModel.getPositionIds());
        }
        if (userConditionModel.getGroupIds() != null) {
            list.addAll(userConditionModel.getGroupIds());
        }
        if (list.size() == 0) {
            list = userRelationService.getListByObjectType(userConditionModel.getType()).stream().map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
            if (PermissionConst.GROUP.equals(userConditionModel.getType())) {
                List<GroupEntity> groupList = groupService.getListByIds(list, true);
                list = groupList.stream().map(GroupEntity::getId).collect(Collectors.toList());
            }
            if (PermissionConst.ORGANIZE.equals(userConditionModel.getType())) {
                List<SysOrganizeEntity> orgList = organizeService.getOrgEntityList(list, true);
                list = orgList.stream().map(SysOrganizeEntity::getId).collect(Collectors.toList());
            }
            if (PermissionConst.ROLE.equals(userConditionModel.getType())) {
                List<SysRoleEntity> roleList = roleService.getListByIds(list, null, false);
                list = roleList.stream().filter(t -> t.getEnabledMark() == 1).map(SysRoleEntity::getId).collect(Collectors.toList());
            }
            if (PermissionConst.POSITION.equals(userConditionModel.getType())) {
                List<SysPositionEntity> positionList = positionService.getPosList(list);
                list = positionList.stream().filter(t -> t.getEnabledMark() == 1).map(SysPositionEntity::getId).collect(Collectors.toList());
            }
        }
        List<String> collect = userRelationService.getListByObjectIdAll(list).stream().map(SysUserRelationEntity::getUserId).collect(Collectors.toList());
        if (userConditionModel.getUserIds() != null) {
            collect.addAll(userConditionModel.getUserIds());
        }
        collect = collect.stream().distinct().collect(Collectors.toList());
        List<SysUserEntity> userName = userService.getUserName(collect, userConditionModel.getPagination());
        List<UserIdListVo> jsonToList = JsonUtil.createJsonToList(userName, UserIdListVo.class);
        Map<String, String> orgIdNameMaps = organizeService.getInfoList();
        jsonToList.forEach(t -> {
            t.setHeadIcon(UploaderUtil.uploaderImg(t.getHeadIcon()));
            t.setFullName(t.getRealName() + "/" + t.getAccount());
            List<SysUserRelationEntity> listByUserId = userRelationService.getListByUserId(t.getId(), PermissionConst.ORGANIZE);
            List<String> orgId = listByUserId.stream().map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
            List<SysOrganizeEntity> organizeName = new ArrayList<>(organizeService.getOrganizeName(orgId, null, false, null).values());
            StringBuilder stringBuilder = new StringBuilder();
            organizeName.forEach(org -> {
                if (StringUtil.isNotEmpty(org.getOrganizeIdTree())) {
                    String fullNameByOrgIdTree = organizeService.getFullNameByOrgIdTree(orgIdNameMaps, org.getOrganizeIdTree(), "/");
                    stringBuilder.append(",");
                    stringBuilder.append(fullNameByOrgIdTree);
                }
            });
            if (stringBuilder.length() > 0) {
                t.setOrganize(stringBuilder.toString().replaceFirst(",", ""));
            }
        });
        PaginationVO paginationVO = BeanUtil.toBean(userConditionModel.getPagination(), PaginationVO.class);
        return ServiceResult.pageList(jsonToList, paginationVO);
    }

    /**
     * 获取用户下拉框列表
     *
     * @param organizeIdForm 组织id
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "获取用户下拉框列表")
    @Parameters({
            @Parameter(name = "organizeId", description = "组织id", required = true),
            @Parameter(name = "pagination", description = "分页模型", required = true)
    })
    @PostMapping("/ImUser/Selector/{organizeId}")
    public ServiceResult<?> imUserSelector(@PathVariable("organizeId") String organizeIdForm, @RequestBody Pagination pagination) {
        String organizeId = XSSEscape.escape(organizeIdForm);
        List<UserSelectorVO> jsonToList = new ArrayList<>();
        Map<String, String> orgIdNameMaps = organizeService.getInfoList();
        Map<String, SysOrganizeEntity> orgMaps = organizeService.getOrgMaps(null, true, null);
        //判断是否搜索关键字
        if (StringUtil.isNotEmpty(pagination.getKeyword())) {
            //通过关键字查询
            List<SysUserEntity> list = userService.getList(pagination, false);
            //遍历用户给要返回的值插入值
            for (SysUserEntity entity : list) {
                UserSelectorVO vo = BeanUtil.toBean(entity, UserSelectorVO.class);
                vo.setParentId(entity.getOrganizeId());
                vo.setFullName(entity.getRealName() + "/" + entity.getAccount());
                vo.setType("user");
                vo.setIcon("icon-linzen icon-linzen-tree-user2");
                vo.setHeadIcon(UploaderUtil.uploaderImg(vo.getHeadIcon()));
                List<SysUserRelationEntity> listByUserId = userRelationService.getListByUserId(entity.getId()).stream().filter(t -> t != null && PermissionConst.ORGANIZE.equals(t.getObjectType())).collect(Collectors.toList());
                StringJoiner stringJoiner = new StringJoiner(",");
                listByUserId.forEach(t -> {
                    SysOrganizeEntity organizeEntity = orgMaps.get(t.getObjectId());
                    if (organizeEntity != null) {
                        String fullNameByOrgIdTree = organizeService.getFullNameByOrgIdTree(orgIdNameMaps, organizeEntity.getOrganizeIdTree(), "/");
                        if (StringUtil.isNotEmpty(fullNameByOrgIdTree)) {
                            stringJoiner.add(fullNameByOrgIdTree);
                        }
                    }
                });
                vo.setOrganize(stringJoiner.toString());
                vo.setHasChildren(false);
                vo.setIsLeaf(true);
                jsonToList.add(vo);
            }
            PaginationVO jsonToBean = BeanUtil.toBean(pagination, PaginationVO.class);
            return ServiceResult.pageList(jsonToList, jsonToBean);
        }
        //获取所有组织
        List<SysOrganizeEntity> collect = new ArrayList<>(orgMaps.values());
        //判断时候传入组织id
        //如果传入组织id，则取出对应的子集
        if (!"0".equals(organizeId)) {
            //通过组织查询部门及人员
            //单个组织
            SysOrganizeEntity organizeEntity = orgMaps.get(organizeId);
            if (organizeEntity != null) {
                //取出组织下的部门
                List<SysOrganizeEntity> collect1 = collect.stream().filter(t -> t.getParentId().equals(organizeEntity.getId())).collect(Collectors.toList());
                for (SysOrganizeEntity entitys : collect1) {
                    UserSelectorVO vo = BeanUtil.toBean(entitys, UserSelectorVO.class);
                    if ("department".equals(entitys.getCategory())) {
                        vo.setIcon("icon-linzen icon-linzen-tree-department1");
                    } else if ("company".equals(entitys.getCategory())) {
                        vo.setIcon("icon-linzen icon-linzen-tree-organization3");
                    }
                    vo.setOrganize(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, entitys.getOrganizeIdTree(), "/"));
                    // 判断组织下是否有人
                    jsonToList.add(vo);
                    vo.setHasChildren(true);
                    vo.setIsLeaf(false);
                }
                //取出组织下的人员
                List<SysUserEntity> entityList = userService.getListByOrganizeId(organizeId, null);
                for (SysUserEntity entity : entityList) {
                    if ("0".equals(String.valueOf(entity.getEnabledMark()))) {
                        continue;
                    }
                    UserSelectorVO vo = BeanUtil.toBean(entity, UserSelectorVO.class);
                    vo.setParentId(organizeId);
                    vo.setFullName(entity.getRealName() + "/" + entity.getAccount());
                    vo.setType("user");
                    vo.setIcon("icon-linzen icon-linzen-tree-user2");
                    List<SysUserRelationEntity> listByUserId = userRelationService.getListByUserId(entity.getId()).stream().filter(t -> t != null && PermissionConst.ORGANIZE.equals(t.getObjectType())).collect(Collectors.toList());
                    StringBuilder stringBuilder = new StringBuilder();
                    listByUserId.forEach(t -> {
                        SysOrganizeEntity organizeEntity1 = orgMaps.get(t.getObjectId());
                        if (organizeEntity1 != null) {
                            String fullNameByOrgIdTree = organizeService.getFullNameByOrgIdTree(orgIdNameMaps, organizeEntity1.getOrganizeIdTree(), "/");
                            if (StringUtil.isNotEmpty(fullNameByOrgIdTree)) {
                                stringBuilder.append("," + fullNameByOrgIdTree);
                            }
                        }
                    });
                    if (stringBuilder.length() > 0) {
                        vo.setOrganize(stringBuilder.toString().replaceFirst(",", ""));
                    }
                    vo.setHeadIcon(UploaderUtil.uploaderImg(vo.getHeadIcon()));
                    vo.setHasChildren(false);
                    vo.setIsLeaf(true);
                    jsonToList.add(vo);
                }
            }
            ListVO<UserSelectorVO> vo = new ListVO<>();
            vo.setList(jsonToList);
            return ServiceResult.success(vo);
        }

        //如果没有组织id，则取出所有组织
        jsonToList = JsonUtil.createJsonToList(collect.stream().filter(t -> "-1".equals(t.getParentId())).collect(Collectors.toList()), UserSelectorVO.class);
        //添加图标
        for (UserSelectorVO userSelectorVO : jsonToList) {
            userSelectorVO.setIcon("icon-linzen icon-linzen-tree-organization3");
            userSelectorVO.setHasChildren(true);
            userSelectorVO.setIsLeaf(false);
            userSelectorVO.setOrganize(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, orgMaps.get(userSelectorVO.getId()).getOrganizeIdTree(), "/"));
        }
        ListVO<UserSelectorVO> vo = new ListVO<>();
        vo.setList(jsonToList);
        return ServiceResult.success(vo);
    }

    /**
     * 获取用户下拉框列表
     *
     * @param organizeId 组织id
     * @param page 关键字
     * @return
     */
    @Operation(summary = "获取用户下拉框列表")
    @Parameters({
            @Parameter(name = "organizeId", description = "组织id", required = true),
            @Parameter(name = "page", description = "关键字", required = true)
    })
    @SaCheckPermission("permission.grade")
    @PostMapping("/GetListByAuthorize/{organizeId}")
    public ServiceResult<ListVO<UserByRoleVO>> getListByAuthorize(@PathVariable("organizeId") String organizeId, @RequestBody Page page) {
        List<UserByRoleVO> jsonToList = userService.getListByAuthorize(organizeId, page);
        ListVO listVO = new ListVO();
        listVO.setList(jsonToList);
        return ServiceResult.success(listVO);
    }

    /**
     * 获取用户信息
     *
     * @param id 用户id
     * @return ignore
     */
    @Operation(summary = "获取用户信息")
    @Parameters({
            @Parameter(name = "id", description = "用户id", required = true)
    })
    @SaCheckPermission("permission.user")
    @GetMapping("/{id}")
    public ServiceResult<UserInfoVO> getInfo(@PathVariable("id") String id) throws DataBaseException {
        SysUserEntity entity = userService.getInfo(id);
        if (entity == null) {
            return ServiceResult.error("用户不存在");
        }

        QueryWrapper<SysUserRelationEntity> roleQuery = new QueryWrapper<>();
        roleQuery.lambda().eq(SysUserRelationEntity::getUserId, id);
        roleQuery.lambda().eq(SysUserRelationEntity::getObjectType, PermissionConst.ROLE);
        List<String> roleIdList = new ArrayList<>();
        for (SysUserRelationEntity ure : userRelationService.list(roleQuery)) {
            roleIdList.add(ure.getObjectId());
        }

        entity.setHeadIcon(UploaderUtil.uploaderImg(entity.getHeadIcon()));
        // 得到组织树
        UserInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, UserInfoVO.class);
        vo.setRoleId(String.join(",", roleIdList));


        // 获取组织id数组
        QueryWrapper<SysUserRelationEntity> query = new QueryWrapper<>();
        query.lambda().eq(SysUserRelationEntity::getUserId, id);
        query.lambda().eq(SysUserRelationEntity::getObjectType, PermissionConst.ORGANIZE);
        List<String> organizeIds = new ArrayList<>();
        userRelationService.list(query).forEach(u -> {
            organizeIds.add(u.getObjectId());
        });

        // 岗位装配
        QueryWrapper<SysUserRelationEntity> positionQuery = new QueryWrapper<>();
        positionQuery.lambda().eq(SysUserRelationEntity::getUserId, id);
        positionQuery.lambda().eq(SysUserRelationEntity::getObjectType, PermissionConst.POSITION);
        String positionIds = "";
        for (SysUserRelationEntity ure : userRelationService.list(positionQuery)) {
            SysPositionEntity info = positionService.getInfo(ure.getObjectId());
            if (info != null) {
                positionIds = positionIds + "," + ure.getObjectId();
            }
        }
        if (positionIds.length() > 0) {
            vo.setPositionId(positionIds.substring(1));
        } else {
            vo.setPositionId(null);
        }
        // 设置分组id
        List<SysUserRelationEntity> listByObjectType = userRelationService.getListByObjectType(entity.getId(), PermissionConst.GROUP);
        StringBuilder groupId = new StringBuilder();
        listByObjectType.stream().forEach(t -> groupId.append("," + t.getObjectId()));
        if (groupId.length() > 0) {
            vo.setGroupId(groupId.toString().replaceFirst(",", ""));
        }
        vo.setOrganizeIdTree(PermissionUtil.getOrgIdsTree(organizeIds, 1, organizeService));
        return ServiceResult.success(vo);
    }

    /**
     * 新建用户
     *
     * @param userCrForm 表单参数
     */
    @UserPermission
    @Operation(summary = "新建用户")
    @Parameters({
            @Parameter(name = "userCrForm", description = "表单参数", required = true)
    })
    @SaCheckPermission("permission.user")
    @PostMapping
    public ServiceResult<String> create(@RequestBody @Valid UserCreateForm userCrForm) throws Exception {
        SysUserEntity entity = BeanUtil.toBean(userCrForm, SysUserEntity.class);
        if (userService.isExistByAccount(userCrForm.getAccount())) {
            return ServiceResult.error("账户名称不能重复");
        }
        if (StringUtil.isEmpty(entity.getGender())) {
            return ServiceResult.error("性别不能为空");
        }
        userService.create(entity);
        threadPoolExecutor.execute(() -> {
            try {
                //添加用户之后判断是否需要同步到企业微信
                synThirdQyService.createUserSysToQy(false, entity, "");
                //添加用户之后判断是否需要同步到钉钉
                synThirdDingTalkService.createUserSysToDing(false, entity, "");
            } catch (Exception e) {
                log.error("添加用户之后同步失败到企业微信或钉钉失败，异常：" + e.getMessage());
            }
        });
        String catchKey = cacheKeyUtil.getAllUser();
        if (redisUtil.exists(catchKey)) {
            redisUtil.remove(catchKey);
        }
        PullUserUtil.syncUser(entity, "create", userProvider.get().getTenantId());
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 修改用户
     *
     * @param userUpForm 表单参数
     * @param id         主键值
     */
    @UserPermission
    @Operation(summary = "修改用户")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true),
            @Parameter(name = "userUpForm", description = "表单参数", required = true)
    })
    @SaCheckPermission("permission.user")
    @PutMapping("/{id}")
    public ServiceResult<String> update(@PathVariable("id") String id, @RequestBody @Valid UserUpdateForm userUpForm) throws Exception {
        SysUserEntity entity = BeanUtil.toBean(userUpForm, SysUserEntity.class);
        if (StringUtil.isEmpty(entity.getGender())) {
            return ServiceResult.error("性别不能为空");
        }
        //将禁用的id加进数据
        SysUserEntity originUser = userService.getInfo(id);
        UserInfoVO infoVO = this.getInfo(id).getData();
        // 如果是管理员的话
        if ("1".equals(String.valueOf(originUser.getIsAdministrator()))) {
            UserInfo operatorUser = userProvider.get();
            // 管理员可以修改自己，但是无法修改其他管理员
            if (operatorUser.getIsAdministrator()) {
                if (originUser.getEnabledMark() != 0 && entity.getEnabledMark() == 0) {
                    return ServiceResult.error("无法禁用管理员用户");
                }
                if (!ADMIN_KEY.equals(userService.getInfo(operatorUser.getUserId()).getAccount())) {
                    if (!operatorUser.getUserId().equals(id)) {
                        return ServiceResult.error("管理员只能修改自己，不能修改其他管理员");
                    }
                }
            } else {
                return ServiceResult.error("无法修改管理员账户");
            }
        }
        //直属主管不能是自己
        if (id.equals(userUpForm.getManagerId())) {
            return ServiceResult.error("直属主管不能是自己");
        }
        if (!originUser.getAccount().equals(entity.getAccount())) {
            if (userService.isExistByAccount(entity.getAccount())) {
                return ServiceResult.error("账户名称不能重复");
            }
        }
        // 验证是否有十级,验证是否是自己的下属
        boolean subordinate = userService.isSubordinate(id, userUpForm.getManagerId());
        if (subordinate) {
            return ServiceResult.error("直属主管不能是我的下属用户");
        }
        // 如果账号被锁定
        if ("2".equals(String.valueOf(entity.getEnabledMark()))) {
            entity.setUnlockTime(null);
            entity.setLogErrorCount(0);
        }
        // 如果原来是锁定，现在不锁定，则置空错误次数
        if (originUser.getEnabledMark() == 2 && entity.getEnabledMark() == 1) {
            entity.setUnlockTime(null);
            entity.setLogErrorCount(0);
        }
        boolean flag = userService.update(id, entity);
        threadPoolExecutor.execute(() -> {
            try {
                //修改用户之后判断是否需要同步到企业微信
                synThirdQyService.updateUserSysToQy(false, entity, "");
                //修改用户之后判断是否需要同步到钉钉
                synThirdDingTalkService.updateUserSysToDing(false, entity, "");
            } catch (Exception e) {
                log.error("修改用户之后同步失败到企业微信或钉钉失败，异常：" + e.getMessage());
            }
        });
        if (!flag) {
            return ServiceResult.error(MsgCode.FA002.get());
        }
        // 删除在线的用户
        if(needSignOut(id,userUpForm,infoVO)){
            userService.delCurUser(null, id);
        }
        PullUserUtil.syncUser(entity, "update", userProvider.get().getTenantId());
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 修改用户【组织】【岗位】【角色】时
     * 需要退出登录的判断
     * @param id
     * @param userUpForm
     * @param infoVO
     * @return
     */
    private boolean needSignOut(String id, UserUpdateForm userUpForm, UserInfoVO infoVO) {
        StringJoiner organizeIds = new StringJoiner(",");
        List<LinkedList<String>> organizeIdTree = infoVO.getOrganizeIdTree();
        for (LinkedList<String> ids : organizeIdTree) {
            if (ids.size() > 0) {
                organizeIds.add(ids.get(ids.size() - 1));
            }
        }
        //修改用户【组织】【岗位】【角色】时用户会强制退出，但超管和分管不受影响。
        if (!Objects.equals(organizeIds.toString(), userUpForm.getOrganizeId()) || !Objects.equals(infoVO.getPositionId(), userUpForm.getPositionId())
                || !Objects.equals(infoVO.getRoleId(), userUpForm.getRoleId())) {
            if (userService.filterOrgAdministrator(new ArrayList() {{
                add(id);
            }}).size() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除用户
     *
     * @param id 主键值
     * @return ignore
     */
    @UserPermission
    @Operation(summary = "删除用户")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("permission.user")
    @DeleteMapping("/{id}")
    public ServiceResult<String> delete(@PathVariable("id") String id) {
        SysUserEntity entity = userService.getInfo(id);
        if (entity != null) {
            if ("1".equals(String.valueOf(entity.getIsAdministrator()))) {
                return ServiceResult.error("无法删除管理员账户");
            }
            //判断是否是部门主管
            if (organizeService.getList(false).stream().filter(t -> id.equals(t.getManagerId())).collect(Collectors.toList()).size() > 0) {
                return ServiceResult.error("此用户为某部门主管，无法删除");
            }
            // 有下属不允许删除
            if (userService.getListByManagerId(id, null).size() > 0) {
                return ServiceResult.error("此用户有下属，无法删除");
            }
            String tenantId = StringUtil.isEmpty(userProvider.get().getTenantId()) ? "" : userProvider.get().getTenantId();
            String catchKey = tenantId + "allUser";
            if (redisUtil.exists(catchKey)) {
                redisUtil.remove(catchKey);
            }
            userService.delete(entity);
            threadPoolExecutor.execute(() -> {
                try {
                    //删除用户之后判断是否需要同步到企业微信
                    synThirdQyService.deleteUserSysToQy(false, id, "");
                    //删除用户之后判断是否需要同步到钉钉
                    synThirdDingTalkService.deleteUserSysToDing(false, id, "");
                } catch (Exception e) {
                    log.error("删除用户之后同步失败到企业微信或钉钉失败，异常：" + e.getMessage());
                }
            });
            userService.delCurUser(null, entity.getId());
            PullUserUtil.syncUser(entity, "delete", userProvider.get().getTenantId());
            return ServiceResult.success(MsgCode.SU003.get());
        }
        return ServiceResult.error(MsgCode.FA003.get());
    }


    /**
     * 修改用户密码
     *
     * @param id 主键
     * @param userResetPasswordForm 修改密码模型
     * @return ignore
     */
    @UserPermission
    @Operation(summary = "修改用户密码")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true),
            @Parameter(name = "userResetPasswordForm", description = "修改密码模型", required = true)
    })
    @SaCheckPermission("permission.user")
    @PostMapping("/{id}/Actions/ResetPassword")
    public ServiceResult<String> modifyPassword(@PathVariable("id") String id, @RequestBody @Valid UserResetPasswordForm userResetPasswordForm) {
        SysUserEntity entity = userService.getInfo(id);
        if (entity != null) {
            entity.setPassword(userResetPasswordForm.getUserPassword());
            userService.updatePassword(entity);
            userService.delCurUser("密码已变更，请重新登录！", entity.getId());
            PullUserUtil.syncUser(entity, "modifyPassword", userProvider.get().getTenantId());
            return ServiceResult.success(MsgCode.SU005.get());
        }
        return ServiceResult.success("操作失败,用户不存在");
    }

    /**
     * 更新用户状态
     *
     * @param id 主键值
     * @return ignore
     */
    @Operation(summary = "更新用户状态")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("permission.user")
    @PutMapping("/{id}/Actions/State")
    public ServiceResult<String> disable(@PathVariable("id") String id) throws Exception {
        SysUserEntity entity = userService.getInfo(id);
        if (entity != null) {
            if ("1".equals(String.valueOf(entity.getIsAdministrator()))) {
                return ServiceResult.error("无法修改管理员账户状态");
            }
            if (entity.getEnabledMark() != null) {
                if ("1".equals(String.valueOf(entity.getEnabledMark()))) {
                    entity.setEnabledMark(0);
                    userService.delCurUser(null, entity.getId());
                    userService.update(id, entity);
                } else {
                    entity.setEnabledMark(1);
                    userService.update(id, entity);
                }
            } else {
                entity.setEnabledMark(1);
                userService.update(id, entity);
            }
            return ServiceResult.success(MsgCode.SU005.get());
        }
        return ServiceResult.success("操作失败,用户不存在");
    }

    /**
     * 解除锁定
     *
     * @param id 主键值
     * @return ignore
     */
    @Operation(summary = "解除锁定")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("permission.user")
    @PutMapping("/{id}/Actions/unlock")
    public ServiceResult<String> unlock(@PathVariable("id") String id) throws Exception {
        SysUserEntity entity = userService.getInfo(id);
        if (entity != null) {
            // 状态变成正常
            entity.setEnabledMark(1);
            entity.setUnlockTime(null);
            entity.setLogErrorCount(0);
            entity.setId(id);
            userService.updateById(entity);
            return ServiceResult.success(MsgCode.SU005.get());
        }
        return ServiceResult.success("操作失败,用户不存在");
    }

    /**
     * 获取用户基本信息
     *
     * @param userIdModel 用户id
     * @return ignore
     */
    @Operation(summary = "获取用户基本信息")
    @Parameters({
            @Parameter(name = "userIdModel", description = "用户id", required = true)
    })
    @PostMapping("/getUserList")
    public ServiceResult<ListVO<UserIdListVo>> getUserList(@RequestBody UserIdModel userIdModel) {
        List<SysUserEntity> userName = userService.getUserName(userIdModel.getIds(), true);
        List<UserIdListVo> list = JsonUtil.createJsonToList(userName, UserIdListVo.class);
        List<SysUserRelationEntity> listByUserIds = userRelationService.getRelationByUserIds(list.stream().map(UserIdListVo::getId).collect(Collectors.toList()));
        Map<String, String> orgIdNameMaps = organizeService.getInfoList();
        for (UserIdListVo entity : list) {
            if (entity == null) {
                break;
            }
            entity.setFullName(entity.getRealName() + "/" + entity.getAccount());
            List<SysUserRelationEntity> listByUserId = listByUserIds.stream().filter(t -> t.getUserId().equals(entity.getId())).collect(Collectors.toList());
            StringBuilder stringBuilder = new StringBuilder();
            List<SysOrganizeEntity> orgEntityList = organizeService.getOrgEntityList(listByUserId.stream().map(SysUserRelationEntity::getObjectId).collect(Collectors.toList()), false);
            listByUserId.forEach(t -> {
                SysOrganizeEntity organizeEntity = orgEntityList.stream().filter(org -> org.getId().equals(t.getObjectId())).findFirst().orElse(null);
                if (organizeEntity != null) {
                    String fullNameByOrgIdTree = organizeService.getFullNameByOrgIdTree(orgIdNameMaps, organizeEntity.getOrganizeIdTree(), "/");
                    if (StringUtil.isNotEmpty(fullNameByOrgIdTree)) {
                        stringBuilder.append("," + fullNameByOrgIdTree);
                    }
                }
            });
            if (stringBuilder.length() > 0) {
                entity.setOrganize(stringBuilder.toString().replaceFirst(",", ""));
            }
            entity.setHeadIcon(UploaderUtil.uploaderImg(entity.getHeadIcon()));
        }
        ListVO<UserIdListVo> listVO = new ListVO<>();
        listVO.setList(list);
        return ServiceResult.success(listVO);
    }

    /**
     * 获取选中组织、岗位、角色、用户基本信息
     *
     * @param userIdModel 用户id
     * @return ignore
     */
    @Operation(summary = "获取选中组织、岗位、角色、用户基本信息")
    @Parameters({
            @Parameter(name = "userIdModel", description = "用户id", required = true)
    })
    @PostMapping("/getSelectedList")
    public ServiceResult<ListVO<UserIdListVo>> getSelectedList(@RequestBody UserIdModel userIdModel) {
        List<String> ids = userIdModel.getIds();
        List<UserIdListVo> list = userService.selectedByIds(ids);
        ListVO<UserIdListVo> listVO = new ListVO<>();
        listVO.setList(list);
        return ServiceResult.success(listVO);
    }



    /**
     * 获取用户基本信息
     *
     * @param userIdModel 用户id
     * @return ignore
     */
    @Operation(summary = "获取选中用户基本信息")
    @Parameters({
            @Parameter(name = "userIdModel", description = "用户id", required = true)
    })
    @PostMapping("/getSelectedUserList")
    public ServiceResult<PageListVO<UserIdListVo>> getSelectedUserList(@RequestBody UserIdModelByPage userIdModel) {
        List<UserIdListVo> jsonToList = userService.getObjList(userIdModel.getIds(), userIdModel.getPagination(), null);
        PaginationVO paginationVO = BeanUtil.toBean(userIdModel.getPagination(), PaginationVO.class);
        return ServiceResult.pageList(jsonToList, paginationVO);
    }

    /**
     * 获取组织下的人员
     *
     * @param page 页面信息
     * @return ignore
     */
    @Operation(summary = "获取组织下的人员")
    @GetMapping("/getOrganization")
    public ServiceResult<List<UserIdListVo>> getOrganization(PageUser page) {
        String departmentId = page.getOrganizeId();
        // 判断是否获取当前组织下的人员
        if ("0".equals(departmentId)) {
            departmentId = userProvider.get().getDepartmentId();
            // 为空则取组织id
            if (StringUtil.isEmpty(departmentId)) {
                departmentId = userProvider.get().getOrganizeId();
            }
        }
        Map<String, SysOrganizeEntity> orgMaps = organizeService.getOrgMaps(null, true, null);
        List<SysUserEntity> list = userService.getListByOrganizeId(departmentId, page.getKeyword());
        List<UserIdListVo> jsonToList = JsonUtil.createJsonToList(list, UserIdListVo.class);
        Map<String, String> orgIdNameMaps = organizeService.getInfoList();
        List<SysUserRelationEntity> listByObjectType = userRelationService.getListByObjectType(PermissionConst.ORGANIZE);
        jsonToList.forEach(t -> {
            t.setRealName(t.getRealName() + "/" + t.getAccount());
            t.setFullName(t.getRealName());
            List<String> collect = listByObjectType.stream().filter(userRelationEntity -> userRelationEntity.getUserId().equals(t.getId())).map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
            StringJoiner stringJoiner = new StringJoiner(",");
            collect.forEach(objectId -> {
                SysOrganizeEntity organizeEntity = orgMaps.get(objectId);
                if (organizeEntity != null) {
                    String fullNameByOrgIdTree = organizeService.getFullNameByOrgIdTree(orgIdNameMaps, organizeEntity.getOrganizeIdTree(), "/");
                    if (StringUtil.isNotEmpty(fullNameByOrgIdTree)) {
                        stringJoiner.add(fullNameByOrgIdTree);
                    }
                }
            });
            t.setOrganize(stringJoiner.toString());
            t.setHeadIcon(UploaderUtil.uploaderImg(t.getHeadIcon()));
        });
        return ServiceResult.success(jsonToList);
    }

    /**
     * 获取岗位人员
     *
     * @param page 页面信息
     * @return ignore
     */
    @Operation(summary = "获取岗位人员")
    @GetMapping("/GetUsersByPositionId")
    public ServiceResult<List<UserByRoleVO>> getUsersByPositionId(UsersByPositionModel page) {
        List<UserByRoleVO> list = new ArrayList<>(1);
        String keyword = page.getKeyword();
        // 岗位id
        String positionId = page.getPositionId();
        // 得到关联的组织id
        SysPositionEntity positionEntity = positionService.getInfo(positionId);
//        List<OrganizeRelationEntity> relationListByObjectIdAndType = organizeRelationService.getRelationListByObjectIdAndType(PermissionConst.POSITION, positionId);
        if (positionEntity != null) {
            UserByRoleVO vo = new UserByRoleVO();
            String organizeId = positionEntity.getOrganizeId();
            // 得到组织信息
            SysOrganizeEntity organizeEntity = organizeService.getInfo(organizeId);
            if (Objects.nonNull(organizeEntity)) {
                vo.setId(organizeEntity.getId());
                vo.setType(organizeEntity.getCategory());
                if ("department".equals(organizeEntity.getCategory())) {
                    vo.setIcon("icon-linzen icon-linzen-tree-department1");
                } else {
                    vo.setIcon("icon-linzen icon-linzen-tree-organization3");
                }
                vo.setEnabledMark(organizeEntity.getEnabledMark());
                Map<String, String> orgIdNameMaps = organizeService.getInfoList();
                // 组装组织名称
                String orgName = organizeService.getFullNameByOrgIdTree(orgIdNameMaps, organizeEntity.getOrganizeIdTree(), "/");
                vo.setFullName(orgName);
                // 赋予子集
                List<UserByRoleVO> userByRoleVOS = new ArrayList<>(16);
                List<SysUserEntity> lists = userService.getListByOrganizeId(organizeEntity.getId(), keyword);
                if (lists.size() > 0) {
                    vo.setHasChildren(true);
                    vo.setIsLeaf(false);
                    lists.stream().forEach(t->{
                        UserByRoleVO userByRoleVO = new UserByRoleVO();
                        userByRoleVO.setParentId(organizeEntity.getId());
                        userByRoleVO.setId(t.getId());
                        userByRoleVO.setFullName(t.getRealName() + "/" + t.getAccount());
                        userByRoleVO.setEnabledMark(t.getEnabledMark());
                        userByRoleVO.setHeadIcon(UploaderUtil.uploaderImg(t.getHeadIcon()));
                        userByRoleVO.setOrganize(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, organizeEntity.getOrganizeIdTree(), "/"));
                        userByRoleVO.setIsLeaf(true);
                        userByRoleVO.setHasChildren(false);
                        userByRoleVO.setIcon("icon-linzen icon-linzen-tree-user2");
                        userByRoleVO.setType("user");
                        userByRoleVOS.add(userByRoleVO);
                    });
                    vo.setChildren(userByRoleVOS);
                } else {
                    vo.setHasChildren(false);
                    vo.setIsLeaf(true);
                    vo.setChildren(new ArrayList<>());
                }
                list.add(vo);
            }
        }
        return ServiceResult.success(list);
    }

    /**
     * 角色成员弹窗
     *
     * @param model 页面信息
     * @return ignore
     */
    @Operation(summary = "角色成员弹窗")
    @SaCheckPermission("permission.role")
    @GetMapping("/GetUsersByRoleOrgId")
    public ServiceResult<List<UserByRoleVO>> getUsersByRoleOrgId(UserByRoleModel model) {
        List<UserByRoleVO> jsonToList = new ArrayList<>(16);
        // 得到组织关系
        List<SysOrganizeRelationEntity> relationListByRoleId = organizeRelationService.getRelationListByRoleId(model.getRoleId());
        // 得到组织信息
        List<SysOrganizeEntity> orgEntityList = organizeService.getOrgEntityList(relationListByRoleId.stream().map(SysOrganizeRelationEntity::getOrganizeId).collect(Collectors.toList()), true);
        Map<String, String> orgIdNameMaps = organizeService.getInfoList();
        //判断是否搜索关键字
        if (StringUtil.isNotEmpty(model.getKeyword())) {
            //通过关键字查询
            List<SysUserEntity> list = userService.getList(orgEntityList.stream().map(SysOrganizeEntity::getId).collect(Collectors.toList()), model.getKeyword());
            List<SysUserRelationEntity> listByUserIds = userRelationService.getRelationByUserIds(list.stream().map(SysUserEntity::getId).collect(Collectors.toList()));
            //遍历用户给要返回的值插入值
            for (SysUserEntity entity : list) {
                UserByRoleVO vo = new UserByRoleVO();
                vo.setId(entity.getId());
                vo.setFullName(entity.getRealName() + "/" + entity.getAccount());
                vo.setEnabledMark(entity.getEnabledMark());
                vo.setIsLeaf(true);
                vo.setHeadIcon(UploaderUtil.uploaderImg(entity.getHeadIcon()));
                List<SysUserRelationEntity> listByUserId = listByUserIds.stream().filter(t -> t.getUserId().equals(entity.getId())).collect(Collectors.toList());
                StringBuilder stringBuilder = new StringBuilder();
                List<SysOrganizeEntity> orgEntityLists = organizeService.getOrgEntityList(listByUserId.stream().map(SysUserRelationEntity::getObjectId).collect(Collectors.toList()), false);
                listByUserId.forEach(t -> {
                    SysOrganizeEntity orgEntity = orgEntityLists.stream().filter(org -> org.getId().equals(t.getObjectId())).findFirst().orElse(null);
                    if (orgEntity != null) {
                        String fullNameByOrgIdTree = organizeService.getFullNameByOrgIdTree(orgIdNameMaps, orgEntity.getOrganizeIdTree(), "/");
                        if (StringUtil.isNotEmpty(fullNameByOrgIdTree)) {
                            stringBuilder.append("," + fullNameByOrgIdTree);
                        }
                    }
                });
                if (stringBuilder.length() > 0) {
                    vo.setOrganize(stringBuilder.toString().replaceFirst(",", ""));
                }
                vo.setHasChildren(false);
                vo.setIcon("icon-linzen icon-linzen-tree-user2");
                vo.setType("user");
                jsonToList.add(vo);
            }
            return ServiceResult.success(jsonToList);
        }
        //获取所有组织
        List<SysOrganizeEntity> collect = organizeService.getList(false).stream().filter(t -> t.getEnabledMark() == 1).collect(Collectors.toList());
        //判断时候传入组织id
        //如果传入组织id，则取出对应的子集
        if (!"0".equals(model.getOrganizeId())) {
            //通过组织查询部门及人员
            //单个组织
            List<SysOrganizeEntity> list = collect.stream().filter(t -> model.getOrganizeId().equals(t.getId())).collect(Collectors.toList());
            if (list.size() > 0) {
                //获取组织信息
                SysOrganizeEntity organizeEntity = list.get(0);
                //取出组织下的部门
                List<SysOrganizeEntity> collect1 = collect.stream().filter(t -> t.getParentId().equals(organizeEntity.getId())).collect(Collectors.toList());
                // 判断组织关系中是否有子部门id
                List<SysOrganizeEntity> organizeEntities = new ArrayList<>();
                for (SysOrganizeEntity entity : collect1) {
                    List<SysOrganizeRelationEntity> collect2 = relationListByRoleId.stream().filter(t -> entity.getId().equals(t.getOrganizeId())).collect(Collectors.toList());
                    collect2.stream().forEach(t->{
                        if (StringUtil.isNotEmpty(t.getOrganizeId())) {
                            organizeEntities.add(organizeService.getInfo(t.getOrganizeId()));
                        }
                    });
                }
                // 其他不是子集的直接显示
                List<SysOrganizeRelationEntity> collect2 = relationListByRoleId.stream()
                        .filter(item -> !organizeEntities.stream().map(e -> e.getId())
                                .collect(Collectors.toList()).contains(item.getOrganizeId()))
                        .collect(Collectors.toList());
                // 移除掉上级不是同一个的
                List<SysOrganizeRelationEntity> collect3 = collect2.stream().filter(t -> !organizeService.getInfo(t.getOrganizeId()).getOrganizeIdTree().contains(model.getOrganizeId())).collect(Collectors.toList());
                collect2.removeAll(collect3);
                List<SysOrganizeRelationEntity> collect4 = collect2.stream().filter(t -> !t.getOrganizeId().equals(model.getOrganizeId())).collect(Collectors.toList());
                List<SysOrganizeEntity> collect5 = collect.stream().filter(x -> collect4.stream().map(SysOrganizeRelationEntity::getOrganizeId).collect(Collectors.toList()).contains(x.getId())).collect(Collectors.toList());
                List<SysOrganizeEntity> organizeEntities1 = new ArrayList<>(collect5);
                // 不是子集的对比子集的
                for (SysOrganizeEntity entity : collect5) {
                    for (SysOrganizeEntity organizeEntity1 : organizeEntities) {
                        if (entity.getOrganizeIdTree().contains(organizeEntity1.getId())) {
                            organizeEntities1.remove(entity);
                        }
                    }
                }

                //取出组织下的人员
                List<SysUserEntity> entityList = userService.getListByOrganizeId(model.getOrganizeId(), null);
                List<SysUserRelationEntity> listByUserIds = userRelationService.getRelationByUserIds(entityList.stream().map(SysUserEntity::getId).collect(Collectors.toList()));
                for (SysUserEntity entity : entityList) {
                    UserByRoleVO vo = new UserByRoleVO();
                    vo.setId(entity.getId());
                    vo.setFullName(entity.getRealName() + "/" + entity.getAccount());
                    vo.setEnabledMark(entity.getEnabledMark());
                    vo.setIsLeaf(true);
                    vo.setHasChildren(false);
                    vo.setIcon("icon-linzen icon-linzen-tree-user2");
                    vo.setType("user");
                    vo.setHeadIcon(UploaderUtil.uploaderImg(entity.getHeadIcon()));
                    List<SysUserRelationEntity> listByUserId = listByUserIds.stream().filter(t -> t.getUserId().equals(entity.getId())).collect(Collectors.toList());
                    StringBuilder stringBuilder = new StringBuilder();
                    List<SysOrganizeEntity> orgEntityLists = organizeService.getOrgEntityList(listByUserId.stream().map(SysUserRelationEntity::getObjectId).collect(Collectors.toList()), false);
                    listByUserId.forEach(t -> {
                        SysOrganizeEntity orgEntity = orgEntityLists.stream().filter(org -> org.getId().equals(t.getObjectId())).findFirst().orElse(null);
                        if (orgEntity != null) {
                            String fullNameByOrgIdTree = organizeService.getFullNameByOrgIdTree(orgIdNameMaps, orgEntity.getOrganizeIdTree(), "/");
                            if (StringUtil.isNotEmpty(fullNameByOrgIdTree)) {
                                stringBuilder.append("," + fullNameByOrgIdTree);
                            }
                        }
                    });
                    if (stringBuilder.length() > 0) {
                        vo.setOrganize(stringBuilder.toString().replaceFirst(",", ""));
                    }
                    jsonToList.add(vo);
                }
                // 处理子集断层
                List<SysOrganizeEntity> organizeEntities2 = new ArrayList<>(collect5);
                for (SysOrganizeEntity entity : organizeEntities2) {
                    List<SysOrganizeEntity> collect6 = organizeEntities2.stream().filter(t -> !entity.getId().equals(t.getId()) && t.getOrganizeIdTree().contains(entity.getOrganizeIdTree())).collect(Collectors.toList());
                    organizeEntities1.removeAll(collect6);
                }
                for (SysOrganizeEntity entity : organizeEntities1) {
                    StringBuffer stringBuffer = new StringBuffer();
                    String[] split = entity.getOrganizeIdTree().split(",");
                    List<String> list1 = Arrays.asList(split);
                    List<String> list2 = new ArrayList<>(list1);
                    int indexOf = list2.indexOf(model.getOrganizeId());
                    while (indexOf >= 0) {
                        list2.remove(indexOf);
                        indexOf--;
                    }
                    StringBuffer organizeIdTree = new StringBuffer();
                    for (String parentId : list2) {
                        SysOrganizeEntity organizes = organizeService.getInfo(parentId);
                        if (Objects.nonNull(organizes) && StringUtil.isNotEmpty(organizes.getFullName())) {
                            organizeIdTree.append("/" + organizes.getFullName());
                        }
                    }
                    String toString = organizeIdTree.toString();
                    if (StringUtil.isNotEmpty(toString)) {
                        String organizeId = toString.replaceFirst("/", "");
                        stringBuffer.append("," + organizeId);
                    }
                    UserByRoleVO userByRoleVO = new UserByRoleVO();
                    userByRoleVO.setId(entity.getId());
                    userByRoleVO.setType(entity.getCategory());
                    userByRoleVO.setFullName(stringBuffer.toString().replace(",", ""));
                    if ("department".equals(entity.getCategory())) {
                        userByRoleVO.setIcon("icon-linzen icon-linzen-tree-department1");
                    } else {
                        userByRoleVO.setIcon("icon-linzen icon-linzen-tree-organization3");
                    }
                    userByRoleVO.setHasChildren(true);
                    userByRoleVO.setIsLeaf(false);
                    userByRoleVO.setEnabledMark(entity.getEnabledMark());
                    jsonToList.add(userByRoleVO);
                }
                for (SysOrganizeEntity entitys : organizeEntities) {
                    UserByRoleVO vo = new UserByRoleVO();
                    vo.setId(entitys.getId());
                    vo.setType(entitys.getCategory());
                    vo.setFullName(entitys.getFullName());
                    if ("department".equals(entitys.getCategory())) {
                        vo.setIcon("icon-linzen icon-linzen-tree-department1");
                    } else {
                        vo.setIcon("icon-linzen icon-linzen-tree-organization3");
                    }
                    vo.setHasChildren(true);
                    vo.setIsLeaf(false);
                    vo.setEnabledMark(entitys.getEnabledMark());
                    jsonToList.add(vo);
                }
            }
            return ServiceResult.success(jsonToList);
        }

        // 判断是否有父级
        Set<SysOrganizeEntity> set = new HashSet<>(16);
        for (SysOrganizeEntity entity : orgEntityList) {
            List<SysOrganizeEntity> collect1 = orgEntityList.stream().filter(t -> !entity.getId().equals(t.getId()) && entity.getOrganizeIdTree().contains(t.getOrganizeIdTree())).collect(Collectors.toList());
            set.addAll(collect1);
        }
        List<SysOrganizeEntity> list = new ArrayList<>(set);
        // 从list中一处已经有的
        List<SysOrganizeEntity> list1 = new ArrayList<>(list);
        for (SysOrganizeEntity organizeEntity : list) {
            List<SysOrganizeEntity> collect1 = list.stream().filter(t -> !organizeEntity.getId().equals(t.getId()) && t.getOrganizeIdTree().contains(organizeEntity.getId())).collect(Collectors.toList());
            list1.removeAll(collect1);
        }
        list = list1;
        // 纯断层的
        List<SysOrganizeEntity> list2 = new ArrayList<>(orgEntityList);
        for (SysOrganizeEntity organizeEntity: orgEntityList){
            if (list.stream().filter(t -> organizeEntity.getOrganizeIdTree().contains(t.getId())).count() > 0) {
                list2.remove(organizeEntity);
            }
        }
        list.addAll(list2);
        for (SysOrganizeEntity organizeEntity : list) {
            if (organizeEntity != null && organizeEntity.getEnabledMark() == 1) {
                UserByRoleVO userByRoleVO = new UserByRoleVO();
                userByRoleVO.setId(organizeEntity.getId());
                userByRoleVO.setType(organizeEntity.getCategory());
                String orgName = organizeService.getFullNameByOrgIdTree(orgIdNameMaps, organizeEntity.getOrganizeIdTree(), "/");
                userByRoleVO.setFullName(orgName);
                if ("department".equals(organizeEntity.getCategory())) {
                    userByRoleVO.setIcon("icon-linzen icon-linzen-tree-department1");
                } else {
                    userByRoleVO.setIcon("icon-linzen icon-linzen-tree-organization3");
                }
                userByRoleVO.setHasChildren(true);
                userByRoleVO.setIsLeaf(false);
                userByRoleVO.setEnabledMark(organizeEntity.getEnabledMark());
                jsonToList.add(userByRoleVO);
            }
        }
        return ServiceResult.success(jsonToList);
    }

    /**
     * 获取我的下属(不取子集)
     *
     * @param page 页面信息
     * @return ignore
     */
    @Operation(summary = "获取我的下属(不取子集)")
    @Parameters({
            @Parameter(name = "page", description = "关键字", required = true)
    })
    @PostMapping("/getSubordinates")
    public ServiceResult<List<UserIdListVo>> getSubordinates(@RequestBody Page page) {
        Map<String, SysOrganizeEntity> orgMaps = organizeService.getOrgMaps(null, false, null);
        List<SysUserEntity> list = userService.getListByManagerId(userProvider.get().getUserId(), page.getKeyword());
        List<UserIdListVo> jsonToList = JsonUtil.createJsonToList(list, UserIdListVo.class);
        Map<String, String> orgIdNameMaps = organizeService.getInfoList();
        jsonToList.forEach(t -> {
            t.setRealName(t.getRealName() + "/" + t.getAccount());
            t.setFullName(t.getRealName());
            t.setHeadIcon(UploaderUtil.uploaderImg(t.getHeadIcon()));
            List<SysUserRelationEntity> listByUserId = userRelationService.getListByUserId(t.getId()).stream().filter(ur -> PermissionConst.ORGANIZE.equals(ur.getObjectType())).collect(Collectors.toList());
            StringJoiner stringJoiner = new StringJoiner(",");
            listByUserId.forEach(tt -> {
                SysOrganizeEntity organizeEntity = orgMaps.get(tt.getObjectId());
                if (organizeEntity != null) {
                    String fullNameByOrgIdTree = organizeService.getFullNameByOrgIdTree(orgIdNameMaps, organizeEntity.getOrganizeIdTree(), "/");
                    if (StringUtil.isNotEmpty(fullNameByOrgIdTree)) {
                        stringJoiner.add(fullNameByOrgIdTree);
                    }
                }
            });
            t.setOrganize(stringJoiner.toString());
            t.setHeadIcon(UploaderUtil.uploaderImg(t.getHeadIcon()));
        });
        return ServiceResult.success(jsonToList);
    }

    /**
     * 导出excel
     *
     * @param dataType   导出方式
     * @param selectKey 选择列
     * @param pagination 分页
     * @return ignore
     */
    @Operation(summary = "导出excel")
    @SaCheckPermission("permission.user")
    @GetMapping("/ExportData")
    public ServiceResult<DownloadVO> Export(String dataType, String selectKey, PaginationUser pagination) {
        // 导出
        DownloadVO vo = userService.exportExcel(dataType, selectKey, pagination);
        return ServiceResult.success(vo);
    }

    /**
     * 模板下载
     *
     * @return ignore
     */
    @Operation(summary = "模板下载")
    @SaCheckPermission("permission.user")
    @GetMapping("/TemplateDownload")
    public ServiceResult<DownloadVO> TemplateDownload() {
        DownloadVO vo = DownloadVO.builder().build();
        try {
            vo.setName("用户信息.xlsx");
            vo.setUrl(UploaderUtil.uploaderFile("/api/file/DownloadModel?encryption=", "用户信息" +
                    ".xlsx" + "#" + "Temporary"));
        } catch (Exception e) {
            log.error("信息导出Excel错误:" + e.getMessage());
        }
        return ServiceResult.success(vo);
    }


    /**
     * 导入数据
     *
     * @param data 导入模型
     * @return ignore
     */
    @Operation(summary = "导入数据")
    @Parameters({
            @Parameter(name = "data", description = "导入模型", required = true)
    })
    @SaCheckPermission("permission.user")
    @PostMapping("/ImportData")
    public ServiceResult<UserImportVO> ImportData(@RequestBody UserExportVO data) {
        List<UserExportVO> dataList = JsonUtil.createJsonToList(data.getList(), UserExportVO.class);
        //导入数据
        UserImportVO result = userService.importData(dataList);
        return ServiceResult.success(result);
    }

    /**
     * 导出错误报告
     *
     * @param data 导出模型
     * @return ignore
     */
    @Operation(summary = "导出错误报告")
    @Parameters({
            @Parameter(name = "data", description = "导出模型", required = true)
    })
    @SaCheckPermission("permission.user")
    @PostMapping("/ExportExceptionData")
    public ServiceResult<DownloadVO> exportExceptionData(@RequestBody UserExportExceptionVO data) {
        List<UserExportExceptionVO> dataList = JsonUtil.createJsonToList(data.getList(), UserExportExceptionVO.class);
        //生成Excel
        DownloadVO vo = userService.exportExceptionData(dataList);
        return ServiceResult.success(vo);
    }

    /*= different =*/

    /**
     * 上传文件(excel)
     *
     * @return ignore
     */
    @Operation(summary = "上传文件")
    @SaCheckPermission("permission.user")
    @PostMapping("/Uploader")
    public ServiceResult<Object> Uploader() {
        List<MultipartFile> list = UpUtil.getFileAll();
        MultipartFile file = list.get(0);
        if (file.getOriginalFilename().endsWith(".xlsx") || file.getOriginalFilename().endsWith(".xls")) {
            String filePath = XSSEscape.escape(configValueUtil.getTemporaryFilePath());
            String fileName = XSSEscape.escape(RandomUtil.uuId() + "." + UpUtil.getFileType(file));
            // 上传文件
            FileInfo fileInfo = FileUploadUtils.uploadFile(file, filePath, fileName);
            DownloadVO vo = DownloadVO.builder().build();
            vo.setName(fileInfo.getFilename());
            return ServiceResult.success(vo);
        } else {
            return ServiceResult.error("选择文件不符合导入");
        }
    }

    /**
     * 导入预览
     *
     * @param fileName 文件名
     * @return
     */
    @Operation(summary = "导入预览")
    @SaCheckPermission("permission.user")
    @GetMapping("/ImportPreview")
    public ServiceResult<Map<String, Object>> ImportPreview(String fileName) throws ImportException {
        Map<String, Object> map = new HashMap<>();
        try {
            String filePath = configValueUtil.getTemporaryFilePath();
            @Cleanup InputStream inputStream = new ByteArrayInputStream(FileUploadUtils.downloadFileByte(filePath, fileName, false));
            // 得到数据
            List<UserExportVO> personList = ExcelUtil.importExcelByInputStream(inputStream, 0, 1, UserExportVO.class);
            // 预览数据
            map = userService.importPreview(personList);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ImportException(e.getMessage());
        }
        return ServiceResult.success(map);
    }

    /**
     * 根据角色ID获取所属组织的所有成员
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "根据角色ID获取所有成员")
    @SaCheckPermission("permission.role")
    @GetMapping("/getUsersByRoleId")
    public ServiceResult getUsersByRoleId(PaginationUser pagination) {
        List<SysUserEntity> userList = new ArrayList<>();
        if (roleService.getInfo(pagination.getRoleId()).getGlobalMark() == 1) {
            userList.addAll(userService.getList(pagination, null, false, false, null, null));
        } else {
            // 根据roleId获取所有组织
            userService.getListByRoleId(pagination.getRoleId()).forEach(u -> {
                userList.add(userService.getInfo(u.getId()));
            });
        }
        // 去重
        List<SysUserEntity> afterUserList = userList.stream().distinct().collect(Collectors.toList());
        if (StringUtil.isNotEmpty(pagination.getKeyword())) {
            afterUserList = afterUserList.stream().filter(t -> t.getRealName().contains(pagination.getKeyword()) || t.getAccount().contains(pagination.getKeyword())).collect(Collectors.toList());
        }
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(afterUserList, paginationVO);
    }

    /**
     * 获取默认当前值用户ID
     *
     * @param userConditionModel 参数
     * @return 执行结构
     * @throws DataBaseException ignore
     */
    @Operation(summary = "获取默认当前值用户ID")
    @Parameters({
            @Parameter(name = "userConditionModel", description = "参数", required = true)
    })
    @PostMapping("/getDefaultCurrentValueUserId")
    public ServiceResult<?> getDefaultCurrentValueUserId(@RequestBody UserConditionModel userConditionModel) throws DataBaseException {
        String userId = userService.getDefaultCurrentValueUserId(userConditionModel);
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("userId", userId);
        return ServiceResult.success("查询成功", dataMap);
    }

    /**
     * 工作交接
     *
     * @param workHandoverModel 模型
     * @return 执行结构
     */
    @Operation(summary = "工作交接")
    @SaCheckPermission("permission.user")
    @Parameters({
            @Parameter(name = "workHandoverModel", description = "模型", required = true)
    })
    @PostMapping("/workHandover")
    public ServiceResult<?> workHandover(@RequestBody @Valid WorkHandoverModel workHandoverModel) {
        // 开始交接就禁用用户
        SysUserEntity entity = userService.getInfo(workHandoverModel.getFromId());
        SysUserEntity entitys = userService.getInfo(workHandoverModel.getToId());
        if (entity == null || entitys == null) {
            return ServiceResult.error(MsgCode.FA001.get());
        }
//        if (entity.getIsAdministrator() == 1 || entitys.getIsAdministrator() == 1) {
//            return ServiceResult.error("工作交接无法转移给管理员");
//        }
        try {
            boolean flag = flowTaskApi.flowWork(workHandoverModel);
            if (!flag) {
                return ServiceResult.error("工作交接失败！");
            }
            permissionGroupService.updateByUser(workHandoverModel.getFromId(), workHandoverModel.getToId(), workHandoverModel.getPermissionList());
            entity.setHandoverMark(1);
            return ServiceResult.success("工作交接成功！");
        } finally {
            userService.updateById(entity);
        }
    }

    /**
     * 获取用户工作详情
     *
     * @return 执行结构
     */
    @Operation(summary = "获取用户工作详情")
    @SaCheckPermission("permission.user")
    @Parameters({
            @Parameter(name = "userId", description = "主键", required = true)
    })
    @GetMapping("/getWorkByUser")
    public ServiceResult<FlowWorkListVO> getWorkByUser(@RequestParam("fromId") String fromId) {
        FlowWorkListVO flowWorkListVO = flowTaskApi.flowWork(fromId);
        if (flowWorkListVO == null) {
            log.error("用户：" + UserProvider.getLoginUserId() + "，待办事宜及负责流程获取失败");
            flowWorkListVO = new FlowWorkListVO();
        }
        List<SysPermissionGroupEntity> permissionGroupByUserId = permissionGroupService.getPermissionGroupAllByUserId(fromId);
        List<FlowWorkModel> jsonToList = JsonUtil.createJsonToList(permissionGroupByUserId, FlowWorkModel.class);
        jsonToList.forEach(t -> t.setIcon("icon-linzen icon-linzen-authGroup"));
        flowWorkListVO.setPermission(jsonToList);
        return ServiceResult.success(flowWorkListVO);
    }


    // ----------------------------- 多租户调用
    /**
     * 重置管理员密码
     *
     * @param userResetPasswordForm 修改密码模型
     * @return ignore
     */
    @UserPermission
    @Operation(summary = "重置管理员密码")
    @Parameters({
            @Parameter(name = "userResetPasswordForm", description = "修改密码模型", required = true)
    })
    @PutMapping("/Tenant/ResetPassword")
    @NoDataSourceBind
    public ServiceResult<String> resetPassword(@RequestBody @Valid TenantReSetPasswordForm userResetPasswordForm) {
        if (configValueUtil.isMultiTenancy()) {
            TenantDataSourceUtil.switchTenant(userResetPasswordForm.getTenantId());
        }
        SysUserEntity entity = userService.getUserByAccount("admin");
        if (entity != null) {
            entity.setPassword(userResetPasswordForm.getUserPassword());
            userService.updatePassword(entity);
            userService.delCurUser("密码已变更，请重新登录！", entity.getId());
            PullUserUtil.syncUser(entity, "modifyPassword", userResetPasswordForm.getTenantId());
            return ServiceResult.success(MsgCode.SU005.get());
        }
        return ServiceResult.error("操作失败,用户不存在");
    }

    /**
     * 获取用户信息
     *
     * @param tenantId 租户号
     * @return ignore
     */
    @Operation(summary = "获取用户信息")
    @Parameters({
            @Parameter(name = "tenantId", description = "租户号", required = true)
    })
    @NoDataSourceBind
    @GetMapping("/Tenant/AdminInfo")
    public AdminInfoVO adminInfo(@RequestParam("tenantId") String tenantId) throws DataBaseException {
        if (configValueUtil.isMultiTenancy()) {
            TenantDataSourceUtil.switchTenant(tenantId);
        }
        SysUserEntity entity = userService.getUserByAccount("admin");
        AdminInfoVO adminInfoVO = BeanUtil.toBean(entity, AdminInfoVO.class);
        return adminInfoVO;
    }

    /**
     * 修改管理员信息
     *
     * @param adminInfoVO 模型
     * @return ignore
     */
    @Operation(summary = "修改管理员信息")
    @Parameters({
            @Parameter(name = "adminInfoVO", description = "模型", required = true)
    })
    @NoDataSourceBind
    @PutMapping("/Tenant/UpdateAdminInfo")
    public ServiceResult adminInfo(@RequestBody AdminInfoVO adminInfoVO) throws DataBaseException {
        if (configValueUtil.isMultiTenancy()) {
            TenantDataSourceUtil.switchTenant(adminInfoVO.getTenantId());
        }
        SysUserEntity entity = userService.getUserByAccount("admin");
        if (entity == null) {
            return ServiceResult.error("操作失败,用户不存在");
        }
        entity.setRealName(adminInfoVO.getRealName());
        entity.setMobilePhone(adminInfoVO.getMobilePhone());
        entity.setEmail(adminInfoVO.getEmail());
        userService.updateById(entity);
        threadPoolExecutor.execute(() -> {
            try {
                //修改用户之后判断是否需要同步到企业微信
                synThirdQyService.updateUserSysToQy(false, entity, "");
                //修改用户之后判断是否需要同步到钉钉
                synThirdDingTalkService.updateUserSysToDing(false, entity, "");
            } catch (Exception e) {
                log.error("修改用户之后同步失败到企业微信或钉钉失败，异常：" + e.getMessage());
            }
        });
        // 删除在线的用户
        PullUserUtil.syncUser(entity, "update", adminInfoVO.getTenantId());
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 移除租户账号在线用户
     *
     * @param tenantId 租户号
     * @return ignore
     */
    @Operation(summary = "移除租户账号在线用户")
    @Parameters({
            @Parameter(name = "tenantId", description = "租户号", required = true)
    })
    @NoDataSourceBind
    @GetMapping("/Tenant/RemoveOnlineByTenantId")
    public void removeOnlineByTenantId(@RequestParam("tenantId") String tenantId) throws DataBaseException {
        List<String> tokenList = new ArrayList<>();
        List<String> tokens = UserProvider.getLoginUserListToken();
        tokens.forEach(token -> {
            UserInfo userInfo = UserProvider.getUser(token);
            if (tenantId.equals(userInfo.getTenantId())) {
                tokenList.add(token);
            }
        });
        authService.kickoutByToken(tokenList.toArray(new String[0]));
    }

}
