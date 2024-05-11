package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.Pagination;
import com.linzen.base.entity.MessageTemplateEntity;
import com.linzen.base.entity.SmsTemplateEntity;
import com.linzen.base.model.messagetemplate.*;
import com.linzen.base.model.systemconfig.SmsModel;
import com.linzen.base.service.MessageTemplateService;
import com.linzen.base.service.SmsTemplateService;
import com.linzen.base.util.SmsUtil;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.constant.MsgCode;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.util.JsonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 消息模板控制类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(description = "BaseMessageTemplateController", name = "消息模板控制类")
@RestController
@RequestMapping("/api/system/MessageTemplate")
public class MessageTemplateController extends SuperController<MessageTemplateService, MessageTemplateEntity> {

    @Autowired
    private MessageTemplateService messageTemplateService;
    @Autowired
    private SmsTemplateService smsTemplateService;
    @Autowired
    private UserService userService;

    /**
     * 消息模板列表
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "消息模板列表")
    @SaCheckPermission("msgTemplate")
    @GetMapping
    public ServiceResult<PageListVO<MessageTemplateListVO>> list(Pagination pagination) {
        List<MessageTemplateEntity> list = messageTemplateService.getList(pagination, false);
        List<MessageTemplateListVO> listVO = JsonUtil.createJsonToList(list, MessageTemplateListVO.class);
        for (MessageTemplateListVO messageTemplateListVO : listVO) {
            StringBuilder noticeMethod = new StringBuilder();
            if (messageTemplateListVO.getIsDingTalk() == 1) {
                noticeMethod.append("、阿里钉钉");
            }
            if (messageTemplateListVO.getIsEmail() == 1) {
                noticeMethod.append("、电子邮箱");
            }
            if (messageTemplateListVO.getIsSms() == 1) {
                noticeMethod.append("、短信");
            }
            if (messageTemplateListVO.getIsStationLetter() == 1) {
                noticeMethod.append("、站内信");
            }
            if (messageTemplateListVO.getIsWecom() == 1) {
                noticeMethod.append("、企业微信");
            }
            if (noticeMethod.length() > 0) {
                messageTemplateListVO.setNoticeMethod(noticeMethod.toString().replaceFirst("、", ""));
            }
            if ("1".equals(messageTemplateListVO.getCategory())) {
                messageTemplateListVO.setCategory("普通");
            } else if ("2".equals(messageTemplateListVO.getCategory())) {
                messageTemplateListVO.setCategory("重要");
            } else if ("3".equals(messageTemplateListVO.getCategory())) {
                messageTemplateListVO.setCategory("紧急");
            }
            SysUserEntity entity = userService.getInfo(messageTemplateListVO.getCreatorUserId());
            messageTemplateListVO.setCreatorUser(entity!= null ? entity.getRealName() + "/" + entity.getAccount() : null);
        }
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(listVO, paginationVO);
    }

    /**
     * 消息模板下拉框
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "消息模板下拉框")
    @GetMapping("/Selector")
    public ServiceResult selector(Pagination pagination) {
        List<MessageTemplateEntity> list = messageTemplateService.getList(pagination, true);
        for (MessageTemplateEntity entity : list) {
            if ("1".equals(entity.getCategory())) {
                entity.setCategory("普通");
            } else if ("2".equals(entity.getCategory())) {
                entity.setCategory("重要");
            } else if ("3".equals(entity.getCategory())) {
                entity.setCategory("紧急");
            }
        }
        List<MessageTemplateSelector> listVO = JsonUtil.createJsonToList(list, MessageTemplateSelector.class);
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(listVO, paginationVO);
    }

    /**
     * 获取消息模板
     *
     * @param id
     * @return
     */
    @Operation(summary = "获取消息模板")
    @Parameter(name = "id", description = "主键", required = true)
    @GetMapping("/{id}")
    public ServiceResult<MessageTemplateVO> info(@PathVariable("id") String id) {
        MessageTemplateEntity entity = messageTemplateService.getInfo(id);
        MessageTemplateVO vo = BeanUtil.toBean(entity, MessageTemplateVO.class);
        SmsTemplateEntity info = smsTemplateService.getInfo(vo.getSmsId());
        vo.setSmsTemplateName(info != null ? info.getFullName() : null);
        return ServiceResult.success(vo);
    }

