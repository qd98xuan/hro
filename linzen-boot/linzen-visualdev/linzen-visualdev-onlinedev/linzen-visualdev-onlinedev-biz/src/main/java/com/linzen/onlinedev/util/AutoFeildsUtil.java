package com.linzen.onlinedev.util;

import cn.hutool.core.util.ObjectUtil;
import com.linzen.model.visualJson.FieLdsModel;
import com.linzen.permission.entity.SysOrganizeEntity;
import com.linzen.permission.entity.SysPositionEntity;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.OrganizeService;
import com.linzen.permission.service.PositionService;
import com.linzen.permission.service.UserService;
import com.linzen.util.JsonUtil;
import com.linzen.util.JsonUtilEx;
import com.linzen.util.StringUtil;
import com.linzen.util.context.SpringContext;
import com.linzen.util.visiual.ProjectKeyConsts;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 处理自动生成字段
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class AutoFeildsUtil {
    private static OrganizeService organizeApi;
    private static UserService userService;
    private static PositionService positionApi;


    //初始化
    public static void init() {
        userService = SpringContext.getBean(UserService.class);
        organizeApi = SpringContext.getBean(OrganizeService.class);
        positionApi=SpringContext.getBean(PositionService.class);
    }

    /**
     * 列表系统自动生成字段转换
     *
     * @return String
     */
    public static String autoFeilds(List<FieLdsModel> fieLdsModelList, String data) {
        for (FieLdsModel fieLdsModel : fieLdsModelList) {
            Map<String, Object> dataMap = JsonUtil.stringToMap(data);
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                    if (Objects.isNull(entry.getValue())){
                        continue;
                    }
                if (fieLdsModel.getVModel().equals(entry.getKey())) {
                    String projectKeyType = fieLdsModel.getConfig().getProjectKey();
                    switch (projectKeyType) {
                        case ProjectKeyConsts.CURRORGANIZE:
                        case ProjectKeyConsts.CURRDEPT:
                            if("all".equals(fieLdsModel.getShowLevel())){
                                List<SysOrganizeEntity> organizeList = new ArrayList<>();
                                organizeApi.getOrganizeId(String.valueOf(entry.getValue()),organizeList);
                                Collections.reverse(organizeList);
                                String value = organizeList.stream().map(SysOrganizeEntity::getFullName).collect(Collectors.joining("/"));
                                entry.setValue(value);
                            }else {
                                SysOrganizeEntity organizeEntity = organizeApi.getInfo(String.valueOf(entry.getValue()));
                                entry.setValue(organizeEntity != null ? organizeEntity.getFullName() : "");
                            }
                            break;
                        case ProjectKeyConsts.CREATEUSER:
                        case ProjectKeyConsts.MODIFYUSER:
                            SysUserEntity userCreEntity = userService.getInfo(String.valueOf(entry.getValue()));
                            if (userCreEntity != null) {
                                entry.setValue(userCreEntity.getRealName());
                            }
                            break;
                        case ProjectKeyConsts.CURRPOSITION:
                            String[] curPos = String.valueOf(entry.getValue()).split(",");
                            List<String> curPosList = new ArrayList<>();
                            for (String pos : curPos){
                                SysPositionEntity posEntity = positionApi.getInfo(pos);
                                String posName = Objects.nonNull(posEntity) ? posEntity.getFullName() : "";
                                curPosList.add(posName);
                            }
                            entry.setValue(curPosList.stream().collect(Collectors.joining(",")));
                            break;
                        case ProjectKeyConsts.CREATETIME:
                        case ProjectKeyConsts.MODIFYTIME:
                            if (ObjectUtil.isNotEmpty(entry.getValue())){
                                String dateStr=String.valueOf(entry.getValue());
                                dateStr=dateStr.length()>19?dateStr.substring(0,19):dateStr;
                                entry.setValue(dateStr);
                            }else {
                                entry.setValue(null);
                            }
                            break;
                        default:
                    }
                }
            }
            data = JsonUtilEx.getObjectToString(dataMap);
        }
        return data;
    }

    public FieLdsModel getTreeRelationSearch(List<FieLdsModel> FieLdsModels, String treeRelationField) {
        FieLdsModel fieLdsModel = new FieLdsModel();
        boolean treeIsChild = treeRelationField.toLowerCase().contains(ProjectKeyConsts.CHILD_TABLE_PREFIX);
        if (treeIsChild){
            String tableField = treeRelationField.substring(0,treeRelationField.indexOf("-"));
            String relationVmodel = treeRelationField.substring(treeRelationField.indexOf("-")+1);
            List<FieLdsModel> allFields = new ArrayList<>();
            recursionFields(FieLdsModels,allFields);
//            List<FieLdsModel> childFields = FieLdsModels.stream().filter(fieLd -> fieLd.getVModel().equals(tableField)).map(f -> f.getConfig().getChildren()).findFirst().orElse(new ArrayList<>());
            fieLdsModel = allFields.stream().filter(swap->relationVmodel.equalsIgnoreCase(swap.getVModel())
                    &&tableField.equals(swap.getConfig().getParentVModel())).findFirst().orElse(null);
        } else {
            //递归出所有表单控件从中去除左侧树的控件属性
            List<FieLdsModel> allFields = new ArrayList<>();
            this.recursionFields(FieLdsModels,allFields);
            fieLdsModel = allFields.stream().filter(swap -> treeRelationField.equalsIgnoreCase(swap.getVModel())).findFirst().orElse(null);
        }
        return fieLdsModel;
    }

    private void recursionFields(List<FieLdsModel> fieLdsModelList,List<FieLdsModel> allFields){
        for (FieLdsModel fieLdsModel : fieLdsModelList){
            if (fieLdsModel.getConfig().getChildren()!=null){
               this.recursionFields(fieLdsModel.getConfig().getChildren(),allFields);
            }else {
                if (StringUtil.isNotEmpty(fieLdsModel.getVModel())){
                    allFields.add(fieLdsModel);
                }
            }
        }
    }

}
