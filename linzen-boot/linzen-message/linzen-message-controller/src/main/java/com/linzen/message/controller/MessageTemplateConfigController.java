


package com.linzen.message.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.base.controller.SuperController;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.service.BillRuleService;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.vo.DownloadVO;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.MsgCode;
import com.linzen.emnus.ModuleTypeEnum;
import com.linzen.exception.DataBaseException;
import com.linzen.message.entity.MessageTemplateConfigEntity;
import com.linzen.message.entity.SmsFieldEntity;
import com.linzen.message.entity.TemplateParamEntity;
import com.linzen.message.model.messagetemplateconfig.MessageTemplateConfigForm;
import com.linzen.message.model.messagetemplateconfig.MessageTemplateConfigInfoVO;
import com.linzen.message.model.messagetemplateconfig.MessageTemplateConfigListVO;
import com.linzen.message.model.messagetemplateconfig.MessageTemplateConfigPagination;
import com.linzen.message.service.MessageTemplateConfigService;
import com.linzen.message.service.SendConfigTemplateService;
import com.linzen.message.service.SmsFieldService;
import com.linzen.message.service.TemplateParamService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 消息模板
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息
 * @date 2023-04-01
 */
@Slf4j
@RestController
@Tag(name = "配置消息模板", description = "配置消息模板")
@RequestMapping("/api/message/MessageTemplateConfig")
public class MessageTemplateConfigController extends SuperController<MessageTemplateConfigService, MessageTemplateConfigEntity> {

    @Autowired
    private FileExport fileExport;

    @Autowired
    private ConfigValueUtil configValueUtil;

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageTemplateConfigService messageTemplateConfigService;

    @Autowired
    private TemplateParamService templateParamService;

    @Autowired
    private SmsFieldService smsFieldService;

    @Autowired
    private DictionaryDataService dictionaryDataService;

    @Autowired
    private SendConfigTemplateService sendConfigTemplateService;

    /**
     * 列表
     *
     * @param messageTemplateConfigPagination 消息模板分页模型
     * @return
     */
    @Operation(summary = "列表")
    @SaCheckPermission(value = {"msgCenter.msgTemplate", "msgCenter.accountConfig"}, mode = SaMode.OR)
    @GetMapping("/list")
    public ServiceResult<PageListVO<MessageTemplateConfigListVO>> list(MessageTemplateConfigPagination messageTemplateConfigPagination) throws IOException {
        // 消息渠道分类
        List<DictionaryDataEntity> msgSendTypeList = dictionaryDataService.getListByTypeDataCode("msgSendType");
        // 消息来源分类
        List<DictionaryDataEntity> msgSourceTypeList = dictionaryDataService.getListByTypeDataCode("msgSourceType");
        // 获取消息模板列表
        List<MessageTemplateConfigEntity> list = messageTemplateConfigService.getList(messageTemplateConfigPagination);

        //处理id字段转名称，若无需转或者为空可删除
        List<MessageTemplateConfigListVO> listVO = JsonUtil.createJsonToList(list, MessageTemplateConfigListVO.class);
        for (MessageTemplateConfigListVO messageTemplateNewVO : listVO) {
            // 消息渠道分类
            if (StringUtil.isNotEmpty(messageTemplateNewVO.getMessageType())) {
                msgSendTypeList.stream().filter(t -> messageTemplateNewVO.getMessageType().equals(t.getEnCode())).findFirst()
                        .ifPresent(dataTypeEntity -> messageTemplateNewVO.setMessageType(dataTypeEntity.getFullName()));
            }
            // 消息来源分类
            if (StringUtil.isNotEmpty(messageTemplateNewVO.getMessageSource())) {
                msgSourceTypeList.stream().filter(t -> messageTemplateNewVO.getMessageSource().equals(t.getEnCode())).findFirst()
                        .ifPresent(dataTypeEntity -> messageTemplateNewVO.setMessageSource(dataTypeEntity.getFullName()));
            }
            // 创建人员(TODO)
            if (StringUtil.isNotBlank(messageTemplateNewVO.getCreatorUserId()) && !"null".equals(messageTemplateNewVO.getCreatorUserId())) {
                SysUserEntity userEntity = userService.getInfo(messageTemplateNewVO.getCreatorUserId());
                if (userEntity != null) {
                    messageTemplateNewVO.setCreatorUser(userEntity.getRealName() + "/" + userEntity.getAccount());
                }
            }
        }

        PageListVO<MessageTemplateConfigListVO> vo = new PageListVO<>();
        vo.setList(listVO);
        PaginationVO page = BeanUtil.toBean(messageTemplateConfigPagination, PaginationVO.class);
        vo.setPagination(page);
        return ServiceResult.success(vo);
    }


