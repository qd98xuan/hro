package com.linzen.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.EmailConfigEntity;
import com.linzen.base.entity.EmailReceiveEntity;
import com.linzen.base.model.MailAccount;
import com.linzen.base.service.SysconfigService;
import com.linzen.base.util.Pop3Util;
import com.linzen.base.vo.PaginationVO;
import com.linzen.entity.EmailSendEntity;
import com.linzen.exception.DataBaseException;
import com.linzen.model.email.*;
import com.linzen.service.EmailReceiveService;
import com.linzen.util.JsonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 邮件配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "邮件收发", description = "Email")
@RestController
@RequestMapping("/api/extend/Email")
public class EmailController {

    @Autowired
    private EmailReceiveService emailReceiveService;
    @Autowired
    private Pop3Util pop3Util;
    @Autowired
    private SysconfigService sysconfigService;

    /**
     * 获取邮件列表(收件箱、标星件、草稿箱、已发送)
     *
     * @param paginationEmail 分页模型
     * @return
     */
    @Operation(summary = "获取邮件列表(收件箱、标星件、草稿箱、已发送)")
    @GetMapping
    @SaCheckPermission("extend.email")
    public ServiceResult receiveList(PaginationEmail paginationEmail) {
        String type = paginationEmail.getType() != null ? paginationEmail.getType() : "inBox";
        switch (type) {
            case "inBox":
                List<EmailReceiveEntity> entity = emailReceiveService.getReceiveList(paginationEmail);
                PaginationVO paginationVO = BeanUtil.toBean(paginationEmail, PaginationVO.class);
                List<EmailReceiveListVO> listVO = JsonUtil.createJsonToList(entity, EmailReceiveListVO.class);
                return ServiceResult.pageList(listVO,paginationVO);
            case "star":
                List<EmailReceiveEntity> entity1 = emailReceiveService.getStarredList(paginationEmail);
                PaginationVO paginationVo1 = BeanUtil.toBean(paginationEmail, PaginationVO.class);
                List<EmailStarredListVO> listVo1 = JsonUtil.createJsonToList(entity1, EmailStarredListVO.class);
                return ServiceResult.pageList(listVo1,paginationVo1);
            case "draft":
                List<EmailSendEntity> entity2 = emailReceiveService.getDraftList(paginationEmail);
                PaginationVO paginationVo2 = BeanUtil.toBean(paginationEmail, PaginationVO.class);
                List<EmailDraftListVO> listVo2 = JsonUtil.createJsonToList(entity2, EmailDraftListVO.class);
                return ServiceResult.pageList(listVo2,paginationVo2);
            case "sent":
                List<EmailSendEntity> entity3 = emailReceiveService.getSentList(paginationEmail);
                PaginationVO paginationVo3 = BeanUtil.toBean(paginationEmail, PaginationVO.class);
                List<EmailSentListVO> listVo3 = JsonUtil.createJsonToList(entity3, EmailSentListVO.class);
                return ServiceResult.pageList(listVo3,paginationVo3);
            default:
                return ServiceResult.error("获取失败");
        }
    }

    /**
     * 获取邮箱配置
     *
     * @return
     */
    @Operation(summary = "获取邮箱配置")
    @GetMapping("/Config")
    @SaCheckPermission("extend.email")
    public ServiceResult<EmailCofigInfoVO> configInfo() {
        EmailConfigEntity entity = emailReceiveService.getConfigInfo();
        EmailCofigInfoVO vo = BeanUtil.toBean(entity, EmailCofigInfoVO.class);
        if(vo==null){
            vo=new EmailCofigInfoVO();
        }
        return ServiceResult.success(vo);
    }

