package com.linzen.model.visualJson;

import cn.hutool.core.bean.BeanUtil;
import com.linzen.model.visualJson.config.ConfigModel;
import com.linzen.model.visualJson.config.HeaderModel;
import com.linzen.model.visualJson.analysis.*;
import com.linzen.util.JsonUtil;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.visiual.ProjectKeyConsts;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 在线工作流开发
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */

public class FormCloumnUtil {

    /**
     * 引擎递归
     **/
    public static void recursionForm(RecursionForm recursionForm, List<FormAllModel> formAllModel) {
        List<TableModel> tableModelList = recursionForm.getTableModelList();
        List<FieLdsModel> list = recursionForm.getList();
        for (FieLdsModel item : list) {
            FieLdsModel fieLdsModel = BeanUtil.copyProperties(item, FieLdsModel.class);
            FormAllModel start = new FormAllModel();
            FormAllModel end = new FormAllModel();
            ConfigModel config = fieLdsModel.getConfig();
            String tipLable = StringUtil.isNotEmpty(config.getTipLabel())?config.getTipLabel().trim().replaceAll("\r", "").replaceAll("\n", " "):config.getTipLabel();
            config.setTipLabel(tipLable);
            String visibility = config.getVisibility();
            multipleChoices(config);
            String projectKey = config.getProjectKey();
            List<FieLdsModel> childrenList = config.getChildren();
            List<String> keyList = new ArrayList() {{
                add(FormEnum.collapseItem.getMessage());
                add(FormEnum.collapseItem.getMessage());
                add(FormEnum.row.getMessage());
                add(FormEnum.card.getMessage());
                add(FormEnum.tab.getMessage());
                add(FormEnum.collapse.getMessage());
                add(FormEnum.tableGrid.getMessage());
                add(FormEnum.tableGridTr.getMessage());
                add(FormEnum.tableGridTd.getMessage());
            }};
            boolean isEndProjectKey = StringUtil.isEmpty(projectKey) || FormEnum.collapseItem.getMessage().equals(projectKey) || FormEnum.tabItem.getMessage().equals(projectKey);
            if (keyList.contains(projectKey) || isEndProjectKey) {
                String key = isEndProjectKey ? FormEnum.collapseItem.getMessage().equals(projectKey) ? FormEnum.collapse.getMessage() : FormEnum.tab.getMessage() : projectKey;
                //布局属性
                FormModel formModel = JsonUtil.createJsonToBean(fieLdsModel, FormModel.class);
                formModel.setSpan(config.getSpan());
                int activeId = 0;
                if (StringUtil.isNotEmpty(config.getActive())) {
                    List<FieLdsModel> children = config.getChildren();
                    for (int i = 0; i < children.size(); i++) {
                        String name = children.get(i).getName();
                        if (config.getActive().equals(name)) {
                            activeId = i;
                        }
                    }
                }
                formModel.setActiveIndex(activeId + "");
                formModel.setActive(config.getActive());
                formModel.setChildNum(config.getChildNum());
                formModel.setModel(config.getModel());
                formModel.setVisibility(config.getVisibility());
                formModel.setMerged(config.getMerged());
                formModel.setColspan(config.getColspan());
                formModel.setRowspan(config.getRowspan());
                formModel.setRowType(config.getRowType());
                formModel.setBorderColor(config.getBorderColor());
                formModel.setBorderType(config.getBorderType());
                formModel.setBorderWidth(config.getBorderWidth());
                String outermost = !isEndProjectKey ? "0" : "1";
                if (FormEnum.tab.getMessage().equals(key) || FormEnum.collapse.getMessage().equals(key)) {
                    if (!isEndProjectKey) {
                        String chidModel = "active" + RandomUtil.enUuId();
                        formModel.setModel(chidModel);
                        for (int i = 0; i < childrenList.size(); i++) {
                            FieLdsModel childModel = childrenList.get(i);
                            ConfigModel childConfig = childModel.getConfig();
                            childConfig.setVisibility(visibility);
                            childConfig.setModel(chidModel);
                            childConfig.setChildNum(i);
                            multipleChoices(childConfig);
                            childModel.setConfig(childConfig);
                        }
                        formModel.setChildren(childrenList);
                    }
                    formModel.setOutermost(outermost);
                }
                start.setProjectKey(key);
                start.setFormModel(formModel);
                formAllModel.add(start);
                RecursionForm recursion = new RecursionForm(childrenList, tableModelList);
                recursionForm(recursion, formAllModel);
                end.setIsEnd("1");
                end.setProjectKey(key);
                //折叠、标签的判断里层还是外层
                FormModel endFormModel = new FormModel();
                endFormModel.setOutermost(outermost);
                endFormModel.setConfig(config);
                end.setFormModel(endFormModel);
                formAllModel.add(end);
            } else if (FormEnum.table.getMessage().equals(projectKey)) {
                tableModel(fieLdsModel, formAllModel);
            } else if (FormEnum.isModel(projectKey)) {
                FormModel formModel = JsonUtil.createJsonToBean(fieLdsModel, FormModel.class);
                formModel.setVisibility(fieLdsModel.getConfig().getVisibility());
                start.setProjectKey(projectKey);
                start.setFormModel(formModel);
                formAllModel.add(start);
            } else {
                model(fieLdsModel, formAllModel, tableModelList);
            }
        }
        for (FormAllModel formModel : formAllModel) {
            if (FormEnum.mast.getMessage().equals(formModel.getProjectKey())) {
                setRelationFieldAttr(formAllModel, formModel.getFormColumnModel().getFieLdsModel());
            } else if (FormEnum.mastTable.getMessage().equals(formModel.getProjectKey())) {
                setRelationFieldAttr(formAllModel, formModel.getFormMastTableModel().getMastTable().getFieLdsModel());
            }
        }
    }

