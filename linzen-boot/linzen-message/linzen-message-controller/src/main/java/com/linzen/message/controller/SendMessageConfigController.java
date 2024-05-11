


package com.linzen.message.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.base.controller.SuperController;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.entity.SysConfigEntity;
import com.linzen.base.service.BillRuleService;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.service.SysconfigService;
import com.linzen.base.util.TestSendConfigUtil;
import com.linzen.base.vo.DownloadVO;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.MsgCode;
import com.linzen.emnus.ModuleTypeEnum;
import com.linzen.exception.DataBaseException;
import com.linzen.message.entity.AccountConfigEntity;
import com.linzen.message.entity.MessageTemplateConfigEntity;
import com.linzen.message.entity.SendConfigTemplateEntity;
import com.linzen.message.entity.SendMessageConfigEntity;
import com.linzen.message.model.message.DingTalkModel;
import com.linzen.message.model.messagetemplateconfig.TemplateParamModel;
import com.linzen.message.model.sendmessageconfig.*;
import com.linzen.message.service.AccountConfigService;
import com.linzen.message.service.MessageTemplateConfigService;
import com.linzen.message.service.SendConfigTemplateService;
import com.linzen.message.service.SendMessageConfigService;
import com.linzen.model.BaseSystemInfo;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.util.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 消息发送配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息
 * @date 2023-04-01
 */
@Slf4j
@RestController
@Tag(name = "消息发送配置", description = "message")
@RequestMapping("/api/message/SendMessageConfig")
public class SendMessageConfigController extends SuperController<SendMessageConfigService, SendMessageConfigEntity> {
    @Autowired
    private FileExport fileExport;

    @Autowired
    private BillRuleService billRuleService;

    @Autowired
    private ConfigValueUtil configValueUtil;

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private UserService userService;


    @Autowired
    private SysconfigService sysconfigService;
    @Autowired
    private SendMessageConfigService sendMessageConfigService;

    @Autowired
    private SendConfigTemplateService sendConfigTemplateService;

    @Autowired
    private AccountConfigService accountConfigService;

    @Autowired
    private MessageTemplateConfigService messageTemplateConfigService;
    @Autowired
    private DictionaryDataService dictionaryDataService;

    @Autowired
    private TestSendConfigUtil testSendConfigUtil;

    /**
     * 列表
     *
     * @param sendMessageConfigPagination 分页模型
     * @return
     */
    @Operation(summary = "消息发送配置列表")
    @SaCheckPermission("msgCenter.sendConfig")
    @GetMapping
    public ServiceResult<PageListVO<SendMessageConfigListVO>> list(SendMessageConfigPagination sendMessageConfigPagination) throws IOException {
        // 消息渠道分类
        List<DictionaryDataEntity> msgSendTypeList = dictionaryDataService.getListByTypeDataCode("msgSendType");
        // 消息来源分类
        List<DictionaryDataEntity> msgSourceTypeList = dictionaryDataService.getListByTypeDataCode("msgSourceType");
        // 消息来源分类
        List<SendMessageConfigEntity> list = sendMessageConfigService.getList(sendMessageConfigPagination, null);
        //处理id字段转名称，若无需转或者为空可删除
        SysUserEntity userEntity = new SysUserEntity();
        List<SendMessageConfigListVO> listVO = JsonUtil.createJsonToList(list, SendMessageConfigListVO.class);
        for (SendMessageConfigListVO sendMessageConfigVO : listVO) {
            List<Map<String, String>> mapList = new ArrayList<>();
            //子表数据转换
            List<SendConfigTemplateEntity> sendConfigTemplateList = sendConfigTemplateService.getDetailListByParentId(sendMessageConfigVO.getId());
            if (sendConfigTemplateList != null && sendConfigTemplateList.size() > 0) {
                sendConfigTemplateList = sendConfigTemplateList.stream().sorted((a, b) -> a.getMessageType().compareTo(b.getMessageType())).collect(Collectors.toList());
                List<String> typeList = sendConfigTemplateList.stream().map(t -> t.getMessageType()).distinct().collect(Collectors.toList());
                if (typeList != null && typeList.size() > 0) {
                    for (String type : typeList) {
                        String messageType = "";
                        Map<String, String> map = new HashMap<>();
                        DictionaryDataEntity dataTypeEntity = msgSendTypeList.stream().filter(t -> t.getEnCode().equals(type)).findFirst().orElse(null);
                        if (dataTypeEntity != null) {
                            messageType = dataTypeEntity.getFullName();
                            map.put("fullName", messageType);
                            map.put("type", type);
                            mapList.add(map);
                        }
                    }
                    sendMessageConfigVO.setMessageType(mapList);
                }
            }
            if (StringUtil.isNotEmpty(sendMessageConfigVO.getCreatorUserId())) {
                userEntity = userService.getInfo(sendMessageConfigVO.getCreatorUserId());
                if (userEntity != null) {
                    sendMessageConfigVO.setCreatorUser(userEntity.getRealName() + "/" + userEntity.getAccount());
                }
            }
            //消息来源
            if (StringUtil.isNotBlank(sendMessageConfigVO.getMessageSource())) {
                msgSourceTypeList.stream().filter(t -> sendMessageConfigVO.getMessageSource().equals(t.getEnCode())).findFirst()
                        .ifPresent(dataTypeEntity -> sendMessageConfigVO.setMessageSource(dataTypeEntity.getFullName()));
            }
        }

        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        PaginationVO page = BeanUtil.toBean(sendMessageConfigPagination, PaginationVO.class);
        vo.setPagination(page);
        return ServiceResult.success(vo);
    }

