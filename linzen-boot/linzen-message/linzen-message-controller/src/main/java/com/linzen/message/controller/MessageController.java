package com.linzen.message.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.base.controller.SuperController;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.entity.SuperBaseEntity;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.service.DictionaryTypeService;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.constant.MsgCode;
import com.linzen.exception.DataBaseException;
import com.linzen.message.entity.MessageEntity;
import com.linzen.message.entity.MessageReceiveEntity;
import com.linzen.message.model.NoticePagination;
import com.linzen.message.model.message.*;
import com.linzen.message.service.MessageService;
import com.linzen.message.service.UserDeviceService;
import com.linzen.message.util.unipush.UinPush;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.util.JsonUtilEx;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统公告
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "系统公告", description = "Message")
@RestController
@RequestMapping("/api/message")
public class MessageController extends SuperController<MessageService, MessageEntity> {

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    private UinPush uinPush;
    @Autowired
    private UserDeviceService userDeviceService;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private DictionaryTypeService dictionaryTypeService;

    /**
     * 列表（通知公告）
     *
     * @param pagination
     * @return
     */
    @Operation(summary = "获取系统公告列表（带分页）")
    @SaCheckPermission("system.notice")
    @PostMapping("/Notice/List")
    public ServiceResult<PageListVO<MessageNoticeVO>> NoticeList(@RequestBody NoticePagination pagination) {
        messageService.updatedelFlag();
        List<MessageEntity> list = messageService.getNoticeList(pagination);
        List<SysUserEntity> userList = userService.getUserName(list.stream().map(MessageEntity::getCreatorUserId).collect(Collectors.toList()));
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        List<DictionaryDataEntity> noticeType = dictionaryDataService.getListByTypeDataCode("NoticeType");
        List<MessageNoticeVO> voList = new ArrayList<>();
        // 判断是否过期
        list.forEach(t -> {
            MessageNoticeVO vo = BeanUtil.toBean(t, MessageNoticeVO.class);
            // 处理是否过期
            if (t.getExpirationTime() != null) {
                // 已发布的情况下
                if (t.getEnabledMark() == 1) {
                    if (t.getExpirationTime().getTime() < System.currentTimeMillis()) {
                        vo.setEnabledMark(2);
                    }
                }
            }
            DictionaryDataEntity dictionaryDataEntity = noticeType.stream().filter(notice -> notice.getEnCode().equals(t.getCategory())).findFirst().orElse(new DictionaryDataEntity());
            vo.setCategory(dictionaryDataEntity.getFullName());
            // 转换创建人、发布人
            SysUserEntity user = userList.stream().filter(ul -> ul.getId().equals(t.getCreatorUserId())).findFirst().orElse(null);
            vo.setCreatorUser(user != null ? user.getRealName() + "/" + user.getAccount() : "");
            if (t.getEnabledMark() != null && t.getEnabledMark() != 0) {
                SysUserEntity entity = userService.getInfo(t.getUpdateUserId());
                vo.setUpdateUserId(entity != null ? entity.getRealName() + "/" + entity.getAccount() : "");
                vo.setReleaseTime(t.getUpdateTime() != null ? t.getUpdateTime().getTime() : null);
                vo.setReleaseUser(vo.getUpdateUserId());
            }
            voList.add(vo);
        });
        return ServiceResult.pageList(voList, paginationVO);
    }

    /**
     * 添加系统公告
     *
     * @param noticeCrForm 实体对象
     * @return
     */
    @Operation(summary = "添加系统公告")
    @Parameters({
            @Parameter(name = "noticeCrForm", description = "新建系统公告模型", required = true)
    })
    @SaCheckPermission("system.notice")
    @PostMapping("/Notice")
    public ServiceResult create(@RequestBody @Valid NoticeCrForm noticeCrForm) {
        MessageEntity entity = BeanUtil.toBean(noticeCrForm, MessageEntity.class);
        if(entity != null && StringUtil.isNotEmpty(entity.getBodyText()) && (entity.getBodyText().contains("&lt;") || entity.getBodyText().contains("&amp;lt;"))){
            return ServiceResult.error("内容不能包含<符号");
        }
        messageService.create(entity);
        return ServiceResult.success("新建成功");
    }

