


package com.linzen.message.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.base.controller.SuperController;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.constant.MsgCode;
import com.linzen.exception.DataBaseException;
import com.linzen.message.entity.MessageMonitorEntity;
import com.linzen.message.model.messagemonitor.*;
import com.linzen.message.service.MessageMonitorService;
import com.linzen.util.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * 消息监控
 *
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Slf4j
@RestController
@Tag(name = "消息监控", description = "message")
@RequestMapping("/api/message/MessageMonitor")
public class MessageMonitorController extends SuperController<MessageMonitorService, MessageMonitorEntity> {


    @Autowired
    private UserProvider userProvider;


    @Autowired
    private MessageMonitorService messageMonitorService;
    @Autowired
    private DictionaryDataService dictionaryDataService;


    /**
     * 列表
     *
     * @param messageMonitorPagination 消息监控分页模型
     * @return ignore
     */
    @Operation(summary = "列表")
    @SaCheckPermission("msgCenter.msgMonitor")
    @GetMapping
    public ServiceResult<PageListVO<MessageMonitorListVO>> list(MessageMonitorPagination messageMonitorPagination) throws IOException {
        List<MessageMonitorEntity> list = messageMonitorService.getList(messageMonitorPagination);

        List<DictionaryDataEntity> msgSendTypeList = dictionaryDataService.getListByTypeDataCode("msgSendType");
        List<DictionaryDataEntity> msgSourceTypeList = dictionaryDataService.getListByTypeDataCode("msgSourceType");

        //处理id字段转名称，若无需转或者为空可删除
        List<MessageMonitorListVO> listVO = JsonUtil.createJsonToList(list, MessageMonitorListVO.class);
        for (MessageMonitorListVO messageMonitorVO : listVO) {
            //消息类型
            if (StringUtil.isNotEmpty(messageMonitorVO.getMessageType())) {
                msgSendTypeList.stream().filter(t -> messageMonitorVO.getMessageType().equals(t.getEnCode())).findFirst()
                        .ifPresent(dataTypeEntity -> messageMonitorVO.setMessageType(dataTypeEntity.getFullName()));
            }
            //消息来源
            if (StringUtil.isNotEmpty(messageMonitorVO.getMessageSource())) {
                msgSourceTypeList.stream().filter(t -> messageMonitorVO.getMessageSource().equals(t.getEnCode())).findFirst()
                        .ifPresent(dataTypeEntity -> messageMonitorVO.setMessageSource(dataTypeEntity.getFullName()));
            }
            //子表数据转换
        }

        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        PaginationVO page = BeanUtil.toBean(messageMonitorPagination, PaginationVO.class);
        vo.setPagination(page);
        return ServiceResult.success(vo);
    }

    /**
     * 创建
     *
     * @param messageMonitorForm 消息监控模型
     * @return ignore
     */
    @Operation(summary = ("创建"))
    @PostMapping
    @Parameters({
            @Parameter(name = "messageMonitorForm", description = "消息监控模型", required = true)
    })
    @SaCheckPermission("msgCenter.msgMonitor")
    @Transactional
    public ServiceResult create(@RequestBody @Valid MessageMonitorForm messageMonitorForm) throws DataBaseException {
        String mainId = RandomUtil.uuId();
        UserInfo userInfo = userProvider.get();
        MessageMonitorEntity entity = BeanUtil.toBean(messageMonitorForm, MessageMonitorEntity.class);
        entity.setCreatorTime(DateUtil.getNowDate());
        entity.setCreatorUserId(userInfo.getUserId());
        entity.setId(mainId);
        messageMonitorService.save(entity);

        return ServiceResult.success("创建成功");
    }


    /**
     * 批量删除
     *
     * @param msgDelForm 消息删除模型
     * @return ignore
     */
    @Operation(summary = ("批量删除"))
    @DeleteMapping("/batchRemove")
    @Parameters({
            @Parameter(name = "msgDelForm", description = "消息删除模型", required = true)
    })
    @SaCheckPermission("msgCenter.msgMonitor")
    @Transactional
    public ServiceResult batchRemove(@RequestBody MsgDelForm msgDelForm) {
        boolean flag = messageMonitorService.delete(msgDelForm.getIds());
        if (flag == false) {
            return ServiceResult.error("删除失败");
        }
        return ServiceResult.success("删除成功");
    }


    /**
     * 一键清空消息监控记录
     *
     * @return
     */
    @Operation(summary = "一键清空消息监控记录")
    @SaCheckPermission("msgCenter.msgMonitor")
    @DeleteMapping("/empty")
    public ServiceResult deleteHandelLog() {
        messageMonitorService.emptyMonitor();
        return ServiceResult.success(MsgCode.SU005.get());
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
    @SaCheckPermission("msgCenter.msgMonitor")
    @GetMapping("/{id}")
    public ServiceResult<MessageMonitorInfoVO> info(@PathVariable("id") String id) {
        MessageMonitorEntity entity = messageMonitorService.getInfo(id);
        MessageMonitorInfoVO vo = BeanUtil.toBean(entity, MessageMonitorInfoVO.class);

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
    @SaCheckPermission("msgCenter.msgMonitor")
    @GetMapping("/detail/{id}")
    public ServiceResult<MessageMonitorInfoVO> detailInfo(@PathVariable("id") String id) {
        MessageMonitorEntity entity = messageMonitorService.getInfo(id);

        List<DictionaryDataEntity> msgSendTypeList = dictionaryDataService.getListByTypeDataCode("msgSendType");
        List<DictionaryDataEntity> msgSourceTypeList = dictionaryDataService.getListByTypeDataCode("msgSourceType");

        MessageMonitorInfoVO vo = BeanUtil.toBean(entity, MessageMonitorInfoVO.class);
        if (StringUtil.isNotEmpty(vo.getMessageType())) {
            msgSendTypeList.stream().filter(t -> vo.getMessageType().equals(t.getEnCode())).findFirst()
                    .ifPresent(dataTypeEntity -> vo.setMessageType(dataTypeEntity.getFullName()));
        }
        if (StringUtil.isNotEmpty(vo.getMessageSource())) {
            msgSourceTypeList.stream().filter(t -> vo.getMessageSource().equals(t.getEnCode())).findFirst()
                    .ifPresent(dataTypeEntity -> vo.setMessageSource(dataTypeEntity.getFullName()));
        }
        if (!"webhook".equals(vo.getMessageType())) {
            vo.setReceiveUser(messageMonitorService.userSelectValues(vo.getReceiveUser()));
        }
        return ServiceResult.success(vo);
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return ignore
     */
    @DeleteMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("msgCenter.msgMonitor")
    @Transactional
    public ServiceResult delete(@PathVariable("id") String id) {
        MessageMonitorEntity entity = messageMonitorService.getInfo(id);
        if (entity != null) {
            messageMonitorService.delete(entity);

        }
        return ServiceResult.success("删除成功");
    }


}