    /**
     * 获取发送配置下拉框
     *
     * @return
     */
    @Operation(summary = "获取发送配置下拉框")
    @GetMapping("/Selector")
    public ServiceResult<PageListVO<SendMessageConfigListVO>> selector(SendMessageConfigPagination sendMessageConfigPagination) {
        List<SendMessageConfigEntity> list = sendMessageConfigService.getSelectorList(sendMessageConfigPagination);
        List<SendMessageConfigListVO> listVO = JsonUtil.createJsonToList(list, SendMessageConfigListVO.class);
        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        PaginationVO page = BeanUtil.toBean(sendMessageConfigPagination, PaginationVO.class);
        vo.setPagination(page);
        return ServiceResult.success(vo);
    }

    /**
     * 消息发送配置弹窗列表
     *
     * @param sendMessageConfigPagination 分页模型
     * @return
     */
    @Operation(summary = "消息发送配置弹窗列表")
    @GetMapping("/getSendConfigList")
    public ServiceResult<PageListVO<SendConfigListVO>> getSendConfigList(SendMessageConfigPagination sendMessageConfigPagination) throws IOException {
        if (StringUtil.isBlank(sendMessageConfigPagination.getEnabledMark())) {
            sendMessageConfigPagination.setEnabledMark("1");
        }
        if (StringUtil.isBlank(sendMessageConfigPagination.getTemplateType())) {
            sendMessageConfigPagination.setTemplateType("0");
        }
        List<SendMessageConfigEntity> list = sendMessageConfigService.getList(sendMessageConfigPagination, null);
        //处理id字段转名称，若无需转或者为空可删除
        List<DictionaryDataEntity> msgSendTypeList = dictionaryDataService.getListByTypeDataCode("msgSendType");
        List<SendConfigListVO> listVO = JsonUtil.createJsonToList(list, SendConfigListVO.class);
        for (SendConfigListVO sendConfigVO : listVO) {
            //子表数据转换
            List<SendConfigTemplateEntity> sendConfigTemplateList = sendConfigTemplateService.getDetailListByParentId(sendConfigVO.getId());
            sendConfigTemplateList = sendConfigTemplateList.stream().filter(t -> "1".equals(String.valueOf(t.getEnabledMark()))).collect(Collectors.toList());
            List<SendConfigTemplateModel> modelList = JsonUtil.createJsonToList(sendConfigTemplateList, SendConfigTemplateModel.class);
            for (SendConfigTemplateModel model : modelList) {
                if (modelList != null && modelList.size() > 0) {
                    List<TemplateParamModel> list1 = messageTemplateConfigService.getParamJson(model.getTemplateId());
//                    if (list != null && list.size() > 0) {
//                        model.setParamJson(JsonUtil.createObjectToString(list1));
//                    }
                    List<MsgTemplateJsonModel> jsonModels = new ArrayList<>();
                    for (TemplateParamModel paramModel : list1) {
                        MsgTemplateJsonModel jsonModel = new MsgTemplateJsonModel();
                        jsonModel.setField(paramModel.getField());
                        jsonModel.setFieldName(paramModel.getFieldName());
                        jsonModel.setMsgTemplateId(model.getId());
                        jsonModels.add(jsonModel);
                    }
                    model.setParamJson(jsonModels);
                    MessageTemplateConfigEntity msgTemEntity = messageTemplateConfigService.getInfo(model.getTemplateId());
                    if (msgTemEntity != null) {
                        model.setMsgTemplateName(msgTemEntity.getFullName());
                    }
                    if (StringUtil.isNotEmpty(model.getMessageType())) {
                        msgSendTypeList.stream().filter(t -> model.getMessageType().equals(t.getEnCode())).findFirst()
                                .ifPresent(dataTypeEntity -> model.setMessageType(dataTypeEntity.getFullName()));
                    }
                }
                sendConfigVO.setTemplateJson(modelList);
            }
        }

        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        PaginationVO page = BeanUtil.toBean(sendMessageConfigPagination, PaginationVO.class);
        vo.setPagination(page);
        return ServiceResult.success(vo);
    }

