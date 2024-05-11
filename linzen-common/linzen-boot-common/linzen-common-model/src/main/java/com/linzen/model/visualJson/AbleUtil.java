package com.linzen.model.visualJson;

import com.linzen.constant.LinzenConst;
import com.linzen.constant.PermissionConst;
import com.linzen.util.JsonUtil;
import com.linzen.util.visiual.ProjectKeyConsts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AbleUtil {

    public static OnlineCusCheckModel ableModel(String ableIdsAll, String projectKey) {
        List<String> ableIdList = new ArrayList<>();
        List<String> ableComIds = new ArrayList<>();
        List<String> ableComIdsStr = new ArrayList<>();
        List<String> ableDepIds = new ArrayList<>();
        List<String> ableGroupIds = new ArrayList<>();
        List<String> ableRoleIds = new ArrayList<>();
        List<String> ablePosIds = new ArrayList<>();
        List<String> ableUserIds = new ArrayList<>();
        List<String> ableSystemIds = new ArrayList<>();
        List<String> ableIds = new ArrayList<>();

        try {
            List<List<String>> list = JsonUtil.createJsonToBean(ableIdsAll, List.class);
            for (List<String> ableId : list) {
                ableIdList.addAll(ableId);
                ableComIdsStr.add(JsonUtil.createListToJsonArray(ableId).toJSONString());
            }
        } catch (Exception e) {
            List<String> list = JsonUtil.createJsonToBean(ableIdsAll, List.class);
            for (String ableId : list) {
                ableIdList.add(ableId);
            }
        }
        for (String id : ableIdList) {
            String[] split = id.split("--");
            if (split.length > 1) {
                if (PermissionConst.COMPANY.equalsIgnoreCase(split[1])) {
                    ableComIds.add(split[0]);
                }
                if (PermissionConst.DEPARTMENT.equalsIgnoreCase(split[1])) {
                    ableDepIds.add(split[0]);
                }
                if (PermissionConst.USER.equalsIgnoreCase(split[1])) {
                    ableUserIds.add(split[0]);
                }
                if (PermissionConst.ROLE.equalsIgnoreCase(split[1])) {
                    ableRoleIds.add(split[0]);
                }
                if (PermissionConst.GROUP.equalsIgnoreCase(split[1])) {
                    ableGroupIds.add(split[0]);
                }
                if (PermissionConst.POSITION.equalsIgnoreCase(split[1])) {
                    ablePosIds.add(split[0]);
                }
                if (PermissionConst.SYSTEM.equalsIgnoreCase(split[1])) {
                    ableSystemIds.add(split[0]);
                }
                ableIds.add(id);
            } else {
                Map<String, String> param = LinzenConst.SYSTEM_PARAM;
                if (param.get(id) != null) {
                    ableSystemIds.add(id);
                } else {
                    if (ProjectKeyConsts.COMSELECT.equalsIgnoreCase(projectKey)) {
                        ableComIds.add(id);
                    }
                    if (ProjectKeyConsts.DEPSELECT.equalsIgnoreCase(projectKey)) {
                        ableDepIds.add(id);
                    }
                    if (ProjectKeyConsts.ROLESELECT.equalsIgnoreCase(projectKey)) {
                        ableRoleIds.add(id);
                    }
                    if (ProjectKeyConsts.GROUPSELECT.equalsIgnoreCase(projectKey)) {
                        ableGroupIds.add(id);
                    }
                    if (ProjectKeyConsts.USERSELECT.equalsIgnoreCase(projectKey) || ProjectKeyConsts.CUSTOMUSERSELECT.equalsIgnoreCase(projectKey)) {
                        ableUserIds.add(id);
                    }

                }
            }
        }
        OnlineCusCheckModel ableModel = OnlineCusCheckModel.builder().ableComIdsStr(ableComIdsStr).ableComIds(ableComIds).ableDepIds(ableDepIds)
                .ableGroupIds(ableGroupIds).ablePosIds(ablePosIds).ableRoleIds(ableRoleIds)
                .ableSystemIds(ableSystemIds).ableUserIds(ableUserIds).ableIds(ableIds).build();
        return ableModel;
    }

}
