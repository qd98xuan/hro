package com.linzen.onlinedev.util.onlineDevUtil;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.entity.ProvinceEntity;
import com.linzen.base.model.ColumnDataModel;
import com.linzen.base.model.datainterface.DataInterfaceActionVo;
import com.linzen.base.service.DataInterfaceService;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.service.ProvinceService;
import com.linzen.base.service.VisualdevService;
import com.linzen.model.visualJson.FieLdsModel;
import com.linzen.onlinedev.model.OnlineDevEnum.MultipleControlEnum;
import com.linzen.onlinedev.model.OnlineDevEnum.OnlineDataTypeEnum;
import com.linzen.onlinedev.model.OnlineDevListModel.OnlineDevListDataVO;
import com.linzen.onlinedev.model.OnlineDevListModel.VisualColumnSearchVO;
import com.linzen.onlinedev.service.VisualDevInfoService;
import com.linzen.onlinedev.service.VisualdevModelDataService;
import com.linzen.permission.service.OrganizeService;
import com.linzen.permission.service.PositionService;
import com.linzen.permission.service.UserService;
import com.linzen.util.*;
import com.linzen.util.visiual.ProjectKeyConsts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
public class OnlineDevListUtils {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private UserService userService;
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
    private OnlineDevInfoUtils onlineDevInfoUtils;

    private Map<String, String> nullDatamap = new HashMap<>();

