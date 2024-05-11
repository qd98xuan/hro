package com.linzen.onlinedev.util.onlineDevUtil;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.entity.VisualdevEntity;
import com.linzen.base.model.datainterface.DataInterfaceActionVo;
import com.linzen.base.service.DataInterfaceService;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.service.ProvinceService;
import com.linzen.base.service.VisualdevService;
import com.linzen.model.visualJson.FieLdsModel;
import com.linzen.model.visualJson.analysis.FormModel;
import com.linzen.onlinedev.model.OnlineDevData;
import com.linzen.onlinedev.model.OnlineDevEnum.MultipleControlEnum;
import com.linzen.onlinedev.model.OnlineDevEnum.OnlineDataTypeEnum;
import com.linzen.onlinedev.model.VisualdevModelDataInfoVO;
import com.linzen.onlinedev.service.VisualDevInfoService;
import com.linzen.onlinedev.service.VisualdevModelDataService;
import com.linzen.permission.entity.*;
import com.linzen.permission.service.*;
import com.linzen.util.*;
import com.linzen.util.visiual.ProjectKeyConsts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public class OnlineDevInfoUtils {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private UserService userService;
    @Autowired
    private FormInfoUtils formInfoUtils;
    @Autowired
    private PositionService positionApi;
    @Autowired
    private OrganizeService organizeApi;
    @Autowired
    private VisualdevService visualdevService;
    @Autowired
    private VisualdevModelDataService visualdevModelDataService;
    @Autowired
    private DataInterfaceService dataInterFaceApi;
    @Autowired
    private VisualDevInfoService visualDevInfoService;
    @Autowired
    private ProvinceService areaApi;
    @Autowired
    private RoleService roleApi;
    @Autowired
    private GroupService groupApi;

    private Map<String, String> nullDatamap = new HashMap<>();

    /**
     * 数据转换(不取缓存)
     *
     * @param modelList
     * @param dataMap
     * @return
     */
    public Map<String, Object> swapChildTableDataInfo(List<FieLdsModel> modelList, Map<String, Object> dataMap, List<FormModel> codeList) {
        Map<String, Object> dataCopyMap = new HashMap<>();
        dataCopyMap.putAll(dataMap);

        Map<String, Map<String, Object>> dataDetailMap = new HashMap<>();
        try {
            for (FieLdsModel swapDataVo : modelList) {
                String projectKey = swapDataVo.getConfig().getProjectKey();
                String dataType = swapDataVo.getConfig().getDataType();
                String vModel = swapDataVo.getVModel();
                Object val = dataMap.get(vModel);
                String modelValue = String.valueOf(val);
                if (StringUtil.isEmpty(modelValue) || "null".equals(modelValue)) {
                    continue;
                }
                if (dataType != null) {
                    //数据接口的数据存放
                    String label = swapDataVo.getProps() != null ? swapDataVo.getProps().getLabel() : "" ;
                    String value = swapDataVo.getProps() != null ? swapDataVo.getProps().getValue() : "" ;
                    String Children = swapDataVo.getProps() != null ? swapDataVo.getProps().getChildren() : "" ;
                    List<Map<String, Object>> options = new ArrayList<>();

                    if (dataType.equals(OnlineDataTypeEnum.STATIC.getType())) {
                        if (StringUtil.isNotEmpty(swapDataVo.getOptions())) {
                            options = JsonUtil.createJsonToListMap(swapDataVo.getOptions());

                            JSONArray data = JsonUtil.createListToJsonArray(options);
                            OnlineDevListUtils.getOptions(label, value, Children, data, options);
                        } else {
                            options = JsonUtil.createJsonToListMap(swapDataVo.getOptions());
                        }
                    }
                    if (dataType.equals(OnlineDataTypeEnum.DYNAMIC.getType())) {
                        ServiceResult data = dataInterFaceApi.infoToId(swapDataVo.getInterfaceId(),null, nullDatamap);
                        //api调用 序列化为linkedHashMap
                        LinkedHashMap<String, List<Map<String, Object>>> actionVo = (LinkedHashMap<String, List<Map<String, Object>>>) data.getData();
                        if (actionVo != null) {
                            List<Map<String, Object>> dataList = actionVo.get("data" );
                            JSONArray dataAll = JsonUtil.createListToJsonArray(dataList);
                            treeToList(label, value, Children, dataAll, options);
                        }
                    }
                    if (dataType.equals(OnlineDataTypeEnum.DICTIONARY.getType())) {
                        List<DictionaryDataEntity> list = dictionaryDataService.getDicList(swapDataVo.getConfig().getDictionaryType());
                        options = list.stream().map(dic -> {
                            Map<String, Object> dictionaryMap = new HashMap<>(16);
                            dictionaryMap.put("id" , dic.getId());
                            dictionaryMap.put("enCode" , dic.getEnCode());
                            dictionaryMap.put("fullName" , dic.getFullName());
                            return dictionaryMap;
                        }).collect(Collectors.toList());
                    }

                    Map<String, String> dataInterfaceMap = new HashMap<>(16);
                    options.stream().forEach(o -> {
                        dataInterfaceMap.put(String.valueOf(o.get(value)), String.valueOf(o.get(label)));
                    });

                    List<String> valueList = new ArrayList<>();
                    if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
                        String[][] data = BeanUtil.toBean(modelValue, String[][].class);
                        for (String[] casData : data) {
                            for (String s : casData) {
                                valueList.add(s);
                            }
                        }
                    } else if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
                        valueList = JsonUtil.createJsonToList(modelValue, String.class);
                    } else {
                        valueList.add(modelValue);
                    }
                    String allValue = valueList.stream().map(va -> dataInterfaceMap.get(va)).collect(Collectors.joining("," ));
                    dataMap.put(vModel, allValue);
                } else {
                    switch (projectKey) {
                        //公司组件
                        case ProjectKeyConsts.COMSELECT:
                            //部门组件
                        case ProjectKeyConsts.DEPSELECT:
                            //所属部门
                        case ProjectKeyConsts.CURRDEPT:
                            dataMap.put(vModel, getOrgValue(modelValue));
                            break;

                        //所属组织
                        case ProjectKeyConsts.CURRORGANIZE:
                            boolean isAll = "all".equals(swapDataVo.getShowLevel());
                            if (isAll) {
                                List<SysOrganizeEntity> organizeList = new ArrayList<>();
                                organizeApi.getOrganizeId(modelValue,organizeList);
                                Collections.reverse(organizeList);
                                String value = organizeList.stream().map(SysOrganizeEntity::getFullName).collect(Collectors.joining("/" ));
                                dataMap.put(vModel, value);
                            } else {
                                SysOrganizeEntity organizeEntity = organizeApi.getInfo(modelValue);
                                dataMap.put(vModel, Objects.nonNull(organizeEntity) ? organizeEntity.getFullName() : modelValue);
                            }
                            break;

                        //岗位组件
                        case ProjectKeyConsts.POSSELECT:
                            //所属岗位
                        case ProjectKeyConsts.CURRPOSITION:
                            dataMap.put(vModel, getPosValue(modelValue));
                            break;

                        //用户组件
                        case ProjectKeyConsts.USERSELECT:
                            //创建用户
                        case ProjectKeyConsts.CREATEUSER:
                            //修改用户
                        case ProjectKeyConsts.MODIFYUSER:
                            if ("admin".equals(modelValue)) {
                                dataMap.put(vModel, "管理员" );
                            } else {
                                dataMap.put(vModel, getUserValue(modelValue));
                            }
                            break;

                        //省市区联动
                        case ProjectKeyConsts.ADDRESS:
                            String value = String.valueOf(dataMap.get(vModel));
                            if (OnlinePublicUtils.getMultiple(value, MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
                                String[][] data = BeanUtil.toBean(value, String[][].class);
                                List<String> addList = new ArrayList<>();
                                for (String[] AddressData : data) {
                                    List<String> adList = new ArrayList<>();
                                    for (String s : AddressData) {
                                        adList.add(s);
                                    }
                                    addList.add(String.join("/" , areaApi.getProList(adList).stream().map(pro -> pro.getFullName()).collect(Collectors.toList())));
                                }
                                dataMap.put(vModel, String.join(";" , addList));
                            } else {
                                List<String> proDataS = JsonUtil.createJsonToList(value, String.class);
                                dataMap.put(vModel, String.join("," , areaApi.getProList(proDataS).stream().map(pro -> pro.getFullName()).collect(Collectors.toList())));
                            }
                            break;

                        case ProjectKeyConsts.RELATIONFORM:
                            VisualdevEntity entity = visualdevService.getInfo(swapDataVo.getModelId());
                            VisualdevModelDataInfoVO infoVO;
                            String keyId = String.valueOf(dataMap.get(vModel));
                            Map<String, Object> formDataMap = new HashMap<>(16);
                            if (!StringUtil.isEmpty(entity.getVisualTables()) && !OnlineDevData.TABLE_CONST.equals(entity.getVisualTables())) {
                                infoVO = visualDevInfoService.getDetailsDataInfo(keyId, entity);
                            } else {
                                infoVO = visualdevModelDataService.infoDataChange(keyId, entity);
                            }
                            formDataMap = JsonUtil.stringToMap(infoVO.getData());
                            String relationField = swapDataVo.getRelationField();
                            if (formDataMap != null && formDataMap.size() > 0) {
                                dataMap.put(vModel + "_id" , dataMap.get(vModel));
                                dataMap.put(vModel, formDataMap.get(relationField));
                                dataDetailMap.put(vModel, formDataMap);
                            }
                            break;

                        case ProjectKeyConsts.POPUPSELECT:
                            ServiceResult data = dataInterFaceApi.infoToId(swapDataVo.getInterfaceId(), null,nullDatamap);
                            //api调用 序列化为linkedHashMap
                            LinkedHashMap<String, List<Map<String, Object>>> actionVo = (LinkedHashMap<String, List<Map<String, Object>>>) data.getData();
                            List<Map<String, Object>> mapList = actionVo.get("data" ) != null ? actionVo.get("data" ) : new ArrayList<>();
                            Map<String, Object> PopMap = mapList.stream().filter(map -> map.get(swapDataVo.getPropsValue()).equals(dataMap.get(vModel))).findFirst().orElse(null);
                            if (PopMap.size() > 0) {
                                dataMap.put(vModel + "_id" , dataMap.get(vModel));
                                dataMap.put(vModel, PopMap.get(swapDataVo.getColumnOptions().get(0).getValue()));
                                dataDetailMap.put(vModel, PopMap);
                            }
                            break;
                        case ProjectKeyConsts.POPUPTABLESELECT:
                            Object popData = dataInterFaceApi.infoToId(swapDataVo.getInterfaceId(),null,null).getData();
                            DataInterfaceActionVo actionPo = (DataInterfaceActionVo) popData;
                            List<Map<String, Object>> popMapList = new ArrayList<>();
                            if (actionPo.getData() instanceof List) {
                                popMapList = (List<Map<String, Object>>) actionPo.getData();
                            }
                            String popValue = String.valueOf(dataMap.get(vModel));
                            List<String> idList = new ArrayList<>();
                            if (popValue.contains("[")) {
                                idList = JsonUtil.createJsonToList(popValue, String.class);
                            } else {
                                idList.add(popValue);
                            }
                            List<String> swapValue = new ArrayList<>();
                            for (String id : idList) {
                                popMapList.stream().filter(map ->
                                        map.get(swapDataVo.getPropsValue()).equals(id)
                                ).forEach(
                                        modelMap -> swapValue.add(String.valueOf(modelMap.get(swapDataVo.getRelationField())))
                                );
                            }
                            dataMap.put(vModel, swapValue.stream().collect(Collectors.joining(",")));
                            break;
                        case ProjectKeyConsts.MODIFYTIME:
                        case ProjectKeyConsts.CREATETIME:
                        case ProjectKeyConsts.DATE:
                            //判断是否为时间戳格式
                            String format;
                            String dateData = String.valueOf(dataMap.get(vModel));
                            String dateSwapInfo = swapDataVo.getFormat() != null ? swapDataVo.getFormat() : swapDataVo.getType() != null && swapDataVo.getType().equals(ProjectKeyConsts.DATE) ? "yyyy-MM-dd" : "yyyy-MM-dd HH:mm:ss" ;
                            if (!dateData.contains("-" ) && !dateData.contains(":" ) && dateData.length() > 10) {
                                DateTimeFormatter ftf = DateTimeFormatter.ofPattern(dateSwapInfo);
                                format = ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) dataMap.get(vModel)), ZoneId.of("+8" )));
                            } else {
                                format = dateData;
                            }
                            if (format.contains("." )) {
                                format = format.substring(0, format.lastIndexOf("." ));
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat(dateSwapInfo);
                            try {
                                Date date = sdf.parse(format);
                                String outTime = sdf.format(sdf.parse(DateUtil.dateFormat(date)));
                                dataMap.put(vModel, outTime);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;

                        //开关 滑块
                        case ProjectKeyConsts.SWITCH:
                            String switchValue = String.valueOf(dataMap.get(vModel)).equals("1" ) ? swapDataVo.getActiveTxt() : swapDataVo.getInactiveTxt();
                            dataMap.put(vModel, switchValue);
                            break;
                        case ProjectKeyConsts.RATE:
                            BigDecimal ratevalue=new BigDecimal(0);
                            if(dataMap.get(vModel)!=null){
                                ratevalue= new BigDecimal(dataMap.get(vModel).toString());
                            }
                            dataMap.put(vModel, ratevalue);
                            break;
                        case ProjectKeyConsts.SLIDER:
                            dataMap.put(vModel, dataMap.get(vModel) != null ? Integer.parseInt(String.valueOf(dataMap.get(vModel))) : null);
                            break;

                        case ProjectKeyConsts.UPLOADFZ:
                        case ProjectKeyConsts.UPLOADIMG:
                            List<Map<String, Object>> fileList = JsonUtil.createJsonToListMap(String.valueOf(dataMap.get(vModel)));
                            dataMap.put(vModel, fileList);
                            break;

                        default:
                            break;
                    }
                }
            }
            //转换二维码
            swapCodeDataInfo(codeList, dataMap, dataCopyMap);
            //关联选择属性
            if (dataDetailMap.size() > 0) {
                getDataAttr(modelList, dataMap, dataDetailMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataMap;
    }


    /**
     * 转换数据格式(编辑页)
     *
     * @param modelList 控件
     * @param dataMap   数据
     * @return
     */
    public Map<String, Object> swapDataInfoType(List<FieLdsModel> modelList, Map<String, Object> dataMap) {
        return formInfoUtils.swapDataInfoType(modelList,dataMap);
    }


    /**
     * 转换数据格式(编辑页)
     *
     * @param modelList 控件
     * @param dataMap   数据
     * @return
     */
    public Map<String, Object> getInitLineData(List<FieLdsModel> modelList, Map<String, Object> dataMap) {
        for (FieLdsModel swapDataVo : modelList) {
            String projectKey = swapDataVo.getConfig().getProjectKey();
            String vModel = swapDataVo.getVModel();
            Object value = dataMap.get(vModel);
            if (value == null || ObjectUtil.isEmpty(value)) {
                continue;
            }
            switch (projectKey) {
                case ProjectKeyConsts.RATE:
                case ProjectKeyConsts.SLIDER:
                    BigDecimal ratevalue=new BigDecimal(0);
                    if(dataMap.get(vModel)!=null){
                        ratevalue= new BigDecimal(dataMap.get(vModel).toString());
                    }
                    dataMap.put(vModel, ratevalue);
                    break;
                case ProjectKeyConsts.UPLOADFZ:
                case ProjectKeyConsts.UPLOADIMG:
                    List<Map<String, Object>> fileList = JsonUtil.createJsonToListMap(String.valueOf(value));
                    dataMap.put(vModel, fileList);
                    break;

                case ProjectKeyConsts.DATE:
                    Long dateTime = DateTimeFormatConstant.getDateObjToLong(dataMap.get(vModel));
                    dataMap.put(vModel, dateTime != null ? dateTime : dataMap.get(vModel));
                    break;

                case ProjectKeyConsts.SWITCH:
                    dataMap.put(vModel, value != null ? Integer.parseInt(String.valueOf(value)) : null);
                    break;
                //系统自动生成控件
                case ProjectKeyConsts.CURRORGANIZE:
                case ProjectKeyConsts.CURRDEPT:
                    //多级组
                    String orgIds = String.valueOf(dataMap.get(vModel));
                    String orgId = "";
                    String orgName = "";
                    try{
                        List<String> jsonToList = JsonUtil.createJsonToList(orgIds, String.class);
                        orgId = jsonToList.get(jsonToList.size()-1);
                    }catch (Exception e){
                        orgId = orgIds;
                    }
                    SysOrganizeEntity organizeEntity = StringUtil.isNotEmpty(orgId) ? organizeApi.getInfo(orgId) : null;
                    if ("all".equals(swapDataVo.getShowLevel())) {
                        if (organizeEntity != null) {
                            List<SysOrganizeEntity> organizeList = new ArrayList<>();
                            organizeApi.getOrganizeId(orgId, organizeList);
                            Collections.reverse(organizeList);
                            orgName = organizeList.stream().map(SysOrganizeEntity::getFullName).collect(Collectors.joining("/" ));
                        }
                    } else {
                        if (organizeEntity != null) {
                            orgName = organizeEntity.getFullName();
                        } else {
                            orgName = " ";
                        }
                    }
                    dataMap.put(vModel,orgName);
                    break;
                case ProjectKeyConsts.CURRPOSITION:
                    SysPositionEntity positionEntity = positionApi.getInfo(String.valueOf(value));
                    dataMap.put(vModel, Objects.nonNull(positionEntity) ? positionEntity.getFullName() : value);
                    break;

                case ProjectKeyConsts.CREATEUSER:
                case ProjectKeyConsts.MODIFYUSER:
                    SysUserEntity userEntity = userService.getInfo(String.valueOf(value));
                    String userValue = Objects.nonNull(userEntity) ? userEntity.getRealName()+"/"+userEntity.getAccount() : String.valueOf(value);
                    dataMap.put(vModel, userValue);
                    break;
                default:
                    dataMap.put(vModel, FormPublicUtils.getDataConversion(value));
                    break;
            }
        }
        return dataMap;
    }

    /**
     * 二维码 条形码详情数据
     *
     * @param codeList    控件集合
     * @param swapDataMap 转换后的数据
     * @param dataMap     转换前
     * @return
     */
    public static void swapCodeDataInfo(List<FormModel> codeList, Map<String, Object> swapDataMap, Map<String, Object> dataMap) {
        for (FormModel formModel : codeList) {
            String projectKey = formModel.getConfig().getProjectKey();
            if (projectKey.equals(ProjectKeyConsts.QR_CODE) || projectKey.equals(ProjectKeyConsts.BARCODE)) {
                String codeDataType = formModel.getDataType();
                if (OnlineDataTypeEnum.RELATION.getType().equals(codeDataType)) {
                    String relationFiled = formModel.getRelationField();
                    if (StringUtil.isNotEmpty(relationFiled)) {
                        Object relationValue = dataMap.get(relationFiled);
                        if (ObjectUtil.isNotEmpty(relationValue)) {
                            swapDataMap.put(relationFiled + "_id" , relationValue);
                        }
                    }
                }
            }
        }
    }

    private static void treeToList(String value, String label, String children, JSONArray data, List<Map<String, Object>> result) {
        for (int i = 0; i < data.size(); i++) {
            JSONObject ob = data.getJSONObject(i);
            Map<String, Object> tree = new HashMap<>(16);
            tree.put(value, String.valueOf(ob.get(value)));
            tree.put(label, String.valueOf(ob.get(label)));
            result.add(tree);
            if (ob.get(children) != null) {
                JSONArray childArray = ob.getJSONArray(children);
                treeToList(value, label, children, childArray, result);
            }
        }
    }

    /**
     * 生成关联属性（弹窗选择属性,关联表单属性）
     *
     * @param fieLdsModelList
     * @param dataMap
     * @param dataDetailMap
     */
    private static void getDataAttr(List<FieLdsModel> fieLdsModelList, Map<String, Object> dataMap, Map<String, Map<String, Object>> dataDetailMap) {
        for (FieLdsModel fieLdsModel : fieLdsModelList) {
            String projectKey = fieLdsModel.getConfig().getProjectKey();
            if (projectKey.equals(ProjectKeyConsts.RELATIONFORM_ATTR) || projectKey.equals(ProjectKeyConsts.POPUPSELECT_ATTR)) {
                String relationField = fieLdsModel.getRelationField();
                String showField = fieLdsModel.getShowField();
                Map<String, Object> formDataMap = dataDetailMap.get(relationField);
                dataMap.put(relationField + "_" + showField, formDataMap.get(showField));
            }
        }
    }

    /**
     * 转换组织
     *
     * @param modelValue
     * @return
     */
    private String getOrgValue(String modelValue) {
        String orgValue;
        List<String> valueList;
        if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
            String[][] prgArray = BeanUtil.toBean(modelValue, String[][].class);
            List<String> addList = new ArrayList<>();
            for (String[] prgData : prgArray) {
                List<String> adList = new ArrayList<>();
                for (String s : prgData) {
                    SysOrganizeEntity info = organizeApi.getInfo(s);
                    adList.add(Objects.nonNull(info) ? info.getFullName() : "" );
                }
                String porData = adList.stream().collect(Collectors.joining("/" ));
                addList.add(porData);
            }
            orgValue = String.join(";" , addList);
        } else {
            if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
                valueList = JsonUtil.createJsonToList(modelValue, String.class);
            } else {
                valueList = Stream.of(modelValue.split("," )).collect(Collectors.toList());
            }
            String allValue = valueList.stream().map(va -> {
                SysOrganizeEntity organizeEntity = organizeApi.getInfo(va);
                return Objects.nonNull(organizeEntity) ? organizeEntity.getFullName() : va;
            }).collect(Collectors.joining("," ));
            orgValue = allValue;
        }
        return orgValue;
    }

    /**
     * 转换岗位
     *
     * @param modelValue
     * @return
     */
    private String getPosValue(String modelValue) {
        String posValue;
        List<String> valueList;
        if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
            String[][] prgArray = BeanUtil.toBean(modelValue, String[][].class);
            List<String> addList = new ArrayList<>();
            for (String[] prgData : prgArray) {
                List<String> adList = new ArrayList<>();
                for (String s : prgData) {
                    SysPositionEntity info = positionApi.getInfo(s);
                    adList.add(Objects.nonNull(info) ? info.getFullName() : "" );
                }
                String porData = adList.stream().collect(Collectors.joining("/" ));
                addList.add(porData);
            }
            posValue = String.join(";" , addList);
        } else {
            if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
                valueList = JsonUtil.createJsonToList(modelValue, String.class);
            } else {
                valueList = Stream.of(modelValue.split("," )).collect(Collectors.toList());
            }
            String allValue = valueList.stream().map(va -> {
                SysPositionEntity positionEntity = positionApi.getInfo(va);
                return Objects.nonNull(positionEntity) ? positionEntity.getFullName() : va;
            }).collect(Collectors.joining("," ));
            posValue = allValue;
        }
        return posValue;
    }

    /**
     * 转换用户
     *
     * @param modelValue
     * @return
     */
    private String getUserValue(String modelValue) {
        String userValue;
        List<String> valueList;
        if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
            String[][] prgArray = BeanUtil.toBean(modelValue, String[][].class);
            List<String> addList = new ArrayList<>();
            for (String[] prgData : prgArray) {
                List<String> adList = new ArrayList<>();
                for (String s : prgData) {
                    SysUserEntity info = userService.getInfo(s);
                    adList.add(Objects.nonNull(info) ? info.getRealName() + "/" + info.getAccount() : "" );
                }
                String porData = adList.stream().collect(Collectors.joining("/" ));
                addList.add(porData);
            }
            userValue = String.join(";" , addList);
        } else {
            if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
                valueList = JsonUtil.createJsonToList(modelValue, String.class);
            } else {
                valueList = Stream.of(modelValue.split("," )).collect(Collectors.toList());
            }
            String allValue = valueList.stream().map(va -> {
                SysUserEntity userEntity = userService.getInfo(va);
                return Objects.nonNull(userEntity) ? userEntity.getRealName() + "/" + userEntity.getAccount() : va;
            }).collect(Collectors.joining("," ));
            userValue = allValue;
        }
        return userValue;
    }


    /**
     * 转换角色
     *
     * @param modelValue
     * @return
     */
    private String getRoleValue(String modelValue) {
        String value;
        List<String> valueList;
        if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
            String[][] prgArray = BeanUtil.toBean(modelValue, String[][].class);
            List<String> addList = new ArrayList<>();
            for (String[] prgData : prgArray) {
                List<String> adList = new ArrayList<>();
                for (String s : prgData) {
                    SysRoleEntity info = roleApi.getInfo(s);
                    adList.add(Objects.nonNull(info) ? info.getFullName() : "");
                }
                String porData = adList.stream().collect(Collectors.joining("/"));
                addList.add(porData);
            }
            value = String.join(";", addList);
        } else {
            if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
                valueList = JsonUtil.createJsonToList(modelValue, String.class);
            } else {
                valueList = Stream.of(modelValue.split(",")).collect(Collectors.toList());
            }
            String allValue = valueList.stream().map(va -> {
                SysRoleEntity userEntity = roleApi.getInfo(va);
                return Objects.nonNull(userEntity) ? userEntity.getFullName() : va;
            }).collect(Collectors.joining(","));
            value = allValue;
        }
        return value;
    }

    /**
     * 转换分组
     *
     * @param modelValue
     * @return
     */
    private String getGroupValue(String modelValue) {
        String value;
        List<String> valueList;
        if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
            String[][] prgArray = BeanUtil.toBean(modelValue, String[][].class);
            List<String> addList = new ArrayList<>();
            for (String[] prgData : prgArray) {
                List<String> adList = new ArrayList<>();
                for (String s : prgData) {
                    GroupEntity info = groupApi.getInfo(s);
                    adList.add(Objects.nonNull(info) ? info.getFullName() : "");
                }
                String porData = adList.stream().collect(Collectors.joining("/"));
                addList.add(porData);
            }
            value = String.join(";", addList);
        } else {
            if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
                valueList = JsonUtil.createJsonToList(modelValue, String.class);
            } else {
                valueList = Stream.of(modelValue.split(",")).collect(Collectors.toList());
            }
            String allValue = valueList.stream().map(va -> {
                GroupEntity info = groupApi.getInfo(va);
                return Objects.nonNull(info) ? info.getFullName() : va;
            }).collect(Collectors.joining(","));
            value = allValue;
        }
        return value;
    }


}