    /**
     * 修改系统公告
     *
     * @param id            主键值
     * @param messageUpForm 实体对象
     * @return
     */
    @Operation(summary = "修改系统公告")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "messageUpForm", description = "修改系统公告模型", required = true)
    })
    @SaCheckPermission("system.notice")
    @PutMapping("/Notice/{id}")
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid NoticeUpForm messageUpForm) {
        MessageEntity entity = BeanUtil.toBean(messageUpForm, MessageEntity.class);
        if(entity != null && StringUtil.isNotEmpty(entity.getBodyText()) && (entity.getBodyText().contains("&lt;") || entity.getBodyText().contains("&amp;lt;"))){
            return ServiceResult.error("内容不能包含<符号");
        }
        boolean flag = messageService.update(id, entity);
        if (flag == false) {
            return ServiceResult.error("更新失败，数据不存在");
        }
        return ServiceResult.success("更新成功");
    }

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "获取/查看系统公告信息")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("system.notice")
    @GetMapping("/Notice/{id}")
    public ServiceResult<NoticeInfoVO> Info(@PathVariable("id") String id) throws DataBaseException {
        MessageEntity entity = messageService.getInfo(id);
        NoticeInfoVO vo = null;
        if (entity != null) {
            SysUserEntity info = userService.getInfo(entity.getCreatorUserId());
            entity.setCreatorUserId(info != null ? info.getRealName() + "/" + info.getAccount() : "");
            vo = JsonUtilEx.getJsonToBeanEx(entity, NoticeInfoVO.class);
            vo.setReleaseUser(entity.getCreatorUserId());
            vo.setReleaseTime(entity.getUpdateTime() != null ? entity.getUpdateTime().getTime() : null);
            SysUserEntity userEntity = userService.getInfo(entity.getUpdateUserId());
            if (userEntity != null && StringUtil.isNotEmpty(userEntity.getId())) {
                String realName = userEntity.getRealName();
                String account = userEntity.getAccount();
                if (StringUtil.isNotEmpty(realName)) {
                    vo.setReleaseUser(realName + "/" + account);
                }
            }
        }
        return ServiceResult.success(vo);
    }

    /**
     * 删除
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "删除系统公告")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("system.notice")
    @DeleteMapping("/Notice/{id}")
    public ServiceResult delete(@PathVariable("id") String id) {
        MessageEntity entity = messageService.getInfo(id);
        if (entity != null) {
            messageService.delete(entity);
            return ServiceResult.success(MsgCode.SU003.get());
        }
        return ServiceResult.error(MsgCode.FA003.get());
    }


    /**
     * 发布公告
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "发布系统公告")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("system.notice")
    @PutMapping("/Notice/{id}/Actions/Release")
    public ServiceResult release(@PathVariable("id") String id) {
        MessageEntity entity = messageService.getInfo(id);
        if (entity != null) {
            List<String> userIds = null;
            if (StringUtil.isNotEmpty(entity.getToUserIds())) {
                userIds = Arrays.asList(entity.getToUserIds().split(","));
            } else {
                userIds = userService.getListId();
            }
            List<String> userIdList = userService.getUserIdList(userIds, null);
            if (messageService.sentNotice(userIdList, entity)) {
            /*if(userIdList != null && userIdList.size()>0) {
                for (String userId : userIdList) {
                    List<String> cidList = userDeviceService.getCidList(userId);
                    if(cidList != null && cidList.size()>0){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("type","1");
                        jsonObject.put("id",entity.getId());
                        jsonObject.put("title",entity.getTitle());
                        String text = JSONObject.toJSONString(jsonObject);
                        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
                        text = Base64.getEncoder().encodeToString(bytes);
                        uinPush.sendUniPush(cidList, entity.getTitle(), "你有一条公告消息", "1", text);
                    }
                }
            }*/
                return ServiceResult.success("发布成功");
            }
        }
        return ServiceResult.error("发布失败");
    }
