package com.linzen.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.Page;
import com.linzen.base.UserInfo;
import com.linzen.base.controller.SuperController;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.entity.ProvinceEntity;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.service.ProvinceService;
import com.linzen.base.vo.ListVO;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.entity.TableExampleEntity;
import com.linzen.exception.DataBaseException;
import com.linzen.model.tableexample.*;
import com.linzen.model.tableexample.postil.PostilInfoVO;
import com.linzen.model.tableexample.postil.PostilModel;
import com.linzen.model.tableexample.postil.PostilSendForm;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.service.TableExampleService;
import com.linzen.util.DateUtil;
import com.linzen.util.JsonUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import com.linzen.util.treeutil.SumTree;
import com.linzen.util.treeutil.TreeDotUtils;
import com.linzen.util.type.StringNumber;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表格示例数据
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "表格示例数据", description = "TableExample")
@RestController
@RequestMapping("/api/extend/TableExample")
public class TableExampleController extends SuperController<TableExampleService, TableExampleEntity> {

    @Autowired
    private TableExampleService tableExampleService;
    @Autowired
    private ProvinceService provinceService;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private UserService userService;

    /**
     * 列表
     *
     * @param paginationTableExample 分页模型
     * @return
     */
    @Operation(summary = "获取表格数据列表")
    @GetMapping
    @SaCheckPermission("extend.tableDemo.commonTable")
    public ServiceResult<PageListVO<TableExampleListVO>> list(PaginationTableExample paginationTableExample) {
        List<TableExampleEntity> data = tableExampleService.getList(paginationTableExample);
        List<TableExampleListVO> list = JsonUtil.createJsonToList(data, TableExampleListVO.class);
        List<String> userId = list.stream().map(t -> t.getRegistrant()).collect(Collectors.toList());
        List<SysUserEntity> userList = userService.getUserName(userId);
        for (TableExampleListVO tableExampleListVO : list) {
            SysUserEntity user = userList.stream().filter(t -> t.getId().equals(tableExampleListVO.getRegistrant())).findFirst().orElse(null);
            tableExampleListVO.setRegistrant(user != null ? user.getRealName() + "/" + user.getAccount() : "");
        }
        PaginationVO paginationVO = BeanUtil.toBean(paginationTableExample, PaginationVO.class);
        return ServiceResult.pageList(list, paginationVO);
    }

    /**
     * 列表（树形表格）
     *
     * @param typeId                 主键
     * @param paginationTableExample 查询模型
     * @return
     */
    @Operation(summary = "（树形表格）")
    @GetMapping("/ControlSample/{typeId}")
    @Parameters({
            @Parameter(name = "typeId", description = "主键", required = true),
    })
    @SaCheckPermission("extend.tableDemo.tableTree")
    public ServiceResult<PageListVO<TableExampleListVO>> list(@PathVariable("typeId") String typeId, PaginationTableExample paginationTableExample) {
        List<TableExampleEntity> data = tableExampleService.getList(typeId, paginationTableExample);
        List<TableExampleListVO> list = JsonUtil.createJsonToList(data, TableExampleListVO.class);
        List<String> userId = list.stream().map(t -> t.getRegistrant()).collect(Collectors.toList());
        List<SysUserEntity> userList = userService.getUserName(userId);
        for (TableExampleListVO tableExampleListVO : list) {
            SysUserEntity user = userList.stream().filter(t -> t.getId().equals(tableExampleListVO.getRegistrant())).findFirst().orElse(null);
            tableExampleListVO.setRegistrant(user != null ? user.getRealName() + "/" + user.getAccount() : "");
        }
        PaginationVO paginationVO = BeanUtil.toBean(paginationTableExample, PaginationVO.class);
        return ServiceResult.pageList(list, paginationVO);
    }