    /**
     * 创建
     *
     * @param sendMessageConfigForm 发送消息配置模型
     * @return
     */
    @Operation(summary = "创建")
    @Parameters({
            @Parameter(name = "sendMessageConfigForm", description = "发送消息配置模型", required = true)
    })
    @SaCheckPermission("msgCenter.sendConfig")
    @PostMapping
    @Transactional
    public ServiceResult create(@RequestBody @Valid SendMessageConfigForm sendMessageConfigForm) throws DataBaseException {
        boolean b = sendMessageConfigService.checkForm(sendMessageConfigForm, 0, "");
        if (b) {
            return ServiceResult.error("编码不能重复");
        }
        if (!"1".equals(sendMessageConfigForm.getTemplateType())) {
            if (sendMessageConfigForm.getEnCode().contains("PZXT")) {
                return ServiceResult.error("自定义模板编码不能使用系统模板编码规则");
            }
        }
        String mainId = RandomUtil.uuId();
        UserInfo userInfo = userProvider.get();
        SendMessageConfigEntity entity = BeanUtil.toBean(sendMessageConfigForm, SendMessageConfigEntity.class);
        entity.setCreatorUserId(userInfo.getUserId());
        entity.setCreatorTime(DateUtil.getNowDate());
        entity.setId(mainId);

        if (sendMessageConfigForm.getSendConfigTemplateList() != null) {
            List<SendConfigTemplateEntity> SendConfigTemplateList = JsonUtil.createJsonToList(sendMessageConfigForm.getSendConfigTemplateList(), SendConfigTemplateEntity.class);
            long dd = SendConfigTemplateList.stream().filter(t -> t.getMessageType().equals("4")).count();
            long qy = SendConfigTemplateList.stream().filter(t -> t.getMessageType().equals("5")).count();
            boolean isQy = true;
            boolean isDd = true;
            Map<String, String> objModel = getSysConfig();
            if (qy > 0) {
                BaseSystemInfo baseSystemInfo = BeanUtil.toBean(objModel, BaseSystemInfo.class);
                // 企业号id
                String corpId = baseSystemInfo.getQyhCorpId();
                // 应用凭证
                String agentId = baseSystemInfo.getQyhAgentId();
                // 凭证密钥
                String agentSecret = baseSystemInfo.getQyhAgentSecret();
                // 同步密钥
                String corpSecret = baseSystemInfo.getQyhCorpSecret();
                if (StringUtil.isNotEmpty(corpId) && StringUtil.isNotEmpty(agentId) && StringUtil.isNotEmpty(corpSecret) && StringUtil.isNotEmpty(agentSecret)) {
                    isQy = true;
                } else {
                    isQy = false;
                }
            }
            if (dd > 0) {
                DingTalkModel dingTalkModel = BeanUtil.toBean(objModel, DingTalkModel.class);
                // 钉钉企业号Id
                String dingAgentId = dingTalkModel.getDingAgentId();
                // 应用凭证
                String dingSynAppKey = dingTalkModel.getDingSynAppKey();
                // 凭证密钥
                String dingSynAppSecret = dingTalkModel.getDingSynAppSecret();
                if (StringUtil.isNotEmpty(dingSynAppKey) && StringUtil.isNotEmpty(dingSynAppSecret) && StringUtil.isNotEmpty(dingAgentId)) {
                    isDd = true;
                } else {
                    isDd = false;
                }
            }
            if (!isQy) {
                return ServiceResult.error("请先前往系统同步设置，配置企业微信账号");
            }
            if (!isDd) {
                return ServiceResult.error("请先前往系统同步设置，配置钉钉账号");
            }
            for (SendConfigTemplateEntity entitys : SendConfigTemplateList) {
                entitys.setId(RandomUtil.uuId());
                entitys.setSendConfigId(entity.getId());
                sendConfigTemplateService.save(entitys);
            }
        }
        sendMessageConfigService.save(entity);
        return ServiceResult.success("创建成功");
    }