    /**
     * 多端选择
     *
     * @param configModel
     * @return
     */
    private static ConfigModel multipleChoices(ConfigModel configModel) {
        String visibility = configModel.getVisibility();
        if (Objects.nonNull(visibility)) {
            configModel.setApp(visibility.contains("app"));
            configModel.setPc(visibility.contains("pc"));
        }
        return configModel;
    }

    /**
     * 主表属性添加
     **/
    private static void model(FieLdsModel fieLdsModel, List<FormAllModel> formAllModel, List<TableModel> tableModelList) {
        FormColumnModel mastModel = formModel(fieLdsModel);
        FormAllModel formModel = new FormAllModel();
        formModel.setProjectKey(FormEnum.mast.getMessage());
        formModel.setFormColumnModel(mastModel);
        if (tableModelList.size() > 0) {
            TableModel tableModel = tableModelList.stream().filter(t -> t.getTable().equals(fieLdsModel.getConfig().getTableName())).findFirst().orElse(null);
            if (tableModel == null) {
                Optional<TableModel> first = tableModelList.stream().filter(t -> "1".equals(t.getTypeId())).findFirst();
                if (first.isPresent()) {
                    tableModel = first.get();
                } else {
                    throw new RuntimeException("未找到主表信息");
                }
            }
            String type = tableModel.getTypeId();
            if ("1".equals(type)) {
                mastModel.getFieLdsModel().getConfig().setTableName(tableModel.getTable());
                formModel.setFormColumnModel(mastModel);
                formAllModel.add(formModel);
            } else {
                mastTable(tableModel, fieLdsModel, formAllModel);
            }
        } else {
            formAllModel.add(formModel);
        }
    }

    /**
     * 主表的属性是子表字段
     */
    private static void mastTable(TableModel tableModel, FieLdsModel fieLdsModel, List<FormAllModel> formAllModel) {
        FormMastTableModel childModel = new FormMastTableModel();
        String vModel = fieLdsModel.getVModel();
        List<TableFields> tableFieldsList = tableModel.getFields();
        String mastKey = "linzen_" + tableModel.getTable() + "_linzen_";
        TableFields tableFields = tableFieldsList.stream().filter(t -> StringUtil.isNotEmpty(vModel) && vModel.equals(mastKey + t.getField())).findFirst().orElse(null);
        FormAllModel formModel = new FormAllModel();
        formModel.setProjectKey(FormEnum.mastTable.getMessage());
        if (tableFields != null) {
            childModel.setTable(tableModel.getTable());
            formModel.setFormMastTableModel(childModel);
            childModel.setField(tableFields.getField());
            childModel.setVModel(vModel);
        }
        FormColumnModel mastTable = formModel(fieLdsModel);
        childModel.setMastTable(mastTable);
        formAllModel.add(formModel);
    }

