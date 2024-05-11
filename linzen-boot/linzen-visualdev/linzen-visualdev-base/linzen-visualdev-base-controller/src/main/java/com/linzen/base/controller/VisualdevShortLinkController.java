package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.base.entity.VisualdevEntity;
import com.linzen.base.entity.VisualdevReleaseEntity;
import com.linzen.base.entity.VisualdevShortLinkEntity;
import com.linzen.base.model.ColumnDataModel;
import com.linzen.base.model.VisualDevJsonModel;
import com.linzen.base.model.VisualWebTypeEnum;
import com.linzen.base.model.shortLink.*;
import com.linzen.base.service.DbLinkService;
import com.linzen.base.service.VisualdevReleaseService;
import com.linzen.base.service.VisualdevService;
import com.linzen.base.service.VisualdevShortLinkService;
import com.linzen.base.util.VisualUtil;
import com.linzen.base.vo.PaginationVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.config.OauthConfigration;
import com.linzen.constant.MsgCode;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.enums.DeviceType;
import com.linzen.exception.DataBaseException;
import com.linzen.exception.LoginException;
import com.linzen.exception.WorkFlowException;
import com.linzen.onlinedev.model.*;
import com.linzen.onlinedev.service.VisualDevInfoService;
import com.linzen.onlinedev.service.VisualDevListService;
import com.linzen.onlinedev.service.VisualdevModelDataService;
import com.linzen.onlinedev.util.onlineDevUtil.OnlinePublicUtils;
import com.linzen.onlinedev.util.onlineDevUtil.OnlineSwapDataUtils;
import com.linzen.service.FormDataService;
import com.linzen.util.*;
import com.linzen.util.context.RequestContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 在线开发表单外链Controller
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "表单外链" , description = "BaseShortLink" )
@RestController
@RequestMapping("/api/visualdev/ShortLink" )
public class VisualdevShortLinkController extends SuperController<VisualdevShortLinkService, VisualdevShortLinkEntity> {

    @Autowired
    private VisualdevShortLinkService visualdevShortLinkService;

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private OauthConfigration oauthConfig;

    @Autowired
    private ConfigValueUtil configValueUtil;

    @Autowired
    protected AuthUtil authUtil;

    @Autowired
    private VisualdevService visualdevService;
    @Autowired
    private VisualdevReleaseService visualdevReleaseService;
    @Autowired
    private FormDataService formDataService;

    @Autowired
    private DbLinkService dblinkService;
    @Autowired
    private FormCheckUtils formCheckUtils;
    @Autowired
    private VisualdevModelDataService visualdevModelDataService;
    @Autowired
    private OnlineSwapDataUtils onlineSwapDataUtils;
    @Autowired
    private VisualDevListService visualDevListService;
    @Autowired
    private VisualDevInfoService visualDevInfoService;

