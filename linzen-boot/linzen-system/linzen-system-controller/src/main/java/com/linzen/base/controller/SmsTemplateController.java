package com.linzen.base.controller;

import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.Page;
import com.linzen.base.Pagination;
import com.linzen.base.entity.SmsTemplateEntity;
import com.linzen.base.model.smstemplate.*;
import com.linzen.base.model.systemconfig.SmsModel;
import com.linzen.base.service.SmsTemplateService;
import com.linzen.base.util.SmsUtil;
import com.linzen.base.vo.ListVO;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.constant.MsgCode;
import com.linzen.util.JsonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 短信模板控制类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(description = "SmsTemplateController", name = "短信模板控制类")
@RestController
@RequestMapping("/api/system/SmsTemplate")
public class SmsTemplateController extends SuperController<SmsTemplateService, SmsTemplateEntity> {

    @Autowired
    private SmsTemplateService smsTemplateService;

    /**
     * 短信模板列表
     *
     * @param pagination
     * @return
     */
    @Operation(summary = "短信模板列表")
    @GetMapping
    public ServiceResult<PageListVO<SmsTemplateListVO>> list(Pagination pagination) {
        List<SmsTemplateEntity> list = smsTemplateService.getList(pagination);
        List<SmsTemplateListVO> listVO = JsonUtil.createJsonToList(list, SmsTemplateListVO.class);
        for (SmsTemplateListVO smsTemplateListVO : listVO) {
            if ("1".equals(smsTemplateListVO.getCompany())) {
                smsTemplateListVO.setCompany("阿里");
            } else if ("2".equals(smsTemplateListVO.getCompany())) {
                smsTemplateListVO.setCompany("腾讯");
            }
        }
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(listVO, paginationVO);
    }

    /**
     * 短信模板下拉框
     *
     * @return
     */
    @Operation(summary = "短信模板下拉框")
    @GetMapping("/Selector")
    public ServiceResult<ListVO<SmsTemplateSelector>> selector(Page page) {
        List<SmsTemplateEntity> list = smsTemplateService.getList(page.getKeyword());
        List<SmsTemplateSelector> jsonToList = JsonUtil.createJsonToList(list, SmsTemplateSelector.class);
        for (SmsTemplateSelector smsTemplateSelector : jsonToList) {
            if ("1".equals(smsTemplateSelector.getCompany())) {
                smsTemplateSelector.setCompany("阿里");
            } else if ("2".equals(smsTemplateSelector.getCompany())) {
                smsTemplateSelector.setCompany("腾讯");
            }
        }
        ListVO<SmsTemplateSelector> listVO = new ListVO<>();
        listVO.setList(jsonToList);
        return ServiceResult.success(listVO);
    }

