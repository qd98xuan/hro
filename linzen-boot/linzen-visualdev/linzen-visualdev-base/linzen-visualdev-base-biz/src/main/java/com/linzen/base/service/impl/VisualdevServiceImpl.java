package com.linzen.base.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.entity.VisualdevEntity;
import com.linzen.base.entity.VisualdevReleaseEntity;
import com.linzen.base.mapper.VisualdevMapper;
import com.linzen.base.model.PaginationVisualdev;
import com.linzen.base.service.FilterService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.base.service.VisualdevReleaseService;
import com.linzen.base.service.VisualdevService;
import com.linzen.base.util.VisualFlowFormUtil;
import com.linzen.database.model.dbfield.DbFieldModel;
import com.linzen.database.model.dbtable.DbTableFieldModel;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.engine.service.FlowTemplateService;
import com.linzen.exception.WorkFlowException;
import com.linzen.model.form.VisualTableModel;
import com.linzen.model.visualJson.FieLdsModel;
import com.linzen.model.visualJson.FormCloumnUtil;
import com.linzen.model.visualJson.FormDataModel;
import com.linzen.model.visualJson.TableModel;
import com.linzen.model.visualJson.analysis.FormAllModel;
import com.linzen.model.visualJson.analysis.RecursionForm;
import com.linzen.model.visualJson.config.ConfigModel;
import com.linzen.onlinedev.model.OnlineDevData;
import com.linzen.service.FlowFormService;
import com.linzen.util.*;
import com.linzen.util.visiual.ProjectKeyConsts;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class VisualdevServiceImpl extends SuperServiceImpl<VisualdevMapper, VisualdevEntity> implements VisualdevService {

    @Autowired
    private ConcurrencyUtils concurrencyVisualUtils;
    @Autowired
    private FlowFormService flowFormService;
    @Autowired
    private FlowTemplateService flowTemplateService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private VisualDevTableCre visualDevTableCreUtil;
    @Autowired
    private ConcurrencyUtils concurrencyUtils;
    @Autowired
    private DbTableServiceImpl dbTableService;
    @Autowired
    private FilterService filterService;
    @Autowired
    private VisualdevReleaseService visualdevReleaseService;
    @Autowired
    private VisualFlowFormUtil visualFlowFormUtil;

    @Override
    public List<VisualdevEntity> getList(PaginationVisualdev paginationVisualdev) {
        // 定义变量判断是否需要使用修改时间倒序
        boolean flag = false;
        QueryWrapper<VisualdevEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(VisualdevEntity::getId, VisualdevEntity::getCategory, VisualdevEntity::getEnCode, VisualdevEntity::getFullName,
                VisualdevEntity::getCreatorTime, VisualdevEntity::getCreatorUserId, VisualdevEntity::getUpdateTime, VisualdevEntity::getUpdateUserId,
                VisualdevEntity::getEnableFlow, VisualdevEntity::getEnabledMark, VisualdevEntity::getSortCode, VisualdevEntity::getState, VisualdevEntity::getType,
                VisualdevEntity::getWebType, VisualdevEntity::getVisualTables);

        if (!StringUtil.isEmpty(paginationVisualdev.getKeyword())) {
            flag = true;
            queryWrapper.lambda().and(t -> t.like(VisualdevEntity::getFullName, paginationVisualdev.getKeyword())
                    .or().like(VisualdevEntity::getEnCode, paginationVisualdev.getKeyword()));
        }

        queryWrapper.lambda().eq(VisualdevEntity::getType, paginationVisualdev.getType());

        if (StringUtil.isNotEmpty(paginationVisualdev.getCategory())) {
            flag = true;
            queryWrapper.lambda().eq(VisualdevEntity::getCategory, paginationVisualdev.getCategory());
        }

        //---功能类型查询
        if (paginationVisualdev.getWebType() != null) {//普通表单
            flag = true;
            if (Objects.equals(paginationVisualdev.getWebType(), 1)) {
                queryWrapper.lambda().eq(VisualdevEntity::getEnableFlow, 0);
                queryWrapper.lambda().ne(VisualdevEntity::getWebType, 4);
            } else if (Objects.equals(paginationVisualdev.getWebType(), 2)) {
                queryWrapper.lambda().eq(VisualdevEntity::getEnableFlow, 1);
                queryWrapper.lambda().ne(VisualdevEntity::getWebType, 4);
            } else {
                queryWrapper.lambda().eq(VisualdevEntity::getWebType, paginationVisualdev.getWebType());
            }
        }

        //是否流程分类
        if (paginationVisualdev.getEnableFlow() != null) {
            flag = true;
            queryWrapper.lambda().eq(VisualdevEntity::getEnableFlow, paginationVisualdev.getEnableFlow());
        }

        //状态
        if (paginationVisualdev.getIsRelease() != null) {
            flag = true;
            queryWrapper.lambda().eq(VisualdevEntity::getState, paginationVisualdev.getIsRelease());
        }

        // 排序
        queryWrapper.lambda().orderByAsc(VisualdevEntity::getSortCode).orderByDesc(VisualdevEntity::getCreatorTime);
        if (flag) {
            queryWrapper.lambda().orderByDesc(VisualdevEntity::getUpdateTime);
        }
        Page<VisualdevEntity> page = new Page<>(paginationVisualdev.getCurrentPage(), paginationVisualdev.getPageSize());
        IPage<VisualdevEntity> userPage = this.page(page, queryWrapper);
        return paginationVisualdev.setData(userPage.getRecords(), page.getTotal());
    }

    @Override
    public List<VisualdevEntity> getPageList(PaginationVisualdev paginationVisualdev) {
        QueryWrapper<VisualdevReleaseEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(
                VisualdevReleaseEntity::getId,
                VisualdevReleaseEntity::getEnableFlow,
                VisualdevReleaseEntity::getFullName,
                VisualdevReleaseEntity::getEnCode);
        if (!StringUtil.isEmpty(paginationVisualdev.getKeyword())) {
            queryWrapper.lambda().like(VisualdevReleaseEntity::getFullName, paginationVisualdev.getKeyword());
        }
        if (ObjectUtil.isNotEmpty(paginationVisualdev.getType())) {
            queryWrapper.lambda().eq(VisualdevReleaseEntity::getType, paginationVisualdev.getType());
        }
        if (ObjectUtil.isNotEmpty(paginationVisualdev.getWebType())) {
            queryWrapper.lambda().eq(VisualdevReleaseEntity::getWebType, paginationVisualdev.getWebType());
        }
        if (ObjectUtil.isNotEmpty(paginationVisualdev.getEnableFlow())) {
            queryWrapper.lambda().eq(VisualdevReleaseEntity::getEnableFlow, paginationVisualdev.getEnableFlow());
        }
        if (StringUtil.isNotEmpty(paginationVisualdev.getCategory())) {
            queryWrapper.lambda().eq(VisualdevReleaseEntity::getCategory, paginationVisualdev.getCategory());
        }
        // 排序
        queryWrapper.lambda().orderByAsc(VisualdevReleaseEntity::getSortCode).orderByDesc(VisualdevReleaseEntity::getCreatorTime);
        Page<VisualdevReleaseEntity> page = new Page<>(paginationVisualdev.getCurrentPage(), paginationVisualdev.getPageSize());
        IPage<VisualdevReleaseEntity> userPage = visualdevReleaseService.page(page, queryWrapper);
        List<VisualdevEntity> list = JsonUtil.createJsonToList(userPage.getRecords(), VisualdevEntity.class);
        return paginationVisualdev.setData(list, page.getTotal());
    }


    @Override
    public List<VisualdevEntity> getList() {
        QueryWrapper<VisualdevEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(VisualdevEntity::getSortCode).orderByDesc(VisualdevEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public VisualdevEntity getInfo(String id) {
        QueryWrapper<VisualdevEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualdevEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public VisualdevEntity getReleaseInfo(String id) {
        VisualdevReleaseEntity visualdevReleaseEntity = visualdevReleaseService.getById(id);
        VisualdevEntity visualdevEntity = null;
        if (visualdevReleaseEntity != null) {
            visualdevEntity = BeanUtil.toBean(visualdevReleaseEntity, VisualdevEntity.class);
        }
        if (visualdevEntity == null) {
            visualdevEntity = getById(id);
        }
        return visualdevEntity;
    }

    @Override
    public Map<String, String> getTableMap(String formData) {
        Map<String, String> tableMap = new HashMap<>();
        if (StringUtil.isEmpty(formData)) {
            return tableMap;
        }
        FormDataModel formDataModel = JsonUtil.createJsonToBean(formData, FormDataModel.class);
        String fields = formDataModel.getFields();
        List<FieLdsModel> list = JsonUtil.createJsonToList(fields, FieLdsModel.class);
        list.forEach(item -> {
            this.solveTableName(item, tableMap);
        });
        return tableMap;
    }

    private void solveTableName(FieLdsModel item, Map<String, String> tableMap) {
        ConfigModel config = item.getConfig();
        if (config != null) {
            List<FieLdsModel> children = config.getChildren();
            if ("table".equals(config.getProjectKey())) {
                if (children != null && !children.isEmpty()) {
                    FieLdsModel fieLdsModel = children.get(0);
                    String parentVModel = item.getVModel();
                    String relationTable = fieLdsModel.getConfig().getRelationTable();
                    tableMap.put(parentVModel, relationTable);
                }
            }
            if (children != null) {
                children.forEach(item2 -> {
                    this.solveTableName(item2, tableMap);
                });
            }
        }
    }

    ;


    @Override
    @SneakyThrows
    public Boolean create(VisualdevEntity entity) {
        if (StringUtil.isEmpty(entity.getId())) {
            entity.setId(RandomUtil.uuId());
        }
        FormDataModel formDataModel = JsonUtil.createJsonToBean(entity.getFormData(), FormDataModel.class);
        if (formDataModel != null) {
            Map<String, String> tableMap = this.getTableMap(entity.getFormData());
            // 保存app,pc过滤配置
            filterService.saveRuleList(entity.getId(), entity, 1, 1, tableMap);
            //是否开启安全锁
            Boolean concurrencyLock = formDataModel.getConcurrencyLock();
            int primaryKeyPolicy = formDataModel.getPrimaryKeyPolicy();
            Boolean logicalDelete = formDataModel.getLogicalDelete();

            //判断是否要创表
            List<TableModel> tableModels = JsonUtil.createJsonToList(entity.getVisualTables(), TableModel.class);
            //有表
            if (tableModels.size() > 0) {
                List<TableModel> visualTables = JsonUtil.createJsonToList(entity.getVisualTables(), TableModel.class);
                TableModel mainTable = visualTables.stream().filter(f -> f.getTypeId().equals("1")).findFirst().orElse(null);

                for (TableModel tableModel : visualTables) {
                    Boolean isAutoIncre = this.getPrimaryDbField(entity.getDbLinkId(), tableModel.getTable());
                    // 1:雪花ID 2:自增ID
                    if (primaryKeyPolicy == 1) {
                        if (isAutoIncre != null && isAutoIncre) {
                            throw new WorkFlowException("主键策略:[雪花ID],表[ " + tableModel.getTable() + " ]主键设置不支持!");
                        }
                    } else if (primaryKeyPolicy == 2) {
                        if (isAutoIncre == null || !isAutoIncre) {
                            throw new WorkFlowException("主键策略:[自增ID],表[ " + tableModel.getTable() + " ]主键设置不支持!");
                        }
                    }
                }
                //在主表创建锁字段
                try {
                    if (logicalDelete && mainTable != null) {
                        //在主表创建逻辑删除
                        concurrencyUtils.creDelFlag(mainTable.getTable(), entity.getDbLinkId());
                    }
                    if (concurrencyLock) {
                        concurrencyUtils.createVersion(mainTable.getTable(), entity.getDbLinkId());
                    }
                    if (entity.getEnableFlow() == 1) {
                        concurrencyUtils.createFlowTaskId(mainTable.getTable(), entity.getDbLinkId());
                    }
                    if (TenantDataSourceUtil.isTenantColumn()) {
                        for (TableModel tableModel : visualTables) {
                            concurrencyUtils.createTenantId(tableModel.getTable(), entity.getDbLinkId());
                        }
                    }
                    concurrencyUtils.createFlowEngine(mainTable.getTable(), entity.getDbLinkId());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("创建锁字段失败");
                    e.printStackTrace();
                }
            }
        }
        entity.setEnabledMark(0);
        entity.setState(0);
        entity.setCreatorTime(new Date());
        entity.setCreatorUserId(UserProvider.getLoginUserId());
        entity.setUpdateTime(null);
        entity.setUpdateUserId(null);
        // 启用流程 在表单新增一条 提供给流程使用
        if (Objects.equals(OnlineDevData.STATE_ENABLE, entity.getEnableFlow()) && entity.getType() < 3) {
            visualFlowFormUtil.saveLogicFlowAndForm(entity);
        }
        this.setIgnoreLogicDelete().removeById(entity.getId());
        boolean result = this.setIgnoreLogicDelete().saveOrUpdate(entity);
        this.clearIgnoreLogicDelete();
        return result;
    }

    @Override
    public boolean update(String id, VisualdevEntity entity) throws Exception {
        entity.setId(id);
        entity.setUpdateUserId(userProvider.get().getUserId());
        boolean b = this.updateById(entity);
        //代码生成修改时就要生成字段
        FormDataModel formDataModel = JsonUtil.createJsonToBean(entity.getFormData(), FormDataModel.class);
        if (formDataModel != null) {
            //是否开启安全锁
            Boolean concurrencyLock = formDataModel.getConcurrencyLock();
            Boolean logicalDelete = formDataModel.getLogicalDelete();
            int primaryKeyPolicy = formDataModel.getPrimaryKeyPolicy();
            //判断是否要创表
            List<TableModel> visualTables = JsonUtil.createJsonToList(entity.getVisualTables(), TableModel.class);
            //有表
            if (visualTables.size() > 0) {
                if (formDataModel != null) {
                    try {
                        TableModel mainTable = visualTables.stream().filter(f -> f.getTypeId().equals("1")).findFirst().orElse(null);
                        if (logicalDelete && mainTable != null) {
                            //在主表创建逻辑删除
                            concurrencyUtils.creDelFlag(mainTable.getTable(), entity.getDbLinkId());
                        }
                        if (concurrencyLock) {
                            //在主表创建锁字段
                            concurrencyUtils.createVersion(mainTable.getTable(), entity.getDbLinkId());
                        }
                        concurrencyUtils.createFlowTaskId(mainTable.getTable(), entity.getDbLinkId());
                        if (TenantDataSourceUtil.isTenantColumn()) {
                            for (TableModel tableModel : visualTables) {
                                concurrencyUtils.createTenantId(tableModel.getTable(), entity.getDbLinkId());
                            }
                        }
                        concurrencyUtils.createFlowEngine(mainTable.getTable(), entity.getDbLinkId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //判断自增是否匹配
                    concurrencyUtils.checkAutoIncrement(primaryKeyPolicy, entity.getDbLinkId(), visualTables);
                }
            }
        }
        return b;
    }

    @Override
    public void delete(VisualdevEntity entity) throws WorkFlowException {
        if (entity != null) {
            //删除表单
            flowFormService.removeById(entity.getId());
            List<String> ids = new ArrayList<>();
            ids.add(entity.getId());
            this.removeByIds(ids);
        }
    }

    @Override
    public Integer getObjByEncode(String encode, Integer type) {
        QueryWrapper<VisualdevEntity> visualWrapper = new QueryWrapper<>();
        visualWrapper.lambda().eq(VisualdevEntity::getEnCode, encode).eq(VisualdevEntity::getType, type);
        Integer count = (int) this.count(visualWrapper);
        return count;
    }

    @Override
    public Integer getCountByName(String name, Integer type) {
        QueryWrapper<VisualdevEntity> visualWrapper = new QueryWrapper<>();
        visualWrapper.lambda().eq(VisualdevEntity::getFullName, name).eq(VisualdevEntity::getType, type);
        Integer count = (int) this.count(visualWrapper);
        return count;
    }

    @Override
    public void createTable(VisualdevEntity entity) throws WorkFlowException {
        boolean isTenant = TenantDataSourceUtil.isTenantColumn();
        FormDataModel formDataModel = JsonUtil.createJsonToBean(entity.getFormData(), FormDataModel.class);
        //是否开启安全锁
        Boolean concurrencyLock = formDataModel.getConcurrencyLock();
        int primaryKeyPolicy = formDataModel.getPrimaryKeyPolicy();
        Boolean logicalDelete = formDataModel.getLogicalDelete();

        Map<String, Object> formMap = JsonUtil.stringToMap(entity.getFormData());
        List<FieLdsModel> list = JsonUtil.createJsonToList(formMap.get("fields"), FieLdsModel.class);
        JSONArray formJsonArray = JsonUtil.createJsonToJsonArray(String.valueOf(formMap.get("fields")));
        List<TableModel> visualTables = JsonUtil.createJsonToList(entity.getVisualTables(), TableModel.class);

        List<FormAllModel> formAllModel = new ArrayList<>();
        RecursionForm recursionForm = new RecursionForm();
        recursionForm.setTableModelList(visualTables);
        recursionForm.setList(list);
        FormCloumnUtil.recursionForm(recursionForm, formAllModel);

        String tableName = "mt" + RandomUtil.uuId();

        String dbLinkId = entity.getDbLinkId();
        VisualTableModel model = new VisualTableModel(formJsonArray, formAllModel, tableName, dbLinkId, entity.getFullName(), concurrencyLock, primaryKeyPolicy, logicalDelete);
        List<TableModel> tableModelList = visualDevTableCreUtil.tableList(model);

        if (formDataModel != null) {
            try {
                TableModel mainTable = visualTables.stream().filter(f -> f.getTypeId().equals("1")).findFirst().orElse(null);
                if (OnlineDevData.STATE_ENABLE.equals(entity.getEnableFlow()) && mainTable != null) {
                    concurrencyUtils.createFlowEngine(mainTable.getTable(), entity.getDbLinkId());
                }
                if (logicalDelete && mainTable != null) {
                    //在主表创建逻辑删除
                    concurrencyUtils.creDelFlag(mainTable.getTable(), entity.getDbLinkId());
                }
                if (concurrencyLock) {
                    //在主表创建锁字段
                    concurrencyUtils.createVersion(mainTable.getTable(), entity.getDbLinkId());
                }
                if (entity.getEnableFlow() == 1) {
                    concurrencyUtils.createFlowTaskId(mainTable.getTable(), entity.getDbLinkId());
                }
                if (isTenant) {
                    for (TableModel tableModel : visualTables) {
                        concurrencyUtils.createTenantId(tableModel.getTable(), entity.getDbLinkId());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        formMap.put("fields", formJsonArray);
        //更新
        entity.setFormData(JsonUtil.createObjectToString(formMap));
        entity.setVisualTables(JsonUtil.createObjectToString(tableModelList));
    }

    @Override
    public Map<String, String> getTableNameToKey(String modelId) {
        Map<String, String> childKeyMap = new HashMap<>();
        VisualdevEntity info = this.getInfo(modelId);
        FormDataModel formDataModel = JsonUtil.createJsonToBean(info.getFormData(), FormDataModel.class);
        List<FieLdsModel> fieLdsModels = JsonUtil.createJsonToList(formDataModel.getFields(), FieLdsModel.class);
        List<FieLdsModel> childFields = fieLdsModels.stream().filter(f -> ProjectKeyConsts.CHILD_TABLE.equals(f.getConfig().getProjectKey())).collect(Collectors.toList());
        childFields.stream().forEach(c ->
                childKeyMap.put(c.getConfig().getTableName().toLowerCase(), c.getVModel())
        );
        return childKeyMap;
    }

    @Override
    public Boolean getPrimaryDbField(String linkId, String table) throws Exception {
        DbTableFieldModel dbTableModel = dbTableService.getDbTableModel(linkId, table);
        List<DbFieldModel> data = dbTableModel.getDbFieldModelList();
        DbFieldModel dbFieldModel = data.stream().filter(DbFieldModel::getIsPrimaryKey).findFirst().orElse(null);
        if (dbFieldModel != null) {
            return dbFieldModel.getIsAutoIncrement() != null && dbFieldModel.getIsAutoIncrement();
        } else {
            return null;
        }
    }

    @Override
    public List<VisualdevEntity> selectorList() {
        QueryWrapper<VisualdevEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(
                VisualdevEntity::getId,
                VisualdevEntity::getFullName,
                VisualdevEntity::getWebType,
                VisualdevEntity::getEnableFlow,
                VisualdevEntity::getType,
                VisualdevEntity::getCategory);
        return this.list(queryWrapper);
    }
}