    @Operation(summary = "获取外链信息" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" ),
    })
    @GetMapping("/{id}" )
    @SaCheckPermission("onlineDev.webDesign" )
    public ServiceResult getInfo(@PathVariable("id" ) String id) {
        VisualdevShortLinkEntity info = visualdevShortLinkService.getById(id);
        VisualdevShortLinkVo vo;
        if (info != null) {
            vo = BeanUtil.toBean(info, VisualdevShortLinkVo.class);
            vo.setAlreadySave(true);
        } else {
            vo = new VisualdevShortLinkVo();
            vo.setId(id);
        }
        vo.setFormLink(geturl(id, "form" ));
        vo.setColumnLink(geturl(id, "list" ));
        return ServiceResult.success(vo);
    }

    /**
     * 获取url
     *
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    private String geturl(String id, String type) {
        String url = oauthConfig.getLinzenDomain() + "/api/visualdev/ShortLink/trigger/" + id + "?encryption=";
        UserInfo userInfo = userProvider.get();
        JSONObject obj = new JSONObject();
        obj.put("type" , type);
        if (configValueUtil.isMultiTenancy()) {
            obj.put("tenantId" , userInfo.getTenantId());
        }
        //参数加密
        String encryption = DesUtil.aesOrDecode(obj.toJSONString(), true,true);
        url += encryption;
        return url;
    }


    @Operation(summary = "修改外链信息" )
    @PutMapping("" )
    @SaCheckPermission("onlineDev.webDesign" )
    public ServiceResult saveOrupdate(@RequestBody VisualdevShortLinkForm data) {
        VisualdevShortLinkEntity entity = JsonUtil.createJsonToBean(data, VisualdevShortLinkEntity.class);
        if(entity.getFormLink().contains(oauthConfig.getLinzenDomain())){
            entity.setFormLink(entity.getFormLink().replace(oauthConfig.getLinzenDomain(),""));
        }
        if(entity.getColumnLink().contains(oauthConfig.getLinzenDomain())){
            entity.setColumnLink(entity.getColumnLink().replace(oauthConfig.getLinzenDomain(),""));
        }
        VisualdevShortLinkEntity info = visualdevShortLinkService.getById(data.getId());
        UserInfo userInfo = userProvider.get();
        if (info != null) {
            entity.setUpdateTime(new Date());
            entity.setUpdateUserId(userInfo.getUserId());
        } else {
            entity.setCreatorTime(new Date());
            entity.setCreatorUserId(userInfo.getUserId());
        }

        String pcLink = "/formShortLink";
        String appLink ="/pages/formShortLink/index";
        entity.setRealPcLink(pcLink);
        entity.setRealAppLink(appLink);
        entity.setUserId(userInfo.getUserId());
        visualdevShortLinkService.saveOrUpdate(entity);
        return ServiceResult.success(MsgCode.SU002.get());
    }

    /**
     * 参数解密切换数据源
     *
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    private VisualdevShortLinkModel aesDecodeMatchDatabase(String encryption) throws LoginException {
        //参数解密
        String str = DesUtil.aesOrDecode(encryption, false,true);
        if (StringUtil.isEmpty(str)) {
            throw new LoginException("参数解析错误!" );
        }
        VisualdevShortLinkModel model = JsonUtil.createJsonToBean(str, VisualdevShortLinkModel.class);
        if (configValueUtil.isMultiTenancy()) {
            if (StringUtil.isNotEmpty(model.getTenantId())) {
                //切换成租户库
                TenantDataSourceUtil.switchTenant(model.getTenantId());
            } else {
                throw new LoginException("缺少租户信息!" );
            }
        }
        return model;
    }

    @NoDataSourceBind
    @Operation(summary = "外链请求入口" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" ),
    })
    @GetMapping("/trigger/{id}" )
    public ServiceResult getlink(@PathVariable("id" ) String id,
                                @RequestParam(value = "encryption" ) String encryption,
                                HttpServletResponse response) throws LoginException, IOException {
        VisualdevShortLinkModel model = aesDecodeMatchDatabase(encryption);
        String link = "";
        VisualdevShortLinkEntity entity = visualdevShortLinkService.getById(id);
        DeviceType deviceType = UserProvider.getDeviceForAgent();
        if (entity != null) {
            if (DeviceType.PC.equals(deviceType)) {
                link = oauthConfig.getLinzenFrontDomain() + entity.getRealPcLink();
            } else {
                link =  oauthConfig.getLinzenAppDomain() + entity.getRealAppLink();
            }
        } else {
            return ServiceResult.error("无效链接" );
        }
        JSONObject obj = new JSONObject();
        obj.put("modelId" , id);
        obj.put("type" , model.getType());
        if (configValueUtil.isMultiTenancy()) {
            obj.put("tenantId" , model.getTenantId());
        }
        //新链接参数加密
        String encryptionNew = DesUtil.aesOrDecode(obj.toJSONString(), true,true);
        link += "?encryption=" + encryptionNew;
//        link += "&modelId=" + id;
        response.sendRedirect(link);
        return ServiceResult.success(MsgCode.SU000.get());
    }

    @NoDataSourceBind
    @Operation(summary = "获取外链配置" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" ),
    })
    @GetMapping("/getConfig/{id}" )
    public ServiceResult getConfig(@PathVariable("id" ) String id, @RequestParam("encryption" ) String encryption) throws LoginException {
        aesDecodeMatchDatabase(encryption);

        VisualdevShortLinkEntity info = visualdevShortLinkService.getById(id);
        VisualdevShortLinkConfigVo vo = BeanUtil.toBean(info, VisualdevShortLinkConfigVo.class);
        vo.setFormLink(geturl(id, "form" ));
        vo.setColumnLink(geturl(id, "list" ));
        return ServiceResult.success(vo);
    }

    @NoDataSourceBind
    @Operation(summary = "密码验证" )
    @PostMapping("/checkPwd" )
    public ServiceResult checkPwd(@RequestBody VisualdevShortLinkPwd form) throws LoginException {
        //参数解密
        VisualdevShortLinkModel model = aesDecodeMatchDatabase(form.getEncryption());

        VisualdevShortLinkEntity info = visualdevShortLinkService.getById(form.getId());
        boolean flag = false;
        if (OnlineDevData.STATE_ENABLE.equals(info.getFormPassUse()) && 0 == form.getType()) {
            if (Md5Util.getStringMd5(info.getFormPassword()).equals(form.getPassword())) {
                flag = true;
            }
        } else if (OnlineDevData.STATE_ENABLE.equals(info.getColumnPassUse()) && 1 == form.getType()) {
            if (Md5Util.getStringMd5(info.getColumnPassword()).equals(form.getPassword())) {
                flag = true;
            }
        }
        if (flag) {
            return ServiceResult.success();
        }
        return ServiceResult.error("密码错误！" );
    }

    @NoDataSourceBind
    @Operation(summary = "获取列表表单配置JSON" )
    @GetMapping("/{modelId}/Config" )
    public ServiceResult getData(@PathVariable("modelId" ) String modelId, @RequestParam(value = "type" , required = false) String type,
                                @RequestParam("encryption" ) String encryption) throws WorkFlowException, LoginException {
        aesDecodeMatchDatabase(encryption);
        VisualdevEntity entity;
        //线上版本
        if ("0".equals(type)) {
            entity = visualdevService.getInfo(modelId);
        } else {
            VisualdevReleaseEntity releaseEntity = visualdevReleaseService.getById(modelId);
            entity = BeanUtil.toBean(releaseEntity, VisualdevEntity.class);
        }
        if (entity == null) {
            return ServiceResult.error("未找到该功能表单" );
        }

        String s = VisualUtil.checkPublishVisualModel(entity, "预览" );
        if (s != null) {
            return ServiceResult.error(s);
        }
        DataInfoVO vo = BeanUtil.toBean(entity, DataInfoVO.class);
        return ServiceResult.success(vo);
    }

    @NoDataSourceBind
    @Operation(summary = "外链数据列表" )
    @Parameters({
            @Parameter(name = "modelId" , description = "模板id" ),
    })
    @PostMapping("/{modelId}/ListLink" )
    public ServiceResult ListLink(@PathVariable("modelId" ) String modelId, @RequestParam("encryption" ) String encryption,
                                 @RequestBody PaginationModel paginationModel) throws WorkFlowException, LoginException {
        aesDecodeMatchDatabase(encryption);

        VisualdevReleaseEntity visualdevEntity = visualdevReleaseService.getById(modelId);
        VisualDevJsonModel visualJsonModel = OnlinePublicUtils.getVisualJsonModel(visualdevEntity);
        //判断请求客户端来源
        if (!RequestContext.isOrignPc()) {
            visualJsonModel.setColumnData(visualJsonModel.getAppColumnData());
        }
        List<Map<String, Object>> realList;
        if (VisualWebTypeEnum.DATA_VIEW.getType().equals(visualdevEntity.getWebType())) {//
            //数据视图的接口数据获取、
            ColumnDataModel columnDataModel = JsonUtil.createJsonToBean(visualdevEntity.getColumnData(), ColumnDataModel.class);
            realList = onlineSwapDataUtils.getInterfaceData(visualdevEntity, paginationModel, columnDataModel);
        } else {
            realList = visualDevListService.getDataListLink(visualJsonModel, paginationModel);
        }
        PaginationVO paginationVO = BeanUtil.toBean(paginationModel, PaginationVO.class);
        return ServiceResult.pageList(realList, paginationVO);
    }

    @NoDataSourceBind
    @Operation(summary = "获取数据信息(带转换数据)" )
    @Parameters({
            @Parameter(name = "modelId" , description = "模板id" ),
            @Parameter(name = "id" , description = "数据id" ),
    })
    @GetMapping("/{modelId}/{id}/DataChange" )
    public ServiceResult infoWithDataChange(@PathVariable("modelId" ) String modelId, @PathVariable("id" ) String id,
                                           @RequestParam("encryption" ) String encryption) throws DataBaseException, ParseException, IOException, SQLException, LoginException {
        aesDecodeMatchDatabase(encryption);

        modelId = XSSEscape.escape(modelId);
        id = XSSEscape.escape(id);
        VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);
        //有表
        if (!StringUtil.isEmpty(visualdevEntity.getVisualTables()) && !OnlineDevData.TABLE_CONST.equals(visualdevEntity.getVisualTables())) {
            VisualdevModelDataInfoVO vo = visualDevInfoService.getDetailsDataInfo(id, visualdevEntity);
            return ServiceResult.success(vo);
        }
        //无表
        VisualdevModelDataInfoVO vo = visualdevModelDataService.infoDataChange(id, visualdevEntity);
        return ServiceResult.success(vo);
    }

    //**********以下微服务和单体不同
    @NoDataSourceBind
    @Operation(summary = "添加数据" )
    @Parameters({
            @Parameter(name = "modelId" , description = "模板id" ),
            @Parameter(name = "visualdevModelDataCrForm" , description = "功能数据创建表单" ),
    })
    @PostMapping("/{modelId}" )
    public ServiceResult create(@PathVariable("modelId" ) String modelId, @RequestParam("encryption" ) String encryption,
                               @RequestBody VisualdevModelDataCrForm visualdevModelDataCrForm) throws WorkFlowException, LoginException {
        VisualdevShortLinkModel visualdevShortLinkModel = aesDecodeMatchDatabase(encryption);
        VisualdevShortLinkEntity info = visualdevShortLinkService.getById(modelId);
        if (1 != info.getFormUse()) {
            return ServiceResult.error("未开启表单外链！" );
        }
        String tenantId=visualdevShortLinkModel.getTenantId();
        try {
            if (configValueUtil.isMultiTenancy()) {
                if (StringUtil.isNotEmpty(tenantId)) {
                    //切换成租户库
                    TenantDataSourceUtil.switchTenant(tenantId);
                } else {
                    return ServiceResult.error("缺少租户信息" );
                }
            }
            VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);
            Map<String, Object> map = JsonUtil.stringToMap(visualdevModelDataCrForm.getData());
            visualdevModelDataService.visualCreate(visualdevEntity, map, true);
        }catch (Exception e){
            throw new WorkFlowException(e.getMessage());
        }
        return ServiceResult.success(MsgCode.SU001.get());
    }
}