    /**
     * 获取消息模板
     *
     * @param id
     * @return
     */
    @Operation(summary = "获取短信模板")
    @GetMapping("/{id}")
    public ServiceResult<SmsTemplateVO> info(@PathVariable("id") String id) {
        SmsTemplateEntity entity = smsTemplateService.getInfo(id);
        SmsTemplateVO vo = BeanUtil.toBean(entity, SmsTemplateVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 新建
     *
     * @return
     */
    @Operation(summary = "新建")
    @PostMapping
    public ServiceResult<String> create(@RequestBody @Valid SmsTemplateCrForm smsTemplateCrForm) {
        SmsTemplateEntity entity = BeanUtil.toBean(smsTemplateCrForm, SmsTemplateEntity.class);
        if (smsTemplateService.isExistByTemplateName(entity.getFullName(), entity.getId())) {
            return ServiceResult.error("新建失败，模板名称不能重复");
        }
        if (smsTemplateService.isExistByEnCode(entity.getEnCode(), entity.getId())) {
            return ServiceResult.error("新建失败，模板编码不能重复");
        }
        smsTemplateService.create(entity);
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 修改
     *
     * @return
     */
    @Operation(summary = "修改")
    @PutMapping("/{id}")
    public ServiceResult<String> update(@PathVariable("id") String id, @RequestBody @Valid SmsTemplateUpForm smsTemplateUpForm) {
        SmsTemplateEntity entity = BeanUtil.toBean(smsTemplateUpForm, SmsTemplateEntity.class);
        if (smsTemplateService.isExistByTemplateName(entity.getFullName(), id)) {
            return ServiceResult.error("修改失败，模板名称不能重复");
        }
        if (smsTemplateService.isExistByEnCode(entity.getEnCode(), id)) {
            return ServiceResult.error("修改失败，模板编码不能重复");
        }
        boolean flag = smsTemplateService.update(id, entity);
        if (!flag) {
            return ServiceResult.error(MsgCode.FA002.get());
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 删除
     *
     * @return
     */
    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    public ServiceResult<String> delete(@PathVariable("id") String id) {
        SmsTemplateEntity entity = smsTemplateService.getInfo(id);
        if (entity == null) {
            return ServiceResult.error(MsgCode.FA003.get());
        }
        smsTemplateService.delete(entity);
        return ServiceResult.success(MsgCode.SU003.get());
    }

    /**
     * 修改状态
     *
     * @return
     */
    @Operation(summary = "修改状态")
    @PutMapping("/{id}/Actions/State")
    public ServiceResult<String> update(@PathVariable("id") String id) {
        SmsTemplateEntity entity = smsTemplateService.getInfo(id);
        if (entity != null) {
            if (entity.getEnabledMark() == 0) {
                entity.setEnabledMark(1);
            } else {
                entity.setEnabledMark(0);
            }
            boolean flag = smsTemplateService.update(id, entity);
            if (!flag) {
                return ServiceResult.error(MsgCode.FA002.get());
            }
            return ServiceResult.success(MsgCode.SU004.get());
        }
        return ServiceResult.error(MsgCode.FA002.get());
    }

    @Operation(summary = "获取模板参数")
    @PostMapping("/getTemplate")
    public ServiceResult<?> testConnect(@RequestBody SmsTemplateCrForm smsTemplateCrForm) {
        // 定义返回对象
        List<String> list = null;
        if (smsTemplateCrForm != null) {
            // 得到短信模型
            SmsModel smsModel = smsTemplateService.getSmsConfig();
            list = SmsUtil.querySmsTemplateRequest(smsTemplateCrForm.getCompany(), smsModel, smsTemplateCrForm.getEndpoint(), smsTemplateCrForm.getRegion(), smsTemplateCrForm.getTemplateId());
        }
        if (list == null) {
            return ServiceResult.error("短信模板不存在");
        }
        return ServiceResult.success(list);
    }

    /**
     * 获取指定短信模板参数
     *
     * @return
     */
    @Operation(summary = "获取指定短信模板参数")
    @GetMapping("/getTemplate/{id}")
    public ServiceResult<?> getTemplateById(@PathVariable("id") String id) {
        // 定义返回对象
        List<String> list = new ArrayList<>();
        SmsTemplateEntity entity = smsTemplateService.getInfo(id);
        if (entity != null && entity.getCompany() != null) {
            // 得到系统配置
            SmsModel smsModel = smsTemplateService.getSmsConfig();
            list = SmsUtil.querySmsTemplateRequest(entity.getCompany(), smsModel, entity.getEndpoint(), entity.getRegion(), entity.getTemplateId());
        }
        if (list == null) {
            return ServiceResult.success(new ArrayList<>());
        }
        return ServiceResult.success(list);
    }

    @Operation(summary = "发送测试短信")
    @PostMapping("/testSent")
    public ServiceResult testSentSms(@RequestBody SmsTemplateCrForm smsTemplateCrForm) {
        if (smsTemplateCrForm.getCompany() != null) {
            // 得到短信模型
            SmsModel smsModel = smsTemplateService.getSmsConfig();
            // 发送短信
            String sentCode = SmsUtil.sentSms(smsTemplateCrForm.getCompany(), smsModel, smsTemplateCrForm.getEndpoint(), smsTemplateCrForm.getRegion(), smsTemplateCrForm.getPhoneNumbers(), smsTemplateCrForm.getSignContent(), smsTemplateCrForm.getTemplateId(), smsTemplateCrForm.getParameters());
            if ("OK".equalsIgnoreCase(sentCode)) {
                return ServiceResult.success("验证通过");
            }
        }
        return ServiceResult.error("验证失败");
    }


}