    /**
     * 子表表属性添加
     **/
    private static void tableModel(FieLdsModel model, List<FormAllModel> formAllModel) {
        List<FormColumnModel> childList = new ArrayList<>();
        ConfigModel config = model.getConfig();
        List<FieLdsModel> childModelList = config.getChildren();
        List<FormColumnModel> childFieldList = new ArrayList<>();
        List<HeaderModel> complexHeaderList = config.getComplexHeaderList();
        String table = model.getVModel();
        List<String> summaryField = StringUtil.isNotEmpty(model.getSummaryField()) ? JsonUtil.createJsonToList(model.getSummaryField(), String.class) : new ArrayList<>();
        Map<String, String> summaryName = new HashMap<>();
        for(int i=0;i<childModelList.size();i++){
            FieLdsModel childmodel =childModelList.get(i);
            String vModel = childmodel.getVModel();
            FormColumnModel childModel = formModel(childmodel);
            boolean isSummary = summaryField.contains(vModel);
            if (isSummary) {
                summaryName.put(vModel, childmodel.getConfig().getLabel());
            }
            relationModel(childModelList, childmodel);
            boolean add = true;
            String tableFixed = childModel.getFieLdsModel().getConfig().getTableFixed();
            //子表复杂表头不能包含冻结字段
            if(tableFixed == null || "none".equals(tableFixed)){
                for (HeaderModel headerModelList : complexHeaderList) {
                    List<FormColumnModel> headerFieldList = headerModelList.getChildList();
                    if (headerModelList.getChildColumns().contains(vModel)) {
                        headerFieldList.add(childModel);
                        add = false;
                    }
                    if (headerFieldList.size() > 0) {
                        headerModelList.setChildList(headerFieldList);
                    }
                }
            }
            if(add) {
                childFieldList.add(childModel);
            }
            childList.add(childModel);
        }

        List<HeaderModel> headerList = new ArrayList<>();
        for (HeaderModel headerModel : complexHeaderList) {
            List<FormColumnModel> headerChildListAll = headerModel.getChildList();
            List<FormColumnModel> headerChildList = new ArrayList<>();
            for (FormColumnModel columnModel : headerChildListAll) {
                ConfigModel headerConfig = columnModel.getFieLdsModel().getConfig();
                Boolean noShow = headerConfig.getNoShow();
                if (!noShow) {
                    headerChildList.add(columnModel);
                }
            }
            if (headerChildList.size() > 0) {
                headerList.add(headerModel);
            }
        }
        multipleChoices(config);
        FormColumnTableModel tableModel = JsonUtil.createJsonToBean(config, FormColumnTableModel.class);
        String tipLable = StringUtil.isNotEmpty(tableModel.getTipLabel()) ? tableModel.getTipLabel().trim().replaceAll("\r", "").replaceAll("\n", " ") : tableModel.getTipLabel();
        tableModel.setTipLabel(tipLable);
        tableModel.setTableModel(table);
        tableModel.setChildList(childList);
        tableModel.setComplexHeaderList(headerList);
        tableModel.setChildFieldList(childFieldList);
        tableModel.setShowSummary(model.getShowSummary());
        tableModel.setSummaryField(JsonUtil.createObjectToString(summaryField));
        tableModel.setSummaryFieldName(JsonUtil.createObjectToString(summaryName));
        tableModel.setThousands(model.isThousands());
        tableModel.setVisibility(config.getVisibility());
        tableModel.setColumnBtnsList(model.getColumnBtnsList());
        tableModel.setFooterBtnsList(model.getFooterBtnsList());
        FormAllModel formModel = new FormAllModel();
        formModel.setProjectKey(FormEnum.table.getMessage());
        formModel.setChildList(tableModel);
        formAllModel.add(formModel);
    }