    /**
     * 查询条件
     *
     * @param list
     * @param searchList
     * @return
     */
    public static List<Map<String, Object>> getNoSwapList(List<Map<String, Object>> list, List<VisualColumnSearchVO> searchList) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (searchList == null) {
            return list;
        }
        for (Map<String, Object> dataVo : list) {
            int i = 0;
            for (VisualColumnSearchVO vo : searchList) {
                Object dataModel = dataVo.get(vo.getVModel());
                if (dataModel == null || ObjectUtil.isEmpty(dataModel)) {
                    continue;
                }
                //多选框默认添加多选属性
                if (vo.getConfig().getProjectKey().equals(ProjectKeyConsts.CHECKBOX) || ProjectKeyConsts.CASCADER.equals(vo.getConfig().getProjectKey())) {
                    vo.setMultiple(true);
                }
                if (vo.getSearchType().equals("1")) {
                    //多选框筛选
                    if (vo.getMultiple() != null && vo.getMultiple() == true) {
                        List<String> asList;
                        if (String.valueOf(dataModel).contains("[")) {
                            asList = JsonUtil.createJsonToList(String.valueOf(dataModel), String.class);
                        } else {
                            String[] multipleList = String.valueOf(dataModel).split(",");
                            asList = Arrays.asList(multipleList);
                        }
                        boolean b = asList.stream().anyMatch(t -> vo.getValue().toString().contains(t));
                        if (b) {
                            i++;
                        }
                    } else {
                        if (String.valueOf(vo.getValue()).equals(String.valueOf(dataModel))) {
                            i++;
                        }
                    }
                }
                if (vo.getSearchType().equals("2")) {
                    if (String.valueOf(dataModel).contains(String.valueOf(vo.getValue()))) {
                        i++;
                    }
                }
                if (vo.getSearchType().equals("3")) {
                    String key = vo.getConfig().getProjectKey();
                    switch (key) {
                        case ProjectKeyConsts.MODIFYTIME:
                        case ProjectKeyConsts.CREATETIME:
                            JSONArray timeStampArray = (JSONArray) vo.getValue();
                            Long o1 = (Long) timeStampArray.get(0);
                            Long o2 = (Long) timeStampArray.get(1);

                            //时间戳转string格式
                            String startTime = DateUtil.daFormat(o1);
                            String endTime = DateUtil.daFormat(o2);
                            //处理时间查询条件范围
                            endTime = endTime.substring(0, 10);
                            String firstTimeDate = OnlineDatabaseUtils.getTimeFormat(startTime);
                            String lastTimeDate = OnlineDatabaseUtils.getLastTimeFormat(endTime);

                            String value = String.valueOf(dataModel);
                            if (value.contains(".")) {
                                value = value.substring(0, value.lastIndexOf("."));
                            }
                            //只判断到日期
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                boolean b = DateUtil.isEffectiveDate(sdf.parse(value), sdf.parse(firstTimeDate), sdf.parse(lastTimeDate));
                                if (b) {
                                    i++;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;
                        case ProjectKeyConsts.NUM_INPUT:
                        case ProjectKeyConsts.CALCULATE:
                            Float firstValue = null;
                            Float secondValue = null;
                            JSONArray objects = (JSONArray) vo.getValue();
                            for (int k = 0; k < objects.size(); k++) {
                                Object n = objects.get(k);
                                if (ObjectUtil.isNotEmpty(n)) {
                                    if (k == 0) {
                                        firstValue = Float.parseFloat(String.valueOf(n));
                                    } else {
                                        secondValue = Float.parseFloat(String.valueOf(n));
                                    }
                                }
                            }
                            //数据
                            Float numValue = Float.parseFloat(String.valueOf(dataModel));

                            //条件1,2组合的情况
                            if (firstValue != null && secondValue == null) {
                                if (numValue >= firstValue) {
                                    i++;
                                }
                            }
                            if (firstValue != null && secondValue != null) {
                                if (numValue >= firstValue && numValue <= secondValue) {
                                    i++;
                                }
                            }
                            if (firstValue == null && secondValue != null) {
                                if (numValue <= secondValue) {
                                    i++;
                                }
                            }
                            break;
                        case ProjectKeyConsts.DATE:
                            String starTimeDates;
                            String endTimeDates;
                            if (dataModel == null) {
                                break;
                            }
                            //时间戳
                            if (!String.valueOf(vo.getValue()).contains(":") && !String.valueOf(vo.getValue()).contains("-")) {
                                JSONArray DateTimeStampArray = (JSONArray) vo.getValue();
                                Long d1 = (Long) DateTimeStampArray.get(0);
                                Long d2 = (Long) DateTimeStampArray.get(1);
                                long d1FirstTime = Long.parseLong(String.valueOf(d1));
                                long d2LastTime = Long.parseLong(String.valueOf(d2));

                                //时间戳转string格式
                                starTimeDates = DateUtil.daFormat(d1FirstTime);
                                endTimeDates = DateUtil.daFormat(d2LastTime);

                            } else {
                                //时间字符串
                                String[] keyArray = String.valueOf(vo.getValue()).split(",");
                                starTimeDates = keyArray[0];
                                endTimeDates = keyArray[1];
                            }
                            if (vo.getFormat() == null) {
                                starTimeDates = starTimeDates.substring(0, 10);
                                endTimeDates = endTimeDates.substring(0, 10);
                            }
                            starTimeDates = OnlineDatabaseUtils.getTimeFormat(starTimeDates);
                            endTimeDates = OnlineDatabaseUtils.getLastTimeFormat(endTimeDates);

                            String dateValue = dataModel.toString();
                            if (!dateValue.contains(":") && !dateValue.contains("-")) {
                                //时间戳
                                Long timeResult = (Long) dataModel;
                                dateValue = DateUtil.daFormat(timeResult);
                            }
                            if (dateValue.contains(".")) {
                                dateValue = dateValue.substring(0, dateValue.lastIndexOf("."));
                            }
                            dateValue = OnlineDatabaseUtils.getTimeFormat(dateValue);
                            //只判断到日期
                            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                Boolean b = DateUtil.isEffectiveDate(sdfDate.parse(dateValue), sdfDate.parse(starTimeDates), sdfDate.parse(endTimeDates));
                                if (b) {
                                    i++;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;
                        case ProjectKeyConsts.TIME:
                            JSONArray timeArray = (JSONArray) vo.getValue();
                            String start = String.valueOf(timeArray.get(0));
                            String end = String.valueOf(timeArray.get(1));
                            start = OnlineDatabaseUtils.getTimeFormat(start);
                            end = OnlineDatabaseUtils.getLastTimeFormat(end);
                            String timeValue = OnlineDatabaseUtils.getTimeFormat(String.valueOf(dataModel));
                            SimpleDateFormat timeSim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                boolean b = DateUtil.isEffectiveDate(timeSim.parse(timeValue), timeSim.parse(start), timeSim.parse(end));
                                if (b) {
                                    i++;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            break;
                    }
                }
                if (i == searchList.size()) {
                    resultList.add(dataVo);
                }
            }
        }
        return resultList;
    }

    /**
     * 取出列表所用到的 用户 组织 岗位的id
     *
     * @param list           数据
     * @param swapDataVoList 控件
     */
    public static void pageIdList(List<OnlineDevListDataVO> list, List<FieLdsModel> swapDataVoList, Map<String, Object> localCache) {
        Set<String> userList = (Set<String>) localCache.get("__user_list");
        Set<String> orgList = (Set<String>) localCache.get("__org_list");
        Set<String> posList = (Set<String>) localCache.get("__pos_list");
        Set<String> AllOrgList = (Set<String>) localCache.get("__allOrg_list");
        Set<String> roleList = (Set<String>) localCache.get("__role_list");
        for (FieLdsModel swapDataVo : swapDataVoList) {
            String projectKey = swapDataVo.getConfig().getProjectKey();
            String vModel = swapDataVo.getVModel();
            for (OnlineDevListDataVO listVo : list) {
                Map<String, Object> dataMap = listVo.getData();
                if (StringUtil.isEmpty(String.valueOf(dataMap.get(vModel))) || dataMap.get(vModel) == null) {
                    continue;
                }
                if (String.valueOf(dataMap.get(vModel)).equals("[]") || String.valueOf(dataMap.get(vModel)).equals("null")) {
                    continue;
                } else {
                    switch (projectKey) {
                        //公司组件
                        case ProjectKeyConsts.COMSELECT:
                            //部门组件
                        case ProjectKeyConsts.DEPSELECT:
                            //所属部门
                        case ProjectKeyConsts.CURRDEPT:
                            //所属公司
                        case ProjectKeyConsts.CURRORGANIZE:
                            if ("all".equals(swapDataVo.getShowLevel())) {
                                getIdInMethod(AllOrgList, dataMap.get(vModel));
                            } else {
                                getIdInMethod(orgList, dataMap.get(vModel));
                            }
                            break;
                        //角色
                        case ProjectKeyConsts.ROLESELECT:
                            getIdInMethod(roleList, dataMap.get(vModel));
                            break;
                        //岗位组件
                        case ProjectKeyConsts.POSSELECT:
                            //所属岗位
                        case ProjectKeyConsts.CURRPOSITION:
                            getIdInMethod(posList, dataMap.get(vModel));
                            break;

                        //用户组件
                        case ProjectKeyConsts.USERSELECT:
                            //创建用户
                        case ProjectKeyConsts.CREATEUSER:
                            //修改用户
                        case ProjectKeyConsts.MODIFYUSER:
                            getIdInMethod(userList, dataMap.get(vModel));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    /**
     * 存取对应id集合
     *
     * @param idList
     * @param modelData
     * @return
     */
    public static Collection<String> getIdInMethod(Collection<String> idList, Object modelData) {
        if (OnlinePublicUtils.getMultiple(String.valueOf(modelData), MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
            String[][] data = JsonUtil.createJsonToBean(String.valueOf(modelData), String[][].class);
            for (String[] AddressData : data) {
                idList.addAll(Arrays.asList(AddressData));
            }
        } else if (OnlinePublicUtils.getMultiple(String.valueOf(modelData), MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
            idList.addAll(JsonUtil.createJsonToList(String.valueOf(modelData), String.class));
        } else {
            String[] modelDatas = String.valueOf(modelData).split(",");
            idList.addAll(Arrays.asList(modelDatas));
        }
        return idList;
    }

    /**
     * 分组页面
     *
     * @param realList
     * @param columnDataModel
     * @return
     */
    public static List<Map<String, Object>> groupData(List<Map<String, Object>> realList, ColumnDataModel columnDataModel) {
        List<Map<String, Object>> columnList = JsonUtil.createJsonToListMap(columnDataModel.getColumnList());
        String firstField;
        String groupField = columnDataModel.getGroupField();
        List<Map<String, Object>> collect = columnList.stream().filter(t -> "left".equals(t.get("fixed"))).collect(Collectors.toList());
        Map<String, Object> map = null;
        if(CollectionUtil.isNotEmpty(collect)){
            map = collect.stream().filter(t -> !String.valueOf(t.get("prop")).equals(columnDataModel.getGroupField())).findFirst().orElse(null);
        }else{
            map = columnList.stream().filter(t -> !String.valueOf(t.get("prop")).equals(columnDataModel.getGroupField())).findFirst().orElse(null);
        }
        if (map == null) {
            map = columnList.stream().filter(t -> String.valueOf(t.get("prop")).equals(columnDataModel.getGroupField())).findFirst().orElse(null);
        }
        firstField = String.valueOf(map.get("prop"));

        Map<String, List<Map<String, Object>>> twoMap = new LinkedHashMap<>(16);

        for (Map<String, Object> realMap : realList) {
            String value = String.valueOf(realMap.get(groupField));
            if(realMap.get(groupField) instanceof Double){
                value = realMap.get(groupField).toString().replaceAll(".0*$","");
            }
            boolean isKey = twoMap.get(value) != null;
            if (isKey) {
                List<Map<String, Object>> maps = twoMap.get(value);
                maps.add(realMap);
                twoMap.put(value, maps);
            } else {
                List<Map<String, Object>> childrenList = new ArrayList<>();
                childrenList.add(realMap);
                twoMap.put(value, childrenList);
            }
        }

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (String key : twoMap.keySet()) {
            Map<String, Object> thirdMap = new HashMap<>(16);
            thirdMap.put(firstField, !key.equals("null") ? key : "");
            thirdMap.put("top", true);
            thirdMap.put("id", RandomUtil.uuId());
            thirdMap.put("children", twoMap.get(key));
            resultList.add(thirdMap);
        }
        return resultList;
    }

    /**
     * 递归查询
     *
     * @param label
     * @param value
     * @param Children
     * @param data
     * @param options
     */
    public static void getOptions(String label, String value, String Children, JSONArray data, List<Map<String, Object>> options) {
        for (int i = 0; i < data.size(); i++) {
            JSONObject ob = data.getJSONObject(i);
            Map<String, Object> tree = new HashMap<>(16);
            tree.put(value, String.valueOf(ob.get(value)));
            tree.put(label, String.valueOf(ob.get(label)));
            options.add(tree);
            if (ob.get(Children) != null) {
                JSONArray childrenArray = ob.getJSONArray(Children);
                getOptions(label, value, Children, childrenArray, options);
            }
        }
    }

    /**
     * 树形列表页面
     *
     * @param realList
     * @param columnDataModel
     * @return
     */
    public static List<Map<String, Object>> treeListData(List<Map<String, Object>> realList, ColumnDataModel columnDataModel) {
        String parentField = columnDataModel.getParentField() + "_id";
        String childField = columnDataModel.getSubField();
        for (int i = 0; i < realList.size(); i++) {
            Map<String, Object> item = realList.get(i);
            if ((item.get(parentField) != null && !StringUtil.isNotEmpty(item.get(parentField).toString())) || (item.get(parentField) != null && !"[]".equals(item.get(parentField).toString()))) {
                if (addChild(item, realList, parentField, childField) && realList.size() > 0) {
                    realList.remove(item);
                    i--;
                }
            }
        }
        return realList;
    }

    /**
     * 级联递归
     *
     * @param value
     * @param label
     * @param children
     * @param data
     * @param result
     */
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

    //递归
    private static boolean addChild(Map<String, Object> node, List<Map<String, Object>> list, String parentField, String childField) {

        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> ele = list.get(i);
            if (ele.get(childField).equals(node.get(parentField))) {
                if (ele.get("children") == null) {
                    ele.put("children", new ArrayList<>());
                }
                List<Map<String, Object>> children = (List<Map<String, Object>>) ele.get("children");
                children.add(node);
                ele.put("children", children);
                return true;
            }
            if (ele.get("children") != null) {
                List<Map<String, Object>> children = (List<Map<String, Object>>) ele.get("children");
                if (addChild(node, children, parentField, childField)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 保存需要转换的数据到redis(系统控件)
     *
     * @param swapDataVoList
     */
    public void sysNeedSwapData(List<FieLdsModel> swapDataVoList, String visualDevId, Map<String, Object> localCache) {

        //公共数据
        String dsName = Optional.ofNullable(TenantHolder.getDatasourceId()).orElse("");

        String redisKey;
        try {
            for (FieLdsModel swapDataVo : swapDataVoList) {
                String projectKey = swapDataVo.getConfig().getProjectKey();
                String dataType = swapDataVo.getConfig().getDataType();
                switch (projectKey) {
                    //省市区联动
                    case ProjectKeyConsts.ADDRESS:
                        redisKey = "";
                        if (!redisUtil.exists("")) {
                            List<ProvinceEntity> provinceEntityList = new ArrayList<>();
                            Map<String, String> provinceMap = new HashMap<>(16);
                            provinceEntityList.stream().forEach(p -> provinceMap.put(p.getId(), p.getFullName()));
                            redisUtil.insert(redisKey, provinceMap, RedisUtil.CAHCEWEEK);
                        }
                        if (!localCache.containsKey(redisKey)) {
                            localCache.put(redisKey, redisUtil.getMap(redisKey));
                        }
                        break;
                    default:
                        break;
                }
                if (dataType != null) {
                    //数据接口的数据存放
                    String label= swapDataVo.getProps().getLabel();
                    String value= swapDataVo.getProps().getValue();
                    String children  = swapDataVo.getProps().getChildren();
                    List<Map<String, Object>> options = new ArrayList<>();
                    Map<String, String> dataInterfaceMap = new HashMap<>(16);

                    //静态数据
                    if (dataType.equals(OnlineDataTypeEnum.STATIC.getType())) {
                        redisKey = String.format("%s-%s-%s", visualDevId, swapDataVo.getVModel(), OnlineDataTypeEnum.STATIC.getType());
                        if (!redisUtil.exists(redisKey)) {
                            if (swapDataVo.getOptions() != null) {
                                options = JsonUtil.createJsonToListMap(swapDataVo.getOptions());
                                String Children = swapDataVo.getProps().getChildren();
                                JSONArray data = JsonUtil.createListToJsonArray(options);
                                getOptions(label, value, Children, data, options);
                            } else {
                                options = JsonUtil.createJsonToListMap(swapDataVo.getOptions());
                            }
                            options.stream().forEach(o -> {
                                dataInterfaceMap.put(String.valueOf(o.get(value)), String.valueOf(o.get(label)));
                            });
                            String staticData = JsonUtil.createObjectToString(dataInterfaceMap);
                            redisUtil.insert(redisKey, staticData, 60 * 5);
                            if (!localCache.containsKey(redisKey)) {
                                localCache.put(redisKey, dataInterfaceMap);
                            }
                        } else {
                            if (!localCache.containsKey(redisKey)) {
                                String staticDataString = redisUtil.getString(redisKey).toString();
                                localCache.put(redisKey, JsonUtil.stringToMap(staticDataString));
                            }
                        }
                    }
                    //远端数据
                    if (dataType.equals(OnlineDataTypeEnum.DYNAMIC.getType())) {
                        redisKey = String.format("%s-%s-%s", dsName, OnlineDataTypeEnum.DYNAMIC.getType(), swapDataVo.getConfig().getPropsUrl());
                        String redisKey2 = String.format("%s-%s-%s-%s-%s-%s", dsName, OnlineDataTypeEnum.DYNAMIC.getType(), swapDataVo.getConfig().getPropsUrl(), label, value, children);
                        if (!redisUtil.exists(redisKey2)) {
                            ServiceResult data = null;
                            if (!redisUtil.exists(redisKey)) {
                                data = dataInterFaceApi.infoToId(swapDataVo.getConfig().getPropsUrl(),null,null);
                                //缓存接口全部数据
                                redisUtil.insert(redisKey, JSONObject.toJSONString(data), 60 * 5);
                            } else {
                                data = JSONObject.parseObject(String.valueOf(redisUtil.getString(redisKey)), ServiceResult.class);
                            }
                            if (!localCache.containsKey(redisKey)) {
                                localCache.put(redisKey, data);
                            }
                            if (data != null && data.getData() != null) {
                                List<Map<String, Object>> dataList = new ArrayList<>();
                                if (data.getData() instanceof DataInterfaceActionVo) {
                                    DataInterfaceActionVo actionVo = (DataInterfaceActionVo) data.getData();
                                    if (actionVo.getData() instanceof List) {
                                        dataList = (List<Map<String, Object>>) actionVo.getData();
                                    }
                                } else if (data.getData() instanceof List) {
                                    dataList = (List<Map<String, Object>>) data.getData();
                                }
                                JSONArray dataAll = JsonUtil.createListToJsonArray(dataList);
                                treeToList(label, value, children, dataAll, options);
                                options.stream().forEach(o -> {
                                    dataInterfaceMap.put(String.valueOf(o.get(value)), String.valueOf(o.get(label)));
                                });

                                //缓存接口根据特定字段转换后的全部数据
                                String dynamicData = JsonUtil.createObjectToString(dataInterfaceMap);
                                redisUtil.insert(redisKey2, dynamicData, 60 * 5);
                                localCache.put(redisKey2, dataInterfaceMap);
                            }
                        } else {
                            if (!localCache.containsKey(redisKey)) {
                                localCache.put(redisKey, JSONObject.parseObject(String.valueOf(redisUtil.getString(redisKey)), ServiceResult.class));
                            }
                            if (!localCache.containsKey(redisKey2)) {
                                //转成map格式
                                String dynamicString = redisUtil.getString(redisKey2).toString();
                                localCache.put(redisKey2, JsonUtil.stringToMap(dynamicString));
                            }
                        }
                    }
                    //数据字典
                    if (dataType.equals(OnlineDataTypeEnum.DICTIONARY.getType())) {
                        redisKey = String.format("%s-%s-%s", dsName, OnlineDataTypeEnum.DICTIONARY.getType(), swapDataVo.getConfig().getDictionaryType());
                        if (!redisUtil.exists(redisKey)) {
                            List<DictionaryDataEntity> list = dictionaryDataService.getDicList(swapDataVo.getConfig().getDictionaryType());
                            options = list.stream().map(dic -> {
                                Map<String, Object> dictionaryMap = new HashMap<>(16);
                                dictionaryMap.put("id", dic.getId());
                                dictionaryMap.put("enCode", dic.getEnCode());
                                dictionaryMap.put("fullName", dic.getFullName());
                                return dictionaryMap;
                            }).collect(Collectors.toList());

                            String dictionaryData = JsonUtil.createObjectToString(options);
                            redisUtil.insert(redisKey, dictionaryData, 60 * 5);
                            localCache.put(redisKey, options);
                        } else {
                            if (!localCache.containsKey(redisKey)) {
                                String dictionaryStringData = redisUtil.getString(redisKey).toString();
                                localCache.put(redisKey, JsonUtil.createJsonToListMap(dictionaryStringData));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("在线开发转换数据异常:" + e.getMessage());
            e.printStackTrace();
        }


    }


}
