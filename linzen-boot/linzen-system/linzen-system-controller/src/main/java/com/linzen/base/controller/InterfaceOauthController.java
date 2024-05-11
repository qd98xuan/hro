package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.DataInterfaceEntity;
import com.linzen.base.entity.DataInterfaceLogEntity;
import com.linzen.base.entity.DataInterfaceUserEntity;
import com.linzen.base.entity.InterfaceOauthEntity;
import com.linzen.base.model.InterfaceOauth.*;
import com.linzen.base.model.datainterface.DataInterfaceVo;
import com.linzen.base.service.DataInterfaceLogService;
import com.linzen.base.service.DataInterfaceService;
import com.linzen.base.service.DataInterfaceUserService;
import com.linzen.base.service.InterfaceOauthService;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.constant.MsgCode;
import com.linzen.exception.DataBaseException;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.util.JsonUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * 接口认证控制器
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "接口认证", description = "interfaceoauth")
@RestController
@RequestMapping(value = "/api/system/InterfaceOauth")
public class InterfaceOauthController extends SuperController<InterfaceOauthService, InterfaceOauthEntity> {
    @Autowired
    private DataInterfaceService dataInterfaceService;
    @Autowired
    private DataInterfaceLogService dataInterfaceLogService;

    @Autowired
    private InterfaceOauthService interfaceOauthService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private DataInterfaceUserService dataInterfaceUserService;


    /**
     * 获取接口认证列表(分页)
     *
     * @param pagination 分页参数
     * @return ignore
     */
    @Operation(summary = "获取接口认证列表(分页)")
    @SaCheckPermission("systemData.interfaceAuth")
    @GetMapping
    public ServiceResult<PageListVO<InterfaceIdentListVo>> getList(PaginationOauth pagination) {
        List<InterfaceOauthEntity> data = interfaceOauthService.getList(pagination);
        List<InterfaceIdentListVo> jsonToList = JsonUtil.createJsonToList(data, InterfaceIdentListVo.class);
        jsonToList.forEach(item -> {
            if (StringUtil.isNotEmpty(userProvider.get().getTenantId())) {
                item.setTenantId(userProvider.get().getTenantId());
            }
            if (item.getCreatorUserId() != null) {
                item.setCreatorUser(userService.getInfo(item.getCreatorUserId()).getRealName());
            }
        });
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(jsonToList, paginationVO);
    }

    /**
     * 添加接口认证
     *
     * @param interfaceIdentForm 添加接口认证模型
     * @return ignore
     */
    @Operation(summary = "添加接口认证")
    @Parameter(name = "interfaceIdentForm", description = "添加接口认证模型", required = true)
    @SaCheckPermission("systemData.interfaceAuth")
    @PostMapping
    public ServiceResult create(@RequestBody @Valid InterfaceIdentForm interfaceIdentForm) {
        InterfaceOauthEntity entity = BeanUtil.toBean(interfaceIdentForm, InterfaceOauthEntity.class);
        if (interfaceOauthService.isExistByAppName(entity.getAppName(), entity.getId())) {
            return ServiceResult.error(MsgCode.EXIST001.get());
        }
        if (interfaceOauthService.isExistByAppId(entity.getAppId(), entity.getId())) {
            return ServiceResult.error("内容不能重复");
        }
        interfaceOauthService.create(entity);


        return ServiceResult.success("接口认证创建成功");
    }