    private static void relationModel(List<FieLdsModel> childModelList, FieLdsModel childmodel) {
        ConfigModel config = childmodel.getConfig();
        String projectKey = config.getProjectKey();
        String startRelationField = config.getStartRelationField();
        String endRelationField = config.getEndRelationField();
        String childRelationField = childmodel.getRelationField();
        if (FormEnum.relationFormAttr.getMessage().equals(projectKey) || FormEnum.popupAttr.getMessage().equals(projectKey)) {
            String relationField = childmodel.getRelationField().split("_linzenTable_")[0];
            FieLdsModel child = childModelList.stream().filter(t -> relationField.equals(t.getVModel())).findFirst().orElse(null);
            if (child != null) {
                childmodel.setInterfaceId(child.getInterfaceId());
                childmodel.setModelId(child.getModelId());
                childmodel.setPropsValue(child.getPropsValue());
                if (StringUtil.isEmpty(childmodel.getRelationFieldSource())) {
                    childmodel.setRelationFieldSource(childmodel.getRelationField());
                }
                if(Objects.equals(0,childmodel.getIsStorage())) {
                    childmodel.getConfig().setNoShow(child.getConfig().getNoShow());
                }
                childmodel.setRelationField(relationField);
            }
        }
        if (ProjectKeyConsts.USERSELECT.equals(projectKey) && StringUtil.isNotEmpty(childRelationField)) {
            String[] relationField = childRelationField.split("-");
            if (relationField.length > 1) {
                childmodel.setRelationField(relationField[1]);
            }
            childmodel.setRelationChild(relationField.length > 1);
        }
        if (ProjectKeyConsts.DATE.equals(projectKey) || ProjectKeyConsts.TIME.equals(projectKey)) {
            if (StringUtil.isNotEmpty(startRelationField)) {
                String[] relationField = startRelationField.split("-");
                if (relationField.length > 1) {
                    childmodel.getConfig().setStartRelationField(relationField[1]);
                }
                childmodel.getConfig().setStartChild(relationField.length > 1);
            }
            if (StringUtil.isNotEmpty(endRelationField)) {
                String[] relationField = endRelationField.split("-");
                if (relationField.length > 1) {
                    childmodel.getConfig().setEndRelationField(relationField[1]);
                }
                childmodel.getConfig().setEndRChild(relationField.length > 1);
            }
        }
    }

    /**
     * 属性赋值
     **/
    private static FormColumnModel formModel(FieLdsModel model) {
        ConfigModel configModel = model.getConfig();
        multipleChoices(configModel);
        if (configModel.getDefaultValue() instanceof String) {
            configModel.setValueType("String");
        }
        if (configModel.getDefaultValue() == null) {
            configModel.setValueType("undefined");
        }
        FormColumnModel formColumnModel = new FormColumnModel();
        formColumnModel.setFieLdsModel(model);
        return formColumnModel;
    }

    /**
     * 判断重复子表
     *
     * @return
     */
    public static boolean repetition(RecursionForm recursionForm, List<FormAllModel> formAllModel) {
        boolean flag = false;
        List<TableModel> tableModelList = recursionForm.getTableModelList();
        recursionForm(recursionForm, formAllModel);
        if (tableModelList.size() > 0) {
            List<FormAllModel> tables = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getProjectKey())).collect(Collectors.toList());
            List<FormAllModel> mastTable = formAllModel.stream().filter(t -> FormEnum.mastTable.getMessage().equals(t.getProjectKey())).collect(Collectors.toList());
            List<String> tableList = tables.stream().map(t -> t.getChildList().getTableName()).collect(Collectors.toList());
            List<String> mastTableList = mastTable.stream().map(t -> t.getFormMastTableModel().getTable()).collect(Collectors.toList());
            flag = tableList.stream().filter(item -> mastTableList.contains(item)).count() > 0;
        }
        return flag;
    }

    /**
     * 获取关联表单字段信息
     *
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * @date 2023-04-01
     */
    private static void setRelationFieldAttr(List<FormAllModel> formAllModel, FieLdsModel formModel) {
        String projectKey = formModel.getConfig().getProjectKey();
        if (FormEnum.relationFormAttr.getMessage().equals(projectKey) || FormEnum.popupAttr.getMessage().equals(projectKey)) {
            List<FieLdsModel> fieLdsModelList = new ArrayList<>();
            fieLdsModelList.addAll(formAllModel.stream().filter(t -> t.getFormColumnModel() != null).map(t -> t.getFormColumnModel().getFieLdsModel()).collect(Collectors.toList()));
            fieLdsModelList.addAll(formAllModel.stream().filter(t -> t.getFormMastTableModel() != null).map(t -> t.getFormMastTableModel().getMastTable().getFieLdsModel()).collect(Collectors.toList()));
            relationModel(fieLdsModelList, formModel);
        }
    }
}
