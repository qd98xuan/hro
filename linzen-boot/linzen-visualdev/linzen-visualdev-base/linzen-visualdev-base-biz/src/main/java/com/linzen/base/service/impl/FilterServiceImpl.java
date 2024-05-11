package com.linzen.base.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.entity.FilterEntity;
import com.linzen.base.entity.VisualdevEntity;
import com.linzen.base.mapper.FilterMapper;
import com.linzen.base.model.filter.RuleInfo;
import com.linzen.base.service.FilterService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.base.util.OnlineFilterUtil;
import com.linzen.database.util.DbTypeUtil;
import com.linzen.database.util.DynamicDataSourceUtil;
import com.linzen.util.JsonUtil;
import com.linzen.util.RandomUtil;
import com.linzen.util.context.RequestContext;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.AndOrCriteriaGroup;
import org.mybatis.dynamic.sql.DerivedColumn;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.where.WhereModel;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class FilterServiceImpl extends SuperServiceImpl<FilterMapper, FilterEntity> implements FilterService {

    @Override
    public void saveRuleList(String moduleId, VisualdevEntity visualdevEntity, Integer app, Integer pc, Map<String,String> tableMap) {

        String columnData = visualdevEntity.getColumnData();
        String appColumnData = visualdevEntity.getAppColumnData();

        if (columnData == null || columnData.length() == 0) {
            columnData = "{}";
        }
        if (appColumnData == null || appColumnData.length() == 0) {
            appColumnData = "{}";
        }
        Map config = JsonUtil.createJsonToBean(columnData, Map.class);
        String ruleList = JSONUtil.toJsonStr(config.get("ruleList"));
        Map configApp = BeanUtil.toBean(appColumnData, Map.class);
        String ruleListApp = JSONUtil.toJsonStr(configApp.get("ruleListApp"));
        FilterEntity entity = new FilterEntity();
        entity.setId(RandomUtil.uuId());
        entity.setModuleId(moduleId);
        replaceRealValue(app, pc, tableMap, ruleList, ruleListApp, entity);
        entity.setCreatorTime(new Date());
        entity.setUpdateTime(new Date());
        this.save(entity);
    }

    @Override
    public void updateRuleList(String moduleId, VisualdevEntity visualdevEntity, Integer app, Integer pc, Map<String,String> tableMap) {
        String columnData = visualdevEntity.getColumnData();
        String appColumnData = visualdevEntity.getAppColumnData();
        if (columnData == null || columnData.isEmpty()) {
            columnData = "{}";
        }
        if (appColumnData == null || appColumnData.isEmpty()) {
            appColumnData = "{}";
        }

        Map config = JsonUtil.createJsonToBean(columnData, Map.class);
        String ruleList = JSONUtil.toJsonStr(config.get("ruleList"));
        Map configApp = BeanUtil.toBean(appColumnData, Map.class);
        String ruleListApp = JSONUtil.toJsonStr(configApp.get("ruleListApp"));

        List<FilterEntity> list = this.getBaseMapper()
                .selectList(new QueryWrapper<FilterEntity>().lambda().eq(FilterEntity::getModuleId, moduleId));
        if (list == null || list.size() == 0) {
            this.saveRuleList(moduleId, visualdevEntity,app,pc, tableMap);
        } else {
            FilterEntity entity = list.get(0);
            replaceRealValue(app, pc, tableMap, ruleList, ruleListApp, entity);
            entity.setUpdateTime(new Date());
            this.updateById(entity);
        }
    }

    /**
     * 把子表的表名换成实际数据库表名
     * @param app 是否更新app配置
     * @param pc 是否更新pc配置
     * @param tableMap 虚拟表名和实际表名映射
     * @param ruleList pc配置
     * @param ruleListApp app配置
     * @param entity 更新的数据
     */
    private void replaceRealValue(Integer app, Integer pc, Map<String, String> tableMap, String ruleList, String ruleListApp, FilterEntity entity) {
        if(app == 1 && StringUtils.isNotBlank(ruleListApp)){
            for(String key : tableMap.keySet()){
                ruleListApp = ruleListApp.replaceAll(key,tableMap.get(key));
            }

            entity.setConfigApp(ruleListApp);
        }
        if(pc==1 && StringUtils.isNotBlank(ruleList)){
            for(String key : tableMap.keySet()){
                ruleList = ruleList.replaceAll(key,tableMap.get(key));
            }
            entity.setConfig(ruleList);
        }
    }


    @Override
    public void handleWhereCondition(SqlTable sqlTable, QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder where, String id, Map<String, SqlTable> subSqlTableMap, String databaseProductName, Map<String,Object> params) {
        try {
            DynamicDataSourceUtil.switchToDataSource(null);
            List<RuleInfo> ruleInfos = this.getCondition(id);

            QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder whereFilter = SqlBuilder.select(sqlTable.allColumns()).from(sqlTable).where();

            for (int i = 0; i < ruleInfos.size(); i++) {
                RuleInfo info = ruleInfos.get(i);
                OnlineFilterUtil genUtil = BeanUtil.toBean(info, OnlineFilterUtil.class);
                genUtil.setDbType(DbTypeUtil.getDbEncodeByProductName(databaseProductName));
                genUtil.setSubSqlTableMap(subSqlTableMap);
                genUtil.setParams(params);
                genUtil.solveValue(whereFilter, sqlTable);
            }

            Method method = whereFilter.getClass().getDeclaredMethod("buildWhereModel");
            method.setAccessible(true);
            WhereModel invoke = (WhereModel) method.invoke(whereFilter);
            List<AndOrCriteriaGroup> groupList = invoke.subCriteria();
            where.and(DerivedColumn.of("1"), SqlBuilder.isEqualTo(1), groupList);


        } catch (Exception ignored) {
        }finally {
            DynamicDataSourceUtil.clearSwitchDataSource();
        }

    }

    /**
     * 在线过滤查询
     * @param sqlTable
     * @param where
     * @param id
     * @param subSqlTableMap
     * @param databaseProductName
     */
    @Override
    public void handleWhereCondition(SqlTable sqlTable, QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder where, String id, Map<String, SqlTable> subSqlTableMap, String databaseProductName) {
        this.handleWhereCondition(sqlTable,where,id,subSqlTableMap,databaseProductName,null);
    }

    /**
     * 获取过滤配置
     * @param id
     * @return
     */
    @Override
    public List<RuleInfo> getCondition(String id) {
        if (StringUtils.isEmpty(id)) {
            return new ArrayList<>();
        }
        QueryWrapper<FilterEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(FilterEntity::getModuleId, id);
        FilterEntity entity = this.getOne(wrapper);
        // 获取app端还是web端
        String config;
        boolean	isApp = !RequestContext.isOrignPc();
        if(isApp){
            config = entity.getConfigApp();
        }else{
            config = entity.getConfig();
        }

        List<RuleInfo> ruleInfos = new ArrayList<>();
        if(StringUtils.isNoneBlank(config)){
            ruleInfos = JsonUtil.createJsonToList(config, RuleInfo.class);
        }

        return ruleInfos;
    }
}