    /**
     * 列表
     *
     * @return
     */
    @Operation(summary = "获取表格分组列表")
    @GetMapping("/All")
    @SaCheckPermission("extend.tableDemo.groupingTable")
    public ServiceResult<ListVO<TableExampleListAllVO>> listAll() {
        List<TableExampleEntity> data = tableExampleService.getList();
        List<TableExampleListAllVO> list = JsonUtil.createJsonToList(data, TableExampleListAllVO.class);
        List<String> userId = list.stream().map(t -> t.getRegistrant()).collect(Collectors.toList());
        List<SysUserEntity> userList = userService.getUserName(userId);
        for (TableExampleListAllVO tableExampleListVO : list) {
            SysUserEntity user = userList.stream().filter(t -> t.getId().equals(tableExampleListVO.getRegistrant())).findFirst().orElse(null);
            tableExampleListVO.setRegistrant(user != null ? user.getRealName() + "/" + user.getAccount() : "");
        }
        ListVO<TableExampleListAllVO> vo = new ListVO<>();
        vo.setList(list);
        return ServiceResult.success(vo);
    }

    /**
     * 列表
     *
     * @param page 查询模型
     * @return
     */
    @Operation(summary = "获取延伸扩展列表(行政区划)")
    @GetMapping("/IndustryList")
    @SaCheckPermission("extend.tableDemo.extension")
    public ServiceResult<ListVO<TableExampleIndustryListVO>> industryList(Page page) {
        String keyword = page.getKeyword();
        List<ProvinceEntity> data = provinceService.getList("-1");
        if (!StringUtil.isEmpty(keyword)) {
            data = data.stream().filter(t -> t.getFullName().contains(keyword)).collect(Collectors.toList());
        }
        List<TableExampleIndustryListVO> listVos = JsonUtil.createJsonToList(data, TableExampleIndustryListVO.class);
        ListVO<TableExampleIndustryListVO> vo = new ListVO<>();
        vo.setList(listVos);
        return ServiceResult.success(vo);
    }

    /**
     * 列表
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "获取城市信息列表(获取延伸扩展列表(行政区划))")
    @GetMapping("/CityList/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission("extend.tableDemo.extension")
    public ServiceResult<ListVO<TableExampleCityListVO>> cityList(@PathVariable("id") String id) {
        List<ProvinceEntity> data = provinceService.getList(id);
        List<TableExampleCityListVO> listVos = JsonUtil.createJsonToList(data, TableExampleCityListVO.class);
        ListVO<TableExampleCityListVO> vo = new ListVO<>();
        vo.setList(listVos);
        return ServiceResult.success(vo);
    }

    /**
     * 列表（表格树形）
     *
     * @param isTree 类型
     * @return
     */
    @Operation(summary = "表格树形")
    @GetMapping("/ControlSample/TreeList")
    @Parameters({
            @Parameter(name = "isTree", description = "类型"),
    })
    @SaCheckPermission("extend.tableDemo.tableTree")
    public ServiceResult<ListVO<TableExampleTreeModel>> treeList(@RequestParam("isTree")String isTree) {
        List<DictionaryDataEntity> data = dictionaryDataService.getList("d59a3cf65f9847dbb08be449e3feae16");
        List<TableExampleTreeModel> treeList = new ArrayList<>();
        for (DictionaryDataEntity entity : data) {
            TableExampleTreeModel treeModel = new TableExampleTreeModel();
            treeModel.setId(entity.getId());
            treeModel.setText(entity.getFullName());
            treeModel.setParentId(entity.getParentId());
            treeModel.setLoaded(true);
            treeModel.setExpanded(true);
            treeModel.setHt(JsonUtil.entityToMap(entity));
            treeList.add(treeModel);
        }
        if (isTree != null && StringNumber.ONE.equals(isTree)) {
            List<SumTree<TableExampleTreeModel>> trees = TreeDotUtils.convertListToTreeDot(treeList);
            List<TableExampleTreeModel> listVO = JsonUtil.createJsonToList(trees, TableExampleTreeModel.class);
            ListVO vo = new ListVO();
            vo.setList(listVO);
            return ServiceResult.success(vo);
        }
        ListVO vo = new ListVO();
        vo.setList(treeList);
        return ServiceResult.success(vo);
    }