    /**
     * 创建
     *
     * @param messageTemplateConfigForm 消息模板页模型
     * @return ignore
     */
    @Operation(summary = "创建")
    @Parameters({
            @Parameter(name = "messageTemplateConfigForm", description = "消息模板页模型", required = true)
    })
    @SaCheckPermission("msgCenter.msgTemplate")
    @PostMapping
    @Transactional
    public ServiceResult<String> create(@RequestBody @Valid MessageTemplateConfigForm messageTemplateConfigForm) throws DataBaseException {
        boolean b = messageTemplateConfigService.checkForm(messageTemplateConfigForm, 0, "");
        if (b) {
            return ServiceResult.error("编码不能重复");
        }
        if (!"1".equals(messageTemplateConfigForm.getTemplateType())) {
            if (messageTemplateConfigForm.getEnCode().contains("MBXT")) {
                return ServiceResult.error("自定义模板编码不能使用系统模板编码规则");
            }
        }
        if (messageTemplateConfigForm.getSmsFieldList() != null && "7".equals(messageTemplateConfigForm.getMessageType())) {
            List<SmsFieldEntity> SmsFieldList = JsonUtil.createJsonToList(messageTemplateConfigForm.getSmsFieldList(), SmsFieldEntity.class);
            List<SmsFieldEntity> list = SmsFieldList.stream().filter(t -> StringUtil.isNotEmpty(String.valueOf(t.getIsTitle())) && !"null".equals(String.valueOf(t.getIsTitle())) && t.getIsTitle() == 1).collect(Collectors.toList());
            if (list.size() > 1) {
                return ServiceResult.error("创建失败，存在多个标题参数");
            }
        }
        String mainId = RandomUtil.uuId();
        UserInfo userInfo = userProvider.get();
        MessageTemplateConfigEntity entity = BeanUtil.toBean(messageTemplateConfigForm, MessageTemplateConfigEntity.class);
        entity.setCreatorTime(DateUtil.getNowDate());
        entity.setCreatorUserId(userInfo.getUserId());
        entity.setId(mainId);
        if ("1".equals(entity.getMessageType()) && "2".equals(entity.getMessageSource())) {
            entity.setContent(null);
        }
        messageTemplateConfigService.save(entity);
        if (messageTemplateConfigForm.getTemplateParamList() != null) {
            List<TemplateParamEntity> TemplateParamList = JsonUtil.createJsonToList(messageTemplateConfigForm.getTemplateParamList(), TemplateParamEntity.class);
            for (TemplateParamEntity entitys : TemplateParamList) {
                entitys.setId(RandomUtil.uuId());
                entitys.setTemplateId(entity.getId());
                templateParamService.save(entitys);
            }
        }
        if (messageTemplateConfigForm.getSmsFieldList() != null) {
            List<SmsFieldEntity> SmsFieldList = JsonUtil.createJsonToList(messageTemplateConfigForm.getSmsFieldList(), SmsFieldEntity.class);
            for (SmsFieldEntity entitys : SmsFieldList) {
                entitys.setId(RandomUtil.uuId());
                entitys.setTemplateId(entity.getId());
                smsFieldService.save(entitys);
            }
        }

        return ServiceResult.success("创建成功");
    }