//=======================================站内消息、消息中心=================================================


    /**
     * 获取消息中心列表
     *
     * @param pagination
     * @return
     */
    @Operation(summary = "列表（通知公告/系统消息/私信消息）")
    @GetMapping
    public ServiceResult<PageListVO<MessageInfoVO>> messageList(PaginationMessage pagination) {
        List<MessageInfoVO> listVO = new ArrayList<>();
        List<MessageReceiveEntity> list = messageService.getMessageList1(pagination, pagination.getType(), pagination.getIsRead());
        List<SysUserEntity> userList = userService.getUserName(list.stream().map(SuperBaseEntity.SuperCBaseEntity::getCreatorUserId).collect(Collectors.toList()));
        list.forEach(t -> {
            MessageInfoVO vo = BeanUtil.toBean(t, MessageInfoVO.class);
            SysUserEntity user = userList.stream().filter(ul -> ul.getId().equals(t.getCreatorUserId())).findFirst().orElse(null);
            if (user != null) {
                vo.setReleaseTime(t.getCreatorTime() != null ? t.getCreatorTime().getTime() : null);
                SysUserEntity entity = userService.getInfo(t.getCreatorUserId());
                vo.setReleaseUser(entity != null ? entity.getRealName() + "/" + entity.getAccount() : "");
                vo.setCreatorUser(entity != null ? entity.getRealName() + "/" + entity.getAccount() : "");
            }
            listVO.add(vo);
        });
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(listVO, paginationVO);
    }


    /**
     * 读取消息
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "读取消息")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @GetMapping("/ReadInfo/{id}")
    public ServiceResult<NoticeInfoVO> readInfo(@PathVariable("id") String id) throws DataBaseException {
        MessageReceiveEntity receive = messageService.messageRead(id);
        if (receive != null) {
            SysUserEntity user = userService.getInfo(receive.getUserId());
            receive.setCreatorUserId(user != null ? user.getRealName() + "/" + user.getAccount() : "");
            receive.setBodyText(receive.getBodyText());
//            if (entity.getType() == 2) {
//                entity.setBodyText(receive.getBodyText());
//            }
        }
        NoticeInfoVO vo = BeanUtil.toBean(receive, NoticeInfoVO.class);
        if (Objects.equals(receive.getType() , 1)) {
            MessageEntity jsonToBean = BeanUtil.toBean(receive.getBodyText(), MessageEntity.class);
            if (jsonToBean != null) {
                vo.setCategory(jsonToBean.getCategory());
                vo.setCoverImage(jsonToBean.getCoverImage());
                vo.setExcerpt(jsonToBean.getExcerpt());
                vo.setExpirationTime(jsonToBean.getExpirationTime() != null ? jsonToBean.getExpirationTime().getTime() : null);
                vo.setFiles(jsonToBean.getFiles());
                vo.setBodyText(jsonToBean.getBodyText());
            }
        }
        vo.setReleaseTime(receive.getCreatorTime() != null ? receive.getCreatorTime().getTime() : null);
//        UserEntity info = usersApi.getInfoById(receive.getCreatorUserId());
        vo.setReleaseUser(receive.getCreatorUserId());
        return ServiceResult.success(vo);
    }


    /**
     * 全部已读
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "全部已读")
    @Parameters({
            @Parameter(name = "pagination", description = "分页模型", required = true)
    })
    @PostMapping("/Actions/ReadAll")
    public ServiceResult allRead(@RequestBody PaginationMessage pagination) {
        List<MessageReceiveEntity> list = messageService.getMessageList3(pagination, pagination.getType(),null,pagination.getIsRead());
        if(list != null && list.size()>0) {
            List<String> idList = list.stream().map(SuperBaseEntity.SuperIBaseEntity::getId).collect(Collectors.toList());
            messageService.messageRead(idList);
            return ServiceResult.success("操作成功");
        }else {
            return ServiceResult.error("暂无未读消息");
        }
    }

    /**
     * app端获取未读数据
     *
     * @return
     */
    @Operation(summary = "app端获取未读数据")
    @GetMapping("/getUnReadMsgNum")
    public ServiceResult getUnReadMsgNum() {
        Map<String, String> map = new HashMap<>();
        UserInfo userInfo = UserProvider.getUser();
        Integer unReadMsg = messageService.getUnreadCount(userInfo.getUserId(), 2);
        Integer unReadSchedule = messageService.getUnreadCount(userInfo.getUserId(),4);
        Integer unReadNotice = messageService.getUnreadCount(userInfo.getUserId(), 1);
        Integer unReadSystemMsg = messageService.getUnreadCount(userInfo.getUserId(), 3);
        Integer unReadNum = unReadMsg+unReadNotice+unReadSchedule+unReadSystemMsg;
        map.put("unReadMsg",unReadMsg.toString());
        map.put("unReadNotice",unReadNotice.toString());
        map.put("unReadSchedule",unReadSchedule.toString());
        map.put("unReadSystemMsg",unReadSystemMsg.toString());
        map.put("unReadNum",unReadNum.toString());
        return ServiceResult.success(map);
    }

    /**
     * 删除记录
     *
     * @param recordForm 已读模型
     * @return
     */
    @Operation(summary = "删除消息")
    @Parameters({
            @Parameter(name = "recordForm", description = "已读模型", required = true)
    })
    @DeleteMapping("/Record")
    public ServiceResult deleteRecord(@RequestBody MessageRecordForm recordForm) {
        String[] id = recordForm.getIds().split(",");
        List<String> list = Arrays.asList(id);
        messageService.deleteRecord(list);
        return ServiceResult.success(MsgCode.SU003.get());
    }
}