    /**
     * 获取消息模板参数
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "获取消息模板参数")
    @Parameter(name = "id", description = "主键", required = true)
    @GetMapping("/getTemplate/{id}")
    public ServiceResult<?> getParameter(@PathVariable("id") String id) {
        MessageTemplateEntity entity = messageTemplateService.getInfo(id);
        if (entity == null) {
            return ServiceResult.error("模板不存在");
        }
        // 获取参数
        String templateJson = entity.getTemplateJson();
        Map<String, Object> map = JsonUtil.stringToMap(templateJson);
        // 如果是短信，获取短信模板参数
        if (entity.getIsSms() == 1) {
            SmsModel smsModel = smsTemplateService.getSmsConfig();
            String smsId = entity.getSmsId();
            SmsTemplateEntity info = smsTemplateService.getInfo(smsId);
            List<String> list = SmsUtil.querySmsTemplateRequest(info.getCompany(), smsModel, info.getEndpoint(), info.getRegion(), info.getTemplateId());
            for (String key : list) {
                map.put(key, null);
            }
        }
        return ServiceResult.success(map);
    }

    /**
     * 新建
     *
     * @param messageTemplateCrForm 新建消息模板
     * @return
     */
    @Operation(summary = "新建")
    @Parameter(name = "messageTemplateCrForm", description = "新建消息模板", required = true)
    @SaCheckPermission("msgTemplate")
    @PostMapping
    public ServiceResult<String> create(@RequestBody @Valid MessageTemplateCrForm messageTemplateCrForm) {
        MessageTemplateEntity entity = BeanUtil.toBean(messageTemplateCrForm, MessageTemplateEntity.class);
        if (messageTemplateService.isExistByFullName(entity.getFullName(), entity.getId())) {
            return ServiceResult.error("新建失败，名称不能重复");
        }
        if (messageTemplateService.isExistByEnCode(entity.getEnCode(), entity.getId())) {
            return ServiceResult.error("新建失败，编码不能重复");
        }
        messageTemplateService.create(entity);
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 修改
     *
     * @param id 主键
     * @param messageTemplateUpForm 修改消息模板
     * @return
     */
    @Operation(summary = "修改")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "messageTemplateUpForm", description = "修改消息模板", required = true)
    })
    @SaCheckPermission("msgTemplate")
    @PutMapping("/{id}")
    public ServiceResult<String> update(@PathVariable("id") String id, @RequestBody @Valid MessageTemplateUpForm messageTemplateUpForm) {
        MessageTemplateEntity entity = BeanUtil.toBean(messageTemplateUpForm, MessageTemplateEntity.class);
        if (entity != null) {
            if (messageTemplateService.isExistByFullName(entity.getFullName(), id)) {
                return ServiceResult.error("更新失败，名称不能重复");
            }
            if (messageTemplateService.isExistByEnCode(entity.getEnCode(), id)) {
                return ServiceResult.error("更新失败，编码不能重复");
            }
            boolean flag = messageTemplateService.update(id, entity);
            if (!flag) {
                return ServiceResult.error(MsgCode.FA002.get());
            }
            return ServiceResult.success(MsgCode.SU004.get());
        }
        return ServiceResult.error(MsgCode.FA002.get());
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
    @SaCheckPermission("msgTemplate")
    @DeleteMapping("/{id}")
    public ServiceResult<String> delete(@PathVariable("id") String id) {
        MessageTemplateEntity entity = messageTemplateService.getInfo(id);
        if (entity == null) {
            return ServiceResult.error(MsgCode.FA003.get());
        }
        messageTemplateService.delete(entity);
        return ServiceResult.success(MsgCode.SU003.get());
    }

    /**
     * 修改状态
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "修改状态")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("msgTemplate")
    @PutMapping("/{id}/Actions/State")
    public ServiceResult<String> update(@PathVariable("id") String id) {
        MessageTemplateEntity entity = messageTemplateService.getInfo(id);
        if (entity != null) {
            if (entity.getEnabledMark() == 0) {
                entity.setEnabledMark(1);
            } else {
                entity.setEnabledMark(0);
            }
            boolean flag = messageTemplateService.update(id, entity);
            if (!flag) {
                return ServiceResult.error(MsgCode.FA002.get());
            }
            return ServiceResult.success(MsgCode.SU004.get());
        }
        return ServiceResult.success(MsgCode.FA002.get());
    }
}
