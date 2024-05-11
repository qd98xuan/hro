package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.entity.SuperBaseEntity;
import com.linzen.base.model.dblink.*;
import com.linzen.base.service.DbLinkService;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.service.DictionaryTypeService;
import com.linzen.base.vo.ListVO;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.constant.MsgCode;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.util.DataSourceUtil;
import com.linzen.exception.DataBaseException;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.util.JsonUtil;
import com.linzen.util.XSSEscape;
import com.linzen.emnus.DictionaryDataEnum;
import com.linzen.util.treeutil.SumTree;
import com.linzen.util.treeutil.newtreeutil.TreeDotUtils;
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
 * 数据连接
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "数据连接", description = "DataSource")
@RestController
@RequestMapping("/api/system/DataSource")
public class DbLinkController extends SuperController<DbLinkService, DbLinkEntity> {

    @Autowired
    private DbLinkService dblinkService;
    @Autowired
    private UserService userService;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private DictionaryTypeService dictionaryTypeService;
    @Autowired
    private DataSourceUtil dataSourceUtil;

    /**
     * 列表
     *
     * @param type 类型
     * @return
     */
    @GetMapping("/Selector")
    @Operation(summary = "获取数据连接下拉框列表")
    public ServiceResult<ListVO<DbLinkSelectorListVO>> selectorList(String type) {
        List<DbLinkModel> modelAll = new LinkedList<>();
        List<DbLinkEntity> dbLinkList = dblinkService.getList();
        List<DictionaryDataEntity> dictionaryDataList = dictionaryDataService.getList(dictionaryTypeService.getInfoByEnCode(DictionaryDataEnum.SYSTEM_DBLINK.getDictionaryTypeId()).getId());
        // 1、连接中字典目录
        for (DictionaryDataEntity entity : dictionaryDataList) {
            long num = dbLinkList.stream().filter(t -> t.getDbType().equals(entity.getEnCode())).count();
            if (num > 0){
                DbLinkModel model = new DbLinkModel();
                model.setNum(num);
                model.setFullName(entity.getFullName());
                model.setParentId("-1");
                model.setId(entity.getId());
                modelAll.add(model);
            }
        }
        // 2、字典中的连接集合
        for (DbLinkEntity entity : dbLinkList) {
            dictionaryDataList.stream().filter(t -> t.getEnCode().equals(entity.getDbType())).findFirst().ifPresent((dataEntity)->{
                DbLinkModel model = BeanUtil.toBean(entity, DbLinkModel.class);
                model.setParentId(dataEntity.getId());
                modelAll.add(model);
            });
        }
        List<SumTree<DbLinkModel>> trees = TreeDotUtils.convertListToTreeDot(modelAll.stream().sorted(Comparator.comparing(DbLinkModel::getFullName)).collect(Collectors.toList()));
        List<DbLinkSelectorListVO> list = new ArrayList<>();
        // type为空时返回默认库
        if(type == null){
            DbLinkListVO dbLink = new DbLinkListVO();
            dbLink.setFullName("默认数据库");
            dbLink.setId("0");
            dbLink.setDbType(dataSourceUtil.getDbType());
            DbLinkSelectorListVO defaultDb = new DbLinkSelectorListVO();
            defaultDb.setFullName("");
            defaultDb.setChildren(Collections.singletonList(dbLink));
            list.add(defaultDb);
        }
        list.addAll(JsonUtil.createJsonToList(trees, DbLinkSelectorListVO.class));
        return ServiceResult.success(new ListVO<>(list));
    }

    /**
     * 2:列表
     *
     * @param page 关键字
     * @return ignore
     */
    @GetMapping
    @Operation(summary = "获取数据连接列表")
    public ServiceResult<PageListVO<DbLinkListVO>> getList(PaginationDbLink page) {
        List<DbLinkEntity> data = dblinkService.getList(page);
        List<String> userId = data.stream().map(SuperBaseEntity.SuperCBaseEntity::getCreatorUserId).collect(Collectors.toList());
        List<String> lastUserId = data.stream().map(SuperBaseEntity.SuperCUBaseEntity::getUpdateUserId).collect(Collectors.toList());
        List<SysUserEntity> userEntities = userService.getUserName(userId);
        List<SysUserEntity> lastUserIdEntities = userService.getUserName(lastUserId);
        List<DictionaryDataEntity> typeList = dictionaryDataService.getList(dictionaryTypeService.getInfoByEnCode(DictionaryDataEnum.SYSTEM_DBLINK.getDictionaryTypeId()).getId());
        List<DbLinkListVO> jsonToList = JsonUtil.createJsonToList(data, DbLinkListVO.class);
        for (DbLinkListVO vo : jsonToList) {
            //存在类型的字典对象
            DictionaryDataEntity dataEntity = typeList.stream().filter(t -> t.getEnCode().equals(vo.getDbType())).findFirst().orElse(null);
            if (dataEntity != null) {
                vo.setDbType(dataEntity.getFullName());
            } else {
                vo.setDbType("");
            }
            //创建者
            SysUserEntity creatorUser = userEntities.stream().filter(t -> t.getId().equals(vo.getCreatorUserId())).findFirst().orElse(null);
            vo.setCreatorUserId(creatorUser != null ? creatorUser.getRealName() + "/" + creatorUser.getAccount() : vo.getCreatorUserId());
            //修改人
            SysUserEntity updateUser = lastUserIdEntities.stream().filter(t -> t.getId().equals(vo.getUpdateUserId())).findFirst().orElse(null);
            vo.setUpdateUserId(updateUser != null ? updateUser.getRealName() + "/" + updateUser.getAccount() : vo.getUpdateUserId());
        }
        PaginationVO paginationVO = BeanUtil.toBean(page, PaginationVO.class);
        return ServiceResult.pageList(jsonToList , paginationVO);
    }

