package com.linzen.util;

import cn.hutool.core.util.ObjectUtil;
import com.linzen.database.util.DynamicDataSourceUtil;
import com.linzen.model.visualJson.FieLdsModel;
import com.linzen.permission.entity.SysPositionEntity;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.util.visiual.ProjectKeyConsts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

import static com.linzen.util.Constants.ADMIN_KEY;

/**
 * 在线详情编辑工具类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Component
public class FormInfoUtils {
    @Autowired
    private ServiceBaseUtil serviceUtil;


    /**
     * 转换数据格式(编辑页)
     *
     * @param modelList 控件
     * @param dataMap   数据
     * @return
     */
    public Map<String, Object> swapDataInfoType(List<FieLdsModel> modelList, Map<String, Object> dataMap) {
        dataMap = Optional.ofNullable(dataMap).orElse(new HashMap<>());
        try {
            DynamicDataSourceUtil.switchToDataSource(null);
            List<String> systemConditions = new ArrayList() {{
                add(ProjectKeyConsts.CURRORGANIZE);
                add(ProjectKeyConsts.CURRDEPT);
                add(ProjectKeyConsts.CURRPOSITION);
            }};
            List<String> nullIsList = new ArrayList() {{
                add(ProjectKeyConsts.UPLOADFZ);
                add(ProjectKeyConsts.UPLOADIMG);
            }};
            for (FieLdsModel swapDataVo : modelList) {
                String projectKey = swapDataVo.getConfig().getProjectKey();
                String vModel = swapDataVo.getVModel();
                Object value = dataMap.get(vModel);
                if (value == null || ObjectUtil.isEmpty(value)) {
                    if (systemConditions.contains(projectKey)) {
                        dataMap.put(vModel, " " );
                    }
                    if (nullIsList.contains(projectKey)) {
                        dataMap.put(vModel, Collections.emptyList());
                    }
                    continue;
                }
                switch (projectKey) {
                    case ProjectKeyConsts.UPLOADFZ:
                    case ProjectKeyConsts.UPLOADIMG:
                        List<Map<String, Object>> fileList = JsonUtil.createJsonToListMap(String.valueOf(value));
                        dataMap.put(vModel, fileList.size() == 0 ? new ArrayList<>() : fileList);
                        break;
                    case ProjectKeyConsts.DATE:
                        Long dateTime = DateTimeFormatConstant.getDateObjToLong(dataMap.get(vModel));
                        dataMap.put(vModel, dateTime != null ? dateTime : dataMap.get(vModel));
                        break;
                    case ProjectKeyConsts.CREATETIME:
                    case ProjectKeyConsts.MODIFYTIME:
                        String pattern = DateTimeFormatConstant.YEAR_MOnTH_DHMS;
                        Long time = DateTimeFormatConstant.getDateObjToLong(dataMap.get(vModel));
                        dataMap.put(vModel, time!=null?DateUtil.dateToString(new Date(time),pattern):"");
                        break;
                    case ProjectKeyConsts.SWITCH:
                    case ProjectKeyConsts.SLIDER:
                    case ProjectKeyConsts.RATE:
                    case ProjectKeyConsts.CALCULATE:
                    case ProjectKeyConsts.NUM_INPUT:
                        dataMap.put(vModel, value != null ? new BigDecimal(String.valueOf(value)) : null);
                        break;
                    case ProjectKeyConsts.CURRPOSITION:
                        SysPositionEntity positionEntity = serviceUtil.getPositionInfo(String.valueOf(value));
                        dataMap.put(vModel, Objects.nonNull(positionEntity) ? positionEntity.getFullName() : value);
                        break;

                    case ProjectKeyConsts.CREATEUSER:
                    case ProjectKeyConsts.MODIFYUSER:
                        SysUserEntity userEntity = serviceUtil.getUserInfo(String.valueOf(value));
                        String userValue = Objects.nonNull(userEntity) ? userEntity.getAccount().equalsIgnoreCase(ADMIN_KEY)
                                ? "管理员/admin" : userEntity.getRealName() + "/" + userEntity.getAccount() : String.valueOf(value);
                        dataMap.put(vModel, userValue);
                        break;
                    case ProjectKeyConsts.CURRORGANIZE:
                        String currentOrganizeName = serviceUtil.getCurrentOrganizeName(value, swapDataVo.getShowLevel());
                        dataMap.put(vModel, currentOrganizeName);
                        break;
                    default:
                        dataMap.put(vModel, FormPublicUtils.getDataConversion(value));
                        break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DynamicDataSourceUtil.clearSwitchDataSource();
        }
        return dataMap;
    }

}