    /**
     * 获取邮件信息
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "获取邮件信息")
    @GetMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键",required = true),
    })
    @SaCheckPermission("extend.email")
    public ServiceResult<EmailInfoVO> info(@PathVariable("id") String id) throws DataBaseException {
        Object entity = emailReceiveService.getInfo(id);
        EmailInfoVO vo = BeanUtil.toBean(entity, EmailInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "删除邮件")
    @DeleteMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键",required = true),
    })
    @SaCheckPermission("extend.email")
    public ServiceResult delete(@PathVariable("id") String id) {
       boolean flag= emailReceiveService.delete(id);
        if(flag==false){
            return ServiceResult.error("删除失败，邮件不存在");
        }
        return ServiceResult.success("删除成功");
    }

    /**
     * 设置已读邮件
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "设置已读邮件")
    @PutMapping("/{id}/Actions/Read")
    @Parameters({
            @Parameter(name = "id", description = "主键",required = true),
    })
    @SaCheckPermission("extend.email")
    public ServiceResult receiveRead(@PathVariable("id") String id) {
        boolean flag= emailReceiveService.receiveRead(id, 1);
        if(flag==false){
            return ServiceResult.error("操作失败，邮件不存在");
        }
        return ServiceResult.success("操作成功");
    }

    /**
     * 设置未读邮件
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "设置未读邮件")
    @PutMapping("/{id}/Actions/Unread")
    @Parameters({
            @Parameter(name = "id", description = "主键",required = true),
    })
    @SaCheckPermission("extend.email")
    public ServiceResult receiveUnread(@PathVariable("id") String id) {
        boolean flag= emailReceiveService.receiveRead(id, 0);
        if(flag==false){
            return ServiceResult.error("操作失败，邮件不存在");
        }
        return ServiceResult.success("操作成功");
    }

    /**
     * 设置星标邮件
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "设置星标邮件")
    @PutMapping("/{id}/Actions/Star")
    @Parameters({
            @Parameter(name = "id", description = "主键",required = true),
    })
    @SaCheckPermission("extend.email")
    public ServiceResult receiveYesStarred(@PathVariable("id") String id) {
        boolean flag= emailReceiveService.receiveStarred(id, 1);
        if(flag==false){
            return ServiceResult.error("操作失败，邮件不存在");
        }
        return ServiceResult.success("操作成功");
    }

    /**
     * 设置取消星标
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "设置取消星标")
    @PutMapping("/{id}/Actions/Unstar")
    @Parameters({
            @Parameter(name = "id", description = "主键",required = true),
    })
    @SaCheckPermission("extend.email")
    public ServiceResult receiveNoStarred(@PathVariable("id") String id) {
        boolean flag= emailReceiveService.receiveStarred(id, 0);
        if(flag==false){
            return ServiceResult.error("操作失败，邮件不存在");
        }
        return ServiceResult.success("操作成功");
    }

    /**
     * 收邮件
     *
     * @return
     */
    @Operation(summary = "收邮件")
    @PostMapping("/Receive")
    @SaCheckPermission("extend.email")
    public ServiceResult receive() {
        EmailConfigEntity configEntity = emailReceiveService.getConfigInfo();
        if (configEntity != null) {
            MailAccount mailAccount = new MailAccount();
            mailAccount.setAccount(configEntity.getAccount());
            mailAccount.setPassword(configEntity.getPassword());
            mailAccount.setPop3Host(configEntity.getPop3Host());
            mailAccount.setPop3Port(configEntity.getPop3Port());
            mailAccount.setSmtpHost(configEntity.getSmtpHost());
            mailAccount.setSmtpPort(configEntity.getSmtpPort());
            if ("1".equals(String.valueOf(configEntity.getEmailSsl()))) {
                mailAccount.setSsl(true);
            } else {
                mailAccount.setSsl(false);
            }
            String checkResult=pop3Util.checkConnected(mailAccount);
            if ("true".equals(checkResult)) {
                int mailCount = emailReceiveService.receive(configEntity);
                return ServiceResult.success("操作成功", mailCount);
            } else {
                return ServiceResult.error("账户认证错误");
            }
        } else {
            return ServiceResult.error("你还没有设置邮件的帐户");
        }
    }

