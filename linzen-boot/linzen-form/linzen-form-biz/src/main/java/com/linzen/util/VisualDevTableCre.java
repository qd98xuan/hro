package com.linzen.util;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.linzen.database.constant.DbFieldConst;
import com.linzen.database.model.dbfield.DbFieldModel;
import com.linzen.database.model.dbtable.DbTableFieldModel;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.source.DbBase;
import com.linzen.database.util.DataSourceUtil;
import com.linzen.exception.WorkFlowException;
import com.linzen.model.form.VisualTableModel;
import com.linzen.model.visualJson.FieLdsModel;
import com.linzen.model.visualJson.TableFields;
import com.linzen.model.visualJson.TableModel;
import com.linzen.model.visualJson.analysis.FormAllModel;
import com.linzen.model.visualJson.analysis.FormColumnTableModel;
import com.linzen.model.visualJson.analysis.FormEnum;
import com.linzen.util.visiual.ProjectKeyConsts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Component
public class VisualDevTableCre {
    private static String relationField = TableFeildsEnum.FID.getField();
    private static String tableField = TableFeildsEnum.FOREIGN.getField();
    @Autowired
    private ServiceBaseUtil serviceUtil;
    @Autowired
    private DataSourceUtil dataSourceUtil;

    /**
     * 表单赋值tableName
     *
     * @param jsonArray
     * @param tableModels
     */
    private void fieldsTableName(JSONArray jsonArray, List<TableModel> tableModels) {
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            String linzenkey = jsonObject.getJSONObject("__config__").getString("projectKey");
            List<String> childrenListAll = new ArrayList(){{
                add(FormEnum.card.getMessage());
                add(FormEnum.row.getMessage());
                add(FormEnum.tab.getMessage());
                add(FormEnum.collapse.getMessage());
                add(FormEnum.collapseItem.getMessage());
                add(FormEnum.tabItem.getMessage());
                add(FormEnum.tableGrid.getMessage());
                add(FormEnum.tableGridTr.getMessage());
                add(FormEnum.tableGridTd.getMessage());
            }};
            if (childrenListAll.contains(linzenkey) || StringUtil.isEmpty(linzenkey)) {
                JSONArray childArray = jsonObject.getJSONObject("__config__").getJSONArray("children");
                this.fieldsTableName(childArray, tableModels);
                jsonObject.getJSONObject("__config__" ).put("children" , childArray);
            } else if (FormEnum.table.getMessage().equals(linzenkey)) {
                JSONArray childrenList = new JSONArray();
                JSONArray children = jsonObject.getJSONObject("__config__" ).getJSONArray("children" );
                String tableModel = "";
                for (int k = 0; k < children.size(); k++) {
                    JSONObject childrenObject = (JSONObject) children.get(k);
                    this.fieldsModel(childrenObject, tableModels);
                    if (StringUtil.isEmpty(tableModel)) {
                        tableModel = childrenObject.getJSONObject("__config__" ).getString("relationTable" );
                    }
                    childrenList.add(childrenObject);
                }
                jsonObject.getJSONObject("__config__" ).put("tableName" , tableModel);
                jsonObject.getJSONObject("__config__" ).put("children" , childrenList);
            } else {
                this.fieldsModel(jsonObject, tableModels);
            }
        }
    }

    /**
     * 赋值table
     *
     * @param jsonObject
     * @param tableModels
     */
    private TableModel fieldsModel(JSONObject jsonObject, List<TableModel> tableModels) {
        String vModel = jsonObject.getString("__vModel__" );
        String relationField = StringUtil.isNotEmpty(jsonObject.getString("relationField" )) ? jsonObject.getString("relationField" ) : "";
        String linzenkey = jsonObject.getJSONObject("__config__" ).getString("projectKey" );
        TableModel tableName = tableModels.stream().filter(t -> "1".equals(t.getTypeId())).findFirst().orElse(null);
        if (tableName != null) {
            jsonObject.getJSONObject("__config__" ).put("tableName" , tableName.getTable());
        }
        List<TableModel> childTableAll = tableModels.stream().filter(t -> "0".equals(t.getTypeId())).collect(Collectors.toList());
        TableModel childTableaa = childTableAll.stream().filter(t -> t.getFields().stream().filter(k -> k.getField().equals(vModel)).count() > 0).findFirst().orElse(null);
        if (childTableaa != null) {
            jsonObject.getJSONObject("__config__" ).put("relationTable" , childTableaa.getTable());
        }
        if (FormEnum.relationFormAttr.getMessage().equals(linzenkey) || FormEnum.popupAttr.getMessage().equals(linzenkey)) {
            if (StringUtil.isNotEmpty(relationField)) {
                Boolean isSubTable = jsonObject.getJSONObject("__config__" ).getBooleanValue("isSubTable" );
                String model = relationField.split("_linzenTable_" )[0];
                jsonObject.put("relationField" , model + "_linzenTable_" + tableName.getTable() + (isSubTable ? "0" : "1" ));
            }
        }
        return childTableaa;
    }

    /**
     * 创建表
     *
     * @return
     */
    public List<TableModel> tableList(VisualTableModel visualTableModel) throws WorkFlowException {
        JSONArray jsonArray = visualTableModel.getJsonArray();
        List<FormAllModel> formAllModel = visualTableModel.getFormAllModel();
        String table = visualTableModel.getTable();
        String linkId = visualTableModel.getLinkId();
        String fullName = visualTableModel.getFullName();
        int primaryKey = visualTableModel.getPrimaryKey();
        List<TableModel> tableModelList = new LinkedList<>();
        Map<String, String> tableNameList = new HashMap<>();
        DbLinkEntity dbLink = serviceUtil.getDbLink(linkId);
        String type = dbLink != null ? dbLink.getDbType() : dataSourceUtil.getDbType();
        boolean isUpperCase = (DbBase.DM.equals(type) || DbBase.ORACLE.equals(type));
        boolean isLowerCase = (DbBase.POSTGRE_SQL.equals(type) || DbBase.KINGBASE_ES.equals(type));
        table = tableName(table, isUpperCase, isLowerCase);
        relationField = tableName(relationField, isUpperCase, isLowerCase);
        tableField = tableName(tableField, isUpperCase, isLowerCase);
        try {
            List<DbFieldModel> fieldList = new ArrayList<>();
            Map<String, List<DbFieldModel>> tableListAll = new HashMap<>();
            for (FormAllModel model : formAllModel) {
                if (FormEnum.mast.getMessage().equals(model.getProjectKey())) {
                    FieLdsModel fieLdsModel = model.getFormColumnModel().getFieLdsModel();
                    this.fieldList(fieLdsModel, table, fieldList);
                } else if (FormEnum.table.getMessage().equals(model.getProjectKey())) {
                    String tableName = "ct" + RandomUtil.uuId();
                    FormColumnTableModel fieLdsModel = model.getChildList();
                    List<DbFieldModel> tableList = new ArrayList<>();
                    String tableModel = fieLdsModel.getTableModel();
                    List<FieLdsModel> fieldsList = fieLdsModel.getChildList().stream().map(t -> t.getFieLdsModel()).collect(Collectors.toList());
                    for (FieLdsModel tableFieLdsModel : fieldsList) {
                        this.fieldList(tableFieLdsModel, tableName, tableList);
                    }
                    this.dbTableField(tableList, true, primaryKey);
                    tableNameList.put(tableModel, tableName);
                    tableListAll.put(tableModel, tableList);
                }
            }

            fieldList.add(ConcurrencyUtils.getDbFieldModel(TableFeildsEnum.FLOWID));
            fieldList.add(ConcurrencyUtils.getDbFieldModel(TableFeildsEnum.VERSION));
            fieldList.add(ConcurrencyUtils.getDbFieldModel(TableFeildsEnum.FLOWTASKID));
            fieldList.add(ConcurrencyUtils.getDbFieldModel(TableFeildsEnum.TENANTID));
            if(visualTableModel.getLogicalDelete()) {//删除标志字段
                fieldList.add(ConcurrencyUtils.getDbFieldModel(TableFeildsEnum.DEL_FLAG));
            }
            this.dbTableField(fieldList, false, primaryKey);
            List<DbTableFieldModel> dbTableList = new ArrayList<>();
            //创建子表
            for (String key : tableListAll.keySet()) {
                String tableName = tableName(tableNameList.get(key), isUpperCase, isLowerCase);
                List<DbFieldModel> datableList = tableListAll.get(key);
                this.tableModel(tableModelList, datableList, tableName, table, true);
                datableList.add(ConcurrencyUtils.getDbFieldModel(TableFeildsEnum.TENANTID));
                DbTableFieldModel dbTable = this.dbTable(linkId, tableName, datableList, true, fullName);
                dbTableList.add(dbTable);
            }
            this.tableModel(tableModelList, fieldList, table, table, false);
            DbTableFieldModel dbTable = this.dbTable(linkId, table, fieldList, false, fullName);
            dbTableList.add(dbTable);
            serviceUtil.createTable(dbTableList);
            this.fieldsTableName(jsonArray, tableModelList);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("表新增错误:{}" , e.getMessage());
            throw new WorkFlowException("表新增错误:" + e.getMessage());
        }
        return tableModelList;
    }

    /**
     * 表名
     *
     * @param tableName
     * @param isUpperCase
     * @param isLowerCase
     * @return
     */
    private String tableName(String tableName, boolean isUpperCase, boolean isLowerCase) {
        String resultName = isUpperCase ? tableName.toUpperCase() : isLowerCase ? tableName.toLowerCase() : tableName;
        return resultName;
    }

    /**
     * 获取表单字段
     *
     * @param fieLdsModel
     * @param tableList
     */
    private void fieldList(FieLdsModel fieLdsModel, String table, List<DbFieldModel> tableList) {
        String vmodel = fieLdsModel.getVModel();
        String lable = fieLdsModel.getConfig().getLabel();
        String linzenkey = fieLdsModel.getConfig().getProjectKey();
        fieLdsModel.getConfig().setTableName(table);
        if (StringUtil.isNotEmpty(vmodel)) {
            DbFieldModel fieldForm = new DbFieldModel();
            fieldForm.setNullSign(DbFieldConst.NULL);
            fieldForm.setDataType("varchar" );
            fieldForm.setLength("255" );
            fieldForm.setIsPrimaryKey(false);
            if (StringUtil.isNotEmpty(fieLdsModel.getVModel())) {
                if (ProjectKeyConsts.getTextField().contains(linzenkey)) {
                    fieldForm.setDataType("text" );
                }
                List<String> date = new ArrayList() {{
                    add(ProjectKeyConsts.MODIFYTIME);
                    add(ProjectKeyConsts.CREATETIME);
                    add(ProjectKeyConsts.DATE);
                }};
                if (date.contains(linzenkey)) {
                    fieldForm.setDataType("datetime" );
                }
                if (ProjectKeyConsts.NUM_INPUT.equals(linzenkey) || ProjectKeyConsts.CALCULATE.equals(linzenkey) || ProjectKeyConsts.SLIDER.equals(linzenkey)) {
                    fieldForm.setDataType("decimal");
                    String precision = "15";
                    if (fieLdsModel.getPrecision() != null) {
                        precision=String.valueOf(fieLdsModel.getPrecision());
                    }
                    fieldForm.setLength("38," + precision);
                    //mysql 最大长度65，Oracle和postgresql最大长度38，精度0-最大长度内取值，当前系统默认给15最大
                }

                if (ProjectKeyConsts.RATE.equals(linzenkey)) {
                    fieldForm.setDataType("decimal");
                    fieldForm.setLength("38,1");
                }

                if (ProjectKeyConsts.LOCATION.equals(linzenkey)) {
                    fieldForm.setLength("500");
                }
                fieldForm.setField(vmodel);
                fieldForm.setComment(lable);
                tableList.add(fieldForm);
            }
        }
    }

    /**
     * 创建主外键字段
     *
     * @param tableList
     * @param isforeign
     */
    private void dbTableField(List<DbFieldModel> tableList, boolean isforeign, int primaryKey) {
        boolean uuid = primaryKey == 1;
        DbFieldModel tableKey = new DbFieldModel();
        tableKey.setNullSign(DbFieldConst.NOT_NULL);
        tableKey.setDataType(!uuid ? "int" : "varchar" );
        tableKey.setLength(TableFeildsEnum.FID.getLength() );
        tableKey.setIsPrimaryKey(true);
        tableKey.setField(relationField);
        tableKey.setIsAutoIncrement(!uuid);
        tableKey.setComment(TableFeildsEnum.FID.getComment() );
        tableList.add(tableKey);
        if (isforeign) {
            DbFieldModel tableForeignKey = new DbFieldModel();
            tableForeignKey.setNullSign(DbFieldConst.NULL);
            tableForeignKey.setDataType(TableFeildsEnum.FOREIGN.getDataType() );
            tableForeignKey.setLength(TableFeildsEnum.FOREIGN.getLength() );
            tableForeignKey.setIsPrimaryKey(false);
            tableForeignKey.setField(tableField);
            tableForeignKey.setComment(TableFeildsEnum.FOREIGN.getComment() );
            tableList.add(tableForeignKey);
        }
    }

    /**
     * 组装字段list
     *
     * @param tableModelList
     * @param dbtable
     * @param table
     * @param mastTable
     * @param isforeign
     */
    private void tableModel(List<TableModel> tableModelList, List<DbFieldModel> dbtable, String table, String mastTable, boolean isforeign) {
        TableModel tableModel = new TableModel();
        tableModel.setRelationField(isforeign ? relationField : "" );
        tableModel.setRelationTable(isforeign ? mastTable : "" );
        tableModel.setTable(table);
        tableModel.setComment(isforeign ? "子表" : "主表" );
        tableModel.setTableField(isforeign ? tableField : "" );
        tableModel.setTypeId(isforeign ? "0" : "1" );
        tableModel.setFields(JsonUtil.createJsonToList(dbtable, TableFields.class));
        tableModelList.add(tableModel);
    }

    /**
     * 组装创表字段
     *
     * @param linkId
     * @param tableName
     * @param tableFieldList
     * @param isforeign
     * @return
     */
    private DbTableFieldModel dbTable(String linkId, String tableName, List<DbFieldModel> tableFieldList, boolean isforeign, String fullName) {
        DbTableFieldModel dbTable = new DbTableFieldModel();
        dbTable.setDbLinkId(linkId);
        dbTable.setTable(tableName);
        dbTable.setDbFieldModelList(tableFieldList);
        String s = isforeign ? "子表" : "主表";
        if (fullName.contains("&" )) {//自动生成表备注的时候带&符号创建不成功问题
            fullName = fullName.replace("&" , " " );
        }
        dbTable.setComment(String.format("%s-%s" , fullName, s));
        return dbTable;
    }
}
