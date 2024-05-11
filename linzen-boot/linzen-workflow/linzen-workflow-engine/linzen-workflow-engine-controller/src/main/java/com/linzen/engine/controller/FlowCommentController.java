package com.linzen.engine.controller;

import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.base.controller.SuperController;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.constant.MsgCode;
import com.linzen.engine.entity.FlowCommentEntity;
import com.linzen.engine.model.flowcomment.FlowCommentForm;
import com.linzen.engine.model.flowcomment.FlowCommentInfoVO;
import com.linzen.engine.model.flowcomment.FlowCommentListVO;
import com.linzen.engine.model.flowcomment.FlowCommentPagination;
import com.linzen.engine.service.FlowCommentService;
import com.linzen.exception.DataBaseException;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.util.JsonUtil;
import com.linzen.util.ServiceAllUtil;
import com.linzen.util.UploaderUtil;
import com.linzen.util.UserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程评论
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Tag(name = "流程评论", description = "Comment")
@RestController
@RequestMapping("/api/workflow/Engine/FlowComment")
public class FlowCommentController extends SuperController<FlowCommentService, FlowCommentEntity> {


    @Autowired
    private ServiceAllUtil serviceUtil;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private FlowCommentService flowCommentService;

    /**
     * 获取流程评论列表
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "获取流程评论列表")
    @GetMapping
    public ServiceResult<PageListVO<FlowCommentListVO>> list(FlowCommentPagination pagination) {
        List<FlowCommentEntity> list = flowCommentService.getlist(pagination);
        List<FlowCommentListVO> listVO = JsonUtil.createJsonToList(list, FlowCommentListVO.class);
        List<String> userId = list.stream().map(t -> t.getCreatorUserId()).collect(Collectors.toList());
        UserInfo userInfo = userProvider.get();
        List<SysUserEntity> userName = serviceUtil.getUserName(userId);
        for (FlowCommentListVO commentModel : listVO) {
            SysUserEntity userEntity = userName.stream().filter(t -> t.getId().equals(commentModel.getCreatorUserId())).findFirst().orElse(null);
            commentModel.setIsDel(commentModel.getCreatorUserId().equals(userInfo.getUserId()));
            commentModel.setCreatorUser(userEntity != null ? userEntity.getRealName() + "/" + userEntity.getAccount() : "");
            commentModel.setCreatorUserHeadIcon(userEntity != null ? UploaderUtil.uploaderImg(userEntity.getHeadIcon()) : commentModel.getCreatorUserHeadIcon());
        }
        PaginationVO vo = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(listVO, vo);
    }

    /**
     * 获取流程评论信息
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "获取流程评论信息")
    @GetMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult<FlowCommentInfoVO> info(@PathVariable("id") String id) {
        FlowCommentEntity entity = flowCommentService.getInfo(id);
        FlowCommentInfoVO vo = BeanUtil.toBean(entity, FlowCommentInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 新建流程评论
     *
     * @param commentForm 流程评论模型
     * @return
     */
    @Operation(summary = "新建流程评论")
    @PostMapping
    @Parameters({
            @Parameter(name = "commentForm", description = "流程评论模型", required = true),
    })
    public ServiceResult create(@RequestBody @Valid FlowCommentForm commentForm) throws DataBaseException {
        FlowCommentEntity entity = BeanUtil.toBean(commentForm, FlowCommentEntity.class);
        flowCommentService.create(entity);
        return ServiceResult.success(MsgCode.SU002.get());
    }

    /**
     * 更新流程评论
     *
     * @param id          主键
     * @param commentForm 流程评论模型
     * @return
     */
    @Operation(summary = "更新流程评论")
    @PutMapping("/{id}")
    @Parameters({
            @Parameter(name = "commentForm", description = "流程评论模型", required = true),
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid FlowCommentForm commentForm) throws DataBaseException {
        FlowCommentEntity info = flowCommentService.getInfo(id);
        if (info != null) {
            FlowCommentEntity entity = BeanUtil.toBean(commentForm, FlowCommentEntity.class);
            flowCommentService.update(id, entity);
            return ServiceResult.success(MsgCode.SU004.get());
        }
        return ServiceResult.error(MsgCode.FA002.get());
    }

    /**
     * 删除流程评论
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "删除流程评论")
    @DeleteMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult delete(@PathVariable("id") String id) {
        FlowCommentEntity entity = flowCommentService.getInfo(id);
        if (entity.getCreatorUserId().equals(userProvider.get().getUserId())) {
            flowCommentService.delete(entity);
            return ServiceResult.success(MsgCode.SU003.get());
        }
        return ServiceResult.success(MsgCode.FA003.get());
    }

}
