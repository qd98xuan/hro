package com.linzen.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.Page;
import com.linzen.base.controller.SuperController;
import com.linzen.base.vo.ListVO;
import com.linzen.entity.ProjectGanttEntity;
import com.linzen.exception.DataBaseException;
import com.linzen.model.projectgantt.*;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.service.ProjectGanttService;
import com.linzen.util.JsonUtil;
import com.linzen.util.UploaderUtil;
import com.linzen.util.treeutil.ListToTreeUtil;
import com.linzen.util.treeutil.SumTree;
import com.linzen.util.treeutil.TreeDotUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目计划
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "项目计划", description = "ProjectGantt")
@RestController
@RequestMapping("/api/extend/ProjectGantt")
public class ProjectGanttController extends SuperController<ProjectGanttService, ProjectGanttEntity> {

    @Autowired
    private ProjectGanttService projectGanttService;
    @Autowired
    private UserService userService;


    /**
     * 项目列表
     *
     * @param page 分页模型
     * @return
     */
    @Operation(summary = "获取项目管理列表")
    @GetMapping
    @SaCheckPermission("extend.projectGantt")
    public ServiceResult<ListVO<ProjectGanttListVO>> list(Page page) {
        List<ProjectGanttEntity> data = projectGanttService.getList(page);
        List<ProjectGanttListVO> list = JsonUtil.createJsonToList(data, ProjectGanttListVO.class);
        //获取用户给项目参与人员列表赋值
        List<String> userId = new ArrayList<>();
        list.forEach(t -> {
            String[] ids = t.getManagerIds().split(",");
            Collections.addAll(userId, ids);
        });
        List<SysUserEntity> userList = userService.getUserName(userId);
        for (ProjectGanttListVO vo : list) {
            List<String> managerList = new ArrayList<>();
            Collections.addAll(managerList, vo.getManagerIds().split(","));
            List<SysUserEntity> user = userList.stream().filter(t -> managerList.contains(t.getId())).collect(Collectors.toList());
            List<ProjectGanttManagerIModel> list1 = new ArrayList<>();
            user.forEach(t -> {
                ProjectGanttManagerIModel model1 = new ProjectGanttManagerIModel();
                model1.setAccount(t.getRealName() + "/" + t.getAccount());
                model1.setHeadIcon(UploaderUtil.uploaderImg(t.getHeadIcon()));
                list1.add(model1);
            });
            vo.setManagersInfo(list1);
        }
        ListVO listVO = new ListVO<>();
        listVO.setList(list);
        return ServiceResult.success(listVO);
    }

    /**
     * 任务列表
     *
     * @param page 分页模型
     * @param projectId 主键
     * @return
     */
    @Operation(summary = "获取项目任务列表")
    @GetMapping("/{projectId}/Task")
    @Parameters({
            @Parameter(name = "projectId", description = "主键",required = true),
    })
    @SaCheckPermission("extend.projectGantt")
    public ServiceResult<ListVO<ProjectGanttTaskTreeVO>> taskList(Page page, @PathVariable("projectId") String projectId) {
        List<ProjectGanttEntity> data = projectGanttService.getTaskList(projectId);
        List<ProjectGanttEntity> dataAll = data;
        if (!StringUtils.isEmpty(page.getKeyword())) {
            data = data.stream().filter(t -> String.valueOf(t.getFullName()).contains(page.getKeyword()) || String.valueOf(t.getEnCode()).contains(page.getKeyword())).collect(Collectors.toList());
        }
        List<ProjectGanttEntity> list = JsonUtil.createJsonToList(ListToTreeUtil.treeWhere(data, dataAll), ProjectGanttEntity.class);
        List<ProjectGanttTreeModel> treeList = JsonUtil.createJsonToList(list, ProjectGanttTreeModel.class);
        List<SumTree<ProjectGanttTreeModel>> trees = TreeDotUtils.convertListToTreeDot(treeList);
        List<ProjectGanttTaskTreeVO> listVO = JsonUtil.createJsonToList(trees, ProjectGanttTaskTreeVO.class);
        ListVO vo = new ListVO();
        vo.setList(listVO);
        return ServiceResult.success(vo);
    }

    /**
     * 任务树形
     *
     * @param projectId 主键
     * @param id 主键
     * @return
     */
    @Operation(summary = "获取项目计划任务树形（新建任务）")
    @GetMapping("/{projectId}/Task/Selector/{id}")
    @Parameters({
            @Parameter(name = "projectId", description = "主键",required = true),
            @Parameter(name = "id", description = "主键",required = true),
    })
    @SaCheckPermission("extend.projectGantt")
    public ServiceResult<ListVO<ProjectGanttTaskTreeVO>> taskTreeView(@PathVariable("projectId") String projectId, @PathVariable("id") String id) {
        List<ProjectGanttTaskTreeModel> treeList = new ArrayList<>();
        List<ProjectGanttEntity> data = projectGanttService.getTaskList(projectId);
        if (!"0".equals(id)) {
            //上级不能选择自己
            data.remove(projectGanttService.getInfo(id));
        }
        for (ProjectGanttEntity entity : data) {
            ProjectGanttTaskTreeModel treeModel = new ProjectGanttTaskTreeModel();
            treeModel.setId(entity.getId());
            treeModel.setFullName(entity.getFullName());
            treeModel.setParentId(entity.getParentId());
            treeList.add(treeModel);
        }
        List<SumTree<ProjectGanttTaskTreeModel>> trees = TreeDotUtils.convertListToTreeDotFilter(treeList);
        List<ProjectGanttTaskTreeVO> listVO = JsonUtil.createJsonToList(trees, ProjectGanttTaskTreeVO.class);
        ListVO vo = new ListVO();
        vo.setList(listVO);
        return ServiceResult.success(vo);
    }

