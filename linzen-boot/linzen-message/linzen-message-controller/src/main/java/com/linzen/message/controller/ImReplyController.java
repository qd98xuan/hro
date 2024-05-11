package com.linzen.message.controller;

import com.linzen.base.ServiceResult;
import com.linzen.base.controller.SuperController;
import com.linzen.base.vo.ListVO;
import com.linzen.message.entity.ImReplyEntity;
import com.linzen.message.model.ImReplyListModel;
import com.linzen.message.model.ImReplyListVo;
import com.linzen.message.service.ImContentService;
import com.linzen.message.service.ImReplyService;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.util.JsonUtil;
import com.linzen.util.UploaderUtil;
import com.linzen.util.UserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息会话接口
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "消息会话接口", description = "imreply")
@RestController
@RequestMapping("/api/message/imreply")
public class ImReplyController extends SuperController<ImReplyService, ImReplyEntity> {
    @Autowired
    private ImReplyService imReplyService;
    @Autowired
    private ImContentService imContentService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserProvider userProvider;

    /**
     * 获取消息会话列表
     *
     * @return
     */
    @Operation(summary = "获取消息会话列表")
    @GetMapping
    public ServiceResult<ListVO<ImReplyListVo>> getList() {
        List<ImReplyListModel> imReplyList = imReplyService.getImReplyList();
        //过滤 发送者删除标记
        imReplyList = imReplyList.stream().filter(t -> {
            if (t.getImreplySendDelFlag() != null) {
                return !t.getImreplySendDelFlag().equals(userProvider.get().getUserId());
            }
            return true;
        }).collect(Collectors.toList());
        List<ImReplyListModel> imReplyLists = new ArrayList<>(imReplyList);
        for (ImReplyListModel vo : imReplyList) {
            SysUserEntity entity = userService.getInfo(vo.getId());
            if (entity == null || entity.getEnabledMark() == 0) {
                imReplyLists.remove(vo);
                continue;
            }
            //拼接账号和名称
            vo.setRealName(entity.getRealName());
            vo.setAccount(entity.getAccount());
            //头像路径拼接
            vo.setHeadIcon(UploaderUtil.uploaderImg(vo.getHeadIcon()));
            //获取未读消息
            vo.setUnreadMessage(imContentService.getUnreadCount(vo.getId(), userProvider.get().getUserId()));
            if (vo.getSendDelFlag() != null && vo.getSendDelFlag().equals(userProvider.get().getUserId()) || vo.getDelFlag() == 1) {
                vo.setLatestMessage("");
                vo.setMessageType("");
            }
        }
        //排序
        imReplyLists = imReplyLists.stream().sorted(Comparator.comparing(ImReplyListModel::getLatestDate).reversed()).collect(Collectors.toList());
        List<ImReplyListVo> imReplyListVoList = JsonUtil.createJsonToList(imReplyLists, ImReplyListVo.class);
        ListVO listVO = new ListVO();
        listVO.setList(imReplyListVoList);
        return ServiceResult.success(listVO);
    }

    /**
     * 删除聊天记录
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "删除聊天记录")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @DeleteMapping("/deleteChatRecord/{id}")
    public ServiceResult deleteChatRecord(@PathVariable("id") String id) {
        imContentService.deleteChatRecord(userProvider.get().getUserId(), id);
        return ServiceResult.success("");
    }

    /**
     * 移除会话列表
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "移除会话列表")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @DeleteMapping("/relocation/{id}")
    public ServiceResult relocation(@PathVariable("id") String id) {
        imReplyService.relocation(userProvider.get().getUserId(), id);
        return ServiceResult.success("");
    }


}
