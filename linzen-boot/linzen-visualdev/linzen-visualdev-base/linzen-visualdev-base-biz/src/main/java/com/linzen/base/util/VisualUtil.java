package com.linzen.base.util;

import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.entity.ModuleDataAuthorizeSchemeEntity;
import com.linzen.base.entity.VisualdevEntity;
import com.linzen.base.model.ColumnDataModel;
import com.linzen.base.model.Template6.BtnData;
import com.linzen.base.model.Template6.ColumnListField;
import com.linzen.base.model.VisualDevPubModel;
import com.linzen.base.model.VisualWebTypeEnum;
import com.linzen.base.model.online.AuthFlieds;
import com.linzen.base.model.online.PerColModels;
import com.linzen.base.model.online.VisualMenuModel;
import com.linzen.base.util.common.DataControlUtils;
import com.linzen.constant.MsgCode;
import com.linzen.model.visualJson.*;
import com.linzen.model.visualJson.analysis.FormAllModel;
import com.linzen.model.visualJson.analysis.FormColumnModel;
import com.linzen.model.visualJson.analysis.FormEnum;
import com.linzen.model.visualJson.analysis.RecursionForm;
import com.linzen.model.visualJson.config.ConfigModel;
import com.linzen.permission.model.authorize.AuthorizeConditionEnum;
import com.linzen.permission.model.authorize.ConditionModel;
import com.linzen.util.JsonUtil;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.visiual.ProjectKeyConsts;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class VisualUtil {

    public static List<String> symbol = new ArrayList(){{add("@userId");add("@organizeId");}};

    public static final Long DEFAULT_SORT_CODE = -9527L;

    /**
     * @param entity
     * @return
     * @Description 删除F_, 且全转小写
     */
    public static VisualdevEntity delfKey(VisualdevEntity entity) {

        List<TableModel> list = JsonUtil.createJsonToList(entity.getVisualTables(), TableModel.class);

        for (TableModel tableModel : list) {
            List<TableFields> fields = tableModel.getFields();
            if (StringUtil.isNotEmpty(tableModel.getTableField()) && "f_".equalsIgnoreCase(tableModel.getTableField().substring(0, 2))) {
                tableModel.setTableField(tableModel.getTableField().substring(2).toLowerCase());
            }
            if (StringUtil.isNotEmpty(tableModel.getRelationField()) && "f_".equalsIgnoreCase(tableModel.getRelationField().substring(0, 2))) {
                tableModel.setRelationField(tableModel.getRelationField().substring(2).toLowerCase());
            }
            for (TableFields tableFields : fields) {
                String feild = tableFields.getField().toLowerCase();
                if ("f_".equals(feild.substring(0, 2))) {
                    tableFields.setField(feild.substring(2).toLowerCase());
                } else {
                    tableFields.setField(feild.toLowerCase());
                }
                tableModel.setFields(fields);
            }
        }
        entity.setVisualTables(JsonUtil.createObjectToString(list));

        //取出列表数据中的查询列表和数据列表
        Map<String, Object> columnDataMap = JsonUtil.stringToMap(entity.getColumnData());
        if (columnDataMap != null) {
            for (Map.Entry<String, Object> entry : columnDataMap.entrySet()) {
                if ("searchList".equals(entry.getKey())) {
                    List<FieLdsModel> fieLdsModelList = JsonUtil.createJsonToList(entry.getValue(), FieLdsModel.class);
                    for (FieLdsModel fieLdsModel : fieLdsModelList) {
                        String vModel = fieLdsModel.getVModel().toLowerCase();
                        String modelStr = fieLdsModel.getVModel();
                        //去除F_
                        if (!StringUtil.isEmpty(vModel) && "f_".equals(vModel.substring(0, 2))) {
                            fieLdsModel.setVModel(modelStr.substring(2).toLowerCase());
                        } else if (!StringUtil.isEmpty(vModel)) {
                            fieLdsModel.setVModel(modelStr.toLowerCase());
                        }

                    }
                    entry.setValue(fieLdsModelList);
                }
                if ("columnList".equals(entry.getKey())) {
                    List<ColumnListField> columnListFields = JsonUtil.createJsonToList(entry.getValue(), ColumnListField.class);
                    for (ColumnListField columnListField : columnListFields) {
                        String prop = columnListField.getProp().toLowerCase();
                        String modelStr = columnListField.getProp();
                        //去除F_
                        if (!StringUtil.isEmpty(prop) && "f_".equals(prop.substring(0, 2))) {
                            columnListField.setProp(modelStr.substring(2).toLowerCase());
                        } else if (!StringUtil.isEmpty(prop)) {
                            columnListField.setProp(modelStr.toLowerCase());
                        }
                    }
                    entry.setValue(columnListFields);
                }
            }
        }

        entity.setColumnData(JsonUtil.createObjectToString(columnDataMap));


        Map<String, Object> formData = JsonUtil.stringToMap(entity.getFormData());

        List<FieLdsModel> modelList = JsonUtil.createJsonToList(formData.get("fields").toString(), FieLdsModel.class);
        for (FieLdsModel fieLdsModel : modelList) {
            //去除F_
            if (!StringUtil.isEmpty(fieLdsModel.getVModel())) {
                if ("f_".equals(fieLdsModel.getVModel().substring(0, 2).toLowerCase())) {
                    String modelStr = fieLdsModel.getVModel();
                    fieLdsModel.setVModel(modelStr.substring(2).toLowerCase());
                } else {
                    String modelStr = fieLdsModel.getVModel();
                    fieLdsModel.setVModel(modelStr.toLowerCase());
                }
            }

            ConfigModel configModel = fieLdsModel.getConfig();
            //子表
            if ("table".equals(configModel.getProjectKey())) {
                List<FieLdsModel> childlist = JsonUtil.createJsonToList(configModel.getChildren(), FieLdsModel.class);
                for (FieLdsModel childmodel : childlist) {
                    //前台界面的属性去掉前2个
                    if (StringUtil.isNotEmpty(childmodel.getVModel())) {
                        if ("f_".equals(childmodel.getVModel().substring(0, 2).toLowerCase())) {
                            String vmodel = childmodel.getVModel().substring(2).toLowerCase();
                            childmodel.setVModel(vmodel);
                        } else {
                            String vmodel = childmodel.getVModel().toLowerCase();
                            childmodel.setVModel(vmodel);
                        }
                    }
                }
                fieLdsModel.getConfig().setChildren(childlist);
            }
        }
        formData.put("fields", JsonUtil.createObjectToString(modelList));
        entity.setFormData(JsonUtil.createObjectToString(formData));

        return entity;
    }

    public static VisualMenuModel getVisual(VisualdevEntity visualdevEntity, VisualDevPubModel visualDevPubModel) {
        VisualMenuModel visualMenuModel = new VisualMenuModel();
        visualMenuModel.setFullName(visualdevEntity.getFullName());
        visualMenuModel.setEncode(visualdevEntity.getEnCode());

        if (!VisualWebTypeEnum.DATA_VIEW.getType().equals(visualdevEntity.getWebType())) {//数据视图不解析formdata
            FormDataModel formDataModel = JsonUtil.createJsonToBean(visualdevEntity.getFormData(), FormDataModel.class);
            //递归封装表单数据
            List<FormAllModel> formAllModel = new ArrayList<>();
            RecursionForm recursionForm = new RecursionForm();
            List<TableModel> tableModels = JsonUtil.createJsonToList(visualdevEntity.getVisualTables(), TableModel.class);
            TableModel tableModel = tableModels.stream().filter(t -> t.getTypeId().equals("1")).findFirst().orElse(null);
            recursionForm.setTableModelList(tableModels);
            recursionForm.setList(JsonUtil.createJsonToList(formDataModel.getFields(), FieLdsModel.class));
            FormCloumnUtil.recursionForm(recursionForm, formAllModel);

            //主表数据
            List<FormAllModel> mast = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getProjectKey())).collect(Collectors.toList());
            //列表子表数据
            List<FormAllModel> mastTable = formAllModel.stream().filter(t -> FormEnum.mastTable.getMessage().equals(t.getProjectKey())).collect(Collectors.toList());
            //子表
            List<FormAllModel> childTable = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getProjectKey())).collect(Collectors.toList());

            String mainTableName = tableModel.getTable();
            List<AuthFlieds> allColumnDataList = new ArrayList<>();
            mast.stream().forEach(formModel -> {
                String vModel = formModel.getFormColumnModel().getFieLdsModel().getVModel();
                String tableName = formModel.getFormColumnModel().getFieLdsModel().getConfig().getTableName();
                if (StringUtil.isNotEmpty(vModel)) {
                    String label = formModel.getFormColumnModel().getFieLdsModel().getConfig().getLabel();
                    AuthFlieds authFlieds = AuthFlieds.builder().encode(vModel).fullName(label).status(false).rule(0).bindTableName(tableName)
                            .projectKey(formModel.getFormColumnModel().getFieLdsModel().getConfig().getProjectKey()).build();
                    allColumnDataList.add(authFlieds);
                }
            });
            mastTable.stream().forEach(formModel -> {
                String vModel = formModel.getFormMastTableModel().getMastTable().getFieLdsModel().getVModel();
                String tableName = formModel.getFormMastTableModel().getMastTable().getFieLdsModel().getConfig().getTableName();
                if (StringUtil.isNotEmpty(vModel)) {
                    String label = formModel.getFormMastTableModel().getMastTable().getFieLdsModel().getConfig().getLabel();
                    AuthFlieds authFlieds = AuthFlieds.builder().encode(vModel).fullName(label).status(false).rule(1).bindTableName(tableName)
                            .projectKey(formModel.getFormMastTableModel().getMastTable().getFieLdsModel().getConfig().getProjectKey()).build();
                    allColumnDataList.add(authFlieds);
                }
            });

            childTable.stream().forEach(formModel -> {
                String vModel = formModel.getChildList().getTableModel();
                String tableName = formModel.getChildList().getTableName();
                String label = formModel.getChildList().getLabel();
                if (StringUtil.isNotEmpty(vModel)) {
                    AuthFlieds authFlieds = AuthFlieds.builder().encode(vModel).fullName(label).status(false).rule(0).projectKey(formModel.getProjectKey()).bindTableName(tableName).build();
                    allColumnDataList.add(authFlieds);
                }
                List<FormColumnModel> childList = formModel.getChildList().getChildList();
                for (FormColumnModel columnModel : childList) {
                    String childlabel = columnModel.getFieLdsModel().getConfig().getLabel();
                    String childvModel = columnModel.getFieLdsModel().getVModel();
                    if (StringUtil.isNotEmpty(childvModel)) {
                        AuthFlieds authFlieds = AuthFlieds.builder().encode(vModel + "-" + childvModel).fullName(label + "-" + childlabel).status(false).bindTableName(tableName).rule(2).childTableKey(vModel)
                                .projectKey(columnModel.getFieLdsModel().getConfig().getProjectKey()).build();
                        allColumnDataList.add(authFlieds);
                    }
                }
            });

            //分配对应权限
            if (1 == visualDevPubModel.getPc()) {
                ColumnDataModel columnDataModel = JsonUtil.createJsonToBean(visualdevEntity.getColumnData(), ColumnDataModel.class);
                visualMenuModel.setPcPerCols(new PerColModels());
                if (Objects.nonNull(columnDataModel)) {
                    visualMenuModel.setPcPerCols(fillPermission(columnDataModel, allColumnDataList, true, mainTableName));
                }
            }

            if (1 == visualDevPubModel.getApp()) {
                ColumnDataModel appColumnDataModel = JsonUtil.createJsonToBean(visualdevEntity.getAppColumnData(), ColumnDataModel.class);
                visualMenuModel.setAppPerCols(new PerColModels());
                if (Objects.nonNull(appColumnDataModel)) {
                    visualMenuModel.setAppPerCols(fillPermission(appColumnDataModel, allColumnDataList, false, mainTableName));
                }
            }
        }

        visualMenuModel.setFullName(visualdevEntity.getFullName());
        visualMenuModel.setEncode(visualdevEntity.getEnCode());
        visualMenuModel.setId(visualdevEntity.getId());
        return visualMenuModel;
    }

    /**
     * 填充权限字段
     *
     * @param columnDataModel
     * @param allColumnDataList
     * @param isPC
     * @return
     */
    private static PerColModels fillPermission(ColumnDataModel columnDataModel, List<AuthFlieds> allColumnDataList, Boolean isPC, String mainTable) {
        PerColModels perColModel = new PerColModels();

        List<ColumnListField> columnListFields = JsonUtil.createJsonToList(columnDataModel.getDefaultColumnList(), ColumnListField.class);
        //副表正则
        String reg = "^[linzen_]\\S*_linzen\\S*";

        //按钮
        if (columnDataModel.getUseBtnPermission()) {
            perColModel.setButtonPermission(getAuthFiledList(isPC, columnDataModel));
        }
        //列表
        if (columnDataModel.getUseColumnPermission() && columnListFields != null) {
            List<AuthFlieds> colAuthFileds = columnListFields.stream().map(col -> {
                boolean matches = col.getProp().matches(reg);
                String childTableKey = "";
                int rule = 0;
                String tableName;
                if (col.getConfig() == null) {
                    tableName = mainTable;
                } else {
                    tableName = col.getConfig().getRelationTable() != null ? col.getConfig().getRelationTable() : StringUtil.isNotEmpty(col.getConfig().getTableName()) ? col.getConfig().getTableName() : mainTable;
                }
                if (matches) {
                    rule = 1;
                } else {
                    rule = col.getProp().contains("tableField") ? 2 : 0;
                    childTableKey = col.getProp().contains("tableField") ? col.getProp().substring(0, col.getProp().indexOf("-")) : null;
                }
                return AuthFlieds.builder().encode(col.getProp()).fullName(col.getLabel()).status(col.getChecked()).rule(rule).bindTableName(tableName).childTableKey(childTableKey).build();
            }).collect(Collectors.toList());
            perColModel.setListPermission(colAuthFileds);
        }

        //表单
        if (columnDataModel.getUseFormPermission()) {
            List<AuthFlieds> formAuthList = allColumnDataList.stream().map(colFlied -> {
                return AuthFlieds.builder().encode(colFlied.getEncode()).fullName(colFlied.getFullName()).status(true).rule(colFlied.getRule()).childTableKey(colFlied.getChildTableKey())
                        .bindTableName(colFlied.getBindTableName()).build();
            }).collect(Collectors.toList());
            perColModel.setFormPermission(formAuthList);
        }




        //数据权限
        if (columnDataModel.getUseDataPermission() && columnListFields != null) {
            List<AuthFlieds> dataAuthFileds = new LinkedList<>();
            List<ColumnListField> mainColFieldList = columnListFields.stream()
                    .filter(col -> !col.getProp().matches(reg) && !col.getProp().contains("tableField")).collect(Collectors.toList());
            //先取状态
//			mainColFieldList=	mainColFieldList.stream().filter(field->field.getChecked()).collect(Collectors.toList());
			//去重
			mainColFieldList = mainColFieldList.stream().filter(DataControlUtils.distinctByKey(data->data.getProjectKey())).collect(Collectors.toList());
			for (ColumnListField field : mainColFieldList){
				String tableName = StringUtils.isNotEmpty(field.getConfig().getTableName()) ? field.getConfig().getTableName() : mainTable;
				if (ProjectKeyConsts.CREATEUSER.equals(field.getProjectKey())){
					AuthFlieds authFlied1 = AuthFlieds.builder().encode(field.getProp()).fullName(field.getLabel()).status(field.getChecked()).projectKey(field.getProjectKey()).rule(0)
							.bindTableName(tableName).AuthCondition(AuthorizeConditionEnum.USER.getCondition()).id(RandomUtil.uuId()).build();
					AuthFlieds authFlied2 = AuthFlieds.builder().encode(field.getProp()).fullName(field.getLabel()).status(field.getChecked()).projectKey(field.getProjectKey()).rule(0)
							.bindTableName(tableName).AuthCondition(AuthorizeConditionEnum.USERANDUNDER.getCondition()).id(RandomUtil.uuId()).build();
					dataAuthFileds.add(authFlied1);
					dataAuthFileds.add(authFlied2);
				} else if (ProjectKeyConsts.CURRORGANIZE.equals(field.getProjectKey())){
					AuthFlieds authFlied1 = AuthFlieds.builder().encode(field.getProp()).fullName(field.getLabel()).status(field.getChecked()).bindTableName(tableName)
							.projectKey(field.getProjectKey()).rule(0).AuthCondition(AuthorizeConditionEnum.ORGANIZE.getCondition())
							.id(RandomUtil.uuId()).build();
					AuthFlieds authFlied2 = AuthFlieds.builder().encode(field.getProp()).fullName(field.getLabel()).status(field.getChecked()).bindTableName(tableName)
							.projectKey(field.getProjectKey()).rule(0).AuthCondition(AuthorizeConditionEnum.ORGANIZEANDUNDER.getCondition())
							.id(RandomUtil.uuId()).build();
					AuthFlieds authFlied3 = AuthFlieds.builder().encode(field.getProp()).fullName(field.getLabel()).status(field.getChecked()).bindTableName(tableName)
							.projectKey(field.getProjectKey()).rule(0).AuthCondition(AuthorizeConditionEnum.BRANCHMANAGEORG.getCondition())
							.id(RandomUtil.uuId()).build();
					AuthFlieds authFlied4 = AuthFlieds.builder().encode(field.getProp()).fullName(field.getLabel()).status(field.getChecked()).bindTableName(tableName)
							.projectKey(field.getProjectKey()).rule(0).AuthCondition(AuthorizeConditionEnum.BRANCHMANAGEORGANIZEUNDER.getCondition())
							.id(RandomUtil.uuId()).build();
					dataAuthFileds.add(authFlied1);
					dataAuthFileds.add(authFlied2);
					dataAuthFileds.add(authFlied3);
					dataAuthFileds.add(authFlied4);
				}
			}
			perColModel.setDataPermission(dataAuthFileds);
			//权限方案生成对象列表
			List<AuthFlieds> schemeAuthList = new ArrayList<>(dataAuthFileds);
			List<ModuleDataAuthorizeSchemeEntity> dataPermissionScheme=new ArrayList<>();
			for (AuthFlieds authFlieds : schemeAuthList) {
				ModuleDataAuthorizeSchemeEntity schemeEntity = new ModuleDataAuthorizeSchemeEntity();
				schemeEntity.setFullName(getSchemeNameByCondition(authFlieds.getAuthCondition()));
				schemeEntity.setEnCode(RandomUtil.uuId());
				String conditionText = "【{" + authFlieds.getFullName() + "} " + "{等于}" + " {" + authFlieds.getAuthCondition() + "}" + "】";
				List<ConditionModel> conditionModels = new ArrayList<>();
				ConditionModel conditionModel = new ConditionModel();
				conditionModel.setLogic("and");
				ConditionModel.ConditionItemModel conditionItemModel = conditionModel.new ConditionItemModel();
				conditionItemModel.setField(authFlieds.getEncode());
				conditionItemModel.setId(authFlieds.getId());
				conditionItemModel.setOp("Equal");
				conditionItemModel.setFieldRule(String.valueOf(authFlieds.getRule()));
				conditionItemModel.setValue(authFlieds.getAuthCondition());
				conditionItemModel.setConditionText(authFlieds.getAuthCondition());
				conditionItemModel.setType("Varchar" );
				conditionItemModel.setBindTable(authFlieds.getBindTableName());
				List<ConditionModel.ConditionItemModel> groups = new ArrayList<>();
				groups.add(conditionItemModel);
				conditionModel.setGroups(groups);
				conditionModels.add(conditionModel);
				schemeEntity.setConditionJson(JsonUtil.createObjectToString(conditionModels));
				schemeEntity.setConditionText(conditionText);
				schemeEntity.setEnabledMark(1);
				schemeEntity.setDescription(authFlieds.getRule()+"_"+authFlieds.getEncode()+"_" + "Equal");
				schemeEntity.setSortCode(DEFAULT_SORT_CODE);
				dataPermissionScheme.add(schemeEntity);
			}
			//数据权限方案
			perColModel.setDataPermissionScheme(dataPermissionScheme);
		}
		return perColModel;
	}

    /**
     * 获取系统按钮集合
     *
     * @param isPC 是否pc端
     * @return
     */
    private static List<AuthFlieds> getAuthFiledList(Boolean isPC, ColumnDataModel columnDataModel) {
        List<AuthFlieds> btnList = new ArrayList<>(6);
        String btnValues = AuthPerConfirm(columnDataModel);
        btnList.add(AuthFlieds.builder().fullName("新增").encode("btn_add").status(false).build());
        btnList.add(AuthFlieds.builder().fullName("编辑").encode("btn_edit").status(false).build());
        btnList.add(AuthFlieds.builder().fullName("删除").encode("btn_remove").status(false).build());
        btnList.add(AuthFlieds.builder().fullName("详情").encode("btn_detail").status(false).build());
        //pc端 按钮
        if (isPC) {
            btnList.add(AuthFlieds.builder().fullName("导入").encode("btn_upload").status(false).build());
            btnList.add(AuthFlieds.builder().fullName("导出").encode("btn_download").status(false).build());
            btnList.add(AuthFlieds.builder().fullName("批量删除").encode("btn_batchRemove").status(false).build());
            btnList.add(AuthFlieds.builder().fullName("批量打印").encode("btn_batchPrint").status(false).build());
        }
        btnList.stream().filter(btn -> btnValues.contains(btn.getEncode().replace("btn_", ""))).forEach(btn -> btn.setStatus(true));
        //自定义按钮区
        List<BtnData> CustomBtnList = JsonUtil.createJsonToList(columnDataModel.getCustomBtnsList(), BtnData.class);
        if (Objects.nonNull(CustomBtnList)) {
            List<AuthFlieds> CustomBtnAuth = CustomBtnList.stream().map(cus -> AuthFlieds.builder().fullName(cus.getLabel()).encode(cus.getValue()).status(true).build()).collect(Collectors.toList());
            btnList.addAll(CustomBtnAuth);
        }
        return btnList;
    }

    private static String AuthPerConfirm(ColumnDataModel columnDataModel) {
        List<BtnData> btnDataList = new ArrayList<>();
        List<BtnData> BtnList = JsonUtil.createJsonToList(columnDataModel.getBtnsList(), BtnData.class);
        List<BtnData> ColumnBtnList = JsonUtil.createJsonToList(columnDataModel.getColumnBtnsList(), BtnData.class);

        btnDataList.addAll(BtnList);
        btnDataList.addAll(ColumnBtnList);

        String btnValue = btnDataList.stream().map(btn -> btn.getValue()).collect(Collectors.joining(","));
        return btnValue;
    }

	/**
	 * 检验是否可发布
	 * @param entity
	 * @param Action
	 * @return
	 */
	public static String checkPublishVisualModel(VisualdevEntity entity,String Action){
		String errorMsg = null;
        //数据视图没有formdata
        if (!VisualWebTypeEnum.DATA_VIEW.getType().equals(entity.getWebType())) {
            FormDataModel formDataModel = JsonUtil.createJsonToBean(entity.getFormData(), FormDataModel.class);
            errorMsg = formDataModel == null ? MsgCode.VS401.get() + Action + "!" : null;
        }
        if(StringUtil.isNotEmpty(errorMsg)){
            return errorMsg;
        }
        if (VisualWebTypeEnum.FORM_LIST.getType().equals(entity.getWebType())
                || VisualWebTypeEnum.DATA_VIEW.getType().equals(entity.getWebType())) {
            ColumnDataModel columnDataModel = JsonUtil.createJsonToBean(entity.getColumnData(), ColumnDataModel.class);
            errorMsg = columnDataModel == null ? MsgCode.VS402.get() + Action + "!" : null;
        }
		return errorMsg;
	}

	private static String getSchemeNameByCondition(String condition) {
		String name = "default";
		if (AuthorizeConditionEnum.USER.getCondition().equals(condition)) {
			name = "当前用户";
		} else if (AuthorizeConditionEnum.USERANDUNDER.getCondition().equals(condition)) {
			name = "当前用户及下属";
		} else if (AuthorizeConditionEnum.ORGANIZE.getCondition().equals(condition)) {
			name = "当前组织";
		} else if (AuthorizeConditionEnum.ORGANIZEANDUNDER.getCondition().equals(condition)) {
			name = "当前组织及子组织";
		} else if (AuthorizeConditionEnum.BRANCHMANAGEORG.getCondition().equals(condition)) {
			name = "当前分管组织";
		} else if (AuthorizeConditionEnum.BRANCHMANAGEORGANIZEUNDER.getCondition().equals(condition)) {
			name = "当前分管组织及子组织";
		}
		return name;
	}
}