    /**
     * 信息
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "获取项目计划信息")
    @GetMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键",required = true),
    })
    @SaCheckPermission("extend.projectGantt")
    public ServiceResult<ProjectGanttInfoVO> info(@PathVariable("id") String id) throws DataBaseException {
        ProjectGanttEntity entity = projectGanttService.getInfo(id);
        ProjectGanttInfoVO vo = BeanUtil.toBean(entity, ProjectGanttInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 信息
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "获取项目计划信息")
    @GetMapping("Task/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键",required = true),
    })
    @SaCheckPermission("extend.projectGantt")
    public ServiceResult<ProjectGanttTaskInfoVO> taskInfo(@PathVariable("id") String id) throws DataBaseException {
        ProjectGanttEntity entity = projectGanttService.getInfo(id);
        ProjectGanttTaskInfoVO vo = BeanUtil.toBean(entity, ProjectGanttTaskInfoVO.class);
        return ServiceResult.success(vo);
    }


    /**
     * 删除
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "删除项目计划/任务")
    @DeleteMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键",required = true),
    })
    @SaCheckPermission("extend.projectGantt")
    public ServiceResult delete(@PathVariable("id") String id) {
        if (projectGanttService.allowDelete(id)) {
            ProjectGanttEntity entity = projectGanttService.getInfo(id);
            if (entity != null) {
                projectGanttService.delete(entity);
                return ServiceResult.success("删除成功");
            }
            return ServiceResult.error("删除失败，此任务不存在");
        } else {
            return ServiceResult.error("此记录被关联引用,不允许被删除");
        }
    }

    /**
     * 创建
     *
     * @param projectGanttCrForm 项目模型
     * @return
     */
    @Operation(summary = "添加项目计划")
    @PostMapping
    @Parameters({
            @Parameter(name = "projectGanttCrForm", description = "项目模型",required = true),
    })
    @SaCheckPermission("extend.projectGantt")
    public ServiceResult create(@RequestBody @Valid ProjectGanttCrForm projectGanttCrForm) {
        ProjectGanttEntity entity = BeanUtil.toBean(projectGanttCrForm, ProjectGanttEntity.class);
        entity.setType(1);
        entity.setParentId("0");
        if (projectGanttService.isExistByFullName(projectGanttCrForm.getFullName(), entity.getId())) {
            return ServiceResult.error("项目名称不能重复");
        }
        if (projectGanttService.isExistByEnCode(projectGanttCrForm.getEnCode(), entity.getId())) {
            return ServiceResult.error("项目编码不能重复");
        }
        projectGanttService.create(entity);
        return ServiceResult.success("新建成功");
    }

    /**
     * 编辑
     *
     * @param id                 主键
     * @param projectGanttUpForm 项目模型
     * @return
     */
    @Operation(summary = "修改项目计划")
    @PutMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键",required = true),
            @Parameter(name = "projectGanttUpForm", description = "项目模型",required = true),
    })
    @SaCheckPermission("extend.projectGantt")
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid ProjectGanttUpForm projectGanttUpForm) {
        ProjectGanttEntity entity = BeanUtil.toBean(projectGanttUpForm, ProjectGanttEntity.class);
        if (projectGanttService.isExistByFullName(projectGanttUpForm.getFullName(), id)) {
            return ServiceResult.error("项目名称不能重复");
        }
        if (projectGanttService.isExistByEnCode(projectGanttUpForm.getEnCode(), id)) {
            return ServiceResult.error("项目编码不能重复");
        }
        boolean flag = projectGanttService.update(id, entity);
        if (flag == false) {
            return ServiceResult.error("更新失败，数据不存在");
        }
        return ServiceResult.success("更新成功");
    }


    /**
     * 创建
     *
     * @param projectGanttTsakCrForm 项目模型
     * @return
     */
    @Operation(summary = "添加项目任务")
    @PostMapping("/Task")
    @Parameters({
            @Parameter(name = "projectGanttTsakCrForm", description = "项目模型",required = true),
    })
    @SaCheckPermission("extend.projectGantt")
    public ServiceResult createTask(@RequestBody @Valid ProjectGanttTsakCrForm projectGanttTsakCrForm) {
        ProjectGanttEntity entity = BeanUtil.toBean(projectGanttTsakCrForm, ProjectGanttEntity.class);
        entity.setType(2);
        if (projectGanttService.isExistByFullName(projectGanttTsakCrForm.getFullName(), entity.getId())) {
            return ServiceResult.error("任务名称不能重复");
        }
        projectGanttService.create(entity);
        return ServiceResult.success("新建成功");
    }

    /**
     * 编辑
     *
     * @param id                     主键
     * @param projectGanttTsakCrForm 项目模型
     * @return
     */
    @Operation(summary = "修改项目任务")
    @PutMapping("/Task/{id}")
    @Parameters({
            @Parameter(name = "projectGanttTsakCrForm", description = "项目模型",required = true),
            @Parameter(name = "id", description = "主键",required = true),
    })
    @SaCheckPermission("extend.projectGantt")
    public ServiceResult updateTask(@PathVariable("id") String id, @RequestBody @Valid ProjectGanttTsakUpForm projectGanttTsakCrForm) {
        ProjectGanttEntity entity = BeanUtil.toBean(projectGanttTsakCrForm, ProjectGanttEntity.class);
        if (projectGanttService.isExistByFullName(projectGanttTsakCrForm.getFullName(), id)) {
            return ServiceResult.error("任务名称不能重复");
        }
        boolean flag = projectGanttService.update(id, entity);
        if (flag == false) {
            return ServiceResult.error("更新失败，数据不存在");
        }
        return ServiceResult.success("更新成功");
    }

}