    /**
     * 信息
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "获取普通表格示例信息")
    @GetMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission("extend.tableDemo.extension")
    public ServiceResult<TableExampleInfoVO> info(@PathVariable("id") String id) throws DataBaseException {
        TableExampleEntity entity = tableExampleService.getInfo(id);
        TableExampleInfoVO vo = BeanUtil.toBean(entity, TableExampleInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "删除项目")
    @DeleteMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission("extend.tableDemo.extension")
    public ServiceResult delete(@PathVariable("id") String id) {
        TableExampleEntity entity = tableExampleService.getInfo(id);
        if (entity != null) {
            tableExampleService.delete(entity);
            return ServiceResult.success("删除成功");
        }
        return ServiceResult.error("删除失败，数据不存在");
    }

    /**
     * 创建
     *
     * @param tableExampleCrForm 项目模型
     * @return
     */
    @Operation(summary = "新建项目")
    @PostMapping
    @Parameters({
            @Parameter(name = "tableExampleCrForm", description = "项目模型",required = true),
    })
    @SaCheckPermission("extend.tableDemo.extension")
    public ServiceResult create(@RequestBody @Valid TableExampleCrForm tableExampleCrForm) {
        TableExampleEntity entity = BeanUtil.toBean(tableExampleCrForm, TableExampleEntity.class);
        entity.setCostAmount(entity.getCostAmount() == null ? new BigDecimal("0") : entity.getCostAmount());
        entity.setTunesAmount(entity.getTunesAmount() == null ? new BigDecimal("0") : entity.getTunesAmount());
        entity.setProjectedIncome(entity.getProjectedIncome() == null ? new BigDecimal("0") : entity.getProjectedIncome());
        entity.setSign("0000000");
        tableExampleService.create(entity);
        return ServiceResult.success("创建成功");
    }

