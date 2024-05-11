package com.linzen.base.util.common;

import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.entity.VisualdevEntity;
import com.linzen.base.model.DownloadCodeForm;
import com.linzen.emnus.ModuleTypeEnum;
import com.linzen.entity.FlowFormEntity;
import com.linzen.model.form.DraftJsonModel;
import com.linzen.model.form.FormDraftJsonModel;
import com.linzen.model.visualJson.FieLdsModel;
import com.linzen.model.visualJson.FormCloumnUtil;
import com.linzen.model.visualJson.FormDataModel;
import com.linzen.model.visualJson.TableModel;
import com.linzen.model.visualJson.analysis.*;
import com.linzen.model.visualJson.config.ConfigModel;
import com.linzen.util.JsonUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.visiual.ProjectKeyConsts;
import lombok.Cleanup;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能流程公共工具
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class FunctionFormPublicUtil {

    public static FlowFormEntity exportFlowFormJson(VisualdevEntity entity, DownloadCodeForm downloadCodeForm) {
        FlowFormEntity flowFormEntity = new FlowFormEntity();
        flowFormEntity.setId(entity.getId());
        flowFormEntity.setEnCode(entity.getEnCode());
        flowFormEntity.setFullName(entity.getFullName());
        flowFormEntity.setFlowType(1);
        flowFormEntity.setFormType(1);
        flowFormEntity.setCategory(entity.getCategory());
        flowFormEntity.setDescription(entity.getDescription());
        flowFormEntity.setSortCode(entity.getSortCode());
        flowFormEntity.setCreatorTime(entity.getCreatorTime());
        flowFormEntity.setCreatorUserId(entity.getCreatorUserId());
        flowFormEntity.setTableJson(entity.getVisualTables());
        flowFormEntity.setDbLinkId(entity.getDbLinkId());
        //填写默认url
        String appUrl = "/pages/apply/" + downloadCodeForm.getClassName();
        flowFormEntity.setAppUrlAddress(appUrl);
        String formFileName = downloadCodeForm.isVue3() ? "/Form.vue" : "/form.vue";
        String webUrl = "extend/" + downloadCodeForm.getClassName().toLowerCase() + formFileName;
        flowFormEntity.setUrlAddress(webUrl);
        String downloadClassName = downloadCodeForm.getClassName().substring(0, 1).toUpperCase() + downloadCodeForm.getClassName().substring(1);
        String interfaceUrl = "/api/" + downloadCodeForm.getModule() + "/" + downloadClassName;
        flowFormEntity.setInterfaceUrl(interfaceUrl);

        if (Objects.equals(entity.getType(), 3)) {
            flowFormEntity.setFlowType(0);
            webUrl = "workFlow/workFlowForm/" + downloadCodeForm.getClassName().toLowerCase() + "/index.vue";
            flowFormEntity.setUrlAddress(webUrl);
            interfaceUrl = "/api/workflow/" + downloadCodeForm.getModule() + "/" + downloadClassName;
            flowFormEntity.setInterfaceUrl(interfaceUrl);
        }

        List<FormAllModel> formAllModel = new ArrayList<>();
        forDataMode(entity, formAllModel);
        List<FormAllModel> mastList = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getProjectKey())).collect(Collectors.toList());
        List<DraftJsonModel> tempJson = new ArrayList<>();
        for (FormAllModel mastModel : mastList) {
            FieLdsModel fieLdsModel = mastModel.getFormColumnModel().getFieLdsModel();
            String model = fieLdsModel.getVModel();
            ConfigModel config = fieLdsModel.getConfig();
            if (StringUtil.isNotEmpty(model)) {
                DraftJsonModel engineModel = new DraftJsonModel();
                String label = config.getLabel();
                engineModel.setFiledId(model);
                engineModel.setFiledName(label);
                engineModel.setRequired(config.isRequired());
                engineModel.setProjectKey(config.getProjectKey());
                engineModel.setMultiple(fieLdsModel.getMultiple());
                tempJson.add(engineModel);
            }
        }
        List<FormAllModel> tableList = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getProjectKey())).collect(Collectors.toList());
        List<TableModel> tableModelList = JsonUtil.createJsonToList(entity.getVisualTables(), TableModel.class);
        Map<String, String> tableListAll = tableNameRename(downloadCodeForm, tableModelList);
        for (FormAllModel model : tableList) {
            String table = model.getChildList().getTableName();
            String name = DataControlUtils.initialLowercase(tableListAll.get(table));
            FormColumnTableModel childList = model.getChildList();
            String label = childList.getLabel();
            boolean required = childList.isRequired();
            DraftJsonModel engineModel = new DraftJsonModel();
            engineModel.setFiledId(name + "List");
            engineModel.setFiledName(label);
            engineModel.setRequired(required);
            tempJson.add(engineModel);
            for (FormColumnModel columnModel : model.getChildList().getChildList()) {
                String vModel = columnModel.getFieLdsModel().getVModel();
                String childLable = columnModel.getFieLdsModel().getConfig().getLabel();
                ConfigModel config = columnModel.getFieLdsModel().getConfig();
                if (StringUtil.isNotEmpty(vModel)) {
                    DraftJsonModel childModel = new DraftJsonModel();
                    childModel.setFiledId(name + "List-" + vModel);
                    childModel.setFiledName(label + "-" + childLable);
                    childModel.setRequired(config.isRequired());
                    childModel.setProjectKey(config.getProjectKey());
                    childModel.setMultiple(columnModel.getFieLdsModel().getMultiple());
                    tempJson.add(childModel);
                }
            }
        }
        List<FormAllModel> mastTableList = formAllModel.stream().filter(t -> FormEnum.mastTable.getMessage().equals(t.getProjectKey())).collect(Collectors.toList());
        for (FormAllModel mastTableModel : mastTableList) {
            FormMastTableModel formMastTableModel = mastTableModel.getFormMastTableModel();
            FieLdsModel fieLdsModel = formMastTableModel.getMastTable().getFieLdsModel();
            String model = formMastTableModel.getVModel();
            ConfigModel config = fieLdsModel.getConfig();
            if (StringUtil.isNotEmpty(model)) {
                DraftJsonModel engineModel = new DraftJsonModel();
                String label = config.getLabel();
                engineModel.setFiledId(model);
                engineModel.setFiledName(label);
                engineModel.setRequired(config.isRequired());
                engineModel.setProjectKey(config.getProjectKey());
                engineModel.setMultiple(fieLdsModel.getMultiple());
                tempJson.add(engineModel);
            }
        }
        FormDraftJsonModel draftJsonModel = new FormDraftJsonModel();
        String Tem = JsonUtil.createObjectToString(tempJson);
        flowFormEntity.setPropertyJson(Tem);
        tableJson(tableList, flowFormEntity, tableListAll);
        draftJsonModel.setDraftJson(Tem);
        draftJsonModel.setTableJson(entity.getVisualTables());
        flowFormEntity.setDraftJson(JsonUtil.createObjectToString(draftJsonModel));
        return flowFormEntity;
    }


    /**
     * 创建文件
     *
     * @param data
     * @param path
     */
    public static void createFile(String data, String path) {
        try {
            File file = new File(path + File.separator + "flow." + ModuleTypeEnum.FLOW_FLOWENGINE.getTableName());
            boolean isCreateNewFile = file.createNewFile();
            if (isCreateNewFile) {
                @Cleanup Writer out = new FileWriter(file);
                out.write(data);
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 封装数据
     *
     * @param entity
     * @param formAllModel
     */
    private static void forDataMode(VisualdevEntity entity, List<FormAllModel> formAllModel) {
        //formTempJson
        FormDataModel formData = JsonUtil.createJsonToBean(entity.getFormData(), FormDataModel.class);
        List<FieLdsModel> list = JsonUtil.createJsonToList(formData.getFields(), FieLdsModel.class);
        List<TableModel> tableModelList = JsonUtil.createJsonToList(entity.getVisualTables(), TableModel.class);
        RecursionForm recursionForm = new RecursionForm(list, tableModelList);
        FormCloumnUtil.recursionForm(recursionForm, formAllModel);
    }

    public static Map<String, String> tableNameRename(DownloadCodeForm downloadCodeForm, List<TableModel> tableModelList) {
        Map<String, String> tableNameMap = new HashMap<>(16);
        int i = 0;
        for (TableModel tableModel : tableModelList) {
            if ("0".equals(tableModel.getTypeId())) {
                String[] subClassName = downloadCodeForm.getSubClassName().split(",");
                tableNameMap.put(tableModel.getTable(), subClassName[i]);
                i++;
            } else {
                tableNameMap.put(tableModel.getTable(), downloadCodeForm.getClassName());
            }
        }
        return tableNameMap;
    }

    private static void tableJson(List<FormAllModel> tableList, VisualdevEntity entity, Map<String, String> tableListAll) {
//        String json = entity.getFlowTemplateJson();
//        for (FormAllModel model : tableList) {
//            String table = model.getChildList().getTableName();
//            String tableModel = model.getChildList().getTableModel();
//            String name = tableListAll.get(table);
//            json = json.replaceAll(tableModel, name + "List");
//        }
//        entity.setFlowTemplateJson(json);
//    }
    }

    private static void tableJson(List<FormAllModel> tableList, FlowFormEntity entity, Map<String, String> tableListAll) {
        String json = entity.getPropertyJson();
        for (FormAllModel model : tableList) {
            String table = model.getChildList().getTableName();
            String tableModel = model.getChildList().getTableModel();
            String name = tableListAll.get(table);
            json = json.replaceAll(tableModel, name + "List");
        }
        entity.setPropertyJson(json);
    }

    public static void getTableModels(List<FieLdsModel> fieLdsModelList, List<FieLdsModel> tableModelFields) {
        for (FieLdsModel fieLdsModel : fieLdsModelList) {
            String projectKey = fieLdsModel.getConfig().getProjectKey();
            if (ProjectKeyConsts.CHILD_TABLE.equals(projectKey)) {
                tableModelFields.add(fieLdsModel);
            } else {
                List<FieLdsModel> children = fieLdsModel.getConfig().getChildren();
                if (children != null) {
                    getTableModels(children, tableModelFields);
                } else {
                    continue;
                }
            }
        }

    }

}