    /**
     * 信息
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "信息")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @GetMapping("/{id}")
    public ServiceResult<SendMessageConfigInfoVO> info(@PathVariable("id") String id) {
        SendMessageConfigEntity entity = sendMessageConfigService.getInfo(id);
        SendMessageConfigInfoVO vo = BeanUtil.toBean(entity, SendMessageConfigInfoVO.class);
        List<DictionaryDataEntity> msgSourceTypeList = dictionaryDataService.getListByTypeDataCode("msgSourceType");
        if (StringUtil.isNotEmpty(vo.getMessageSource())) {
            msgSourceTypeList.stream().filter(t -> vo.getMessageSource().equals(t.getEnCode())).findFirst()
                    .ifPresent(dataTypeEntity -> vo.setMessageSourceName(dataTypeEntity.getFullName()));
        }
        //子表
        List<SendConfigTemplateEntity> sendConfigTemplateList = sendMessageConfigService.getSendConfigTemplateList(id);
        for (SendConfigTemplateEntity sendconfigtemplateEntity : sendConfigTemplateList) {
            AccountConfigEntity accountConfigEntity = accountConfigService.getInfo(sendconfigtemplateEntity.getAccountConfigId());
            if (accountConfigEntity != null) {
                sendconfigtemplateEntity.setAccountCode(accountConfigEntity.getEnCode());
                sendconfigtemplateEntity.setAccountName(accountConfigEntity.getFullName());
            }
            MessageTemplateConfigEntity messageTemplateConfigEntity = messageTemplateConfigService.getInfo(sendconfigtemplateEntity.getTemplateId());
            if (messageTemplateConfigEntity != null) {
                sendconfigtemplateEntity.setTemplateCode(messageTemplateConfigEntity.getEnCode());
                sendconfigtemplateEntity.setTemplateName(messageTemplateConfigEntity.getFullName());
            }
        }
        vo.setSendConfigTemplateList(sendConfigTemplateList);
        //副表
        return ServiceResult.success(vo);
    }

    /**
     * 根据编码获取信息
     *
     * @param enCode 编码
     * @return
     */
    @Operation(summary = "根据编码获取信息")
    @Parameters({
            @Parameter(name = "enCode", description = "编码", required = true)
    })
    @SaCheckPermission("msgCenter.sendConfig")
    @GetMapping("/getInfoByEnCode/{enCode}")
    public ServiceResult<SendMessageConfigInfoVO> getInfo(@PathVariable("enCode") String enCode) {
        SendMessageConfigEntity entity = sendMessageConfigService.getInfoByEnCode(enCode);
        SendMessageConfigInfoVO vo = BeanUtil.toBean(entity, SendMessageConfigInfoVO.class);
        //子表
        List<SendConfigTemplateEntity> sendConfigTemplateList = sendMessageConfigService.getSendConfigTemplateList(entity.getId());
        for (SendConfigTemplateEntity sendconfigtemplateEntity : sendConfigTemplateList) {
            AccountConfigEntity accountConfigEntity = accountConfigService.getInfo(sendconfigtemplateEntity.getAccountConfigId());
            if (accountConfigEntity != null) {
                sendconfigtemplateEntity.setAccountCode(accountConfigEntity.getEnCode());
                sendconfigtemplateEntity.setAccountName(accountConfigEntity.getFullName());
            }
            MessageTemplateConfigEntity messageTemplateConfigEntity = messageTemplateConfigService.getInfo(sendconfigtemplateEntity.getTemplateId());
            if (messageTemplateConfigEntity != null) {
                sendconfigtemplateEntity.setTemplateCode(messageTemplateConfigEntity.getEnCode());
                sendconfigtemplateEntity.setTemplateName(messageTemplateConfigEntity.getFullName());
            }
        }
        vo.setSendConfigTemplateList(sendConfigTemplateList);
        //副表
        return ServiceResult.success(vo);
    }