    /**
     * 修改接口认证
     *
     * @param interfaceIdentForm 添加接口认证模型
     * @return ignore
     */
    @Operation(summary = "修改接口认证")
    @Parameters({
            @Parameter(name = "interfaceIdentForm", description = "添加接口认证模型", required = true),
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("systemData.interfaceAuth")
    @PutMapping("/{id}")
    public ServiceResult update(@RequestBody @Valid InterfaceIdentForm interfaceIdentForm, @PathVariable("id") String id) throws DataBaseException {
        InterfaceOauthEntity entity = BeanUtil.toBean(interfaceIdentForm, InterfaceOauthEntity.class);
        if (interfaceOauthService.isExistByAppName(entity.getAppName(), id)) {
            return ServiceResult.error(MsgCode.EXIST001.get());
        }
        if (interfaceOauthService.isExistByAppId(entity.getAppId(), id)) {
            return ServiceResult.error("内容不能重复");
        }
        boolean flag = interfaceOauthService.update(entity, id);
        if (flag == false) {
            return ServiceResult.error(MsgCode.FA002.get());
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 删除接口认证
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "删除接口")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("systemData.interfaceAuth")
    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable String id) {
        InterfaceOauthEntity entity = interfaceOauthService.getInfo(id);
        if (entity != null) {
            interfaceOauthService.delete(entity);
            return ServiceResult.success(MsgCode.SU003.get());
        }
        return ServiceResult.error(MsgCode.FA003.get());
    }

    /**
     * 获取秘钥
     *
     * @return
     */
    @Operation(summary = "获取接口认证密钥")
    @SaCheckPermission("systemData.interfaceAuth")
    @GetMapping("/getAppSecret")
    public ServiceResult getAppSecret() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return ServiceResult.success("获取成功", uuid);
    }


    /**
     * 保存綁定认证接口
     *
     * @return
     */
    @Operation(summary = "保存綁定认证接口")
    @Parameters({
            @Parameter(name = "identInterfaceListModel", description = "授权接口列表模型", required = true)
    })
    @SaCheckPermission("systemData.interfaceAuth")
    @PostMapping("/saveInterfaceList")
    public ServiceResult getInterfaceList(@RequestBody IdentInterfaceListModel identInterfaceListModel) {
        InterfaceOauthEntity entity = new InterfaceOauthEntity();
        entity.setId(identInterfaceListModel.getInterfaceIdentId());
        entity.setDataInterfaceIds(identInterfaceListModel.getDataInterfaceIds());
        boolean b = interfaceOauthService.updateById(entity);
        if (b) {
            return ServiceResult.success(MsgCode.SU002.get());
        }
        return ServiceResult.success(MsgCode.FA101.get());
    }

    /**
     * 获取接口授权绑定接口列表
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "获取认证基础信息及接口列表")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("systemData.interfaceAuth")
    @GetMapping("/{id}")
    public ServiceResult getInterfaceList(@PathVariable("id") String id) {
        InterfaceOauthEntity entity = interfaceOauthService.getInfo(id);
        InterfaceIdentVo bean = BeanUtil.toBean(entity, InterfaceIdentVo.class);
        if (StringUtils.isNotEmpty(bean.getDataInterfaceIds())) {
            List<DataInterfaceVo> listDataInterfaceVo = new ArrayList<>();
            List<DataInterfaceEntity> list = dataInterfaceService.getList(false);
            list.forEach(item -> {
                if (bean.getDataInterfaceIds().contains(item.getId())) {
                    DataInterfaceVo dataInterfaceVo = BeanUtil.toBean(item, DataInterfaceVo.class);
                    listDataInterfaceVo.add(dataInterfaceVo);
                }
            });
            bean.setList(listDataInterfaceVo);
        }

        //添加授权用户信息
        List<InterfaceUserVo> listIuv =new ArrayList<>();
        List<DataInterfaceUserEntity> select = dataInterfaceUserService.select(id);
        for(DataInterfaceUserEntity diue:select){
            String userId = diue.getUserId();
            SysUserEntity info = userService.getInfo(userId);
            InterfaceUserVo iuv=new InterfaceUserVo();
            iuv.setUserId(userId);
            iuv.setUserKey(diue.getUserKey());
            iuv.setUserName(info.getRealName()+"/"+info.getAccount());
            listIuv.add(iuv);
        }
        bean.setUserList(listIuv);
        return ServiceResult.success("获取成功", bean);
    }

    /**
     * 获取日志列表
     *
     * @param id 主键
     * @param paginationIntrfaceLog 分页参数
     * @return
     */
    @Operation(summary = "获取日志列表")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("systemData.interfaceAuth")
    @GetMapping("/dataInterfaceLog/{id}")
    public ServiceResult<PageListVO<IdentDataInterfaceLogVO>> getInterfaceList(@PathVariable("id") String id,PaginationIntrfaceLog paginationIntrfaceLog) {
        InterfaceOauthEntity entity = interfaceOauthService.getInfo(id);
        List<IdentDataInterfaceLogVO> voList = null;
        PaginationVO vo = null;
        if (entity!=null&&StringUtils.isNotEmpty(entity.getDataInterfaceIds())) {
            String dataInterfaceIds = entity.getDataInterfaceIds();
            String[] split = dataInterfaceIds.split(",");
            List<String> list = Arrays.asList(split);
            List<DataInterfaceLogEntity> listByIds = dataInterfaceLogService.getListByIds(entity.getAppId(),list, paginationIntrfaceLog);
            voList = JsonUtil.createJsonToList(listByIds, IdentDataInterfaceLogVO.class);
            List<DataInterfaceEntity> listDataInt = dataInterfaceService.getList(false);
            for (IdentDataInterfaceLogVO invo : voList) {
                if (StringUtil.isNotEmpty(userProvider.get().getTenantId())) {
                    invo.setTenantId(userProvider.get().getTenantId());
                }
                //绑定用户
                SysUserEntity userEntity = userService.getInfo(invo.getUserId());
                if (userEntity != null) {
                    invo.setUserId(userEntity.getRealName() + "/" + userEntity.getAccount());
                }
                //绑定接口基础数据
                listDataInt.forEach(item -> {
                    if (invo.getInvokId().contains(item.getId())) {
                        DataInterfaceVo dataInterfaceVo = BeanUtil.toBean(item, DataInterfaceVo.class);
                        invo.setFullName(dataInterfaceVo.getFullName());
                        invo.setEnCode(dataInterfaceVo.getEnCode());
                    }
                });
            }
            vo = BeanUtil.toBean(paginationIntrfaceLog, PaginationVO.class);

        }
        return ServiceResult.pageList(voList, vo);
    }


    @Operation(summary = "授权用户")
    @SaCheckPermission("systemData.interfaceAuth")
    @PostMapping("/SaveUserList")
    public ServiceResult saveUserList(@RequestBody InterfaceUserForm interfaceUserForm) {
        dataInterfaceUserService.saveUserList(interfaceUserForm);
        return ServiceResult.success(MsgCode.SU002.get());
    }

}
