package com.linzen.onlinedev.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.linzen.base.UserInfo;
import com.linzen.base.entity.VisualdevShortLinkEntity;
import com.linzen.base.model.ColumnDataModel;
import com.linzen.base.model.Template6.ColumnListField;
import com.linzen.base.model.VisualDevJsonModel;
import com.linzen.base.service.DbLinkService;
import com.linzen.base.service.FilterService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.base.service.VisualdevShortLinkService;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.model.superQuery.ConditionJsonModel;
import com.linzen.database.model.superQuery.SuperJsonModel;
import com.linzen.database.model.superQuery.SuperQueryJsonModel;
import com.linzen.database.util.ConnUtil;
import com.linzen.database.util.DynamicDataSourceUtil;
import com.linzen.engine.entity.FlowTaskEntity;
import com.linzen.engine.model.flowtemplate.FlowTemplateInfoVO;
import com.linzen.engine.service.FlowTaskService;
import com.linzen.engine.service.FlowTemplateJsonService;
import com.linzen.engine.service.FlowTemplateService;
import com.linzen.exception.WorkFlowException;
import com.linzen.mapper.FlowFormDataMapper;
import com.linzen.model.visualJson.*;
import com.linzen.model.visualJson.analysis.FormAllModel;
import com.linzen.model.visualJson.analysis.RecursionForm;
import com.linzen.onlinedev.entity.VisualdevModelDataEntity;
import com.linzen.onlinedev.mapper.VisualdevModelDataMapper;
import com.linzen.onlinedev.model.OnlineDevData;
import com.linzen.onlinedev.model.OnlineDevListModel.VisualColumnSearchVO;
import com.linzen.onlinedev.model.PaginationModel;
import com.linzen.onlinedev.service.VisualDevListService;
import com.linzen.onlinedev.util.OnlineDevDbUtil;
import com.linzen.onlinedev.util.onlineDevUtil.*;
import com.linzen.permission.model.authorize.OnlineDynamicSqlModel;
import com.linzen.permission.service.AuthorizeService;
import com.linzen.service.FlowFormService;
import com.linzen.util.*;
import com.linzen.util.visiual.ProjectKeyConsts;
import lombok.Cleanup;
import org.apache.commons.collections4.CollectionUtils;
import org.mybatis.dynamic.sql.*;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.select.aggregate.AbstractCount;
import org.mybatis.dynamic.sql.select.join.EqualTo;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 在线开发列表
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class VisualDevListServiceImpl extends SuperServiceImpl<VisualdevModelDataMapper, VisualdevModelDataEntity> implements VisualDevListService {

    public static List<String> needAllFieldsDB = new ArrayList() {{
        add("Microsoft SQL Server");
        add("DM DBMS");
        add("PostgreSQL");
        add("Oracle");
        add("KingbaseES");
    }};
    public static List<String> needUpcaseFieldsDB = new ArrayList() {{
        add("DM DBMS");
        add("Oracle");
    }};
    @Autowired
    private DbLinkService dblinkService;
    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private OnlineSwapDataUtils onlineSwapDataUtils;
    @Autowired
    private FlowFormDataUtil flowFormDataUtil;
    @Autowired
    private AuthorizeService authorizeService;
    @Autowired
    private FlowFormDataMapper flowFormDataMapper;
    @Autowired
    private FlowFormService flowFormService;
    @Autowired
    private OnlineDevDbUtil onlineDevDbUtil;
    @Autowired
    private VisualdevShortLinkService visualdevShortLinkService;
    @Autowired
    private FlowTemplateService flowTemplateService;
    @Autowired
    private FlowTemplateJsonService flowTemplateJsonService;
    @Autowired
    private FilterService filterService;

    @Override
    public List<Map<String, Object>> getDataList(VisualDevJsonModel visualDevJsonModel, PaginationModel paginationModel) throws WorkFlowException {
        List<Map<String, Object>> realList;
        ColumnDataModel columnDataModel = visualDevJsonModel.getColumnData();
        FormDataModel formDataModel = visualDevJsonModel.getFormData();
        List<FieLdsModel> fieLdsModels = JsonUtil.createJsonToList(formDataModel.getFields(), FieLdsModel.class);
        List<TableModel> visualTables = visualDevJsonModel.getVisualTables();
        //解析所有控件
        RecursionForm recursionForm = new RecursionForm(fieLdsModels, visualTables);
        List<FormAllModel> formAllModel = new ArrayList<>();
        FormCloumnUtil.recursionForm(recursionForm, formAllModel);
        List<VisualColumnSearchVO> searchVOS = new ArrayList<>();
        //判断有无表
        if (visualTables.size() > 0) {
            //当前用户信息
            UserInfo userInfo = userProvider.get();
            //菜单id
            String moduleId = paginationModel.getMenuId();
            //封装搜索数据
            OnlineProductSqlUtils.queryList(formAllModel, visualDevJsonModel, paginationModel);
            realList = this.getListWithTable(visualDevJsonModel, paginationModel, userInfo, moduleId, null);
        } else {
            realList = this.getWithoutTableData(visualDevJsonModel.getId());
            realList = this.getList(realList, searchVOS, paginationModel);
        }

        if (realList.size() < 1) {
            return realList;
        }
        //编辑表格(行内编辑)
        boolean inlineEdit = columnDataModel.getType() != null && columnDataModel.getType() == 4;

        //复制父级字段+_id
        realList.forEach(item -> {
            item.put(columnDataModel.getParentField() + "_id", item.get(columnDataModel.getParentField()));
        });
        //数据转换
        //递归处理控件
        List<FieLdsModel> fields = new ArrayList<>();
        OnlinePublicUtils.recursionFields(fields, fieLdsModels);
        visualDevJsonModel.setFormListModels(fields);
        realList = onlineSwapDataUtils.getSwapList(realList, fields, visualDevJsonModel.getId(), inlineEdit, new ArrayList<>());

        //取回传主键
        String pkeyId = visualDevJsonModel.getPkeyId() != null ? visualDevJsonModel.getPkeyId() : TableFeildsEnum.FID.getField();
        //结果集添加id
        for (Map<String, Object> objectMap : realList) {
            List<String> keyset = new ArrayList<>(objectMap.keySet());
            for (String key : keyset) {
                if (pkeyId.equalsIgnoreCase(key)) {
                    objectMap.put("id", objectMap.get(key));
                }
            }
        }
        //树形子字段key
        columnDataModel.setSubField(pkeyId);

        //添加流程状态
        if (visualDevJsonModel.isFlowEnable()) {
            FlowTemplateInfoVO templateInfo = flowTemplateService.info(visualDevJsonModel.getId());
            if (templateInfo == null) {
                throw new WorkFlowException("该功能未配置流程不可用!");
            }
            List<String> ids = realList.stream().map(i -> i.get("id").toString()).collect(Collectors.toList());
            List<FlowTaskEntity> tasks = flowTaskService.getInfosSubmit(ids.toArray(new String[]{}), FlowTaskEntity::getStatus, FlowTaskEntity::getId, FlowTaskEntity::getProcessId);
            realList.stream().forEach(m -> {
                String id = m.get("id").toString();
                m.put("flowState", "");
                tasks.forEach(i -> {
                    if (i.getId().equals(id) || i.getProcessId().equals(id)) {
                        m.put("flowState", i.getStatus());
                    }
                });
            });
        }
        return realList;
    }

    @Override
    public List<Map<String, Object>> getDataListLink(VisualDevJsonModel visualDevJsonModel, PaginationModel paginationModel) throws WorkFlowException {
        List<Map<String, Object>> realList;
        VisualdevShortLinkEntity shortLinkEnt = visualdevShortLinkService.getById(visualDevJsonModel.getId());
        List<VisualColumnSearchVO> listCondition = StringUtil.isNotEmpty(shortLinkEnt.getColumnCondition()) ? JsonUtil.createJsonToList(shortLinkEnt.getColumnCondition(), VisualColumnSearchVO.class) : new ArrayList<>();
        List<FieLdsModel> listFields = StringUtil.isNotEmpty(shortLinkEnt.getColumnCondition()) ? JsonUtil.createJsonToList(shortLinkEnt.getColumnText(), FieLdsModel.class) : new ArrayList<>();
        visualDevJsonModel.setFormListModels(listFields);
        FormDataModel formDataModel = visualDevJsonModel.getFormData();
        List<TableModel> visualTables = visualDevJsonModel.getVisualTables();
        //当前用户信息
        UserInfo userInfo = userProvider.get();
        List<String> isBetween = new ArrayList() {{
            add(ProjectKeyConsts.DATE);
            add(ProjectKeyConsts.TIME);
            add(ProjectKeyConsts.NUM_INPUT);
            add(ProjectKeyConsts.RATE);
            add(ProjectKeyConsts.SLIDER);
        }};
        for (VisualColumnSearchVO searchVO : listCondition) {
            String projectKey = searchVO.getConfig().getProjectKey();
            searchVO.setSearchType(isBetween.contains(projectKey) ? "3" : "2");
        }
        //菜单id
        String moduleId = paginationModel.getMenuId();
        ColumnDataModel columnDataModel = new ColumnDataModel();
        List<ColumnListField> list = JsonUtil.createJsonToList(shortLinkEnt.getColumnText(), ColumnListField.class);
        columnDataModel.setColumnList(JsonUtil.createListToJsonArray(list).toJSONString());//查询字段构造
        columnDataModel.setSearchList(JsonUtil.createListToJsonArray(listCondition).toJSONString());
        columnDataModel.setType(1);//普通列表
        visualDevJsonModel.setColumnData(columnDataModel);
        //查询
        List<FieLdsModel> fieLdsModels = JsonUtil.createJsonToList(formDataModel.getFields(), FieLdsModel.class);
        RecursionForm recursionForm = new RecursionForm(fieLdsModels, visualTables);
        List<FormAllModel> formAllModel = new ArrayList<>();
        FormCloumnUtil.recursionForm(recursionForm, formAllModel);
        //封装搜索数据
        OnlineProductSqlUtils.queryList(formAllModel, visualDevJsonModel, paginationModel);
        realList = this.getListWithTable(visualDevJsonModel, paginationModel, userInfo, moduleId, null);
        if (realList.size() < 1) {
            return realList;
        }
        //数据转换
        List<FieLdsModel> fields = new ArrayList<>();
        OnlinePublicUtils.recursionFields(fields, fieLdsModels);
        visualDevJsonModel.setFormListModels(fields);
        realList = onlineSwapDataUtils.getSwapList(realList, fields, visualDevJsonModel.getId(), false, new ArrayList<>());

        //添加流程状态
        if (visualDevJsonModel.isFlowEnable()) {
            FlowTemplateInfoVO templateInfo = flowTemplateService.info(visualDevJsonModel.getId());
            if (templateInfo == null) {
                throw new WorkFlowException("该功能未配置流程不可用!");
            }
            List<String> ids = realList.stream().map(i -> i.get("id").toString()).collect(Collectors.toList());
            List<FlowTaskEntity> tasks = flowTaskService.getInfosSubmit(ids.toArray(new String[]{}), FlowTaskEntity::getStatus, FlowTaskEntity::getId, FlowTaskEntity::getProcessId);
            realList.stream().forEach(m -> {
                String id = m.get("id").toString();
                m.put("flowState", "");
                tasks.forEach(i -> {
                    if (i.getId().equals(id) || i.getProcessId().equals(id)) {
                        m.put("flowState", i.getStatus());
                    }
                });
            });
        }
        return realList;
    }

    @Override
    public List<Map<String, Object>> getList(List<Map<String, Object>> noSwapDataList, List<VisualColumnSearchVO> searchVOList, PaginationModel paginationModel) {
        if (searchVOList.size() > 0) {
            //条件查询
            noSwapDataList = OnlineDevListUtils.getNoSwapList(noSwapDataList, searchVOList);
        }
        //排序
        if (noSwapDataList.size() > 0) {
            if (StringUtil.isNotEmpty(paginationModel.getSidx())) {
                //排序处理
                noSwapDataList.sort((o1, o2) -> {
                    Map<String, Object> i1 = o1;
                    Map<String, Object> i2 = o2;
                    String s1 = String.valueOf(i1.get(paginationModel.getSidx()));
                    String s2 = String.valueOf(i2.get(paginationModel.getSidx()));
                    if ("desc".equalsIgnoreCase(paginationModel.getSort())) {
                        return s2.compareTo(s1);
                    } else {
                        return s1.compareTo(s2);
                    }
                });
            }

            long total = noSwapDataList.size();

            //数据分页
            noSwapDataList = PageUtil.getListPage((int) paginationModel.getCurrentPage(), (int) paginationModel.getPageSize(), noSwapDataList);
            noSwapDataList = paginationModel.setData(noSwapDataList, total);
        }
        return noSwapDataList;
    }

    @Override
    public List<Map<String, Object>> getWithoutTableData(String modelId) {
        QueryWrapper<VisualdevModelDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualdevModelDataEntity::getVisualDevId, modelId);
        List<VisualdevModelDataEntity> list = this.list(queryWrapper);
        List<Map<String, Object>> dataVoList = list.parallelStream().map(t -> {
            Map<String, Object> dataMap = JsonUtil.stringToMap(t.getData());
            dataMap.put("id", t.getId());
            return dataMap;
        }).collect(Collectors.toList());
        return dataVoList;
    }

    @Override
    public List<Map<String, Object>> getListWithTable(VisualDevJsonModel visualDevJsonModel, PaginationModel paginationModel, UserInfo userInfo, String moduleId, List<String> columnPropList) {
        ColumnDataModel columnDataModel = visualDevJsonModel.getColumnData();
        List<Map<String, Object>> dataList = new ArrayList<>();
        //数据过滤
        SuperJsonModel ruleQuery = visualDevJsonModel.getRuleQuery();
        //高级搜索
        SuperJsonModel superQuery = visualDevJsonModel.getSuperQuery();
        //列表搜索
        SuperJsonModel query = visualDevJsonModel.getQuery();
        //数据过滤
        List<SuperJsonModel> authorizeListAll = visualDevJsonModel.getAuthorize();
        //关键词
        SuperJsonModel keyQuery = visualDevJsonModel.getKeyQuery();

        //数据源
        DbLinkEntity linkEntity = dblinkService.getInfo(visualDevJsonModel.getDbLinkId());
        try {
            DynamicDataSourceUtil.switchToDataSource(linkEntity);
            @Cleanup Connection connection = ConnUtil.getConnOrDefault(linkEntity);
            String databaseProductName = connection.getMetaData().getDatabaseProductName().trim();
            List<TableModel> tableModelList = visualDevJsonModel.getVisualTables();
            //主表
            TableModel mainTable = tableModelList.stream().filter(t -> t.getTypeId().equals("1")).findFirst().orElse(null);

            List<ColumnListField> modelList = JsonUtil.createJsonToList(columnDataModel.getColumnList(), ColumnListField.class);

            FormDataModel formData = visualDevJsonModel.getFormData();
            List<FieLdsModel> jsonToList = JsonUtil.createJsonToList(formData.getFields(), FieLdsModel.class);
            //递归处理控件
            List<FieLdsModel> allFieLds = new ArrayList<>();
            OnlinePublicUtils.recursionFields(allFieLds, jsonToList);
            //列表中区别子表正则
            String reg = "^[linzen_]\\S*_linzen\\S*";

            //所有字段
            List<String> collect = columnPropList != null ? columnPropList : modelList.stream().map(mode -> mode.getProp()).collect(Collectors.toList());
            if (OnlineDevData.TYPE_FIVE_COLUMNDATA.equals(columnDataModel.getType()) && !collect.contains(columnDataModel.getParentField())) {
                collect.add(columnDataModel.getParentField());
            }

            Map<String, String> tableFieldAndTableName = new HashMap<>(8);
            Map<String, String> tableNameAndTableField = new HashMap<>(8);
            allFieLds.stream().filter(f -> f.getConfig().getProjectKey().equals(ProjectKeyConsts.CHILD_TABLE)).forEach(f -> {
                tableFieldAndTableName.put(f.getVModel(), f.getConfig().getTableName());
                tableNameAndTableField.put(f.getConfig().getTableName(), f.getVModel());
            });

            Integer primaryKeyPolicy = formData.getPrimaryKeyPolicy();
            if (primaryKeyPolicy == 2 && !visualDevJsonModel.isFlowEnable()) {
                primaryKeyPolicy = 1;
            }
            String pkeyId = flowFormDataUtil.getKey(connection, mainTable.getTable(), primaryKeyPolicy);
            String childpkeyId = pkeyId;
            visualDevJsonModel.setPkeyId(pkeyId);
            if (!visualDevJsonModel.isFlowEnable() && primaryKeyPolicy == 2) {
                childpkeyId = flowFormDataUtil.getKey(connection, mainTable.getTable(), 1);
            }

            List<OnlineDynamicSqlModel> sqlModelList = new ArrayList<>();
            //根据表字段创建sqltable
            for (TableModel model : tableModelList) {
                OnlineDynamicSqlModel sqlModel = new OnlineDynamicSqlModel();
                sqlModel.setSqlTable(SqlTable.of(model.getTable()));
                sqlModel.setTableName(model.getTable());
                if (model.getTypeId().equals("1")) {
                    sqlModel.setMain(true);
                } else {
                    sqlModel.setForeign(model.getTableField());
                    sqlModel.setRelationKey(model.getRelationField());
                    sqlModel.setMain(false);
                }
                sqlModelList.add(sqlModel);
            }

            OnlineProductSqlUtils.getColumnListSql(sqlModelList, visualDevJsonModel, collect, linkEntity);
            //主表
            OnlineDynamicSqlModel mainSqlModel = sqlModelList.stream().filter(OnlineDynamicSqlModel::isMain).findFirst().orElse(null);
            //非主表
            List<OnlineDynamicSqlModel> dycList = sqlModelList.stream().filter(dyc -> !dyc.isMain()).collect(Collectors.toList());
            List<BasicColumn> sqlColumns = new ArrayList<>();
            Map<String, String> aliasMap = new HashMap<>();
            boolean isOracle = databaseProductName.equalsIgnoreCase("oracle");
            boolean isDm = databaseProductName.equalsIgnoreCase("DM DBMS");

            AbstractCount countDistinct;
            if (tableModelList.size() > 1) {
                countDistinct = SqlBuilder.countDistinct(mainSqlModel.getSqlTable().column(pkeyId));
            } else {
                countDistinct = SqlBuilder.count(mainSqlModel.getSqlTable().column(pkeyId));
            }
            for (OnlineDynamicSqlModel dynamicSqlModel : sqlModelList) {
                List<BasicColumn> basicColumns = Optional.ofNullable(dynamicSqlModel.getColumns()).orElse(new ArrayList<>());
                //达梦或者oracle 别名太长转换-底下有方法进行还原
                if (isOracle || isDm) {
                    for (int i = 0; i < basicColumns.size(); i++) {
                        BasicColumn item = basicColumns.get(i);
                        String alias = item.alias().orElse(null);
                        if (StringUtil.isNotEmpty(alias)) {
                            String aliasNewName = "A" + RandomUtil.uuId();
                            aliasMap.put(aliasNewName, alias);
                            basicColumns.set(i, item.as(aliasNewName));
                        }
                    }
                }
                sqlColumns.addAll(basicColumns);
            }
            QueryExpressionDSL<SelectModel> from = SqlBuilder.selectDistinct(sqlColumns).from(mainSqlModel.getSqlTable());
            QueryExpressionDSL<SelectModel> fromcount = SqlBuilder.select(countDistinct).from(mainSqlModel.getSqlTable());

            // 构造table和table下字段的分组
            Map<String, List<String>> tableFieldGroup = new HashMap<>(8);
            allFieLds.forEach(f -> {
                tableFieldGroup.computeIfAbsent(f.getConfig().getTableName(), k -> new ArrayList<>()).add(
                        "table".equals(f.getConfig().getType()) ? f.getConfig().getTableName() : f.getVModel()
                );
            });
            Map<String, SqlTable> subSqlTableMap = new HashMap<>();
            if (dycList.size() > 0) {
                for (OnlineDynamicSqlModel sqlModel : dycList) {
                    String relationKey = primaryKeyPolicy == 2 ? childpkeyId : sqlModel.getRelationKey();
                    //postgresql自增  int和varchar无法对比-添加以下判断
                    if (Objects.equals(formData.getPrimaryKeyPolicy(), 2) && "PostgreSQL".equalsIgnoreCase(databaseProductName)) {
                        relationKey += "::varchar";
                    }
                    from.leftJoin(sqlModel.getSqlTable())
                            .on(sqlModel.getSqlTable().column(sqlModel.getForeign()), new EqualTo(mainSqlModel.getSqlTable().column(relationKey)));
                    fromcount.leftJoin(sqlModel.getSqlTable())
                            .on(sqlModel.getSqlTable().column(sqlModel.getForeign()), new EqualTo(mainSqlModel.getSqlTable().column(relationKey)));
                    String tableName = sqlModel.getTableName();
                    List<String> fieldList = tableFieldGroup.get(tableName);
                    if (fieldList != null) {
                        fieldList.forEach(fieldKey -> {
                            subSqlTableMap.put(fieldKey, sqlModel.getSqlTable());
                        });
                    }
                }
            }

            QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder where = from.where();
            QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder whereCount = fromcount.where();

            //逻辑删除不展示
            if (visualDevJsonModel.getFormData().getLogicalDelete()) {
                where.and(mainSqlModel.getSqlTable().column(TableFeildsEnum.DEL_FLAG.getField()), SqlBuilder.isNull());
                whereCount.and(mainSqlModel.getSqlTable().column(TableFeildsEnum.DEL_FLAG.getField()), SqlBuilder.isNull());
            }
            //查询条件sql
            OnlineProductSqlUtils.getSuperSql(where, query, sqlModelList, databaseProductName, null, false);
            OnlineProductSqlUtils.getSuperSql(whereCount, query, sqlModelList, databaseProductName, null, false);

            //高级查询
            OnlineProductSqlUtils.getSuperSql(where, superQuery, sqlModelList, databaseProductName, null, false);
            OnlineProductSqlUtils.getSuperSql(whereCount, superQuery, sqlModelList, databaseProductName, null, false);

            // 数据过滤
            OnlineProductSqlUtils.getSuperSql(where, ruleQuery, sqlModelList, databaseProductName, null, false);
            OnlineProductSqlUtils.getSuperSql(whereCount, ruleQuery, sqlModelList, databaseProductName, null, false);

            // 关键词搜索
            OnlineProductSqlUtils.getSuperSql(where, keyQuery, sqlModelList, databaseProductName, null, false);
            OnlineProductSqlUtils.getSuperSql(whereCount, keyQuery, sqlModelList, databaseProductName, null, false);

            //数据权限
            if (columnDataModel.getUseDataPermission() != null && columnDataModel.getUseDataPermission()) {
                if (!userInfo.getIsAdministrator()) {
                    if (authorizeListAll.size() == 0) {
                        return new ArrayList<>();
                    }
                    OnlineProductSqlUtils.getSuperSql(where, authorizeListAll, sqlModelList, databaseProductName, null);
                    OnlineProductSqlUtils.getSuperSql(whereCount, authorizeListAll, sqlModelList, databaseProductName, null);
                }
            }

            //排序
            if (StringUtil.isNotEmpty(paginationModel.getSidx())) {
                String[] split = paginationModel.getSidx().split(",");
                List<SortSpecification> sidxList = new ArrayList<>();
                for (String sidx : split) {
                    //目前只支持主表排序
                    if (sidx.toLowerCase().contains("_linzen_") || sidx.toLowerCase().contains(ProjectKeyConsts.CHILD_TABLE_PREFIX)) {
                        continue;
                    }
                    SortSpecification sortSpecification;
                    if (sidx.startsWith("-")) {
                        sortSpecification = SqlBuilder.sortColumn(mainTable.getTable(), SqlTable.of(mainTable.getTable()).column(sidx.substring(1))).descending();
                    } else {
                        sortSpecification = SqlBuilder.sortColumn(mainTable.getTable(), SqlTable.of(mainTable.getTable()).column(sidx));
                    }
                    sidxList.add(sortSpecification);
                }
                where.orderBy(sidxList);
            } else {
                where.orderBy(SqlBuilder.sortColumn(mainTable.getTable(), SqlTable.of(mainTable.getTable()).column(pkeyId)));
            }

            // 1导出全部 0导出当前页 null 列表分页
            long count = 0;
            if (paginationModel.getDataType() == null) {
                SelectStatementProvider renderCount = whereCount.build().render(RenderingStrategies.MYBATIS3);
                count = flowFormDataMapper.count(renderCount);
                if (count == 0) {
                    return new ArrayList<>();
                }
                //树形和分组不需要分页。有脏数据传添加判断
                if (!Objects.equals(columnDataModel.getType(), 5) && !Objects.equals(columnDataModel.getType(), 3)) {
                    PageHelper.startPage((int) paginationModel.getCurrentPage(), (int) paginationModel.getPageSize(), false);
                }
            } else if ("0".equals(paginationModel.getDataType()) && !Objects.equals(columnDataModel.getType(), 5) && !Objects.equals(columnDataModel.getType(), 3)) {
                //where.limit(paginationModel.getPageSize()).offset((paginationModel.getCurrentPage()-1) * paginationModel.getPageSize());
                PageHelper.startPage((int) paginationModel.getCurrentPage(), (int) paginationModel.getPageSize(), false);
            }
            //分页语句放在最后执行, 后面不允许查询数据库, 否则分页失效
            SelectStatementProvider render = where.build().render(RenderingStrategies.MYBATIS3);
            dataList = flowFormDataMapper.selectManyMappedRows(render);

            String finalPkeyId = childpkeyId;
            List<String> idStringList = dataList.stream().map(m -> m.get(finalPkeyId).toString()).distinct().collect(Collectors.toList());
            if (idStringList.size() > 0) {
                //处理子表
                List<String> tableFields = collect.stream().filter(c -> c.toLowerCase().contains(ProjectKeyConsts.CHILD_TABLE_PREFIX)).collect(Collectors.toList());
                List<TableModel> childTableModels = tableModelList.stream().filter(t -> t.getTypeId().equals("0")).collect(Collectors.toList());
                Map<String, List<String>> tableMap = tableFields.stream().collect(Collectors.groupingBy(t -> t.substring(0, t.lastIndexOf("-"))));
                for (TableModel tableModel : childTableModels) {
                    String table = tableModel.getTable();
                    String tableField = tableNameAndTableField.get(table);
                    String fogID = tableModel.getTableField();
                    List<String> childFields = tableMap.get(tableField);
                    FieLdsModel fieLdsModel = allFieLds.stream().filter(item -> item.getVModel().equals(tableField)).findFirst().orElse(null);
                    List<FieLdsModel> childrenFieLdsModel = fieLdsModel != null ? fieLdsModel.getConfig().getChildren() : Collections.EMPTY_LIST;
                    if (childFields != null) {
                        OnlineDynamicSqlModel onlineDynamicSqlModel = sqlModelList.stream().filter(s -> s.getTableName().equalsIgnoreCase(table)).findFirst().orElse(null);
                        SqlTable childSqlTable = onlineDynamicSqlModel.getSqlTable();
                        List<BasicColumn> childSqlColumns = new ArrayList<>();
                        for (String c : childFields) {
                            String childT = c.substring(0, c.lastIndexOf("-"));
                            String childF = c.substring(c.lastIndexOf("-") + 1);
                            FieLdsModel thisFieLdsModel = childrenFieLdsModel.stream().filter(item -> item.getVModel().equals(childF)).findFirst().orElse(null);
                            SqlColumn<Object> column = childSqlTable.column(childF);
                            if ((isDm || isOracle) && thisFieLdsModel != null) {
                                String projectKey = thisFieLdsModel.getConfig().getProjectKey();
                                if (ProjectKeyConsts.getTextField().contains(projectKey)) {
                                    column = childSqlTable.column("dbms_lob.substr(" + childF + ")").as(childF);
                                }
                            }
                            childSqlColumns.add(column);
                        }
                        childSqlColumns.add(childSqlTable.column(fogID));
                        QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder childWhere = SqlBuilder.select(childSqlColumns).from(childSqlTable).where();
                        childWhere.and(childSqlTable.column(fogID), SqlBuilder.isIn(idStringList));
                        //查询条件sql
                        OnlineProductSqlUtils.getSuperSql(childWhere, query, sqlModelList, databaseProductName, table, false);
                        //高级查询
                        OnlineProductSqlUtils.getSuperSql(childWhere, superQuery, sqlModelList, databaseProductName, table, false);
                        // 数据过滤
                        OnlineProductSqlUtils.getSuperSql(childWhere, ruleQuery, sqlModelList, databaseProductName, table, false);
                        // 关键词搜索
                        OnlineProductSqlUtils.getSuperSql(childWhere, keyQuery, sqlModelList, databaseProductName, table, false);
                        //数据权限
                        if (columnDataModel.getUseDataPermission() != null && columnDataModel.getUseDataPermission()) {
                            if (!userInfo.getIsAdministrator()) {
                                OnlineProductSqlUtils.getSuperSql(childWhere, authorizeListAll, sqlModelList, databaseProductName, table);
                            }
                        }
                        SelectStatementProvider childRender = childWhere.build().render(RenderingStrategies.MYBATIS3);
                        List<Map<String, Object>> mapList = flowFormDataMapper.selectManyMappedRows(childRender);
                        Map<String, List<Map<String, Object>>> idMap = mapList.stream().collect(Collectors.groupingBy(m -> m.get(fogID).toString()));

                        for (Map<String, Object> m : dataList) {
                            String s = m.get(childpkeyId).toString();
                            Map<String, Object> valueMap = new HashMap<>();
                            valueMap.put(tableField, idMap.get(s));
                            m.putAll(valueMap);
                        }
                    }
                }
            } else {
                return new ArrayList<>();
            }

            //添加id属性
            dataList = FormPublicUtils.addIdToList(dataList, finalPkeyId, visualDevJsonModel.isFlowEnable());

            //别名key还原
            setAliasKey(dataList, aliasMap);

            PageInfo pageInfo = new PageInfo(dataList);
            paginationModel.setTotal(count);
            paginationModel.setCurrentPage(pageInfo.getPageNum());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DynamicDataSourceUtil.clearSwitchDataSource();
        }
        return dataList;
    }

    private List<ConditionJsonModel> superQueryList(List<SuperQueryJsonModel> queryJsonModels, Map<String, String> tableFieldAndTableName, String mainTable) {
        List<ConditionJsonModel> superQueryList = new ArrayList<>();
        for (SuperQueryJsonModel jsonModel : queryJsonModels) {
            List<FieLdsModel> conditionList = JsonUtil.createJsonToList(jsonModel.getGroups(), FieLdsModel.class);
            for (FieLdsModel fieLdsModel : conditionList) {
                ConditionJsonModel sup = BeanUtil.toBean(fieLdsModel, ConditionJsonModel.class);
                sup.setTableName(fieLdsModel.getConfig().getRelationTable());
                String field = sup.getField();
                String mastKey = fieLdsModel.getConfig().getProjectKey();
                if (field.toLowerCase().contains(ProjectKeyConsts.CHILD_TABLE_PREFIX)) {
                    String substring = field.substring(field.lastIndexOf("-") + 1);
                    String tableField = field.substring(0, field.indexOf("-"));
                    sup.setField(substring);
                    sup.setTableName(tableFieldAndTableName.get(tableField));
                } else if (StringUtil.isEmpty(sup.getTableName())) {
                    sup.setTableName(mainTable);
                }
                if (mastKey.equals(ProjectKeyConsts.CHECKBOX) || mastKey.equals(ProjectKeyConsts.ADDRESS) || mastKey.equals(ProjectKeyConsts.CASCADER) || mastKey.equals(ProjectKeyConsts.COMSELECT)) {
                    fieLdsModel.setMultiple(true);
                }
                if (fieLdsModel.getMultiple() && StringUtil.isEmpty(sup.getFieldValue())) {
                    sup.setFieldValue("[]");
                }
                superQueryList.add(sup);
            }
        }
        return superQueryList;
    }

    private void getGroupFeild(TableFeildsEnum flowid, SqlColumn n, String databaseProductName, List<BasicColumn> groupBySqlTable, TableModel mainTable) {
        if (flowid.getField().equalsIgnoreCase(n.name())) {
            if (needUpcaseFieldsDB.contains(databaseProductName)) {
                groupBySqlTable.add(SqlTable.of(mainTable.getTable()).column(flowid.getField().toUpperCase()));
            } else {
                groupBySqlTable.add(SqlTable.of(mainTable.getTable()).column(flowid.getField()));
            }
        }
    }

    @Override
    public List<Map<String, Object>> getRelationFormList(VisualDevJsonModel visualDevJsonModel, PaginationModel paginationModel) {
        FormDataModel formData = visualDevJsonModel.getFormData();
        List<String> collect = StringUtil.isNotEmpty(paginationModel.getColumnOptions()) ? Arrays.asList(paginationModel.getColumnOptions().split(",")) : new ArrayList<>();
        List<FieLdsModel> fieLdsModels = JsonUtil.createJsonToList(formData.getFields(), FieLdsModel.class);

        List<FieLdsModel> mainFieldModelList = new ArrayList<>();


        List<Map<String, Object>> noSwapDataList = new ArrayList<>();
        List<VisualColumnSearchVO> searchVOList = new ArrayList<>();
        //列表中区别子表正则
        String reg = "^[linzen_]\\S*_linzen\\S*";
        //查询的关键字
        String keyword = paginationModel.getKeyword();
        //判断有无表
        if (visualDevJsonModel.getVisualTables().size() > 0) {
            try {
                List<TableModel> tableModelList = JsonUtil.createJsonToList(visualDevJsonModel.getVisualTables(), TableModel.class);


                OnlinePublicUtils.recursionFields(mainFieldModelList, fieLdsModels);

                //主表
                TableModel mainTable = tableModelList.stream().filter(t -> t.getTypeId().equals("1")).findFirst().orElse(null);

                DbLinkEntity linkEntity = dblinkService.getInfo(visualDevJsonModel.getDbLinkId());

                List<OnlineDynamicSqlModel> sqlModelList = new ArrayList<>();
                //根据表字段创建sqltable
                for (TableModel model : tableModelList) {
                    OnlineDynamicSqlModel sqlModel = new OnlineDynamicSqlModel();
                    sqlModel.setSqlTable(SqlTable.of(model.getTable()));
                    sqlModel.setTableName(model.getTable());
                    if (model.getTypeId().equals("1")) {
                        sqlModel.setMain(true);
                    } else {
                        sqlModel.setForeign(model.getTableField());
                        sqlModel.setRelationKey(model.getRelationField());
                        sqlModel.setMain(false);
                    }
                    sqlModelList.add(sqlModel);
                }

                //判断是否分页
                Boolean isPage = paginationModel.getPageSize() > 500 ? false : true;
                //获取表单主表副表全字段
                List<String> allFields = new ArrayList<>();
                for (FieLdsModel item : mainFieldModelList) {
                    if (StringUtil.isNotEmpty(item.getVModel()) && !item.getVModel().toLowerCase().startsWith(ProjectKeyConsts.CHILD_TABLE_PREFIX)) {
                        allFields.add(item.getVModel());
                    }
                }
                OnlineProductSqlUtils.getColumnListSql(sqlModelList, visualDevJsonModel, allFields, linkEntity);
                DynamicDataSourceUtil.switchToDataSource(linkEntity);
                @Cleanup Connection connection = ConnUtil.getConnOrDefault(linkEntity);
                String databaseProductName = connection.getMetaData().getDatabaseProductName().trim();
                //主表
                OnlineDynamicSqlModel mainSqlModel = sqlModelList.stream().filter(OnlineDynamicSqlModel::isMain).findFirst().orElse(null);
                //非主表
                List<OnlineDynamicSqlModel> dycList = sqlModelList.stream().filter(dyc -> !dyc.isMain()).collect(Collectors.toList());
                List<BasicColumn> sqlColumns = new ArrayList<>();
                for (OnlineDynamicSqlModel dynamicSqlModel : sqlModelList) {
                    List<BasicColumn> basicColumns = Optional.ofNullable(dynamicSqlModel.getColumns()).orElse(new ArrayList<>());
                    sqlColumns.addAll(basicColumns);
                }
                QueryExpressionDSL<SelectModel> from = SqlBuilder.selectDistinct(sqlColumns).from(mainSqlModel.getSqlTable());

                if (dycList.size() > 0) {
                    for (OnlineDynamicSqlModel sqlModel : dycList) {
                        from.leftJoin(sqlModel.getSqlTable()).on(sqlModel.getSqlTable().column(sqlModel.getForeign()), new EqualTo(mainSqlModel.getSqlTable().column(sqlModel.getRelationKey())));
                    }
                }
                //富文本。强行加一个字段转换
                boolean isClobDbType = databaseProductName.equalsIgnoreCase("oracle") || databaseProductName.equalsIgnoreCase("DM DBMS");
                if (isClobDbType) {
                    for (int n = 0; n < sqlColumns.size(); n++) {
                        SqlColumn sq = (SqlColumn) sqlColumns.get(n);
                        String name = sq.name();
                        for (TableFields tableFields : mainTable.getFields()) {
                            if ("text".equals(tableFields.getDataType()) && name.equals(tableFields.getField())) {
                                if (dycList.size() > 0) {
                                    sqlColumns.set(n, SqlTable.of("dbms_lob.substr(" + mainSqlModel.getTableName()).column(name + ")").as(name));
                                } else {
                                    sqlColumns.set(n, SqlTable.of(mainSqlModel.getTableName()).column("dbms_lob.substr(" + name + ")").as(name));
                                }
                            }
                        }
                    }
                }

                QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder where = from.where();

                //数据过滤
                List<TableModel> visualTables = visualDevJsonModel.getVisualTables();
                RecursionForm recursionForm = new RecursionForm(fieLdsModels, visualTables);
                List<FormAllModel> formAllModel = new ArrayList<>();
                FormCloumnUtil.recursionForm(recursionForm, formAllModel);
                OnlineProductSqlUtils.queryList(formAllModel, visualDevJsonModel, paginationModel);
                SuperJsonModel ruleQuery = visualDevJsonModel.getRuleQuery();
                OnlineProductSqlUtils.getSuperSql(where, ruleQuery, sqlModelList, databaseProductName, null, false);

                //逻辑删除不展示
                if (visualDevJsonModel.getFormData().getLogicalDelete()) {
                    where.and(mainSqlModel.getSqlTable().column(TableFeildsEnum.DEL_FLAG.getField()), SqlBuilder.isNull());
                }

                Integer primaryKeyPolicy = visualDevJsonModel.getFormData().getPrimaryKeyPolicy();
                if (primaryKeyPolicy == 2 && !visualDevJsonModel.isFlowEnable()) {
                    primaryKeyPolicy = 1;
                }
                String pkeyId = flowFormDataUtil.getKey(connection, mainTable.getTable(), primaryKeyPolicy);
                visualDevJsonModel.setPkeyId(pkeyId);

                //排序
                if (StringUtil.isNotEmpty(paginationModel.getSidx())) {
                    String[] split = paginationModel.getSidx().split(",");
                    List<SortSpecification> sidxList = new ArrayList<>();
                    for (String sidx : split) {
                        //目前只支持主表排序
                        if (sidx.toLowerCase().contains("_linzen_") || sidx.toLowerCase().contains(ProjectKeyConsts.CHILD_TABLE_PREFIX)) {
                            continue;
                        }
                        SortSpecification sortSpecification;
                        if (sidx.startsWith("-")) {
                            sortSpecification = SqlBuilder.sortColumn(mainTable.getTable(), SqlTable.of(mainTable.getTable()).column(sidx.substring(1))).descending();
                        } else {
                            sortSpecification = SqlBuilder.sortColumn(mainTable.getTable(), SqlTable.of(mainTable.getTable()).column(sidx));
                        }
                        sidxList.add(sortSpecification);
                    }
                    where.orderBy(sidxList);
                } else {
                    where.orderBy(SqlBuilder.sortColumn(mainTable.getTable(), SqlTable.of(mainTable.getTable()).column(pkeyId)));
                }

                SelectStatementProvider render = where.build().render(RenderingStrategies.MYBATIS3);
                List<Map<String, Object>> dataList = flowFormDataMapper.selectManyMappedRows(render);

                noSwapDataList = dataList.stream().map(data -> {
                    data.put("id", String.valueOf(data.get(pkeyId)));
                    return data;
                }).collect(Collectors.toList());

                //第二种 有关键字不分页
                if (StringUtil.isNotEmpty(keyword)) {
                    for (FieLdsModel fieldsModel : mainFieldModelList) {
                        if (fieldsModel.getVModel() != null) {
                            boolean b = collect.stream().anyMatch(c -> fieldsModel.getVModel().equalsIgnoreCase(c));
                            //组装为查询条件
                            if (b) {
                                VisualColumnSearchVO vo = new VisualColumnSearchVO();
                                vo.setSearchType("2");
                                vo.setVModel(fieldsModel.getVModel());
                                vo.setValue(keyword);
                                vo.setConfig(fieldsModel.getConfig());
                                Boolean multiple = fieldsModel.getMultiple();
                                vo.setMultiple(multiple);
                                searchVOList.add(vo);
                            }
                        }
                    }
                    noSwapDataList = onlineSwapDataUtils.getSwapList(noSwapDataList, mainFieldModelList, visualDevJsonModel.getId(), false, new ArrayList<>());

                    noSwapDataList = RelationFormUtils.getRelationListByKeyword(noSwapDataList, searchVOList);
                } else {
                    noSwapDataList = onlineSwapDataUtils.getSwapList(noSwapDataList, mainFieldModelList, visualDevJsonModel.getId(), false, new ArrayList<>());
                }
                //假分页
                if (isPage) {
                    if (CollectionUtils.isNotEmpty(noSwapDataList)) {
                        paginationModel.setTotal(noSwapDataList.size());
                        List<List<Map<String, Object>>> partition = Lists.partition(noSwapDataList, (int) paginationModel.getPageSize());
                        noSwapDataList = partition.get((int) paginationModel.getCurrentPage() - 1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                DynamicDataSourceUtil.clearSwitchDataSource();
            }
        }
        if (noSwapDataList.size() < 1) {
            return new ArrayList<>();
        }
        return noSwapDataList;
    }

    /**
     * 达梦或者oracle 别名太长转换-别名还原
     *
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    private void setAliasKey(List<Map<String, Object>> dataList, Map<String, String> aliasMap) {
        if (dataList.size() > 0 && aliasMap.size() > 0) {
            for (int i = 0; i < dataList.size(); i++) {
                Map<String, Object> objMap = dataList.get(i);
                Set<String> aliasKey = aliasMap.keySet();
                Map<String, Object> newObj = new HashMap<>();
                for (String key : objMap.keySet()) {
                    Object value = objMap.get(key);
                    String oldKey = aliasMap.get(key);
                    if (aliasKey.contains(key)) {
                        newObj.put(oldKey, value);
                    } else {
                        newObj.put(key, value);
                    }
                }
                dataList.remove(i);
                dataList.add(i, newObj);
            }
        }
    }
}