    /**
     * 更新
     *
     * @param id                 主键
     * @param tableExampleUpForm 项目模型
     * @return
     */
    @Operation(summary = "更新项目")
    @PutMapping("/{id}")
    @Parameters({
            @Parameter(name = "tableExampleUpForm", description = "项目模型",required = true),
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission("extend.tableDemo.postilTable")
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid TableExampleUpForm tableExampleUpForm) {
        TableExampleEntity entity = BeanUtil.toBean(tableExampleUpForm, TableExampleEntity.class);
        entity.setCostAmount(entity.getCostAmount() == null ? new BigDecimal("0") : entity.getCostAmount());
        entity.setTunesAmount(entity.getTunesAmount() == null ? new BigDecimal("0") : entity.getTunesAmount());
        entity.setProjectedIncome(entity.getProjectedIncome() == null ? new BigDecimal("0") : entity.getProjectedIncome());
        boolean flag = tableExampleService.update(id, entity);
        if (flag == false) {
            return ServiceResult.error("更新失败，数据不存在");
        }
        return ServiceResult.success("更新成功");
    }

    /**
     * 更新标签
     *
     * @param id                     主键
     * @param tableExampleSignUpForm 项目模型
     * @return
     */
    @Operation(summary = "更新标记")
    @PutMapping("/UpdateSign/{id}")
    @Parameters({
            @Parameter(name = "tableExampleSignUpForm", description = "项目模型",required = true),
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission("extend.tableDemo.postilTable")
    public ServiceResult updateSign(@PathVariable("id") String id, @RequestBody @Valid TableExampleSignUpForm tableExampleSignUpForm) {
        TableExampleEntity entity = BeanUtil.toBean(tableExampleSignUpForm, TableExampleEntity.class);
        TableExampleEntity tableExampleEntity = tableExampleService.getInfo(id);
        if (tableExampleEntity == null) {
            return ServiceResult.success("更新失败，数据不存在");
        }
        tableExampleEntity.setSign(entity.getSign());
        tableExampleService.update(id, entity);
        return ServiceResult.success("更新成功");
    }

    /**
     * 行编辑
     *
     * @param tableExampleRowUpForm 项目模型
     * @param id                    主键
     * @return
     */
    @Operation(summary = "行编辑")
    @PutMapping("/{id}/Actions/RowsEdit")
    @Parameters({
            @Parameter(name = "tableExampleRowUpForm", description = "项目模型",required = true),
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission("extend.tableDemo.redactTable")
    public ServiceResult rowEditing(@PathVariable("id") String id, @RequestBody @Valid TableExampleRowUpForm tableExampleRowUpForm) {
        TableExampleEntity entity = BeanUtil.toBean(tableExampleRowUpForm, TableExampleEntity.class);
        entity.setCostAmount(entity.getCostAmount() == null ? new BigDecimal("0") : entity.getCostAmount());
        entity.setTunesAmount(entity.getTunesAmount() == null ? new BigDecimal("0") : entity.getTunesAmount());
        entity.setProjectedIncome(entity.getProjectedIncome() == null ? new BigDecimal("0") : entity.getProjectedIncome());
        entity.setId(id);
        boolean falg = tableExampleService.rowEditing(entity);
        if (falg == false) {
            return ServiceResult.error("更新失败，数据不存在");
        }
        return ServiceResult.success("更新成功");
    }

    /**
     * 发送
     *
     * @param postilSendForm 项目模型
     * @param id             主键
     * @return
     */
    @Operation(summary = "发送批注")
    @PostMapping("/{id}/Postil")
    @Parameters({
            @Parameter(name = "postilSendForm", description = "项目模型",required = true),
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission("extend.tableDemo.postilTable")
    public ServiceResult sendPostil(@PathVariable("id") String id, @RequestBody PostilSendForm postilSendForm) {
        TableExampleEntity tableExampleEntity = tableExampleService.getInfo(id);
        if (tableExampleEntity == null) {
            return ServiceResult.success("发送失败，数据不存在");
        }
        UserInfo userInfo = userProvider.get();
        PostilModel model = new PostilModel();
        model.setCreatorTime(DateUtil.getNow("+8"));
        model.setText(postilSendForm.getText());
        model.setUserId(userInfo != null ? userInfo.getUserName() + "/" + userInfo.getUserAccount() : "");
        List<PostilModel> list = new ArrayList<>();
        list.add(model);
        if (!StringUtil.isEmpty(tableExampleEntity.getPostilJson())) {
            list.addAll(JsonUtil.createJsonToList(tableExampleEntity.getPostilJson(), PostilModel.class));
        }

        String postilJson = JsonUtil.createObjectToString(list);
        tableExampleEntity.setPostilJson(postilJson);
        tableExampleEntity.setPostilCount(list.size());
        tableExampleService.update(id, tableExampleEntity);
        return ServiceResult.success("发送成功");
    }


    /**
     * 发送
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "获取批注")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @GetMapping("/{id}/Actions/Postil")
    @SaCheckPermission("extend.tableDemo.postilTable")
    public ServiceResult<PostilInfoVO> getPostil(@PathVariable("id") String id) {
        TableExampleEntity tableExampleEntity = tableExampleService.getInfo(id);
        if (tableExampleEntity == null) {
            return ServiceResult.success("获取失败，数据不存在");
        }
        PostilInfoVO vo = new PostilInfoVO();
        vo.setPostilJson(tableExampleEntity.getPostilJson());
        return ServiceResult.success(vo);
    }

    /**
     * 删除批注
     *
     * @param id    主键值
     * @param index 行数
     * @return
     */
    @Operation(summary = "删除批注")
    @DeleteMapping("/{id}/Postil/{index}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "index", description = "行数", required = true),
    })
    @SaCheckPermission("extend.tableDemo.postilTable")
    public ServiceResult deletePostil(@PathVariable("id") String id, @PathVariable("index") int index) {
        TableExampleEntity tableExampleEntity = tableExampleService.getInfo(id);
        if (tableExampleEntity == null) {
            return ServiceResult.success("删除失败，数据不存在");
        }
        List<PostilModel> list = JsonUtil.createJsonToList(tableExampleEntity.getPostilJson(), PostilModel.class);
        list.remove(index);
        String postilJson = JsonUtil.createObjectToString(list);
        tableExampleEntity.setPostilJson(postilJson);
        tableExampleEntity.setPostilCount((list.size()));
        tableExampleService.update(id, tableExampleEntity);
        return ServiceResult.success("删除成功");
    }
}