    /**
     * 列表分组
     *
     * @param page 查询条件
     * @return 数据源分组列表
     */
    @GetMapping("/getGroup")
    @Operation(summary = "获取数据连接列表")
    public ServiceResult<List<Map<String, Object>>> getGroup(PaginationDbLink page) {
        ServiceResult<PageListVO<DbLinkListVO>> result = getList(page);
        List<DbLinkListVO> voList = result.getData().getList();
        // host分组
        Set<String> hostSet = voList.stream().sorted(Comparator.comparing(DbLinkListVO::getHost))
                .map(DbLinkListVO::getHost).collect(Collectors.toSet());
        List<Map<String, Object>> groupList = new ArrayList<>();
        for (String host : hostSet) {
            Map<String, Object> groupMap = new HashMap<>();
            groupMap.put("host", host);
            groupMap.put("options", voList.stream().filter(vo->vo.getHost().equals(host)).toArray());
            groupList.add(groupMap);
        }
        return ServiceResult.success(groupList);
    }

    /**
     * 3:单条数据连接
     *
     * @param id 主键
     * @return ignore
     * @throws DataBaseException ignore
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取数据连接")
    @Parameters({
            @Parameter(name = "id", description = "主键")
    })
    @SaCheckPermission("systemData.dataSource")
    public ServiceResult<DbLinkInfoVO> get(@PathVariable("id") String id) throws DataBaseException {
        return ServiceResult.success(new DbLinkInfoVO().getDbLinkInfoVO(dblinkService.getInfo(XSSEscape.escape(id))));
    }

    /**
     * 4:新建数据连接
     *
     * @param dbLinkCreUpForm 新建数据连接表单对象
     * @return ignore
     */
    @PostMapping
    @Operation(summary = "添加数据连接")
    @Parameters({
            @Parameter(name = "dbLinkCreUpForm", description = "新建数据连接表单对象", required = true)
    })
    @SaCheckPermission("systemData.dataSource")
    public ServiceResult<String> create(@RequestBody @Valid DbLinkCreUpForm dbLinkCreUpForm) {
        DbLinkEntity entity = dbLinkCreUpForm.getDbLinkEntity(dbLinkCreUpForm);
        if (dblinkService.isExistByFullName(entity.getFullName(), entity.getId())) {
            return ServiceResult.error(MsgCode.EXIST001.get());
        }
        dblinkService.create(entity);
        return ServiceResult.success("创建成功");
    }

    /**
     * 5:更新数据连接
     *
     * @param id              主键
     * @param dbLinkCreUpForm dto实体
     * @return ignore
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改数据连接")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "dbLinkCreUpForm", description = "新建数据连接表单对象", required = true)
    })
    @SaCheckPermission("systemData.dataSource")
    public ServiceResult<String> update(@PathVariable("id") String id, @RequestBody @Valid DbLinkCreUpForm dbLinkCreUpForm) {
        id = XSSEscape.escape(id);
        DbLinkEntity entity = dbLinkCreUpForm.getDbLinkEntity(dbLinkCreUpForm);
        if (dblinkService.isExistByFullName(entity.getFullName(), id)) {
            return ServiceResult.error(MsgCode.EXIST001.get());
        }
        if (!dblinkService.update(id, entity)) {
            return ServiceResult.error(MsgCode.FA002.get());
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 6:删除
     *
     * @param id 主键
     * @return ignore
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除数据连接")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("systemData.dataSource")
    public ServiceResult<String> delete(@PathVariable("id") String id) {
        DbLinkEntity entity = dblinkService.getInfo(id);
        if (entity != null) {
            dblinkService.delete(entity);
            return ServiceResult.success(MsgCode.SU003.get());
        }
        return ServiceResult.error(MsgCode.FA003.get());
    }

    /**
     * 7:测试连接
     *
     * @param dbLinkBaseForm 数据连接参数
     * @return ignore
     * @throws DataBaseException ignore
     */
    @PostMapping("/Actions/Test")
    @Operation(summary = "测试连接")
    @Parameters({
            @Parameter(name = "dbLinkBaseForm", description = "数据连接参数", required = true)
    })
    @SaCheckPermission("systemData.dataSource")
    public ServiceResult<String> test(@RequestBody DbLinkBaseForm dbLinkBaseForm) throws Exception {
        boolean data = dblinkService.testDbConnection(dbLinkBaseForm.getDbLinkEntity(dbLinkBaseForm));
        if (data) {
            return ServiceResult.success(MsgCode.DB301.get());
        } else {
            return ServiceResult.error(MsgCode.DB302.get());
        }
    }

}