    /**
     * 存草稿
     *
     * @param emailSendCrForm 邮件模型
     * @return
     */
    @Operation(summary = "存草稿")
    @PostMapping("/Actions/SaveDraft")
    @Parameters({
            @Parameter(name = "emailSendCrForm", description = "邮件模型",required = true),
    })
    @SaCheckPermission("extend.email")
    public ServiceResult saveDraft(@RequestBody @Valid EmailSendCrForm emailSendCrForm) {
        EmailSendEntity entity = BeanUtil.toBean(emailSendCrForm, EmailSendEntity.class);
        emailReceiveService.saveDraft(entity);
        return ServiceResult.success("保存成功");
    }

    /**
     * 发邮件
     *
     * @param emailCrForm 发送邮件模型
     * @return
     */
    @Operation(summary = "发邮件")
    @PostMapping
    @Parameters({
            @Parameter(name = "emailCrForm", description = "发送邮件模型",required = true),
    })
    @SaCheckPermission("extend.email")
    public ServiceResult saveSent(@RequestBody @Valid EmailCrForm emailCrForm) {
        EmailSendEntity entity = BeanUtil.toBean(emailCrForm, EmailSendEntity.class);
        EmailConfigEntity configEntity = emailReceiveService.getConfigInfo();
        if (configEntity != null) {
            MailAccount mailAccount = new MailAccount();
            mailAccount.setAccount(configEntity.getAccount());
            mailAccount.setPassword(configEntity.getPassword());
            mailAccount.setPop3Host(configEntity.getPop3Host());
            mailAccount.setPop3Port(configEntity.getPop3Port());
            mailAccount.setSmtpHost(configEntity.getSmtpHost());
            mailAccount.setSmtpPort(configEntity.getSmtpPort());
            if ("1".equals(String.valueOf(configEntity.getEmailSsl()))) {
                mailAccount.setSsl(true);
            } else {
                mailAccount.setSsl(false);
            }
            int flag = emailReceiveService.saveSent(entity, configEntity);
            if (flag == 0) {
                return ServiceResult.success("发送成功");
            } else {
                return ServiceResult.error("账户认证错误");
            }
        } else {
            return ServiceResult.error("你还没有设置邮件的帐户");
        }
    }

    /**
     * 更新邮件配置
     *
     * @param emailCheckForm 邮件配置模型
     * @return
     */
    @Operation(summary = "更新邮件配置")
    @PutMapping("/Config")
    @Parameters({
            @Parameter(name = "emailCheckForm", description = "邮件配置模型",required = true),
    })
    @SaCheckPermission("extend.email")
    public ServiceResult saveConfig(@RequestBody @Valid EmailCheckForm emailCheckForm) throws DataBaseException {
        EmailConfigEntity entity = BeanUtil.toBean(emailCheckForm, EmailConfigEntity.class);
        emailReceiveService.saveConfig(entity);
        return ServiceResult.success("保存成功");
    }

    /**
     * 邮箱配置-测试连接
     *
     * @param emailCheckForm 邮件配置模型
     * @return
     */
    @Operation(summary = "邮箱配置-测试连接")
    @PostMapping("/Config/Actions/CheckMail")
    @Parameters({
            @Parameter(name = "emailCheckForm", description = "邮件配置模型",required = true),
    })
    @SaCheckPermission("extend.email")
    public ServiceResult checkLogin(@RequestBody @Valid EmailCheckForm emailCheckForm) {
        EmailConfigEntity entity = BeanUtil.toBean(emailCheckForm, EmailConfigEntity.class);
        String result = sysconfigService.checkLogin(entity);
        if ("true".equals(result)) {
            return ServiceResult.success("验证成功");
        } else {
            return ServiceResult.error("账户认证错误");
        }
    }

}