    /**
     * 信息
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "信息")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission(value = {"msgCenter.msgTemplate", "msgCenter.accountConfig"}, mode = SaMode.OR)
    @GetMapping("/{id}")
    public ServiceResult<MessageTemplateConfigInfoVO> info(@PathVariable("id") String id) {
        MessageTemplateConfigEntity entity = messageTemplateConfigService.getInfo(id);
        MessageTemplateConfigInfoVO vo = BeanUtil.toBean(entity, MessageTemplateConfigInfoVO.class);
        //子表
        List<TemplateParamEntity> templateParamList = messageTemplateConfigService.getTemplateParamList(id);
        vo.setTemplateParamList(templateParamList);
        List<SmsFieldEntity> smsFieldList = messageTemplateConfigService.getSmsFieldList(id);
        vo.setSmsFieldList(smsFieldList);
        //副表
        return ServiceResult.success(vo);
    }

    /**
     * 表单信息(详情页)
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "表单信息(详情页)")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("msgCenter.msgTemplate")
    @GetMapping("/detail/{id}")
    public ServiceResult<MessageTemplateConfigInfoVO> detailInfo(@PathVariable("id") String id) {
        MessageTemplateConfigEntity entity = messageTemplateConfigService.getInfo(id);
        MessageTemplateConfigInfoVO vo = BeanUtil.toBean(entity, MessageTemplateConfigInfoVO.class);
        //子表数据转换
        List<TemplateParamEntity> templateParamList = messageTemplateConfigService.getTemplateParamList(id);
        vo.setTemplateParamList(templateParamList);
        List<SmsFieldEntity> smsFieldList = messageTemplateConfigService.getSmsFieldList(id);
        vo.setSmsFieldList(smsFieldList);

        return ServiceResult.success(vo);
    }


    /**
     * 更新
     *
     * @param id                        主键
     * @param messageTemplateConfigForm 消息模板页模型
     * @return
     */
    @Operation(summary = "更新")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "messageTemplateConfigForm", description = "消息模板页模型", required = true)
    })
    @SaCheckPermission("msgCenter.msgTemplate")
    @PutMapping("/{id}")
    @Transactional
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid MessageTemplateConfigForm messageTemplateConfigForm) throws DataBaseException {

        boolean b = messageTemplateConfigService.checkForm(messageTemplateConfigForm, 0, messageTemplateConfigForm.getId());
        if (b) {
            return ServiceResult.error("编码不能重复");
        }
        if (!"1".equals(messageTemplateConfigForm.getTemplateType())) {
            if (messageTemplateConfigForm.getEnCode().contains("MBXT")) {
                return ServiceResult.error("自定义模板编码不能使用系统模板编码规则");
            }
        }
        //判断配置是否被引用
        if ("0".equals(String.valueOf(messageTemplateConfigForm.getEnabledMark()))) {
            if (sendConfigTemplateService.isUsedTemplate(messageTemplateConfigForm.getId())) {
                return ServiceResult.error("此记录与“消息发送配置”关联引用，不允许被禁用");
            }
        }
        if (messageTemplateConfigForm.getSmsFieldList() != null && "7".equals(messageTemplateConfigForm.getMessageType())) {
            List<SmsFieldEntity> SmsFieldList = JsonUtil.createJsonToList(messageTemplateConfigForm.getSmsFieldList(), SmsFieldEntity.class);
            List<SmsFieldEntity> list = SmsFieldList.stream().filter(t -> StringUtil.isNotEmpty(String.valueOf(t.getIsTitle())) && !"null".equals(String.valueOf(t.getIsTitle())) && t.getIsTitle() == 1).collect(Collectors.toList());
            if (list.size() > 1) {
                return ServiceResult.error("更新失败，存在多个标题参数");
            }
        }
        UserInfo userInfo = userProvider.get();
        MessageTemplateConfigEntity entity = messageTemplateConfigService.getInfo(id);
        if (entity != null) {
            MessageTemplateConfigEntity subentity = BeanUtil.toBean(messageTemplateConfigForm, MessageTemplateConfigEntity.class);
            subentity.setCreatorTime(entity.getCreatorTime());
            subentity.setCreatorUserId(entity.getCreatorUserId());
            subentity.setUpdateTime(DateUtil.getNowDate());
            subentity.setUpdateUserId(userInfo.getUserId());
            if ("1".equals(subentity.getMessageType()) && "2".equals(subentity.getMessageSource())) {
                subentity.setContent(null);
            }
            boolean b1 = messageTemplateConfigService.updateById(subentity);
            if (!b1) {
                return ServiceResult.error("当前表单原数据已被调整，请重新进入该页面编辑并提交数据");
            }

            //明细表数据更新
            List<TemplateParamEntity> addParamList = new ArrayList<>();
            List<TemplateParamEntity> updParamList = new ArrayList<>();
            List<TemplateParamEntity> delParamList = new ArrayList<>();
            if (messageTemplateConfigForm.getTemplateParamList() != null) {
                List<TemplateParamEntity> templateParamList = JsonUtil.createJsonToList(messageTemplateConfigForm.getTemplateParamList(), TemplateParamEntity.class);
                for (TemplateParamEntity entitys : templateParamList) {
                    if (StringUtil.isNotBlank(entitys.getId()) && !"null".equals(entitys.getId())) {
                        TemplateParamEntity paramEntity = templateParamService.getInfo(entitys.getId());
                        if (paramEntity != null) {
                            paramEntity.setId(entitys.getId());
                            paramEntity.setTemplateId(entitys.getTemplateId());
                            paramEntity.setField(entitys.getField());
                            paramEntity.setFieldName(entitys.getFieldName());
                            paramEntity.setCreatorUserId(entity.getCreatorUserId());
                            paramEntity.setCreatorTime(entitys.getCreatorTime());
                            paramEntity.setUpdateUserId(userInfo.getUserId());
                            paramEntity.setUpdateTime(DateUtil.getNowDate());
                            updParamList.add(paramEntity);
                        }
                    } else {
                        entitys.setId(RandomUtil.uuId());
                        entitys.setTemplateId(entity.getId());
                        entitys.setCreatorUserId(userInfo.getUserId());
                        entitys.setCreatorTime(DateUtil.getNowDate());
                        addParamList.add(entitys);
                    }
                }

                //删除参数记录
                List<TemplateParamEntity> paramEntityList = templateParamService.getDetailListByParentId(entity.getId());
                if (paramEntityList != null) {
                    for (TemplateParamEntity paramEntity : paramEntityList) {
                        TemplateParamEntity paramEntity1 = templateParamList.stream().filter(t -> t.getId().equals(paramEntity.getId())).findFirst().orElse(null);
                        if (paramEntity1 == null) {
                            delParamList.add(paramEntity);
                        }
                    }
                }
                if (!addParamList.isEmpty()) {
                    templateParamService.saveBatch(addParamList);
                }
                if (!updParamList.isEmpty()) {
                    templateParamService.updateBatchById(updParamList);
                }
                if (!delParamList.isEmpty()) {
                    templateParamService.removeByIds(delParamList.stream().map(TemplateParamEntity::getId).collect(Collectors.toList()));
                }
            }

            //短信参数明细表数据更新
            List<SmsFieldEntity> addSmsList = new ArrayList<>();
            List<SmsFieldEntity> updSmsList = new ArrayList<>();
            List<SmsFieldEntity> delSmsList = new ArrayList<>();
            if (messageTemplateConfigForm.getSmsFieldList() != null) {
                List<SmsFieldEntity> smsFieldList = JsonUtil.createJsonToList(messageTemplateConfigForm.getSmsFieldList(), SmsFieldEntity.class);
                for (SmsFieldEntity entitys : smsFieldList) {
                    if (StringUtil.isNotBlank(entitys.getId()) && !"null".equals(entitys.getId())) {
                        SmsFieldEntity smsFieldEntity = smsFieldService.getInfo(entitys.getId());
                        if (smsFieldEntity != null) {
                            smsFieldEntity.setId(entitys.getId());
                            smsFieldEntity.setTemplateId(entity.getId());
                            smsFieldEntity.setFieldId(entitys.getFieldId());
                            smsFieldEntity.setField(entitys.getField());
                            smsFieldEntity.setSmsField(entitys.getSmsField());
                            smsFieldEntity.setCreatorTime(entitys.getCreatorTime());
                            smsFieldEntity.setCreatorUserId(entitys.getCreatorUserId());
                            smsFieldEntity.setUpdateTime(DateUtil.getNowDate());
                            smsFieldEntity.setUpdateUserId(userInfo.getUserId());
                            smsFieldEntity.setIsTitle(entitys.getIsTitle());
                            updSmsList.add(smsFieldEntity);
                        }
                    } else {
                        entitys.setId(RandomUtil.uuId());
                        entitys.setTemplateId(entity.getId());
                        entitys.setCreatorTime(DateUtil.getNowDate());
                        entitys.setCreatorUserId(userInfo.getUserId());
                        addSmsList.add(entitys);
                    }
                }
                //删除短信参数明细表
                List<SmsFieldEntity> smsFieldEntityList = smsFieldService.getDetailListByParentId(entity.getId());
                if (smsFieldEntityList != null && !smsFieldEntityList.isEmpty()) {
                    for (SmsFieldEntity smsFieldEntity : smsFieldEntityList) {
                        SmsFieldEntity smsFieldEntity1 = smsFieldList.stream().filter(t -> t.getId().equals(smsFieldEntity.getId())).findFirst().orElse(null);
                        if (smsFieldEntity1 == null) {
                            delSmsList.add(smsFieldEntity);
                        }
                    }
                }
                if (!addSmsList.isEmpty()) {
                    smsFieldService.saveBatch(addSmsList);
                }
                if (!updSmsList.isEmpty()) {
                    smsFieldService.updateBatchById(updSmsList);
                }
                if (!delSmsList.isEmpty()) {
                    smsFieldService.removeByIds(delSmsList.stream().map(SmsFieldEntity::getId).collect(Collectors.toList()));
                }
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
     * @return ignore
     */
    @Operation(summary = "删除")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("msgCenter.msgTemplate")
    @DeleteMapping("/{id}")
    @Transactional
    public ServiceResult delete(@PathVariable("id") String id) {
        MessageTemplateConfigEntity entity = messageTemplateConfigService.getInfo(id);
        if (entity != null) {
            if (sendConfigTemplateService.isUsedTemplate(entity.getId())) {
                return ServiceResult.error("此记录与“消息发送配置”关联引用，不允许被删除");
            }
            messageTemplateConfigService.delete(entity);
            QueryWrapper<TemplateParamEntity> queryWrapperTemplateParam = new QueryWrapper<>();
            queryWrapperTemplateParam.lambda().eq(TemplateParamEntity::getTemplateId, entity.getId());
            templateParamService.remove(queryWrapperTemplateParam);
            QueryWrapper<SmsFieldEntity> queryWrapperSmsField = new QueryWrapper<>();
            queryWrapperSmsField.lambda().eq(SmsFieldEntity::getTemplateId, entity.getId());
            smsFieldService.remove(queryWrapperSmsField);

        }
        return ServiceResult.success("删除成功");
    }

    /**
     * 开启或禁用
     *
     * @param id
     * @return
     */
    @Operation(summary = "开启或禁用")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("msgCenter.msgTemplate")
    @PostMapping("/unable/{id}")
    @Transactional
    public ServiceResult unable(@PathVariable("id") String id) {
        MessageTemplateConfigEntity entity = messageTemplateConfigService.getInfo(id);
        if (entity != null) {
            if ("1".equals(String.valueOf(entity.getEnabledMark()))) {
                entity.setEnabledMark(0);
                return ServiceResult.success("禁用成功");
            } else {
                //判断是否被引用

                entity.setEnabledMark(1);
                return ServiceResult.success("启用成功");
            }
        } else {
            return ServiceResult.error("操作失败，数据不存在");
        }
    }

    /**
     * 复制
     *
     * @param id
     * @return
     */
    @Operation(summary = "复制")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("msgCenter.msgTemplate")
    @PostMapping("/{id}/Actions/Copy")
    @Transactional
    public ServiceResult copy(@PathVariable("id") String id) {
        UserInfo userInfo = userProvider.get();
        MessageTemplateConfigEntity entity = messageTemplateConfigService.getInfo(id);
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
            MessageTemplateConfigEntity copyEntity = BeanUtil.toBean(entity, MessageTemplateConfigEntity.class);
            if (copyEntity.getEnCode().length() > 50 || copyEntity.getFullName().length() > 50) {
                return ServiceResult.error("已到达该模板复制上限，请复制源模板");
            }
            messageTemplateConfigService.create(copyEntity);
            List<TemplateParamEntity> copyParamList = new ArrayList<>();
            List<TemplateParamEntity> baseParamList = templateParamService.getDetailListByParentId(id);
            if (baseParamList != null && baseParamList.size() > 0) {
                for (TemplateParamEntity entitys : baseParamList) {
                    entitys.setId(RandomUtil.uuId());
                    entitys.setTemplateId(copyEntity.getId());
                    entitys.setCreatorTime(DateUtil.getNowDate());
                    entitys.setCreatorUserId(userInfo.getUserId());
                    entitys.setUpdateTime(null);
                    entitys.setUpdateUserId(null);
                    copyParamList.add(entitys);
                }
            }
            if (copyParamList != null && copyParamList.size() > 0) {
                templateParamService.saveBatch(copyParamList);
            }
            List<SmsFieldEntity> copySmsList = new ArrayList<>();
            List<SmsFieldEntity> baseSmsFieldList = smsFieldService.getDetailListByParentId(id);
            if (baseSmsFieldList != null && baseSmsFieldList.size() > 0) {
                for (SmsFieldEntity entitys : baseSmsFieldList) {
                    entitys.setId(RandomUtil.uuId());
                    entitys.setTemplateId(copyEntity.getId());
                    entitys.setCreatorTime(DateUtil.getNowDate());
                    entitys.setCreatorUserId(userInfo.getUserId());
                    entitys.setUpdateTime(null);
                    entitys.setUpdateUserId(null);
                    copySmsList.add(entitys);
                }
            }
            if (copySmsList != null && copySmsList.size() > 0) {
                smsFieldService.saveBatch(copySmsList);
            }
            return ServiceResult.success("复制数据成功");
        } else {
            return ServiceResult.error("复制失败，数据不存在");
        }
    }

    /**
     * 导出消息模板
     *
     * @param id 消息模板id
     * @return ignore
     */
    @Operation(summary = "导出")
    @GetMapping("/{id}/Action/Export")
    public ServiceResult export(@PathVariable String id) {
        MessageTemplateConfigEntity entity = messageTemplateConfigService.getInfo(id);
        MessageTemplateConfigInfoVO vo = BeanUtil.toBean(entity, MessageTemplateConfigInfoVO.class);
        //子表
        List<TemplateParamEntity> BaseTemplateParamList = messageTemplateConfigService.getTemplateParamList(id);
        vo.setTemplateParamList(BaseTemplateParamList);
        List<SmsFieldEntity> BaseSmsFieldList = messageTemplateConfigService.getSmsFieldList(id);
        vo.setSmsFieldList(BaseSmsFieldList);
        //导出文件
        DownloadVO downloadVO = fileExport.exportFile(vo, configValueUtil.getTemporaryFilePath(), entity.getFullName(), ModuleTypeEnum.MESSAGE_TEMPLATE.getTableName());
        return ServiceResult.success(downloadVO);
    }

    /**
     * 导入消息模板
     *
     * @param multipartFile 备份json文件
     * @return 执行结果标识
     */
    @Operation(summary = "导入")
    @PostMapping(value = "/Action/Import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResult importData(@RequestPart("file") MultipartFile multipartFile) throws DataBaseException {
        UserInfo userInfo = userProvider.get();
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.MESSAGE_TEMPLATE.getTableName())) {
            return ServiceResult.error(MsgCode.IMP002.get());
        }
        //获取文件内容
        String fileContent = FileUtil.getFileContent(multipartFile);
        MessageTemplateConfigInfoVO infoVO = JsonUtil.createJsonToBean(fileContent, MessageTemplateConfigInfoVO.class);
        MessageTemplateConfigEntity entity = BeanUtil.toBean(infoVO, MessageTemplateConfigEntity.class);
        entity.setCreatorUserId(userInfo.getUserId());
        entity.setCreatorTime(DateUtil.getNowDate());
        //子表数据导入
        if (infoVO.getTemplateParamList() != null && infoVO.getTemplateParamList().size() > 0) {
            List<TemplateParamEntity> templateParamList = JsonUtil.createJsonToList(infoVO.getTemplateParamList(), TemplateParamEntity.class);
            templateParamService.saveBatch(templateParamList);
        }
        if (infoVO.getSmsFieldList() != null && infoVO.getSmsFieldList().size() > 0) {
            List<SmsFieldEntity> smsFieldList = JsonUtil.createJsonToList(infoVO.getSmsFieldList(), SmsFieldEntity.class);
            smsFieldService.saveBatch(smsFieldList);
        }
        return messageTemplateConfigService.ImportData(entity);
    }

}