    /**
     * 表单信息(详情页)
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "表单信息(详情页)")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("msgCenter.sendConfig")
    @GetMapping("/detail/{id}")
    public ServiceResult<SendMessageConfigInfoVO> detailInfo(@PathVariable("id") String id) {
        SendMessageConfigEntity entity = sendMessageConfigService.getInfo(id);
        SendMessageConfigInfoVO vo = BeanUtil.toBean(entity, SendMessageConfigInfoVO.class);

        //子表数据转换
        List<SendConfigTemplateEntity> sendConfigTemplateList = sendMessageConfigService.getSendConfigTemplateList(id);
        for (SendConfigTemplateEntity sendconfigtemplateEntity : sendConfigTemplateList) {
            AccountConfigEntity accountConfigEntity = accountConfigService.getInfo(sendconfigtemplateEntity.getAccountConfigId());
            if (accountConfigEntity != null) {
                sendconfigtemplateEntity.setAccountCode(accountConfigEntity.getEnCode());
                sendconfigtemplateEntity.setAccountName(accountConfigEntity.getFullName());
            }
            MessageTemplateConfigEntity messageTemplateConfigEntity = messageTemplateConfigService.getInfo(sendconfigtemplateEntity.getTemplateId());
            if (messageTemplateConfigEntity != null) {
                sendconfigtemplateEntity.setTemplateCode(messageTemplateConfigEntity.getEnCode());
                sendconfigtemplateEntity.setTemplateName(messageTemplateConfigEntity.getFullName());
            }
        }
        vo.setSendConfigTemplateList(sendConfigTemplateList);
        return ServiceResult.success(vo);
    }


    /**
     * 更新
     *
     * @param id                    主键
     * @param sendMessageConfigForm 发送信息配置模型
     * @return
     */
    @Operation(summary = "更新")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "sendMessageConfigForm", description = "发送信息配置模型", required = true)
    })
    @SaCheckPermission("msgCenter.sendConfig")
    @PutMapping("/{id}")
    @Transactional
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid SendMessageConfigForm sendMessageConfigForm) throws DataBaseException {

        boolean b = sendMessageConfigService.checkForm(sendMessageConfigForm, 0, sendMessageConfigForm.getId());
        if (b) {
            return ServiceResult.error("编码不能重复");
        }
        if ("0".equals(sendMessageConfigForm.getEnabledMark())) {
            if (sendMessageConfigService.idUsed(id)) {
                return ServiceResult.error("此记录与“流程通知”关联引用，不允许被禁用");
            }
        }
        if (!"1".equals(sendMessageConfigForm.getTemplateType())) {
            if (sendMessageConfigForm.getEnCode().contains("PZXT")) {
                return ServiceResult.error("自定义模板编码不能使用系统模板编码规则");
            }
        }
        UserInfo userInfo = userProvider.get();
        SendMessageConfigEntity entity = sendMessageConfigService.getInfo(id);
        if (entity != null) {
            SendMessageConfigEntity subentity = BeanUtil.toBean(sendMessageConfigForm, SendMessageConfigEntity.class);
            subentity.setCreatorUserId(entity.getCreatorUserId());
            subentity.setCreatorTime(entity.getCreatorTime());
            subentity.setUpdateUserId(userInfo.getUserId());
            subentity.setUpdateTime(DateUtil.getNowDate());

            //明细表数据更新
            List<SendConfigTemplateEntity> addTemplateList = new ArrayList<>();
            List<SendConfigTemplateEntity> updTemplateList = new ArrayList<>();
            List<SendConfigTemplateEntity> delTemplateList = new ArrayList<>();
            if (sendMessageConfigForm.getSendConfigTemplateList() != null) {
                List<SendConfigTemplateEntity> sendConfigTemplateEntityList = JsonUtil.createJsonToList(sendMessageConfigForm.getSendConfigTemplateList(), SendConfigTemplateEntity.class);
                long dd = sendConfigTemplateEntityList.stream().filter(t -> t.getMessageType().equals("4")).count();
                long qy = sendConfigTemplateEntityList.stream().filter(t -> t.getMessageType().equals("5")).count();
                boolean isQy = true;
                boolean isDd = true;
                Map<String, String> objModel = getSysConfig();
                if (qy > 0) {
                    BaseSystemInfo baseSystemInfo = BeanUtil.toBean(objModel, BaseSystemInfo.class);
                    // 企业号id
                    String corpId = baseSystemInfo.getQyhCorpId();
                    // 应用凭证
                    String agentId = baseSystemInfo.getQyhAgentId();
                    // 凭证密钥
                    String agentSecret = baseSystemInfo.getQyhAgentSecret();
                    // 同步密钥
                    String corpSecret = baseSystemInfo.getQyhCorpSecret();
                    if (StringUtil.isNotEmpty(corpId) && StringUtil.isNotEmpty(agentId) && StringUtil.isNotEmpty(corpSecret) && StringUtil.isNotEmpty(agentSecret)) {
                        isQy = true;
                    } else {
                        isQy = false;
                    }
                }
                if (dd > 0) {
                    DingTalkModel dingTalkModel = BeanUtil.toBean(objModel, DingTalkModel.class);
                    // 钉钉企业号Id
                    String dingAgentId = dingTalkModel.getDingAgentId();
                    // 应用凭证
                    String dingSynAppKey = dingTalkModel.getDingSynAppKey();
                    // 凭证密钥
                    String dingSynAppSecret = dingTalkModel.getDingSynAppSecret();
                    if (StringUtil.isNotEmpty(dingSynAppKey) && StringUtil.isNotEmpty(dingSynAppSecret) && StringUtil.isNotEmpty(dingAgentId)) {
                        isDd = true;
                    } else {
                        isDd = false;
                    }
                }
                if (!isQy) {
                    return ServiceResult.error("请先前往系统同步设置，配置企业微信账号");
                }
                if (!isDd) {
                    return ServiceResult.error("请先前往系统同步设置，配置钉钉账号");
                }
                for (SendConfigTemplateEntity entitys : sendConfigTemplateEntityList) {
                    SendConfigTemplateEntity templateEntity = StringUtil.isNotEmpty(entitys.getId()) ? sendConfigTemplateService.getInfo(entitys.getId()) : null;
                    if (templateEntity != null) {
                        templateEntity.setSendConfigId(entity.getId());
                        templateEntity.setId(entitys.getId());
                        templateEntity.setEnabledMark(entitys.getEnabledMark());
                        templateEntity.setCreatorTime(entitys.getCreatorTime());
                        templateEntity.setCreatorUserId(entitys.getCreatorUserId());
                        templateEntity.setDescription(entitys.getDescription());
                        templateEntity.setAccountConfigId(entitys.getAccountConfigId());
                        templateEntity.setSortCode(entitys.getSortCode());
                        templateEntity.setUpdateTime(DateUtil.getNowDate());
                        templateEntity.setUpdateUserId(userInfo.getUserId());
                        templateEntity.setTemplateId(entitys.getTemplateId());
                        updTemplateList.add(templateEntity);
                    } else {
                        entitys.setId(RandomUtil.uuId());
                        entitys.setSendConfigId(entity.getId());
                        entitys.setCreatorUserId(userInfo.getUserId());
                        entitys.setCreatorTime(DateUtil.getNowDate());
                        addTemplateList.add(entitys);
                    }
                }
                //删除参数记录
                List<SendConfigTemplateEntity> paramEntityList = sendConfigTemplateService.getDetailListByParentId(entity.getId());
                if (paramEntityList != null) {
                    for (SendConfigTemplateEntity templateEntity : paramEntityList) {
                        SendConfigTemplateEntity templateEntity1 = sendConfigTemplateEntityList.stream().filter(t -> t.getId().equals(templateEntity.getId())).findFirst().orElse(null);
                        if (templateEntity1 == null) {
                            delTemplateList.add(templateEntity);
                        }
                    }
                }
                if (addTemplateList != null && addTemplateList.size() > 0) {
                    sendConfigTemplateService.saveBatch(addTemplateList);
                }
                if (updTemplateList != null && updTemplateList.size() > 0) {
                    sendConfigTemplateService.updateBatchById(updTemplateList);
                }
                if (delTemplateList != null && delTemplateList.size() > 0) {
                    sendConfigTemplateService.removeByIds(delTemplateList.stream().map(SendConfigTemplateEntity::getId).collect(Collectors.toList()));
                }
            }
            boolean b1 = sendMessageConfigService.updateById(subentity);
            if (!b1) {
                return ServiceResult.error("当前表单原数据已被调整，请重新进入该页面编辑并提交数据");
            }
            return ServiceResult.success("更新成功");
        } else {
            return ServiceResult.error("更新失败，数据不存在");
        }
    }


    /**
     * 删除
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "删除")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("msgCenter.sendConfig")
    @DeleteMapping("/{id}")
    @Transactional
    public ServiceResult delete(@PathVariable("id") String id) {
        SendMessageConfigEntity entity = sendMessageConfigService.getInfo(id);
        if (entity != null) {
            if (sendMessageConfigService.idUsed(id)) {
                return ServiceResult.error("删除失败，此记录与“流程通知”关联引用，不允许被删除");
            }
            sendMessageConfigService.delete(entity);
            QueryWrapper<SendConfigTemplateEntity> queryWrapperSendConfigTemplate = new QueryWrapper<>();
            queryWrapperSendConfigTemplate.lambda().eq(SendConfigTemplateEntity::getSendConfigId, entity.getId());
            sendConfigTemplateService.remove(queryWrapperSendConfigTemplate);

        }
        return ServiceResult.success("删除成功");
    }

    /**
     * 获取消息发送配置
     *
     * @param id 发送配置id
     * @return
     */
    @Operation(summary = "获取消息发送配置")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("msgCenter.sendConfig")
    @PostMapping("/getTestConfig/{id}")
    @Transactional
    public ServiceResult getTestConfig(@PathVariable("id") String id) {
        List<SendConfigTemplateEntity> configTemplateList = sendConfigTemplateService.getConfigTemplateListByConfigId(id);
        List<DictionaryDataEntity> msgSendTypeList = dictionaryDataService.getListByTypeDataCode("msgSendType");
        if (configTemplateList != null && configTemplateList.size() > 0) {
            List<SendConfigTemplateModel> modelList = JsonUtil.createJsonToList(configTemplateList, SendConfigTemplateModel.class);
            for (SendConfigTemplateModel model : modelList) {
                List<TemplateParamModel> list = messageTemplateConfigService.getParamJson(model.getTemplateId());
                if (list != null && list.size() > 0) {
                    model.setParamJson(list);
                }
                MessageTemplateConfigEntity msgTemEntity = messageTemplateConfigService.getInfo(model.getTemplateId());
                if (msgTemEntity != null) {
                    model.setMsgTemplateName(msgTemEntity.getFullName());
                }
                if (StringUtil.isNotEmpty(model.getMessageType())) {
                    msgSendTypeList.stream().filter(t -> model.getMessageType().equals(t.getEnCode())).findFirst()
                            .ifPresent(dataTypeEntity -> model.setMessageType(dataTypeEntity.getFullName()));
                }
            }
            return ServiceResult.success(modelList);
        } else {
            return ServiceResult.error("配置模板无数据，无法测试");
        }
    }

    /**
     * 测试消息发送配置
     *
     * @param modelList 发送配置
     * @return
     */
    @Operation(summary = "测试消息发送配置")
    @Parameters({
            @Parameter(name = "modelList", description = "发送配置", required = true)
    })
    @SaCheckPermission("msgCenter.sendConfig")
    @PostMapping("/testSendConfig")
    @Transactional
    public ServiceResult testSendConfig(@RequestBody @Valid List<SendConfigTemplateModel> modelList) throws NoSuchAlgorithmException, InvalidKeyException {
        UserInfo userInfo = userProvider.get();
        List<SendConfigTestResultModel> resultList = new ArrayList<>();
        List<DictionaryDataEntity> msgSendTypeList = dictionaryDataService.getListByTypeDataCode("msgSendType");
        if (modelList != null && modelList.size() > 0) {
            for (SendConfigTemplateModel model : modelList) {
                SendConfigTestResultModel resultModel = new SendConfigTestResultModel();
                String result = testSendConfigUtil.sendMessage(model, userInfo);
                MessageTemplateConfigEntity msgTemEntity = messageTemplateConfigService.getInfo(model.getTemplateId());
                if (msgTemEntity != null) {
                    msgSendTypeList.stream().filter(t -> msgTemEntity.getMessageType().equals(t.getEnCode())).findFirst()
                            .ifPresent(dataTypeEntity -> resultModel.setMessageType("消息类型：" + dataTypeEntity.getFullName()));
                    resultModel.setResult(result);
                    if (result != null) {
                        resultModel.setIsSuccess("0");
                    } else {
                        resultModel.setIsSuccess("1");
                    }
                }
                resultList.add(resultModel);
            }
        }
        return ServiceResult.success(resultList);
    }

    /**
     * 复制
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "复制")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("msgCenter.sendConfig")
    @PostMapping("/{id}/Actions/Copy")
    @Transactional
    public ServiceResult copy(@PathVariable("id") String id) {
        UserInfo userInfo = userProvider.get();
        SendMessageConfigEntity entity = sendMessageConfigService.getInfo(id);
        if (entity != null) {
            entity.setEnabledMark(0);
            String copyNum = UUID.randomUUID().toString().substring(0, 5);
            entity.setFullName(entity.getFullName() + ".副本" + copyNum);
            entity.setEnCode(entity.getEnCode() + copyNum);
            entity.setCreatorTime(DateUtil.getNowDate());
            entity.setCreatorUserId(userInfo.getUserId());
            entity.setUpdateTime(null);
            entity.setUpdateUserId(null);
            entity.setTemplateType("0");
            entity.setId(RandomUtil.uuId());
            SendMessageConfigEntity copyEntity = BeanUtil.toBean(entity, SendMessageConfigEntity.class);
            if (copyEntity.getEnCode().length() > 50 || copyEntity.getFullName().length() > 50) {
                return ServiceResult.error("已到达该模板复制上限，请复制源模板");
            }
            sendMessageConfigService.create(copyEntity);
            List<SendConfigTemplateEntity> copyConfigTemplateList = new ArrayList<>();
            List<SendConfigTemplateEntity> configTemplateList = sendConfigTemplateService.getDetailListByParentId(id);
            if (configTemplateList != null && configTemplateList.size() > 0) {
                for (SendConfigTemplateEntity entitys : configTemplateList) {
                    entitys.setId(RandomUtil.uuId());
                    entitys.setSendConfigId(copyEntity.getId());
                    entitys.setCreatorTime(DateUtil.getNowDate());
                    entitys.setCreatorUserId(userInfo.getUserId());
                    entitys.setUpdateTime(null);
                    entitys.setUpdateUserId(null);
                    copyConfigTemplateList.add(entitys);
                }
            }
            if (copyConfigTemplateList != null && copyConfigTemplateList.size() > 0) {
                sendConfigTemplateService.saveBatch(copyConfigTemplateList);
            }
            return ServiceResult.success("复制数据成功");
        } else {
            return ServiceResult.error("复制失败，数据不存在");
        }
    }

    /**
     * 导出消息发送配置
     *
     * @param id 账号配置id
     * @return ignore
     */
    @Operation(summary = "导出")
    @GetMapping("/{id}/Action/Export")
    public ServiceResult export(@PathVariable String id) {
        SendMessageConfigEntity entity = sendMessageConfigService.getInfo(id);
        SendMessageConfigInfoVO vo = BeanUtil.toBean(entity, SendMessageConfigInfoVO.class);

        //子表数据
        List<SendConfigTemplateEntity> sendConfigTemplateList = sendMessageConfigService.getSendConfigTemplateList(id);
        vo.setSendConfigTemplateList(sendConfigTemplateList);
        //导出文件
        DownloadVO downloadVO = fileExport.exportFile(vo, configValueUtil.getTemporaryFilePath(), entity.getFullName(), ModuleTypeEnum.MESSAGE_SEND_CONFIG.getTableName());
        return ServiceResult.success(downloadVO);
    }

    /**
     * 导入账号配置
     *
     * @param multipartFile 备份json文件
     * @return 执行结果标识
     */
    @Operation(summary = "导入")
    @PostMapping(value = "/Action/Import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResult importData(@RequestPart("file") MultipartFile multipartFile) throws DataBaseException {
        UserInfo userInfo = userProvider.get();
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.MESSAGE_SEND_CONFIG.getTableName())) {
            return ServiceResult.error(MsgCode.IMP002.get());
        }
        //获取文件内容
        String fileContent = FileUtil.getFileContent(multipartFile);
        SendMessageConfigInfoVO infoVO = JsonUtil.createJsonToBean(fileContent, SendMessageConfigInfoVO.class);
        SendMessageConfigEntity entity = BeanUtil.toBean(infoVO, SendMessageConfigEntity.class);
        entity.setCreatorUserId(userInfo.getUserId());
        entity.setCreatorTime(DateUtil.getNowDate());
        if (infoVO.getSendConfigTemplateList() != null) {
            List<SendConfigTemplateEntity> sendConfigTemplateList = JsonUtil.createJsonToList(infoVO.getSendConfigTemplateList(), SendConfigTemplateEntity.class);
            sendConfigTemplateService.saveBatch(sendConfigTemplateList);
        }
        return sendMessageConfigService.ImportData(entity);
    }


    public Map<String, String> getSysConfig() {
        Map<String, String> objModel = new HashMap<>();
        List<SysConfigEntity> configList = sysconfigService.getList("SysConfig");
        for (SysConfigEntity entity : configList) {
            objModel.put(entity.getFkey(), entity.getValue());
        }
        return objModel;
    }
}
