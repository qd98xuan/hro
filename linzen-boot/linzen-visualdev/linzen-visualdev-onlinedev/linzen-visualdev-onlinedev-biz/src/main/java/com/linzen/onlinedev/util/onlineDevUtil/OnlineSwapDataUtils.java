package com.linzen.onlinedev.util.onlineDevUtil;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.base.entity.*;
import com.linzen.base.model.ColumnDataModel;
import com.linzen.base.model.VisualDevJsonModel;
import com.linzen.base.model.datainterface.DataInterfaceModel;
import com.linzen.base.model.datainterface.DataInterfacePage;
import com.linzen.base.service.*;
import com.linzen.constant.LinzenConst;
import com.linzen.constant.PermissionConst;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.util.DynamicDataSourceUtil;
import com.linzen.engine.entity.FlowTaskEntity;
import com.linzen.engine.service.FlowTaskService;
import com.linzen.exception.WorkFlowException;
import com.linzen.mapper.FlowFormDataMapper;
import com.linzen.model.flow.DataModel;
import com.linzen.model.visualJson.*;
import com.linzen.model.visualJson.analysis.FormModel;
import com.linzen.model.visualJson.config.ConfigModel;
import com.linzen.model.visualJson.config.RegListModel;
import com.linzen.onlinedev.model.OnlineDevData;
import com.linzen.onlinedev.model.OnlineDevEnum.CacheKeyEnum;
import com.linzen.onlinedev.model.OnlineDevEnum.MultipleControlEnum;
import com.linzen.onlinedev.model.OnlineDevEnum.OnlineDataTypeEnum;
import com.linzen.onlinedev.model.OnlineDevListModel.InterefaceParamModel;
import com.linzen.onlinedev.model.OnlineImport.ExcelImportModel;
import com.linzen.onlinedev.model.OnlineImport.ImportFormCheckUniqueModel;
import com.linzen.onlinedev.model.OnlineInfoModel;
import com.linzen.onlinedev.model.PaginationModel;
import com.linzen.onlinedev.model.VisualdevModelDataInfoVO;
import com.linzen.onlinedev.service.VisualDevInfoService;
import com.linzen.onlinedev.service.VisualdevModelDataService;
import com.linzen.permission.entity.*;
import com.linzen.permission.model.organize.OrganizeConditionModel;
import com.linzen.permission.model.organize.OrganizeModel;
import com.linzen.permission.service.*;
import com.linzen.util.*;
import com.linzen.util.visiual.ProjectKeyConsts;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据解析
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Component
public class OnlineSwapDataUtils {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private UserService userService;
    @Autowired
    private PositionService positionApi;
    @Autowired
    private ProvinceService areaApi;
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
    private RoleService roleApi;
    @Autowired
    private GroupService groupApi;
    @Autowired
    private DbLinkService dataSourceApi;
    @Autowired
    private OnlineDevInfoUtils onlineDevInfoUtils;
    @Autowired
    private BillRuleService billRuleApi;
    @Autowired
    private FlowFormDataMapper flowFormDataMapper;
    @Autowired
    private UserRelationService userRelationApi;
    @Autowired
    private OrganizeRelationService organizeRelationApi;
    @Autowired
    private FlowFormDataUtil flowDataUtil;
    @Autowired
    private FlowTaskService flowTaskApi;
    @Autowired
    private OnlineExecutor executor;
    @Autowired
    private ProvinceService provinceService;

    public final static long DEFAULT_CACHE_TIME = 60 * 5;

    //后期将是否缓存转到前端控件配置
    //缓存系统权限数据, 组织、岗位、分组、角色、用户
    public final static boolean NEEDCACHE_SYS = true;
    //缓存远端数据, 静态、字典、接口、弹窗选择
    public final static boolean NEEDCACHE_REMOTE = true;
    //缓存关联数据, 关联表单
    public final static boolean NEEDCACHE_RELATION = true;

    public List<Map<String, Object>> getSwapList(List<Map<String, Object>> list, List<FieLdsModel> fieLdsModelList, String visualDevId, Boolean inlineEdit, List<FormModel> codeList) {
        if (list.isEmpty()) {
            return list;
        }
        return getSwapList(list, fieLdsModelList, visualDevId, inlineEdit, codeList, null, true, null);
    }

    public List<Map<String, Object>> getSwapInfo(List<Map<String, Object>> list, List<FieLdsModel> fieLdsModelList, String visualDevId, Boolean inlineEdit, List<FormModel> codeList, Map<String, Object> mainAndMast) {
        if (list.isEmpty()) {
            return list;
        }
        return getSwapList(list, fieLdsModelList, visualDevId, inlineEdit, codeList, null, false, mainAndMast);
    }

    public List<Map<String, Object>> getSwapList(List<Map<String, Object>> list, List<FieLdsModel> fieLdsModelList, String visualDevId, Boolean inlineEdit,
                                                 List<FormModel> codeList, Map<String, Object> localCacheParent, boolean isList, Map<String, Object> mainAndMast) {
        try {
            DynamicDataSourceUtil.switchToDataSource(null);
            if (list.isEmpty()) {
                return list;
            }
            //主表的缓存数据继续使用, 不重新初始化
            Map<String, Object> localCache = Optional.ofNullable(localCacheParent).orElse(new HashMap<>());
            //初始化系统缓存
//            sysNeedSwapData(fieLdsModelList, visualDevId, localCache);
            //初始化系统缓存-多线程
            executor.executorRedis(localCache, fieLdsModelList, visualDevId, inlineEdit, list, mainAndMast);
            //redis key
            String dsName = Optional.ofNullable(TenantHolder.getDatasourceId()).orElse("");
            writeRedisAndList(localCache, fieLdsModelList, dsName, visualDevId, inlineEdit, list, codeList, isList, mainAndMast);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DynamicDataSourceUtil.clearSwitchDataSource();
        }
        return list;
    }

    private List<Map<String, Object>> writeRedisAndList(Map<String, Object> localCache, List<FieLdsModel> fieLdsModelList, String dsName, String visualDevId, Boolean inlineEdit,
                                                        List<Map<String, Object>> list, List<FormModel> codeList, boolean isList, Map<String, Object> mainAndMast) {

        Map<String, Object> userMap = (Map<String, Object>) localCache.get("__user_map");
        Map<String, Object> orgMap = (Map<String, Object>) localCache.get("__org_map");
        Map<String, Object> posMap = (Map<String, Object>) localCache.get("__pos_map");
        Map<String, Object> roleMap = (Map<String, Object>) localCache.get("__role_map");
        Map<String, Object> groupMap = (Map<String, Object>) localCache.get("__group_map");

        List<String> arrProjectKey = new ArrayList<>();
        arrProjectKey.add(ProjectKeyConsts.UPLOADIMG);
        arrProjectKey.add(ProjectKeyConsts.UPLOADFZ);

        for (int x = 0; x < list.size(); x++) {
            Map<String, Object> dataMap = list.get(x);
            if (dataMap == null) {
                dataMap = new HashMap<>();
                list.set(x, dataMap);
            }
            Map<String, Object> dataCopyMap = new HashMap<>(dataMap);
            for (FieLdsModel fieLdsModel : fieLdsModelList) {
                String projectKey = fieLdsModel.getConfig().getProjectKey();
                if (StringUtil.isEmpty(fieLdsModel.getVModel())) {
                    continue;
                }
                String swapVModel = fieLdsModel.getVModel();
                String vModel = inlineEdit && isList ? fieLdsModel.getVModel() + "_name" : fieLdsModel.getVModel();
                String dataType = fieLdsModel.getConfig().getDataType();
                Boolean isMultiple = Objects.nonNull(fieLdsModel.getMultiple()) ? fieLdsModel.getMultiple() : false;

                try {
                    Map<String, Map<String, Object>> dataDetailMap = new HashMap<>();
//                  关联表单获取原字段数据
                    this.relationGetLinzenId(dataMap, projectKey, dataMap.get(swapVModel), swapVModel);
                    if (StringUtil.isEmpty(String.valueOf(dataMap.get(swapVModel))) || String.valueOf(dataMap.get(swapVModel)).equals("[]")
                            || String.valueOf(dataMap.get(swapVModel)).equals("null")) {
                        if (projectKey.equals(ProjectKeyConsts.CHILD_TABLE)) {
                            dataMap.put(vModel, new ArrayList<>());
                        } else if (arrProjectKey.contains(projectKey)) {
                            dataMap.put(swapVModel, new ArrayList<>());
                        } else {
//							dataCopyMap.putAll(dataMap);
                            if (inlineEdit) {
                                dataMap.put(swapVModel, null);
                            }
                            dataMap.put(vModel, null);
                        }
                        continue;
                    } else {
                        //是否联动
                        boolean DynamicNeedCache;
                        String redisKey;
                        String separator = fieLdsModel.getSeparator();
                        switch (projectKey) {
                            case ProjectKeyConsts.CALCULATE:
                            case ProjectKeyConsts.NUM_INPUT:
                                Object decimalValue = dataCopyMap.get(fieLdsModel.getVModel());
                                Integer precision = fieLdsModel.getPrecision();
                                if (decimalValue instanceof BigDecimal) {
                                    BigDecimal bd = (BigDecimal) decimalValue;
                                    String value = bd.toPlainString();
                                    if (precision != null && precision > 0) {
                                        String formatZ = "000000000000000";
                                        String format = formatZ.substring(0, precision);
                                        DecimalFormat decimalFormat = new DecimalFormat("0." + format);
                                        value = decimalFormat.format(bd);
                                    } else {
                                        value = String.valueOf(bd.stripTrailingZeros().toPlainString());
                                    }
                                    dataMap.put(vModel, value);
                                } else {
                                    dataMap.put(vModel, decimalValue);
                                }
                                break;
                            //公司组件
                            case ProjectKeyConsts.COMSELECT:
                                //部门组件
                            case ProjectKeyConsts.DEPSELECT:
                                //所属部门
                            case ProjectKeyConsts.CURRDEPT:
                                dataMap.put(vModel, OnlinePublicUtils.getDataInMethod(orgMap, dataMap.get(swapVModel), isMultiple));
                                break;
                            //所属组织
                            case ProjectKeyConsts.CURRORGANIZE:
                                //多级组织
                                String orgIds = String.valueOf(dataMap.get(swapVModel));
                                String orgId = "";
                                String orgName = "";
                                try {
                                    List<String> jsonToList = JsonUtil.createJsonToList(orgIds, String.class);
                                    orgId = jsonToList.get(jsonToList.size() - 1);
                                } catch (Exception e) {
                                    orgId = orgIds;
                                }
                                SysOrganizeEntity organizeEntity = StringUtil.isNotEmpty(orgId) ? organizeApi.getInfo(orgId) : null;
                                if ("all".equals(fieLdsModel.getShowLevel())) {
                                    if (organizeEntity != null) {
                                        List<SysOrganizeEntity> organizeList = new ArrayList<>();
                                        organizeApi.getOrganizeId(orgId, organizeList);
                                        Collections.reverse(organizeList);
                                        orgName = organizeList.stream().map(SysOrganizeEntity::getFullName).collect(Collectors.joining("/"));
                                    }
                                } else {
                                    if (organizeEntity != null) {
                                        orgName = organizeEntity.getFullName();
                                    } else {
                                        orgName = " ";
                                    }
                                }
                                dataMap.put(vModel, orgName);
                                break;

                            //岗位组件
                            case ProjectKeyConsts.POSSELECT:
                                //所属岗位
                            case ProjectKeyConsts.CURRPOSITION:
                                String posData = OnlinePublicUtils.getDataInMethod(posMap, dataMap.get(swapVModel), isMultiple);
                                if (ObjectUtil.isEmpty(dataMap.get(swapVModel))) {
                                    dataMap.put(vModel, " ");
                                } else {
                                    dataMap.put(vModel, posData);
                                }
                                break;

                            //用户组件
                            case ProjectKeyConsts.USERSELECT:
                                //创建用户
                            case ProjectKeyConsts.CREATEUSER:
                                //修改用户
                            case ProjectKeyConsts.MODIFYUSER:
                                String userData = OnlinePublicUtils.getDataInMethod(userMap, dataMap.get(swapVModel), isMultiple);
                                dataMap.put(vModel, userData);
                                break;
                            case ProjectKeyConsts.CUSTOMUSERSELECT:
                                List<String> dataNoSwapInMethod = OnlinePublicUtils.getDataNoSwapInMethod(dataMap.get(swapVModel));
                                StringJoiner valueJoin = new StringJoiner(",");
                                for (String data : dataNoSwapInMethod) {
                                    String id = data.contains("--") ? data.substring(0, data.lastIndexOf("--")) : data;
                                    String type = data.contains("--") ? data.substring(data.lastIndexOf("--") + 2) : "";
                                    Map<String, Object> cacheMap;
                                    switch (type) {
                                        case "role":
                                            cacheMap = roleMap;
                                            break;
                                        case "position":
                                            cacheMap = posMap;
                                            break;
                                        case "company":
                                        case "department":
                                            cacheMap = orgMap;
                                            break;
                                        case "group":
                                            cacheMap = groupMap;
                                            break;
                                        case "user":
                                        default:
                                            cacheMap = userMap;
                                            break;
                                    }
                                    valueJoin.add(Optional.ofNullable(cacheMap.get(id)).orElse("").toString());
                                }
                                dataMap.put(vModel, valueJoin.toString());
                                break;
                            //角色选择
                            case ProjectKeyConsts.ROLESELECT:
                                String roleData = OnlinePublicUtils.getDataInMethod(roleMap, dataMap.get(swapVModel), isMultiple);
                                dataMap.put(vModel, roleData);
                                break;

                            case ProjectKeyConsts.GROUPSELECT:
                                String groupData = OnlinePublicUtils.getDataInMethod(groupMap, dataMap.get(swapVModel), isMultiple);
                                dataMap.put(vModel, groupData);
                                break;

                            //省市区联动
                            case ProjectKeyConsts.ADDRESS:
                                String addressValue = String.valueOf(dataMap.get(swapVModel));
                                if (OnlinePublicUtils.getMultiple(addressValue, MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
                                    String[][] data = JsonUtil.createJsonToBean(addressValue, String[][].class);
                                    List<String> proDataS = Arrays.stream(data)
                                            .flatMap(Arrays::stream)
                                            .collect(Collectors.toList());
                                    Map<String, String> provinceNames = provinceService.getProList(proDataS).stream().collect(Collectors.toMap(
                                            ProvinceEntity::getId, ProvinceEntity::getFullName
                                            , (k1, k2) -> k2
                                            , () -> new LinkedHashMap<>(proDataS.size(), 1.0F)
                                    ));
                                    List<String> addList = new ArrayList<>();
                                    for (String[] AddressData : data) {
                                        List<String> adList = new ArrayList<>();
                                        for (int i = 0; i < AddressData.length; i++) {
                                            String addressDatum = AddressData[i];
                                            String value = provinceNames.getOrDefault(addressDatum, "");
                                            adList.add(value);
                                        }
                                        addList.add(String.join("/", adList));
                                    }
                                    dataMap.put(vModel, String.join(";", addList));
                                } else if (OnlinePublicUtils.getMultiple(addressValue, MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
                                    List<String> proDataS = JsonUtil.createJsonToList(String.valueOf(dataMap.get(swapVModel)), String.class);
                                    Map<String, String> provinceNames = provinceService.getProList(proDataS).stream().collect(Collectors.toMap(
                                            ProvinceEntity::getId, ProvinceEntity::getFullName
                                            , (k1, k2) -> k2
                                            , () -> new LinkedHashMap<>(proDataS.size(), 1.0F)
                                    ));
                                    List<String> adList = new ArrayList<>();
                                    for (int i = 0; i < proDataS.size(); i++) {
                                        String addressDatum = proDataS.get(i);
                                        String value = provinceNames.getOrDefault(addressDatum, "");
                                        adList.add(value);
                                    }
                                    dataMap.put(vModel, String.join("/", adList));
                                }
                                break;
                            //开关
                            case ProjectKeyConsts.SWITCH:
                                String switchValue = String.valueOf(dataMap.get(swapVModel)).equals("1") ? fieLdsModel.getActiveTxt() : fieLdsModel.getInactiveTxt();
                                dataMap.put(vModel, switchValue);
                                break;

                            case ProjectKeyConsts.CASCADER:
                            case ProjectKeyConsts.RADIO:
                            case ProjectKeyConsts.CHECKBOX:
                            case ProjectKeyConsts.SELECT:
                            case ProjectKeyConsts.TREESELECT:
                                if (StringUtil.isEmpty(separator)) {
                                    separator = "/";
                                }
                                if (ProjectKeyConsts.CHECKBOX.equals(projectKey)) {
                                    isMultiple = true;
                                }
                                DynamicNeedCache = fieLdsModel.getConfig().getTemplateJson().size() == 0;
                                String interfacelabel = fieLdsModel.getProps().getLabel() != null ? fieLdsModel.getProps().getLabel() : "";
                                String interfaceValue = fieLdsModel.getProps().getValue() != null ? fieLdsModel.getProps().getValue() : "";
                                String interfaceChildren = fieLdsModel.getProps().getChildren() != null ? fieLdsModel.getProps().getChildren() : "";
                                if (DynamicNeedCache) {
                                    if (OnlineDataTypeEnum.STATIC.getType().equals(dataType)) {
                                        redisKey = String.format("%s-%s-%s", visualDevId, fieLdsModel.getConfig().getRelationTable() + fieLdsModel.getVModel(), OnlineDataTypeEnum.STATIC.getType());
                                    } else if (dataType.equals(OnlineDataTypeEnum.DYNAMIC.getType())) {
                                        redisKey = String.format("%s-%s-%s-%s-%s-%s", dsName, OnlineDataTypeEnum.DYNAMIC.getType(), fieLdsModel.getConfig().getPropsUrl(), interfaceValue, interfacelabel, interfaceChildren);
                                    } else {
                                        redisKey = String.format("%s-%s-%s", dsName, OnlineDataTypeEnum.DICTIONARY.getType(), fieLdsModel.getConfig().getDictionaryType());
                                    }
                                    Map<String, Object> cascaderMap;
                                    if (dataType.equals(OnlineDataTypeEnum.DICTIONARY.getType())) {
                                        List<Map<String, Object>> checkBoxList = (List<Map<String, Object>>) localCache.get(redisKey);
                                        cascaderMap = OnlinePublicUtils.getDataMap(checkBoxList, fieLdsModel);
                                    } else {
                                        cascaderMap = (Map<String, Object>) localCache.get(redisKey);
                                    }
                                    dataMap.put(vModel, FormPublicUtils.getDataConversion(cascaderMap, dataMap.get(swapVModel), isMultiple, separator));

                                } else {
                                    List<TemplateJsonModel> templateJsonModels = JsonUtil.createJsonToList(fieLdsModel.getConfig().getTemplateJson(), TemplateJsonModel.class);
                                    Map<String, String> paramMap = new HashMap<>();
                                    for (TemplateJsonModel templateJsonModel : templateJsonModels) {
                                        String relationField = templateJsonModel.getRelationField();
                                        String Field = templateJsonModel.getField();
                                        String obj = inlineEdit ? "" : Optional.ofNullable(dataCopyMap.get(relationField)).orElse("").toString();
                                        if (StringUtil.isEmpty(relationField)) {
                                            if ("@formId".equals(templateJsonModel.getDefaultValue())) {
                                                paramMap.put(Field, String.valueOf(dataCopyMap.get("id")));
                                            } else {
                                                paramMap.put(Field, templateJsonModel.getDefaultValue());
                                            }
                                            continue;
                                        }
                                        if (relationField.toLowerCase().contains(ProjectKeyConsts.CHILD_TABLE_PREFIX)) {
                                            String childField = relationField.split("-")[1];
                                            obj = Optional.ofNullable(dataCopyMap.get(childField)).orElse("").toString();
                                        } else if (mainAndMast != null) {
                                            obj = Optional.ofNullable(mainAndMast.get(relationField)).orElse("").toString();
                                        }
                                        paramMap.put(Field, obj);
                                    }
                                    List<Map<String, Object>> dataList = null;
                                    List<Map<String, Object>> options = new ArrayList<>();
                                    Map<String, Object> dataInterfaceMap = new HashMap();
                                    //缓存Key 租户-远端数据-id-base64({params})
                                    redisKey = String.format("%s-%s-%s-%s", dsName, OnlineDataTypeEnum.DYNAMIC.getType(), fieLdsModel.getConfig().getPropsUrl(), Base64.getEncoder().encodeToString(JsonUtil.createObjectToString(paramMap).getBytes(StandardCharsets.UTF_8)));
                                    if (localCache.containsKey(redisKey) || redisUtil.exists(redisKey)) {
                                        if (localCache.containsKey(redisKey)) {
                                            dataList = (List<Map<String, Object>>) localCache.get(redisKey);
                                        } else {
                                            List<Object> tmpList = redisUtil.get(redisKey, 0, -1);
                                            List<Map<String, Object>> tmpMapList = new ArrayList<>();
                                            tmpList.forEach(item -> {
                                                tmpMapList.add(JsonUtil.entityToMap(item));
                                            });
                                            dataList = tmpMapList;
                                            localCache.put(redisKey, dataList);
                                        }
                                    } else {
                                        ServiceResult data = dataInterFaceApi.infoToId(fieLdsModel.getConfig().getPropsUrl(), null, paramMap);
                                        if (data != null && data.getData() != null) {
                                            if (data.getData() instanceof List) {
                                                dataList = (List<Map<String, Object>>) data.getData();
                                                if (NEEDCACHE_REMOTE && CollectionUtils.isNotEmpty(dataList) && !ProjectKeyConsts.TREESELECT.equals(projectKey)) {
                                                    redisUtil.insert(redisKey, dataList, DEFAULT_CACHE_TIME);
                                                }
                                                localCache.put(redisKey, dataList);
                                            }
                                        }
                                    }
                                    if (dataList != null) {
                                        JSONArray dataAll = JsonUtil.createListToJsonArray(dataList);
                                        treeToList(interfacelabel, interfaceValue, interfaceChildren, dataAll, options);
                                        options.stream().forEach(o -> {
                                            dataInterfaceMap.put(String.valueOf(o.get(interfaceValue)), String.valueOf(o.get(interfacelabel)));
                                        });
                                    }
                                    dataMap.put(vModel, FormPublicUtils.getDataConversion(dataInterfaceMap, dataMap.get(swapVModel), isMultiple, separator));
                                }
                                break;
                            case ProjectKeyConsts.RELATIONFORM:
                                //取关联表单数据 按绑定功能加字段区分数据
                                redisKey = String.format("%s-%s-%s-%s-%s", dsName, ProjectKeyConsts.RELATIONFORM, fieLdsModel.getModelId(), fieLdsModel.getRelationField(), dataMap.get(vModel));
                                VisualdevModelDataInfoVO infoVO = null;
                                if (localCache.containsKey(redisKey) || redisUtil.exists(redisKey)) {
                                    infoVO = new VisualdevModelDataInfoVO();
                                    if (localCache.containsKey(redisKey)) {
                                        infoVO.setData(localCache.get(redisKey).toString());
                                    } else {
                                        infoVO.setData(redisUtil.getString(redisKey).toString());
                                        localCache.put(redisKey, infoVO.getData());
                                    }

                                } else {
                                    VisualdevEntity entity = visualdevService.getInfo(fieLdsModel.getModelId());
                                    String keyId = String.valueOf(dataMap.get(swapVModel));
                                    if (!StringUtil.isEmpty(entity.getVisualTables()) && !OnlineDevData.TABLE_CONST.equals(entity.getVisualTables())) {
                                        infoVO = visualDevInfoService.getDetailsDataInfo(keyId, entity, OnlineInfoModel.builder().needRlationFiled(false).build());
                                    } else {
                                        infoVO = visualdevModelDataService.infoDataChange(keyId, entity);
                                    }
                                    String data = infoVO == null ? StringUtil.EMPTY : infoVO.getData();
                                    if (NEEDCACHE_RELATION) {
                                        redisUtil.insert(redisKey, data, DEFAULT_CACHE_TIME);
                                    }
                                    localCache.put(redisKey, data);
                                }
                                if (infoVO != null && StringUtil.isNotEmpty(infoVO.getData())) {
                                    Map<String, Object> formDataMap = JsonUtil.stringToMap(infoVO.getData());
                                    String relationField = fieLdsModel.getRelationField();
                                    if (formDataMap != null && formDataMap.size() > 0) {
                                        dataMap.put(fieLdsModel.getVModel() + "_id", dataMap.get(swapVModel));
                                        dataMap.put(vModel, formDataMap.get(relationField));
                                        dataDetailMap.put(vModel, formDataMap);
                                    }
                                }
                                break;
                            case ProjectKeyConsts.POPUPSELECT:
                            case ProjectKeyConsts.POPUPTABLESELECT:
                                //是否联动
                                List<TemplateJsonModel> templateJsonModels = JsonUtil.createJsonToList(fieLdsModel.getTemplateJson(), TemplateJsonModel.class);
                                //DynamicNeedCache = templateJsonModels.size() == 0;
                                List<Map<String, Object>> mapList;
                                Map<String, Object> popMaps = new HashMap<>();
                                String value = String.valueOf(dataMap.get(swapVModel));

                                List<DataInterfaceModel> listParam = new ArrayList<>();
                                for (TemplateJsonModel templateJsonModel : templateJsonModels) {
                                    String relationField = templateJsonModel.getRelationField();
                                    DataInterfaceModel dataInterfaceModel = JsonUtil.createJsonToBean(templateJsonModel, DataInterfaceModel.class);
                                    if (StringUtil.isEmpty(relationField)) {
                                        if ("@formId".equals(templateJsonModel.getDefaultValue())) {
                                            dataInterfaceModel.setDefaultValue(String.valueOf(dataCopyMap.get("id")));
                                        }
                                        listParam.add(dataInterfaceModel);
                                        continue;
                                    }
                                    String obj = inlineEdit ? "" : Optional.ofNullable(dataCopyMap.get(relationField)).orElse("").toString();
                                    if (relationField.toLowerCase().contains(ProjectKeyConsts.CHILD_TABLE_PREFIX)) {
                                        String childField = relationField.split("-")[1];
                                        obj = Optional.ofNullable(dataCopyMap.get(childField)).orElse("").toString();
                                    } else if (mainAndMast != null) {
                                        obj = Optional.ofNullable(mainAndMast.get(relationField)).orElse("").toString();
                                    }
                                    dataInterfaceModel.setDefaultValue(obj);
                                    listParam.add(dataInterfaceModel);
                                }
                                DataInterfacePage dataInterfacePage = new DataInterfacePage();
                                dataInterfacePage.setParamList(listParam);
                                dataInterfacePage.setInterfaceId(fieLdsModel.getInterfaceId());
                                List<String> ids = new ArrayList<>();
                                if (value.startsWith("[")) {
                                    ids = JsonUtil.createJsonToList(value, String.class);
                                } else {
                                    ids.add(value);
                                }
                                dataInterfacePage.setIds(ids);
                                //缓存Key 租户-远端数据-base64({id, params, ids})
                                redisKey = String.format("%s-%s-%s-%s", dsName, OnlineDataTypeEnum.DYNAMIC.getType(), fieLdsModel.getInterfaceId(), Base64.getEncoder().encodeToString(JsonUtil.createObjectToString(dataInterfacePage).getBytes(StandardCharsets.UTF_8)));
                                if (localCache.containsKey(redisKey) || redisUtil.exists(redisKey)) {
                                    if (localCache.containsKey(redisKey)) {
                                        mapList = (List<Map<String, Object>>) localCache.get(redisKey);
                                    } else {
                                        List<Object> tmpList = redisUtil.get(redisKey, 0, -1);
                                        List<Map<String, Object>> tmpMapList = new ArrayList<>();
                                        tmpList.forEach(item -> {
                                            tmpMapList.add(JsonUtil.entityToMap(item));
                                        });
                                        mapList = tmpMapList;
                                        localCache.put(redisKey, mapList);
                                    }
                                } else {
                                    dataInterfacePage.setPropsValue(fieLdsModel.getPropsValue());
                                    dataInterfacePage.setRelationField(fieLdsModel.getRelationField());
                                    mapList = dataInterFaceApi.infoToInfo(fieLdsModel.getInterfaceId(), dataInterfacePage);
                                    if (NEEDCACHE_REMOTE) {
                                        redisUtil.insert(redisKey, mapList, DEFAULT_CACHE_TIME);
                                    }
                                    localCache.put(redisKey, mapList);
                                }

                                StringJoiner stringJoiner = new StringJoiner(",");
                                List<String> popList = new ArrayList<>();
                                if (value.startsWith("[")) {
                                    popList = JsonUtil.createJsonToList(value, String.class);
                                } else {
                                    popList.add(value);
                                }
                                for (String va : popList) {
                                    if (popMaps.size() > 0) {
                                        stringJoiner.add(String.valueOf(popMaps.get(va)));
                                    } else {
                                        Map<String, Object> PopMap = mapList.stream().filter(map -> Objects.equals(map.get(fieLdsModel.getPropsValue()), va)).findFirst().orElse(new HashMap<>());
                                        if (PopMap.size() > 0) {
                                            dataMap.put(vModel + "_id", dataMap.get(swapVModel));
                                            stringJoiner.add(String.valueOf(PopMap.get(fieLdsModel.getRelationField())));
                                            dataDetailMap.put(vModel, PopMap);
                                        }
                                    }
                                }
                                dataMap.put(vModel, String.valueOf(stringJoiner));
                                break;
                            case ProjectKeyConsts.MODIFYTIME:
                            case ProjectKeyConsts.CREATETIME:
//                            case ProjectKeyConsts.TIME:
                            case ProjectKeyConsts.DATE:
                                //判断是否为时间戳格式
                                Object dateObj = dataMap.get(swapVModel);
                                LocalDateTime dateTime = LocalDateTimeUtil.of(new Date(DateTimeFormatConstant.getDateObjToLong(dateObj)));
                                String format = DateTimeFormatConstant.getFormat(fieLdsModel.getFormat());

//                                if (isList && (ProjectKeyConsts.MODIFYTIME.equals(projectKey) || ProjectKeyConsts.CREATETIME.equals(projectKey))) {
//                                    format = DateTimeFormatConstant.YEAR_MOnTH_DHM;
//                                }
                                if (StringUtil.isEmpty(format)) {
                                    format = DateTimeFormatConstant.YEAR_MOnTH_DHMS;
                                }
                                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
                                String date = dateTimeFormatter.format(dateTime);
                                dataMap.put(vModel, date);
                                if (ProjectKeyConsts.MODIFYTIME.equals(projectKey) || ProjectKeyConsts.CREATETIME.equals(projectKey)) {
                                    dataMap.put(fieLdsModel.getVModel(), date);
                                }
                                break;
                            case ProjectKeyConsts.RATE:
                            case ProjectKeyConsts.SLIDER:
                                //滑块评分不需要补零转浮点型
                                Double ratevalue = new Double(0);
                                if (dataMap.get(swapVModel) != null) {
                                    ratevalue = new Double(dataMap.get(swapVModel).toString());
                                }
                                dataMap.put(vModel, ratevalue);
                                break;
                            case ProjectKeyConsts.UPLOADFZ:
                            case ProjectKeyConsts.UPLOADIMG:
                                List<Map<String, Object>> fileList = JsonUtil.createJsonToListMap(String.valueOf(dataMap.get(swapVModel)));
                                dataMap.put(vModel, fileList);
                                break;
                            case ProjectKeyConsts.LOCATION:
                                //定位-列表取全名。
                                if (isList) {
                                    Map omap = JsonUtil.stringToMap(String.valueOf(dataMap.get(swapVModel)));
                                    dataMap.put(vModel, omap.get("fullAddress") != null ? omap.get("fullAddress") : "");
                                }
                                break;
                            case ProjectKeyConsts.CHILD_TABLE:
                                List<FieLdsModel> childrens = fieLdsModel.getConfig().getChildren();
                                List<Map<String, Object>> childList = (List<Map<String, Object>>) dataMap.get(fieLdsModel.getVModel());
                                List<Map<String, Object>> swapList = getSwapList(childList, childrens, visualDevId, inlineEdit, codeList, localCache, isList, dataCopyMap);
                                dataMap.put(fieLdsModel.getVModel(), swapList);
                                break;
                            default:
                                dataMap.put(vModel, dataMap.get(swapVModel));
                                break;
                        }
                    }
                    //二维码 条形码最后处理
                    swapCodeDataInfo(codeList, dataMap, dataCopyMap);
                    //关联选择属性
                    if (dataDetailMap.size() > 0) {
                        getDataAttr(fieLdsModelList, dataMap, dataDetailMap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("在线开发转换数据错误:" + e.getMessage());
                }
            }
        }
        if (inlineEdit && isList) {
            for (Map<String, Object> map : list) {
                //行内编辑过滤子表
                fieLdsModelList = fieLdsModelList.stream().filter(s -> !s.getVModel().contains("tableField")).collect(Collectors.toList());
                onlineDevInfoUtils.getInitLineData(fieLdsModelList, map);
            }
        }
        return list;
    }

    /**
     * 保存需要转换的数据到redis(系统控件)
     *
     * @param fieLdsModelList
     * @param visualDevId
     * @param localCache
     */
    public void sysNeedSwapData(List<FieLdsModel> fieLdsModelList, String visualDevId, Map<String, Object> localCache) {
        //公共数据
        String dsName = Optional.ofNullable(TenantHolder.getDatasourceId()).orElse("");

        String redisKey;
        try {
            boolean needUser = false, needOrg = false, needPos = false, needRole = false, needGroup = false, needProvince = false;
            for (FieLdsModel fieLdsModel : fieLdsModelList) {
                String projectKey = fieLdsModel.getConfig().getProjectKey();
                String dataType = fieLdsModel.getConfig().getDataType();
                switch (projectKey) {
                    case ProjectKeyConsts.CHILD_TABLE:
                        List<FieLdsModel> children = fieLdsModel.getConfig().getChildren();
                        sysNeedSwapData(children, visualDevId, localCache);
                        break;
                    //用户组件
                    case ProjectKeyConsts.USERSELECT:
                        //创建用户
                    case ProjectKeyConsts.CREATEUSER:
                        //修改用户
                    case ProjectKeyConsts.MODIFYUSER:
                        needUser = true;
                        break;
                    //公司组件
                    case ProjectKeyConsts.COMSELECT:
                        //部门组件
                    case ProjectKeyConsts.DEPSELECT:
                        //所属部门
                    case ProjectKeyConsts.CURRDEPT:
                        //所属组织
                    case ProjectKeyConsts.CURRORGANIZE:
                        needOrg = true;
                        break;
                    //岗位组件
                    case ProjectKeyConsts.POSSELECT:
                        //所属岗位
                    case ProjectKeyConsts.CURRPOSITION:
                        needPos = true;
                        break;
                    //角色选择
                    case ProjectKeyConsts.ROLESELECT:
                        needRole = true;
                        break;
                    //分组选择
                    case ProjectKeyConsts.GROUPSELECT:
                        needGroup = true;
                        break;
                    //用户选择组件
                    case ProjectKeyConsts.CUSTOMUSERSELECT:
                        needUser = needOrg = needPos = needGroup = needRole = true;
                        break;
                    //省市区选择组件
                    case ProjectKeyConsts.ADDRESS:
                        needProvince = true;
                        break;
                    default:
                        break;
                }
                if (dataType != null) {
                    //数据接口的数据存放
                    String label = fieLdsModel.getProps().getLabel() != null ? fieLdsModel.getProps().getLabel() : "";
                    String value = fieLdsModel.getProps().getValue() != null ? fieLdsModel.getProps().getValue() : "";
                    String children = fieLdsModel.getProps().getChildren() != null ? fieLdsModel.getProps().getChildren() : "";
                    List<Map<String, Object>> options = new ArrayList<>();
                    if (fieLdsModel.getConfig().getProjectKey().equals(ProjectKeyConsts.POPUPSELECT) || fieLdsModel.getConfig().getProjectKey().equals(ProjectKeyConsts.POPUPTABLESELECT)) {
                        label = fieLdsModel.getRelationField();
                        value = fieLdsModel.getPropsValue();
                    }
                    Map<String, String> dataInterfaceMap = new HashMap<>(16);
                    String finalValue = value;
                    String finalLabel = label;
                    //静态数据
                    if (dataType.equals(OnlineDataTypeEnum.STATIC.getType())) {
                        redisKey = String.format("%s-%s-%s", visualDevId, fieLdsModel.getConfig().getRelationTable() + fieLdsModel.getVModel(), OnlineDataTypeEnum.STATIC.getType());
                        if (!localCache.containsKey(redisKey)) {
                            if (!redisUtil.exists(redisKey)) {
                                if (fieLdsModel.getOptions() != null) {
                                    options = JsonUtil.createJsonToListMap(fieLdsModel.getOptions());
                                    JSONArray data = JsonUtil.createListToJsonArray(options);
                                    getOptions(label, value, children, data, options);
                                } else {
                                    options = JsonUtil.createJsonToListMap(fieLdsModel.getOptions());
                                }

                                options.stream().forEach(o -> {
                                    dataInterfaceMap.put(String.valueOf(o.get(finalValue)), String.valueOf(o.get(finalLabel)));
                                });
                                if (NEEDCACHE_REMOTE) {
                                    redisUtil.insert(redisKey, dataInterfaceMap, DEFAULT_CACHE_TIME);
                                }
                                localCache.put(redisKey, dataInterfaceMap);
                            } else {
                                localCache.put(redisKey, redisUtil.getMap(redisKey));
                            }
                        }
                    }
                    //远端数据
                    if (dataType.equals(OnlineDataTypeEnum.DYNAMIC.getType())) {
                        //联动状态下不做缓存， 具体查数据时做缓存
                        boolean dynamicIsNeedCache = fieLdsModel.getConfig().getTemplateJson().size() == 0;
                        if (dynamicIsNeedCache) {
                            redisKey = String.format("%s-%s-%s-%s-%s-%s", dsName, OnlineDataTypeEnum.DYNAMIC.getType(), fieLdsModel.getConfig().getPropsUrl(), value, label, children);
                            if (!localCache.containsKey(redisKey)) {
                                if (!redisUtil.exists(redisKey)) {
                                    ServiceResult data = dataInterFaceApi.infoToId(fieLdsModel.getConfig().getPropsUrl(), null, null);
                                    if (data != null && data.getData() != null) {
                                        List<Map<String, Object>> dataList = new ArrayList<>();
                                        if (data.getData() instanceof List) {
                                            dataList = (List<Map<String, Object>>) data.getData();
                                        }
                                        JSONArray dataAll = JsonUtil.createListToJsonArray(dataList);
                                        treeToList(label, value, children, dataAll, options);
                                        options.stream().forEach(o -> {
                                            dataInterfaceMap.put(String.valueOf(o.get(finalValue)), String.valueOf(o.get(finalLabel)));
                                        });
                                        if (NEEDCACHE_REMOTE && CollectionUtils.isNotEmpty(dataList) && !ProjectKeyConsts.TREESELECT.equals(projectKey)) {
                                            redisUtil.insert(redisKey, dataInterfaceMap, DEFAULT_CACHE_TIME);
                                        }
                                        localCache.put(redisKey, dataInterfaceMap);
                                    }
                                } else {
                                    localCache.put(redisKey, redisUtil.getMap(redisKey));
                                }
                            }
                        }
                    }
                    //数据字典
                    if (dataType.equals(OnlineDataTypeEnum.DICTIONARY.getType())) {
                        redisKey = String.format("%s-%s-%s", dsName, OnlineDataTypeEnum.DICTIONARY.getType(), fieLdsModel.getConfig().getDictionaryType());
                        if (!localCache.containsKey(redisKey)) {
                            if (!redisUtil.exists(redisKey)) {
                                List<DictionaryDataEntity> list = dictionaryDataService.getDicList(fieLdsModel.getConfig().getDictionaryType());
                                options = list.stream().map(dic -> {
                                    Map<String, Object> dictionaryMap = new HashMap<>(16);
                                    dictionaryMap.put("id", dic.getId());
                                    dictionaryMap.put("enCode", dic.getEnCode());
                                    dictionaryMap.put("fullName", dic.getFullName());
                                    return dictionaryMap;
                                }).collect(Collectors.toList());
                                String dictionaryData = JsonUtil.createObjectToString(options);
                                if (NEEDCACHE_REMOTE) {
                                    redisUtil.insert(redisKey, dictionaryData, DEFAULT_CACHE_TIME);
                                }
                                localCache.put(redisKey, options);
                            } else {
                                String dictionaryStringData = redisUtil.getString(redisKey).toString();
                                localCache.put(redisKey, JsonUtil.createJsonToListMap(dictionaryStringData));
                            }
                        }
                    }
                }
            }
            //有使用相关组件的情况才初始化数据
            if (needUser && !localCache.containsKey("__user_map")) {
                //人员
                redisKey = dsName + CacheKeyEnum.USER.getName();
                Map<String, Object> userMap;
                if (redisUtil.exists(redisKey)) {
                    userMap = redisUtil.getMap(redisKey);
                    userMap = Optional.ofNullable(userMap).orElse(new HashMap<>(20));
                } else {
                    userMap = userService.getUserMap();
                    if (NEEDCACHE_SYS) {
                        redisUtil.insert(redisKey, userMap, DEFAULT_CACHE_TIME);
                    }
                }
                localCache.put("__user_map", userMap);
            }
            if (needOrg && !localCache.containsKey("__org_map")) {
                //组织
                redisKey = dsName + CacheKeyEnum.ORG.getName();
                Map<String, Object> orgMap;
                if (redisUtil.exists(redisKey)) {
                    orgMap = redisUtil.getMap(redisKey);
                    orgMap = Optional.ofNullable(orgMap).orElse(new HashMap<>(20));
                } else {
                    orgMap = organizeApi.getOrgMap();
                    if (NEEDCACHE_SYS) {
                        redisUtil.insert(redisKey, orgMap, DEFAULT_CACHE_TIME);
                    }
                }
                localCache.put("__org_map", orgMap);
            }
			/*if(needOrg && !localCache.containsKey("__allOrg_map")){
				//组织需要递归显示
				Map<Object, Object> allOrgMap = redisUtil.getMap(dsName + CacheKeyEnum.AllORG.getName());
				allOrgMap = Optional.ofNullable(allOrgMap).orElse(new HashMap<>(20));
				localCache.put("__allOrg_map", allOrgMap);
			}*/
            if (needPos && !localCache.containsKey("__pos_map")) {
                //岗位
                redisKey = dsName + CacheKeyEnum.POS.getName();
                Map<String, Object> posMap;
                if (redisUtil.exists(redisKey)) {
                    posMap = redisUtil.getMap(redisKey);
                    posMap = Optional.ofNullable(posMap).orElse(new HashMap<>(20));
                } else {
                    posMap = positionApi.getPosMap();
                    if (NEEDCACHE_SYS) {
                        redisUtil.insert(redisKey, posMap, DEFAULT_CACHE_TIME);
                    }
                }
                localCache.put("__pos_map", posMap);
            }
            if (needRole && !localCache.containsKey("__role_map")) {
                //角色
                redisKey = dsName + CacheKeyEnum.ROLE.getName();
                Map<String, Object> roleMap;
                if (redisUtil.exists(redisKey)) {
                    roleMap = redisUtil.getMap(redisKey);
                    roleMap = Optional.ofNullable(roleMap).orElse(new HashMap<>(20));
                } else {
                    roleMap = roleApi.getRoleMap();
                    if (NEEDCACHE_SYS) {
                        redisUtil.insert(redisKey, roleMap, DEFAULT_CACHE_TIME);
                    }
                }
                localCache.put("__role_map", roleMap);
            }
            if (needGroup && !localCache.containsKey("__group_map")) {
                //分组
                redisKey = dsName + CacheKeyEnum.GROUP.getName();
                Map<String, Object> groupMap;
                if (redisUtil.exists(redisKey)) {
                    groupMap = redisUtil.getMap(redisKey);
                    groupMap = Optional.ofNullable(groupMap).orElse(new HashMap<>(20));
                } else {
                    groupMap = groupApi.getGroupMap();
                    if (NEEDCACHE_SYS) {
                        redisUtil.insert(redisKey, groupMap, DEFAULT_CACHE_TIME);
                    }
                }
                localCache.put("__group_map", groupMap);
            }
            if (needProvince && !localCache.containsKey("__pro_maplist")) {
                //省市区
                Map<Object, Object> proMap = redisUtil.getMap(String.format("%s-%s-%d", dsName, "province", 1));
                List<Map<String, String>> proMapList = new ArrayList<>();
                if (proMap.size() == 0) {
                    proMapList = fillProMap(dsName);
                } else {
                    for (int i = 1; i <= 4; i++) {
                        proMapList.add(redisUtil.getMap(String.format("%s-%s-%d", dsName, "province", i)));
                    }
                }
                localCache.put("__pro_maplist", proMapList);
            }
        } catch (Exception e) {
            log.error("在线开发转换数据异常:" + e.getMessage(), e);
        }
    }


    private List<Map<String, String>> fillProMap(String dsName) {
        List<Map<String, String>> proMapList = new ArrayList<>();
        //分级存储
        for (int i = 1; i <= 4; i++) {
            String redisKey = String.format("%s-%s-%d", dsName, "province", i);
            if (!redisUtil.exists(redisKey)) {
                List<ProvinceEntity> provinceEntityList = areaApi.getProListBytype(String.valueOf(i));
                Map<String, String> provinceMap = new HashMap<>(16);
                if (provinceEntityList != null) {
                    provinceEntityList.stream().forEach(p -> provinceMap.put(p.getId(), p.getFullName()));
                }
                proMapList.add(provinceMap);
                //区划基本不修改 不做是否缓存判断
                redisUtil.insert(redisKey, provinceMap, RedisUtil.CAHCEWEEK);
            }
        }
        return proMapList;
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
    public static void treeToList(String value, String label, String children, JSONArray data, List<Map<String, Object>> result) {
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
     * 生成关联属性（弹窗选择属性,关联表单属性）
     *
     * @param fieLdsModelList
     * @param dataMap
     * @param dataDetailMap
     */
    private static void getDataAttr(List<FieLdsModel> fieLdsModelList, Map<String, Object> dataMap, Map<String, Map<String, Object>> dataDetailMap) {
        for (FieLdsModel fieLdsModel : fieLdsModelList) {
            if (ObjectUtil.isEmpty(fieLdsModel)) {
                continue;
            }
            ConfigModel config = fieLdsModel.getConfig();
            String projectKey = config.getProjectKey();
            if (projectKey.equals(ProjectKeyConsts.RELATIONFORM_ATTR) || projectKey.equals(ProjectKeyConsts.POPUPSELECT_ATTR)) {
                //0展示数据 ? 1存储数据
                boolean isShow = fieLdsModel.getIsStorage() == 0;
                if (isShow) {
                    String relationField = fieLdsModel.getRelationField();
                    if (relationField.contains("_linzenTable_")) {
                        relationField = relationField.split("_linzenTable_")[0];
                    }
                    String showField = fieLdsModel.getShowField();
                    Map<String, Object> formDataMap = dataDetailMap.get(relationField);
                    if (formDataMap != null) {
                        dataMap.put(relationField + "_" + showField, formDataMap.get(showField));
                    }
                }
            }
        }
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
                            swapDataMap.put(relationFiled + "_id", relationValue);
                        }
                    }
                }
            }
        }
    }

    public ExcelImportModel createExcelData(List<Map<String, Object>> dataList, VisualDevJsonModel visualJsonModel, VisualdevEntity visualdevEntity) throws WorkFlowException {
        ExcelImportModel excelImportModel = new ExcelImportModel();
        Integer primaryKeyPolicy = visualJsonModel.getFormData().getPrimaryKeyPolicy();
        //导入功能流程列表数据
        boolean flowEnable = visualJsonModel.isFlowEnable();

        String uploaderTemplateJson = visualJsonModel.getColumnData().getUploaderTemplateJson();
        UploaderTemplateModel uploaderTemplateModel = JsonUtil.createJsonToBean(uploaderTemplateJson, UploaderTemplateModel.class);
        String dataType = uploaderTemplateModel.getDataType();
        ImportFormCheckUniqueModel uniqueModel = new ImportFormCheckUniqueModel();
        uniqueModel.setMain(true);
        uniqueModel.setDbLinkId(visualJsonModel.getDbLinkId());
        uniqueModel.setUpdate(dataType.equals("2"));
        uniqueModel.setPrimaryKeyPolicy(primaryKeyPolicy);
        uniqueModel.setLogicalDelete(visualJsonModel.getFormData().getLogicalDelete());
        uniqueModel.setTableModelList(visualJsonModel.getVisualTables());
        //获取缓存
        Map<String, Object> localCache = getlocalCache();
        List<Map<String, Object>> failResult = new ArrayList<>();

        List<VisualdevModelDataInfoVO> dataInfo = new ArrayList<>();
        try {
            for (Map<String, Object> data : dataList) {
                Map<String, Object> resultMap = new HashMap<>(data);
                StringJoiner errInfo = new StringJoiner(",");
                Map<String, Object> errorMap = new HashMap<>(data);
                boolean hasError = this.checkExcelData(visualJsonModel.getFormListModels(),
                        data, localCache, resultMap, errInfo, errorMap, uniqueModel);
                if (hasError) {
                    failResult.add(errorMap);
                } else {
                    VisualdevModelDataInfoVO infoVO = new VisualdevModelDataInfoVO();
                    //导入时默认第一个流程
                    if (flowEnable) {
                        resultMap.put(FlowFormConstant.FLOWID, visualJsonModel.getFlowId());
                    }
                    if (StringUtil.isNotEmpty(uniqueModel.getId())) {
                        visualdevModelDataService.visualUpdate(visualdevEntity, resultMap, uniqueModel.getId(), true);
                        infoVO.setId(uniqueModel.getId());
                        infoVO.setIntegrateId(uniqueModel.getId());
                        infoVO.setData(JsonUtil.createObjectToString(resultMap));
                    } else {
                        DataModel dataModel = visualdevModelDataService.visualCreate(visualdevEntity, resultMap, false, true);
                        infoVO.setId(dataModel.getMainId());
                        infoVO.setData(JsonUtil.createObjectToString(resultMap));
                    }
                    dataInfo.add(infoVO);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new WorkFlowException("导入异常！");
        } finally {
            DynamicDataSourceUtil.clearSwitchDataSource();
        }
        excelImportModel.setFnum(failResult.size());
        excelImportModel.setSnum(dataList.size() - failResult.size());
        excelImportModel.setResultType(failResult.size() > 0 ? 1 : 0);
        excelImportModel.setFailResult(failResult);
        excelImportModel.setDataInfoList(dataInfo);
        return excelImportModel;
    }

    /**
     * 获取系统控件缓存数据
     */
    public Map<String, Object> getlocalCache() {
        Map<String, Object> localCache = new HashMap<>();
        //读取系统控件 所需编码 id
        Map<String, Object> depMap = organizeApi.getOrgEncodeAndName("department");
        localCache.put("_dep_map", depMap);
        Map<String, Object> comMap = organizeApi.getOrgNameAndId("");
        localCache.put("_com_map", comMap);
        Map<String, Object> posMap = positionApi.getPosEncodeAndName();
        localCache.put("_pos_map", posMap);
        Map<String, Object> userMap = userService.getUserNameAndIdMap();
        localCache.put("_user_map", userMap);
        Map<String, Object> roleMap = roleApi.getRoleNameAndIdMap();
        localCache.put("_role_map", roleMap);
        Map<String, Object> groupMap = groupApi.getGroupEncodeMap();
        localCache.put("_group_map", groupMap);
        Map<String, Object> allOrgsTreeName = organizeApi.getAllOrgsTreeName();
        localCache.put("_com_tree_map", allOrgsTreeName);

        Map<String, String> orgIdNameMaps = organizeApi.getInfoList();
        List<String> idAll = new ArrayList<>();
        List<String> orgList = orgList(LinzenConst.CURRENT_ORG_TYPE, false, orgIdNameMaps);
        localCache.put("_org_list", orgList);
        List<String> orgSubList = orgList(LinzenConst.CURRENT_ORG_SUB_TYPE, false, orgIdNameMaps);
        localCache.put("_orgSub_list", orgSubList);
        List<String> gradeList = orgList(LinzenConst.CURRENT_GRADE_TYPE, false, orgIdNameMaps);
        localCache.put("_grade_list", gradeList);
        List<String> orgListStr = orgList(LinzenConst.CURRENT_ORG_TYPE, true, orgIdNameMaps);
        localCache.put("_org_listStr", orgListStr);
        List<String> orgSubListStr = orgList(LinzenConst.CURRENT_ORG_SUB_TYPE, true, orgIdNameMaps);
        localCache.put("_orgSub_listStr", orgSubListStr);
        List<String> gradeListStr = orgList(LinzenConst.CURRENT_GRADE_TYPE, true, orgIdNameMaps);
        localCache.put("_grade_listStr", gradeListStr);
        idAll.addAll(orgList);
        idAll.addAll(orgSubList);
        idAll.addAll(gradeList);
        List<SysOrganizeRelationEntity> relationList = organizeRelationApi.getRelationListByOrganizeId(idAll, "");
        List<SysUserRelationEntity> listByObjectIdAll = userRelationApi.getListByObjectIdAll(idAll);
        for (SysUserRelationEntity userRelationEntity : listByObjectIdAll) {
            SysOrganizeRelationEntity model = new SysOrganizeRelationEntity();
            model.setObjectType(PermissionConst.USER);
            model.setOrganizeId(userRelationEntity.getObjectId());
            model.setObjectId(userRelationEntity.getUserId());
            relationList.add(model);
        }
        localCache.put("_relation_list", relationList);
        return localCache;
    }

    /**
     * 获取组织id
     *
     * @param type
     * @param isTree true 返回【xxx,xxx】json树形列表  | false 返回 xxx 最后组织id列表
     * @return
     */
    public List<String> orgList(String type, boolean isTree, Map<String, String> orgIdNameMaps) {
        List<String> org = new ArrayList() {{
            add(type);
        }};
        OrganizeConditionModel orgType = new OrganizeConditionModel();
        orgType.setDepartIds(org);
        orgType.setOrgIdNameMaps(orgIdNameMaps);
        List<String> orgList = new ArrayList<>();
        List<OrganizeModel> orgIdsList = organizeRelationApi.getOrgIdsList(orgType);
        for (OrganizeModel organizeModel : orgIdsList) {
            if (isTree) {
                List<String> treeList = StringUtil.isNotEmpty(organizeModel.getOrganizeIdTree()) ? Arrays.asList(organizeModel.getOrganizeIdTree().split(",")) : new ArrayList<>();
                orgList.add(JsonUtil.createListToJsonArray(treeList).toJSONString());
            } else {
                orgList.add(organizeModel.getId());
            }
        }
        return orgList;
    }

    public boolean checkExcelData(List<FieLdsModel> modelList, Map<String, Object> data, Map<String, Object> localCache, Map<String, Object> insMap,
                                  StringJoiner errInfo, Map<String, Object> errorMap, ImportFormCheckUniqueModel uniqueModel) throws Exception {
        try {
            UserInfo userInfo = UserProvider.getUser();
            SysUserEntity userEntity = userService.getInfo(userInfo.getUserId());

            if (uniqueModel.isMain()) {
                uniqueModel.setId(null);
            }

            //读取系统控件 所需编码 id
            Map<String, Object> depMap = (Map<String, Object>) localCache.get("_dep_map");
            Map<String, Object> comMap = (Map<String, Object>) localCache.get("_com_map");
            Map<String, Object> posMap = (Map<String, Object>) localCache.get("_pos_map");
            Map<String, Object> userMap = (Map<String, Object>) localCache.get("_user_map");
            Map<String, Object> roleMap = (Map<String, Object>) localCache.get("_role_map");
            Map<String, Object> groupMap = (Map<String, Object>) localCache.get("_group_map");
            Map<String, Object> allOrgsTreeName = (Map<String, Object>) localCache.get("_com_tree_map");

            List<String> orgList = (List<String>) localCache.get("_org_list");
            List<String> orgSubList = (List<String>) localCache.get("_orgSub_list");
            List<String> gradeList = (List<String>) localCache.get("_grade_list");
            List<String> orgListStr = (List<String>) localCache.get("_org_listStr");
            List<String> orgSubListStr = (List<String>) localCache.get("_orgSub_listStr");
            List<String> gradeListStr = (List<String>) localCache.get("_grade_listStr");
            List<SysOrganizeRelationEntity> relationListAll = (List<SysOrganizeRelationEntity>) localCache.get("_relation_list");

            //异常数据
            for (FieLdsModel fieLdsModel : modelList) {
                try {
                    String projectKey = fieLdsModel.getConfig().getProjectKey();
                    String dataType = fieLdsModel.getConfig().getDataType();
                    Object valueO = data.get(fieLdsModel.getVModel());
                    String label = fieLdsModel.getConfig().getLabel();
                    //是否必填
                    boolean required = fieLdsModel.getConfig().isRequired();
                    if (valueO == null || "null".equals(valueO) || StringUtil.isEmpty(String.valueOf(valueO))) {
                        if (required && !ProjectKeyConsts.getUploadMaybeNull().contains(projectKey)) {
                            errInfo.add(label + "值不能为空");
                        }
                        continue;
                    }
                    String value = String.valueOf(valueO);
                    if (StringUtil.isEmpty(value)) {
                        continue;
                    }
                    Boolean multiple = fieLdsModel.getMultiple();
                    if (ProjectKeyConsts.CHECKBOX.equals(projectKey)) {
                        multiple = true;
                    }
                    if (ProjectKeyConsts.CASCADER.equals(projectKey)) {
                        multiple = fieLdsModel.getMultiple();
                    }

                    //导入清空系统控件信息
                    if (ProjectKeyConsts.getSystemKey().contains(projectKey)) {
                        insMap.put(fieLdsModel.getVModel(), null);
                    }

                    boolean valueMul = value.contains(",");
                    value = value.trim();
                    List<String> valueList = valueMul ? Arrays.asList(value.split(",")) : new ArrayList<>();
                    if (!valueMul) {
                        valueList.add(value);
                    }
                    String ableIds = fieLdsModel.getAbleIds() != null ? fieLdsModel.getAbleIds() : "[]";
                    OnlineCusCheckModel cusCheckModel = AbleUtil.ableModel(ableIds, projectKey);
                    List<String> ableSystemIds = cusCheckModel.getAbleSystemIds();
                    List<String> idList = new ArrayList<>();
                    List<String> idStr = new ArrayList<>();
                    List<String> orgUserList = new ArrayList<>();
                    for (String systemId : ableSystemIds) {
                        if (LinzenConst.CURRENT_ORG_TYPE.equals(systemId) || LinzenConst.CURRENT_ORG.equals(systemId)) {
                            idList.addAll(orgList);
                            idStr.addAll(orgListStr);
                            orgUserList.addAll(orgList);
                        }
                        if (LinzenConst.CURRENT_GRADE_TYPE.equals(systemId) || LinzenConst.CURRENT_GRADE.equals(systemId)) {
                            idList.addAll(gradeList);
                            idStr.addAll(gradeListStr);
                            orgUserList.addAll(gradeList);
                        }
                        if (LinzenConst.CURRENT_ORG_SUB_TYPE.equals(systemId) || LinzenConst.CURRENT_ORG_SUB.equals(systemId)) {
                            idList.addAll(orgSubList);
                            idStr.addAll(orgSubListStr);
                            orgUserList.addAll(orgSubList);
                        }
                    }
                    cusCheckModel.getAbleComIds().addAll(idList);
                    cusCheckModel.getAbleDepIds().addAll(idList);
                    cusCheckModel.getAbleComIdsStr().addAll(idStr);
                    List<SysOrganizeRelationEntity> relationList = relationListAll.stream().filter(t -> orgUserList.contains(t.getOrganizeId())).collect(Collectors.toList());
                    Map<String, List<SysOrganizeRelationEntity>> relationMap = relationList.stream().collect(Collectors.groupingBy(SysOrganizeRelationEntity::getObjectType));
                    for (String key : relationMap.keySet()) {
                        List<String> list = relationMap.get(key).stream().map(SysOrganizeRelationEntity::getObjectId).collect(Collectors.toList());
                        for (String objectId : list) {
                            if (PermissionConst.ROLE.equalsIgnoreCase(key)) {
                                cusCheckModel.getAbleRoleIds().add(objectId);
                                cusCheckModel.getAbleIds().add(objectId + "--" + PermissionConst.ROLE.toLowerCase());
                            }
                            if (PermissionConst.POSITION.equalsIgnoreCase(key)) {
                                cusCheckModel.getAblePosIds().add(objectId);
                                cusCheckModel.getAbleIds().add(objectId + "--" + PermissionConst.POSITION.toLowerCase());
                            }
                            if (PermissionConst.USER.equalsIgnoreCase(key)) {
                                cusCheckModel.getAbleUserIds().add(objectId);
                                cusCheckModel.getAbleIds().add(objectId + "--" + PermissionConst.USER.toLowerCase());
                            }
                            if (PermissionConst.GROUP.equalsIgnoreCase(key)) {
                                cusCheckModel.getAbleGroupIds().add(objectId);
                                cusCheckModel.getAbleIds().add(objectId + "--" + PermissionConst.GROUP.toLowerCase());
                            }
                            if (PermissionConst.DEPARTMENT.equalsIgnoreCase(key)) {
                                cusCheckModel.getAbleDepIds().add(objectId);
                                cusCheckModel.getAbleIds().add(objectId + "--" + PermissionConst.DEPARTMENT.toLowerCase());
                            }
                            if (PermissionConst.ORGANIZE.equalsIgnoreCase(key)) {
                                cusCheckModel.getAbleComIds().add(objectId);
                                cusCheckModel.getAbleIds().add(objectId + "--" + PermissionConst.ORGANIZE.toLowerCase());
                            }
                        }
                    }
                    cusCheckModel.setControlType(projectKey);
                    List<String> dataList;
                    switch (projectKey) {
                        case ProjectKeyConsts.NUM_INPUT:
                            String regNum = "^[0-9]*+(.[0-9]*)?$";
                            if (StringUtil.isNotEmpty(value) && !value.matches(regNum)) {
                                errInfo.add(label + "值不正确");
                            }
                            break;
                        /**
                         * 高级控件
                         */
                        case ProjectKeyConsts.COMSELECT:
                            boolean comErrorHapen = false;
                            if (!multiple) {
                                if (valueList.size() > 1) {
                                    comErrorHapen = true;
                                    errInfo.add(label + "非多选");
                                }
                            }
                            if (!comErrorHapen) {
                                List<List<String>> comTwoList = new ArrayList<>();
                                List<String> comOneList = new ArrayList<>();
                                for (String comValue : valueList) {
                                    for (String key : allOrgsTreeName.keySet()) {
                                        Object o = allOrgsTreeName.get(key);
                                        if (comValue.equals(o.toString())) {
                                            String[] split = key.split(",");
                                            comOneList.addAll(Arrays.asList(split));
                                            comTwoList.add(Arrays.asList(split));
                                            break;
                                        }
                                    }
                                }
                                if (CollectionUtil.isEmpty(comTwoList)) {
                                    errInfo.add(label + "值不正确");
                                } else {
                                    insMap.put(fieLdsModel.getVModel(), !multiple ? JsonUtil.createObjectToString(comOneList) : JsonUtil.createObjectToString(comTwoList));
                                    if (fieLdsModel.getSelectType().equals("custom")) {
                                        List<String> strList = new ArrayList<>();
                                        for (List<String> strings : comTwoList) {
                                            strList.add(JsonUtil.createListToJsonArray(strings).toJSONString());
                                        }
                                        cusCheckModel.setDataList(strList);
                                        checkCustomControl(cusCheckModel, errInfo, label);
                                    }
                                }
                            }
                            break;
                        case ProjectKeyConsts.DEPSELECT:
                            dataList = checkOptionsControl(multiple, insMap, fieLdsModel.getVModel(), label, depMap, valueList, errInfo);
                            if (dataList.size() == valueList.size() && fieLdsModel.getSelectType().equals("custom")) {
                                cusCheckModel.setDataList(dataList);
                                checkCustomControl(cusCheckModel, errInfo, label);
                            }
                            break;
                        case ProjectKeyConsts.POSSELECT:
                            dataList = checkOptionsControl(multiple, insMap, fieLdsModel.getVModel(), label, posMap, valueList, errInfo);
                            if (dataList.size() == valueList.size() && fieLdsModel.getSelectType().equals("custom")) {
                                cusCheckModel.setDataList(dataList);
                                checkCustomControl(cusCheckModel, errInfo, label);
                            }
                            break;
                        case ProjectKeyConsts.USERSELECT:
                            dataList = checkOptionsControl(multiple, insMap, fieLdsModel.getVModel(), label, userMap, valueList, errInfo);
                            if (dataList.size() == valueList.size() && fieLdsModel.getSelectType().equals("custom")) {
                                cusCheckModel.setDataList(dataList);
                                checkCustomControl(cusCheckModel, errInfo, label);
                            }
                            break;
                        case ProjectKeyConsts.CUSTOMUSERSELECT:
                            boolean cusUserErrorHapen = false;
                            if (!multiple) {
                                //非多选填入多选值
                                if (valueList.size() > 1) {
                                    cusUserErrorHapen = true;
                                    errInfo.add(label + "非多选");
                                }
                            }
                            if (!cusUserErrorHapen) {
                                boolean cusUserErrorHapen1 = false;
                                List<String> cusUserList = new ArrayList<>();
                                for (String va : valueList) {
                                    String type = null;
                                    String id = null;
                                    if (groupMap.get(va) != null) {
                                        type = "group";
                                        id = groupMap.get(va).toString();
                                    } else if (roleMap.get(va) != null) {
                                        type = "role";
                                        id = roleMap.get(va).toString();
                                    } else if (depMap.get(va) != null) {
                                        type = "department";
                                        id = depMap.get(va).toString();
                                    } else if (comMap.get(va) != null) {
                                        type = "company";
                                        id = comMap.get(va).toString();
                                    } else if (posMap.get(va) != null) {
                                        type = "position";
                                        id = posMap.get(va).toString();
                                    } else if (userMap.get(va) != null) {
                                        type = "user";
                                        id = userMap.get(va).toString();
                                    }
                                    if (type == null && id == null) {
                                        cusUserErrorHapen1 = true;
                                    } else {
                                        String lastCusId = id + "--" + type;
                                        cusUserList.add(lastCusId);
                                    }
                                }
                                if (cusUserErrorHapen1) {
                                    errInfo.add(label + "值不正确");
                                } else {
                                    insMap.put(fieLdsModel.getVModel(), !multiple ? cusUserList.get(0) : JsonUtil.createObjectToString(cusUserList));
                                    if (fieLdsModel.getSelectType().equals("custom")) {
                                        cusCheckModel.setDataList(cusUserList);
                                        checkCustomControl(cusCheckModel, errInfo, label);
                                    }
                                }
                            }
                            break;
                        case ProjectKeyConsts.ROLESELECT:
                            dataList = checkOptionsControl(multiple, insMap, fieLdsModel.getVModel(), label, roleMap, valueList, errInfo);
                            if (dataList.size() == valueList.size() && fieLdsModel.getSelectType().equals("custom")) {
                                cusCheckModel.setDataList(dataList);
                                checkCustomControl(cusCheckModel, errInfo, label);
                            }
                            break;
                        case ProjectKeyConsts.GROUPSELECT:
                            dataList = checkOptionsControl(multiple, insMap, fieLdsModel.getVModel(), label, groupMap, valueList, errInfo);
                            if (dataList.size() == valueList.size() && fieLdsModel.getSelectType().equals("custom")) {
                                cusCheckModel.setDataList(dataList);
                                checkCustomControl(cusCheckModel, errInfo, label);
                            }
                            break;
                        case ProjectKeyConsts.ADDRESS:
                            boolean addressErrorHapen = false;
                            if (!multiple) {
                                //非多选填入多选值
                                if (valueList.size() > 1) {
                                    addressErrorHapen = true;
                                    errInfo.add(label + "非多选");
                                }
                            }
                            if (!addressErrorHapen) {
                                boolean addressErrorHapen1 = false;
                                valueList = Arrays.asList(value.split(","));
                                List<String[]> addresss = new ArrayList<>();
                                List<String> addressList1 = new ArrayList<>();
                                for (String va : valueList) {
                                    String[] addressSplit = va.split("/");
                                    if (addressSplit.length != fieLdsModel.getLevel() + 1) {
                                        addressErrorHapen1 = true;
                                    }
                                    List<String> addressJoined = new ArrayList<>();
                                    List<String> addressParentID = new ArrayList<>();
                                    for (String add : addressSplit) {
                                        ProvinceEntity PRO = areaApi.getInfo(add, addressParentID);
                                        if (PRO == null) {
                                            addressErrorHapen1 = true;
                                        } else {
                                            addressJoined.add(PRO.getId());
                                            addressParentID.add(PRO.getId());
                                        }
                                    }
                                    addressList1.addAll(addressJoined);
                                    addresss.add(addressJoined.toArray(new String[addressJoined.size()]));
                                }
                                if (addressErrorHapen1) {
                                    errInfo.add(label + "值不正确");
                                } else {
                                    insMap.put(fieLdsModel.getVModel(), multiple ? JsonUtil.createObjectToString(addresss) : JsonUtil.createObjectToString(addressList1));
                                }
                            }
                            break;
                        /**
                         * 系统控件
                         */
                        case ProjectKeyConsts.CURRORGANIZE:
                            List<SysUserRelationEntity> OrgRelations = userRelationApi.getListByUserId(userInfo.getUserId(), PermissionConst.ORGANIZE);
                            //						String currentOrgValue = userEntity.getOrganizeId();
                            //						if (!"all".equals(fieLdsModel.getShowLevel())) {
                            //							OrganizeEntity organizeInfo = organizeService.getInfo(userEntity.getOrganizeId());
                            //							if ("company".equals(organizeInfo.getCategory())) {
                            //								currentOrgValue = null;
                            //							}
                            //						}
                            insMap.put(fieLdsModel.getVModel(), OrgRelations.size() > 0 ? OrgRelations.get(0).getObjectId() : null);
                            break;
                        case ProjectKeyConsts.CURRDEPT:
                            List<SysUserRelationEntity> depUserRelations = userRelationApi.getListByUserId(userInfo.getUserId(), PermissionConst.DEPARTMENT);
                            insMap.put(fieLdsModel.getVModel(), depUserRelations.size() > 0 ? depUserRelations.get(0).getObjectId() : null);
                            break;
                        case ProjectKeyConsts.CREATEUSER:
                            insMap.put(fieLdsModel.getVModel(), userEntity.getId());
                            break;
                        case ProjectKeyConsts.CREATETIME:
                            insMap.put(fieLdsModel.getVModel(), DateUtil.now());
                            break;
                        case ProjectKeyConsts.MODIFYTIME:
                            break;
                        case ProjectKeyConsts.MODIFYUSER:
                            break;
                        case ProjectKeyConsts.CURRPOSITION:
                            insMap.put(fieLdsModel.getVModel(), userEntity.getPositionId());
                            break;
                        case ProjectKeyConsts.BILLRULE:
                            String billNo = "";
                            try {
                                String rule = fieLdsModel.getConfig().getRule();
                                billNo = billRuleApi.getBillNumber(rule, false);
                            } catch (Exception e) {
                                log.error("导入excel:获取单据失败");
                            }
                            insMap.put(fieLdsModel.getVModel(), billNo);
                            break;
                        /**
                         * 基础控件
                         */
                        case ProjectKeyConsts.SWITCH:
                            String activeTxt = fieLdsModel.getActiveTxt();
                            String inactiveTxt = fieLdsModel.getInactiveTxt();
                            if (value.equals(activeTxt)) {
                                insMap.put(fieLdsModel.getVModel(), 1);
                            } else if (value.equals(inactiveTxt)) {
                                insMap.put(fieLdsModel.getVModel(), 0);
                            } else {
                                errInfo.add(label + "值不正确");
                            }
                            break;
                        case ProjectKeyConsts.RATE:
                            Double ratevalue = new Double(value);
                            Double maxvalue = new Double(0);
                            if (fieLdsModel.getCount() != -1) {
                                maxvalue = new Double(fieLdsModel.getCount());
                            }
                            if (fieLdsModel.getAllowhalf()) {
                                if (ratevalue % 0.5 != 0 || ratevalue > maxvalue || ratevalue < 0) {
                                    errInfo.add(label + "值不正确");
                                }
                            } else {
                                if (ratevalue % 1 != 0 || ratevalue > maxvalue || ratevalue < 0) {
                                    errInfo.add(label + "值不正确");
                                }
                            }
                            break;
                        case ProjectKeyConsts.SLIDER:
                            BigDecimal Ivalue = new BigDecimal(value);
                            boolean errorHapen = false;
                            if (fieLdsModel.getMin() != null) {
                                BigDecimal min = new BigDecimal(fieLdsModel.getMin());
                                errorHapen = Ivalue.compareTo(min) == -1;
                            }
                            if (!errorHapen) {
                                if (fieLdsModel.getMax() != null) {
                                    BigDecimal max = new BigDecimal(fieLdsModel.getMax());
                                    errorHapen = Ivalue.compareTo(max) == 1;
                                }
                            }
                            if (errorHapen) {
                                errInfo.add(label + "值不在范围内");
                            }
                            break;
                        case ProjectKeyConsts.COM_INPUT:
                            try {
                                DbLinkEntity linkEntity = dataSourceApi.getInfo(uniqueModel.getDbLinkId());
                                DynamicDataSourceUtil.switchToDataSource(linkEntity);
                                @Cleanup Connection connection = DynamicDataSourceUtil.getCurrentConnection();
                                Boolean unique = fieLdsModel.getConfig().getUnique();
                                boolean comInputError = false;
                                if (unique) {
                                    if (!uniqueModel.isMain()) {
                                        //子表重复只判断同一个表单
                                        if (insMap.get("child_table_list") != null) {
                                            List<Map<String, Object>> childList = (List<Map<String, Object>>) insMap.get("child_table_list");
                                            if (childList.size() > 1) {
                                                String finalValue = value;
                                                List<Map<String, Object>> collect = childList.stream().filter(t -> finalValue.equals(t.get(fieLdsModel.getVModel()))).collect(Collectors.toList());
                                                if (collect.size() > 1) {
                                                    comInputError = true;
                                                    errInfo.add(label + "字段数据重复无法进行导入");
                                                }
                                            }
                                        }
                                    } else {
                                        String tableName = Optional.ofNullable(fieLdsModel.getConfig().getRelationTable()).orElse(fieLdsModel.getConfig().getTableName());
                                        //验证唯一
                                        SqlTable sqlTable = SqlTable.of(tableName);
                                        String key = flowDataUtil.getKey(connection, tableName, uniqueModel.getPrimaryKeyPolicy());
                                        SelectStatementProvider render;
                                        String vModelThis = fieLdsModel.getVModel();

                                        String foriegKey = "";
                                        String columnName = "";
                                        boolean isMain = uniqueModel.isMain();
                                        TableModel tableModel = uniqueModel.getTableModelList().stream().filter(t -> {
                                            //子表判断
                                            if (StringUtil.isNotEmpty(fieLdsModel.getConfig().getRelationTable())) {
                                                return fieLdsModel.getConfig().getRelationTable().equals(t.getTable());
                                            }
                                            //主副表判断
                                            return fieLdsModel.getConfig().getTableName().equals(t.getTable());
                                        }).findFirst().orElse(null);
                                        if (tableModel != null) {
                                            String fieldName = vModelThis;
                                            if (vModelThis.contains("_linzen_")) {
                                                fieldName = vModelThis.split("_linzen_")[1];
                                                isMain = false;
                                                foriegKey = tableModel.getTableField();
                                            }
                                            String finalFieldName = fieldName;
                                            TableFields tableFields = tableModel.getFields().stream().filter(t -> t.getField().equals(finalFieldName)).findFirst().orElse(null);
                                            if (tableFields != null) {
                                                columnName = StringUtil.isNotEmpty(tableFields.getColumnName()) ? tableFields.getColumnName() : fieldName;
                                            }
                                        }

                                        List<BasicColumn> selectKey = new ArrayList<>();
                                        selectKey.add(sqlTable.column(columnName));
                                        selectKey.add(sqlTable.column(key));
                                        if (StringUtil.isNotEmpty(foriegKey)) {
                                            String finalForiegKey = foriegKey;
                                            TableFields tableFields = tableModel.getFields().stream().filter(t -> t.getField().equals(finalForiegKey)).findFirst().orElse(null);
                                            if (tableFields != null) {
                                                foriegKey = StringUtil.isNotEmpty(tableFields.getColumnName()) ? tableFields.getColumnName() : finalForiegKey;
                                            }
                                            selectKey.add(sqlTable.column(foriegKey));
                                        }
                                        if (uniqueModel.getLogicalDelete()) {
                                            //开启逻辑删除
                                            render = SqlBuilder.select(selectKey).from(sqlTable)
                                                    .where(sqlTable.column(columnName), SqlBuilder.isEqualTo(value))
                                                    .and(sqlTable.column(TableFeildsEnum.DEL_FLAG.getField()), SqlBuilder.isNull())
                                                    .build().render(RenderingStrategies.MYBATIS3);
                                        } else {
                                            render = SqlBuilder.select(selectKey).from(sqlTable)
                                                    .where(sqlTable.column(columnName), SqlBuilder.isEqualTo(value))
                                                    .build().render(RenderingStrategies.MYBATIS3);
                                        }
                                        List<Map<String, Object>> mapList = flowFormDataMapper.selectManyMappedRows(render);
                                        int count = mapList.size();
                                        if (count > 0) {
                                            //是否开启支持导入数据更新
                                            if (uniqueModel.isUpdate()) {
                                                //是否主表
                                                if (isMain) {
                                                    Map<String, Object> map = mapList.get(0);
                                                    uniqueModel.setId(map.get(key).toString());
                                                } else if (StringUtil.isNotEmpty(foriegKey)) {
                                                    Map<String, Object> map = mapList.get(0);
                                                    if (StringUtil.isEmpty(uniqueModel.getId())) {
                                                        uniqueModel.setId(map.get(foriegKey).toString());
                                                    } else if (mapList.size() > 0) {
                                                        //取到第一个有数据的唯一字段之后，其他唯一字段导入查询有值，不用来做更新，而拿来做判断。
                                                        comInputError = true;
                                                        errInfo.add(label + "字段数据重复无法进行导入");
                                                    }
                                                }
                                            } else {
                                                comInputError = true;
                                                errInfo.add(label + "字段数据重复无法进行导入");
                                            }
                                        }
                                    }
                                }
                                //验证正则
                                if (StringUtil.isNotEmpty(fieLdsModel.getConfig().getRegList())) {
                                    List<RegListModel> regList = JsonUtil.createJsonToList(fieLdsModel.getConfig().getRegList(), RegListModel.class);
                                    for (RegListModel regListModel : regList) {
                                        //处理正则格式
                                        String reg = regListModel.getPattern();
                                        if (reg.startsWith("/") && reg.endsWith("/")) {
                                            reg = reg.substring(1, reg.length() - 1);
                                        }
                                        boolean matches = value.matches(reg);
                                        if (!matches) {
                                            comInputError = true;
                                            errInfo.add(label + regListModel.getMessage());
                                        }
                                    }
                                }
                                if (!comInputError) {
                                    insMap.put(fieLdsModel.getVModel(), value);
                                }
                            } catch (Exception e) {
                                errInfo.add(label + "值不正确");
                            } finally {
                                DynamicDataSourceUtil.clearSwitchDataSource();
                            }
                            break;
                        case ProjectKeyConsts.TIME:
                            try {
                                int formatSize = 3;
                                if ("HH:mm".equals(fieLdsModel.getFormat())) {
                                    formatSize = 2;
                                }
                                String[] timeSplit = value.split(":");
                                boolean timeHasError = false;
                                if (Integer.parseInt(timeSplit[0]) > 23 || timeSplit.length != formatSize) {
                                    timeHasError = true;
                                }
                                if (Integer.parseInt(timeSplit[1]) > 59 || timeSplit.length != formatSize) {
                                    timeHasError = true;
                                }
                                if (formatSize == 3) {
                                    if (Integer.parseInt(timeSplit[2]) > 59 || timeSplit.length != formatSize) {
                                        timeHasError = true;
                                    }
                                }
                                boolean timeHasRangeError = false;
                                //判断时间是否在设置范围内
                                String dataFomrat = "yyyy-MM-dd " + fieLdsModel.getFormat();
                                if (fieLdsModel.getConfig().getStartTimeRule() && StringUtil.isNotEmpty(fieLdsModel.getConfig().getStartTimeValue())) {
                                    String startTimeValue = "2000-01-01 " + fieLdsModel.getConfig().getStartTimeValue();
                                    String valueTime = "2000-01-01 " + value;
                                    long startTimeLong = DateUtil.parse(startTimeValue, dataFomrat).getTime();
                                    long valueTimeLong = DateUtil.parse(valueTime, dataFomrat).getTime();
                                    if (valueTimeLong < startTimeLong) {
                                        timeHasRangeError = true;
                                    }

                                }
                                if (fieLdsModel.getConfig().getEndTimeRule() && StringUtil.isNotEmpty(fieLdsModel.getConfig().getEndTimeValue())) {
                                    String endTimeValue = "2000-01-01 " + fieLdsModel.getConfig().getEndTimeValue();
                                    String valueTime = "2000-01-01 " + value;
                                    long endTimeLong = DateUtil.parse(endTimeValue, dataFomrat).getTime();
                                    long valueTimeLong = DateUtil.parse(valueTime, dataFomrat).getTime();
                                    if (valueTimeLong > endTimeLong) {
                                        timeHasRangeError = true;
                                    }
                                }
                                if (timeHasError) {
                                    throw new RuntimeException();
                                }
                                if (timeHasRangeError) {
                                    errInfo.add(label + "值不在范围内");
                                    break;
                                }
                                insMap.put(fieLdsModel.getVModel(), value);
                            } catch (Exception e) {
                                errInfo.add(label + "值不正确");
                            }
                            break;
                        case ProjectKeyConsts.DATE:
                            String format = fieLdsModel.getFormat();
                            if (StringUtil.isNotEmpty(format)) {
                                format = DateTimeFormatConstant.getFormat(format);
                            }
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                            try {
                                if (value.length() != format.length()) {
                                    throw new RuntimeException();
                                }
                                simpleDateFormat.parse(String.valueOf(value));
                                //判断时间是否在设置范围内
                                boolean timeHasRangeError = FormPublicUtils.dateTimeCondition(fieLdsModel, format, value, data, projectKey);
                                if (timeHasRangeError) {
                                    errInfo.add(label + "值不在范围内");
                                    break;
                                }
                                Date parse = simpleDateFormat.parse(String.valueOf(value));
                                insMap.put(fieLdsModel.getVModel(), parse != null ? parse.getTime() : null);
                            } catch (Exception e) {
                                errInfo.add(label + "值不正确");
                            }
                            break;
                        /**
                         * 子表
                         */
                        case ProjectKeyConsts.CHILD_TABLE:
                            StringJoiner childJoiner = new StringJoiner(",");
                            List<Map<String, Object>> childTable = new ArrayList<>();
                            for (Map<String, Object> item : (List<Map<String, Object>>) data.get(fieLdsModel.getVModel())) {
                                Map<String, Object> childMap = new HashMap<>(item);
                                childMap.put("mainAndMast", data);
                                childMap.put("child_table_list", data.get(fieLdsModel.getVModel()));
                                Map<String, Object> childTableMap = new HashMap<>(childMap);
                                Map<String, Object> childerrorMap = new HashMap<>(childMap);
                                uniqueModel.setMain(false);
                                StringJoiner childJoiner1 = new StringJoiner(",");
                                boolean childHasError = this.checkExcelData(fieLdsModel.getConfig().getChildren(), childMap, localCache, childTableMap, childJoiner1, childerrorMap, uniqueModel);
                                if (childHasError) {
                                    childJoiner.add(childJoiner1.toString());
                                } else {
                                    childTable.add(childTableMap);
                                }
                            }
                            if (childJoiner.length() == 0) {
                                insMap.put(fieLdsModel.getVModel(), childTable);
                            } else {
                                errInfo.add(childJoiner.toString());
                            }
                            uniqueModel.setMain(true);
                            break;
                        default:
                            break;

                    }
                    /**
                     * 数据接口
                     */
                    if (dataType != null) {
                        List<Map<String, Object>> options = new ArrayList<>();
                        String dataLabel = fieLdsModel.getProps().getLabel() != null ? fieLdsModel.getProps().getLabel() : "";
                        String dataValue = fieLdsModel.getProps().getValue() != null ? fieLdsModel.getProps().getValue() : "";
                        String children = fieLdsModel.getProps().getChildren() != null ? fieLdsModel.getProps().getChildren() : "";
                        boolean isCascader = ProjectKeyConsts.CASCADER.equals(projectKey);

                        String localCacheKey;
                        Map<String, Object> dataInterfaceMap = new HashMap<>();
                        //静态数据
                        if (dataType.equals(OnlineDataTypeEnum.STATIC.getType())) {
                            localCacheKey = String.format("%s-%s", fieLdsModel.getConfig().getRelationTable() + fieLdsModel.getVModel(), OnlineDataTypeEnum.STATIC.getType());
                            if (!localCache.containsKey(localCacheKey)) {
                                if (fieLdsModel.getOptions() != null) {
                                    options = JsonUtil.createJsonToListMap(fieLdsModel.getOptions());
                                    String Children = fieLdsModel.getProps().getChildren();
                                    JSONArray staticData = JsonUtil.createListToJsonArray(options);
                                    getOptions(dataLabel, dataValue, Children, staticData, options);
                                } else {
                                    options = JsonUtil.createJsonToListMap(fieLdsModel.getOptions());
                                }
                                Map<String, Object> finalDataInterfaceMap = new HashMap<>(16);
                                String finalDataLabel = dataLabel;
                                String finalDataValue = dataValue;
                                options.stream().forEach(o -> {
                                    finalDataInterfaceMap.put(String.valueOf(o.get(finalDataLabel)), o.get(finalDataValue));
                                });
                                localCache.put(localCacheKey, finalDataInterfaceMap);
                                dataInterfaceMap = finalDataInterfaceMap;
                            } else {
                                dataInterfaceMap = (Map<String, Object>) localCache.get(localCacheKey);
                            }

                            checkFormDataInteface(multiple, insMap, fieLdsModel.getVModel(), label, dataInterfaceMap, valueList, errInfo, isCascader);
                            //远端数据
                        } else if (dataType.equals(OnlineDataTypeEnum.DYNAMIC.getType())) {
                            localCacheKey = String.format("%s-%s-%s-%s", OnlineDataTypeEnum.DYNAMIC.getType(), fieLdsModel.getConfig().getPropsUrl(), dataValue, dataLabel);
                            if (!localCache.containsKey(localCacheKey)) {
                                List<TemplateJsonModel> templateJson = fieLdsModel.getConfig().getTemplateJson();
                                Map<String, String> param = new HashMap<>();
                                for (TemplateJsonModel tm : templateJson) {
                                    param.put(tm.getField(), tm.getDefaultValue());
                                }
                                ServiceResult ServiceResult = dataInterFaceApi.infoToId(fieLdsModel.getConfig().getPropsUrl(), null, param);
                                if (ServiceResult != null && ServiceResult.getData() != null) {
                                    List<Map<String, Object>> dycDataList = new ArrayList<>();
                                    if (ServiceResult.getData() instanceof List) {
                                        dycDataList = (List<Map<String, Object>>) ServiceResult.getData();
                                    }
                                    JSONArray dataAll = JsonUtil.createListToJsonArray(dycDataList);
                                    treeToList(dataLabel, dataValue, children, dataAll, options);
                                    Map<String, Object> finalDataInterfaceMap1 = new HashMap<>(16);
                                    String finalDataLabel2 = dataLabel;
                                    String finalDataValue1 = dataValue;
                                    options.stream().forEach(o -> {
                                        finalDataInterfaceMap1.put(String.valueOf(o.get(finalDataLabel2)), String.valueOf(o.get(finalDataValue1)));
                                    });
                                    dataInterfaceMap = finalDataInterfaceMap1;
                                    localCache.put(localCacheKey, dataInterfaceMap);
                                }
                            } else {
                                dataInterfaceMap = (Map<String, Object>) localCache.get(localCacheKey);
                            }
                            checkFormDataInteface(multiple, insMap, fieLdsModel.getVModel(), label, dataInterfaceMap, valueList, errInfo, isCascader);
                            //数据字典
                        } else if (dataType.equals(OnlineDataTypeEnum.DICTIONARY.getType())) {
                            localCacheKey = String.format("%s-%s", OnlineDataTypeEnum.DICTIONARY.getType(), fieLdsModel.getConfig().getDictionaryType());
                            dataLabel = fieLdsModel.getProps().getLabel();
                            dataValue = fieLdsModel.getProps().getValue();
                            if (!localCache.containsKey(localCacheKey)) {
                                List<DictionaryDataEntity> list = dictionaryDataService.getDicList(fieLdsModel.getConfig().getDictionaryType());
                                options = list.stream().map(dic -> {
                                    Map<String, Object> dictionaryMap = new HashMap<>(16);
                                    dictionaryMap.put("id", dic.getId());
                                    dictionaryMap.put("enCode", dic.getEnCode());
                                    dictionaryMap.put("fullName", dic.getFullName());
                                    return dictionaryMap;
                                }).collect(Collectors.toList());
                                localCache.put(localCacheKey, options);
                            } else {
                                options = (List<Map<String, Object>>) localCache.get(localCacheKey);
                            }
                            Map<String, Object> finalDataInterfaceMap1 = new HashMap<>(16);
                            String finalDataLabel3 = dataLabel;
                            String finalDataValue3 = dataValue;
                            options.stream().forEach(o -> finalDataInterfaceMap1.put(String.valueOf(o.get(finalDataLabel3)), o.get(finalDataValue3)));

                            checkFormDataInteface(multiple, insMap, fieLdsModel.getVModel(), label, finalDataInterfaceMap1, valueList, errInfo, isCascader);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errInfo.add(e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (StringUtil.isNotEmpty(uniqueModel.getId()) && uniqueModel.isMain()) {
            List<String> flowIdList = new ArrayList() {{
                add(uniqueModel.getId());
            }};
            List<FlowTaskEntity> flowTaskList = flowTaskApi.getOrderStaList(flowIdList);
            if (flowTaskList.size() > 0) {
                boolean errorMsg = flowTaskList.stream().filter(t -> Objects.equals(t.getStatus(), 0)).count() == 0;
                String msg = "已发起流程，导入失败";
                boolean isFlowMsg = errInfo.toString().contains(msg);
                if (errorMsg && !isFlowMsg) {
                    errInfo.add(msg);
                }
            }
        }
        if (errInfo.length() > 0) {
            errorMap.put("errorsInfo", errInfo.toString());
            insMap = errorMap;
            return true;
        } else {
            return false;
        }
    }

    private List<String> checkOptionsControl(boolean multiple, Map<String, Object> insMap, String vModel, String label, Map<String, Object> cacheMap, List<String> valueList, StringJoiner errInfo) {
        boolean error = false;
        if (!multiple) {
            //非多选填入多选值
            if (valueList.size() > 1) {
                error = true;
                errInfo.add(label + "非多选");
            }
        }
        List<String> dataList = new ArrayList<>();
        if (!error) {
            boolean errorHapen = false;
            for (String va : valueList) {
                Object vo = cacheMap.get(va);
                if (vo == null) {
                    errorHapen = true;
                } else {
                    dataList.add(vo.toString());
                }

            }
            if (errorHapen) {
                errInfo.add(label + "值不正确");
            } else {
                insMap.put(vModel, !multiple ? dataList.get(0) : JsonUtil.createObjectToString(dataList));
            }
        }
        return dataList;
    }

    public void checkCustomControl(OnlineCusCheckModel cusCheckModel, StringJoiner errInfo, String label) {
        boolean contains;
        List<String> ableIdsAll = new ArrayList<>();
        List<String> ableDepIds = cusCheckModel.getAbleDepIds();
        List<String> ableGroupIds = cusCheckModel.getAbleGroupIds();
        List<String> ablePosIds = cusCheckModel.getAblePosIds();
        List<String> ableRoleIds = cusCheckModel.getAbleRoleIds();
        List<String> ableUserIds = cusCheckModel.getAbleUserIds();
        List<String> ableComIds = cusCheckModel.getAbleComIds();
        List<String> ableComIdsStr = cusCheckModel.getAbleComIdsStr();
        List<String> dataList = cusCheckModel.getDataList();
        String controlType = cusCheckModel.getControlType();
        switch (controlType) {
            case ProjectKeyConsts.GROUPSELECT:
                ableIdsAll.addAll(ableGroupIds);
                break;
            case ProjectKeyConsts.ROLESELECT:
                ableIdsAll.addAll(ableRoleIds);
                break;
            case ProjectKeyConsts.DEPSELECT:
                ableIdsAll.addAll(ableDepIds);
                break;
            case ProjectKeyConsts.COMSELECT:
                ableIdsAll.addAll(ableComIdsStr);
                break;
            case ProjectKeyConsts.CUSTOMUSERSELECT:
                for (String id : ableDepIds) {
                    ableIdsAll.add(id + "--department");
                }
                for (String id : ableGroupIds) {
                    ableIdsAll.add(id + "--group");
                }
                for (String id : ablePosIds) {
                    ableIdsAll.add(id + "--position");
                }
                for (String id : ableRoleIds) {
                    ableIdsAll.add(id + "--role");
                }
                for (String id : ableUserIds) {
                    ableIdsAll.add(id + "--user");
                }
                for (String id : ableComIds) {
                    ableIdsAll.add(id + "--company");
                }
                ableIdsAll.addAll(ableDepIds);
                ableIdsAll.addAll(ableGroupIds);
                ableIdsAll.addAll(ablePosIds);
                ableIdsAll.addAll(ableRoleIds);
                ableIdsAll.addAll(ableUserIds);
                ableIdsAll.addAll(ableComIds);
                List<SysUserRelationEntity> listByObjectIdAll = userRelationApi.getListByObjectIdAll(ableIdsAll);
                for (SysUserRelationEntity userRelationEntity : listByObjectIdAll) {
                    ableIdsAll.add(userRelationEntity.getUserId() + "--user");
                }
                break;
            case ProjectKeyConsts.USERSELECT:
                List<String> objIds = new ArrayList<>();
                if (ableComIds.size() > 0) {
                    List<String> lastIds = new ArrayList<>();
                    for (String str : ableComIds) {
                        lastIds.add(str);
                    }
                    objIds.addAll(lastIds);
                }
                if (ableGroupIds.size() > 0) {
                    objIds.addAll(ableGroupIds);
                }
                if (ablePosIds.size() > 0) {
                    objIds.addAll(ablePosIds);
                }
                if (ableRoleIds.size() > 0) {
                    objIds.addAll(ableRoleIds);
                }
                List<String> UserIds = userRelationApi.getListByObjectIdAll(objIds).stream().map(SysUserRelationEntity::getUserId).collect(Collectors.toList());
                UserIds.addAll(ableUserIds);
                ableIdsAll.addAll(UserIds);
                break;
            case ProjectKeyConsts.POSSELECT:
                List<String> posIds = new ArrayList<>();
                if (ableDepIds.size() > 0) {
                    List<String> depIds = positionApi.getListByOrganizeId(ableDepIds, false).stream().map(SysPositionEntity::getId).collect(Collectors.toList());
                    posIds.addAll(depIds);
                }
                if (ablePosIds.size() > 0) {
                    posIds.addAll(ablePosIds);
                }
                ableIdsAll.addAll(posIds);
                break;
            default:
                break;
        }
        if (ableIdsAll.size() > 0) {
            for (String id : dataList) {
                contains = ableIdsAll.contains(id);
                if (!contains) {
                    errInfo.add(label + "不在范围内");
                    break;
                }
            }
        }
    }

    public void checkFormDataInteface(boolean multiple, Map<String, Object> insMap, String vModel, String label,
                                      Map<String, Object> cacheMap, List<String> valueList, StringJoiner errInfo, boolean isCascader) {
        List<String[]> staticStrData = new ArrayList<>();
        List<String> staticStrDataList1 = new ArrayList<>();

        boolean hasError = false;
        boolean takeOne = false;
        for (String dicValue : valueList) {
            if (isCascader) {
                List<String> staticStrDataList2 = new ArrayList<>();
                if (!multiple && valueList.size() > 1) hasError = true;
                if (dicValue.contains("/")) {
                    String[] split = dicValue.split("/");
                    for (String s : split) {
                        Object s1 = cacheMap.get(s);
                        if (s1 != null) {
                            staticStrDataList2.add(s1.toString());
                            staticStrDataList1.add(s1.toString());
                        } else {
                            hasError = true;
                        }
                    }
                    staticStrData.add(staticStrDataList2.toArray(new String[staticStrDataList2.size()]));
                } else {
                    if (cacheMap.get(dicValue) == null) {
                        hasError = true;
                    } else {
                        staticStrDataList1.add(cacheMap.get(dicValue).toString());
                    }
                }
            } else {
                takeOne = true;
                Object s1 = cacheMap.get(dicValue);
                if (s1 != null) {
                    staticStrDataList1.add(s1.toString());
                } else {
                    hasError = true;
                }
            }
        }

        if (hasError) {
            errInfo.add(label + "值不正确");
        } else {
            String v = multiple ? takeOne ? JsonUtil.createObjectToString(staticStrDataList1) : JsonUtil.createObjectToString(staticStrData)
                    : takeOne ? staticStrDataList1.get(0) : JsonUtil.createObjectToString(staticStrDataList1);
            insMap.put(vModel, v);
        }
    }

    /**
     * 获取接口api数据结果
     *
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    public List<Map<String, Object>> getInterfaceData(VisualdevReleaseEntity visualdevEntity, PaginationModel paginationModel, ColumnDataModel columnDataModel) {
        List<Map<String, Object>> realList = new ArrayList<>();
        try {
            Map<String, String> parameterMap = new HashMap<>();
            if (StringUtil.isNotEmpty(visualdevEntity.getInterfaceParam())) {
                List<InterefaceParamModel> jsonToList = JsonUtil.createJsonToList(visualdevEntity.getInterfaceParam(), InterefaceParamModel.class);
                for (InterefaceParamModel mapStr : jsonToList) {
                    if (mapStr.getUseSearch() != null && Objects.equals(mapStr.getUseSearch(), true)) {
                        Map<String, Object> keyJsonMap = JsonUtil.stringToMap(paginationModel.getQueryJson());
                        if (keyJsonMap != null && keyJsonMap.get(mapStr.getField()) != null && StringUtil.isNotEmpty(keyJsonMap.get(mapStr.getField()).toString())) {
                            parameterMap.put(mapStr.getField(), keyJsonMap.get(mapStr.getField()).toString());
                        } else {
                            parameterMap.put(mapStr.getField(), null);
                        }
                    } else {
                        parameterMap.put(mapStr.getField(), mapStr.getDefaultValue());
                    }
                }
            }

            //组装查询条件
            List<FieLdsModel> queryCondition = this.getQueryCondition(paginationModel, columnDataModel);

            DataInterfaceEntity info = dataInterFaceApi.getInfo(visualdevEntity.getInterfaceId());
            //封装sql---sql普通查询塞参数到数据接口那边去组装sql
            OnlinePublicUtils.getViewQuerySql(info, queryCondition, parameterMap);

            ServiceResult dataInterfaceInfo = dataInterFaceApi.infoToId(visualdevEntity.getInterfaceId(), null, parameterMap);
            if (dataInterfaceInfo.getCode() == 200) {
                List<Map<String, Object>> dataRes = (List<Map<String, Object>>) dataInterfaceInfo.getData();
                //假查询条件-不为sql时查询在此过滤
                List<Map<String, Object>> dataInterfaceList = OnlinePublicUtils.getViewQueryNotSql(info, queryCondition, dataRes);

                //判断是否有id没有则随机
                dataInterfaceList.forEach(item -> {
                    if (item.get("id") == null) {
                        item.put("id", RandomUtil.uuId());
                    }
                    if (item.get("f_id") != null) {
                        item.put("id", item.get("f_id"));
                    }
                    if (item.get("children") != null) {
                        item.remove("children");
                    }
                });

                //排序
                if (StringUtil.isNotEmpty(paginationModel.getSidx())) {
                    String[] split = paginationModel.getSidx().split(",");
                    Collections.sort(dataInterfaceList, new Comparator<Map<String, Object>>() {
                        @Override
                        public int compare(Map<String, Object> a, Map<String, Object> b) {
                            for (String sidx : split) {
                                if (sidx.startsWith("-")) {
                                    if (!a.get(sidx.substring(1)).equals(b.get(sidx.substring(1)))) {
                                        return String.valueOf(b.get(sidx.substring(1))).compareTo(String.valueOf(a.get(sidx.substring(1))));
                                    }
                                } else {
                                    if (!a.get(sidx).equals(b.get(sidx))) {
                                        return String.valueOf(a.get(sidx)).compareTo(String.valueOf(b.get(sidx)));
                                    }
                                }
                            }
                            return 0;
                        }
                    });
                }

                if ("1".equals(paginationModel.getDataType())) {//导出全部数据用
                    return dataInterfaceList;
                }
                //假分页
                if (columnDataModel.getHasPage() && CollectionUtils.isNotEmpty(dataInterfaceList)) {
                    List<List<Map<String, Object>>> partition = Lists.partition(dataInterfaceList, (int) paginationModel.getPageSize());
                    realList = partition.get((int) paginationModel.getCurrentPage() - 1);
                    paginationModel.setTotal(dataInterfaceList.size());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("数据视图，接口请求失败!message={}", e.getMessage());
        }
//        dataId(realList);
        return realList;
    }

    private void dataId(List<Map<String, Object>> data) {
        for (Map<String, Object> item : data) {
            if (item.get("id") == null) {
                item.put("id", RandomUtil.uuId());
            }
            if (item.get("children") != null) {
                List<Map<String, Object>> children = new ArrayList<>();
                try {
                    children.addAll(JsonUtil.createJsonToListMap(String.valueOf(item.get("children"))));
                } catch (Exception e) {

                }
                if (children.size() > 0) {
                    dataId(children);
                    item.put("children", children);
                }
            }
        }
    }

    public static List convertToList(Object obj) {
        if (obj instanceof List) {
            List arrayList = (List) obj;
            return arrayList;
        } else {
            List arrayList = new ArrayList();
            arrayList.add(obj);
            return arrayList;
        }
    }

    public static String convertValueToString(String obj, boolean mult, boolean isOrg) {
        if (StringUtil.isNotEmpty(obj)) {
            String prefix = "[";
            if (isOrg) {
                prefix = "[[";
            }
            if (mult) {
                if (!obj.startsWith(prefix)) {
                    JSONArray arr = new JSONArray();
                    if (isOrg) {
                        //组织多选为二维数组
                        arr.add(JSONArray.parse(obj));
                    } else {
                        arr.add(obj);
                    }
                    return arr.toJSONString();
                }
            } else {
                if (obj.startsWith(prefix)) {
                    JSONArray objects = JSONArray.parseArray(obj);
                    return objects.size() > 0 ? objects.get(0).toString() : "";
                }
            }
        }
        return obj;
    }

    /**
     * 获取组织数据中的最后一级组织ID
     * 单选数据获取数组中最后一个组织本身的ID
     * 多选数据获取最后一组组织数据中的最后一个组织本身的ID
     *
     * @param data
     * @return
     */
    public static String getLastOrganizeId(Object data) {
        if (data instanceof List) {
            List listData = (List) data;
            data = listData.get(listData.size() - 1);
            return getLastOrganizeId(data);
        } else if (data instanceof String) {
            String strData = (String) data;
            if (strData.startsWith(StrPool.BRACKET_START)) {
                JSONArray jsonArray = JSONArray.parseArray(strData);
                return getLastOrganizeId(jsonArray);
            } else {
                return strData;
            }
        }
        return data.toString();
    }

    /**
     * 输入时表单时间字段根据格式转换去尾巴
     *
     * @param list 字段属性
     * @param map  数据
     */
    public static void swapDatetime(List<FieLdsModel> list, Map<String, Object> map) {
        List<FieLdsModel> fields = new ArrayList<>();
        FormPublicUtils.recursionFieldsExceptChild(fields, list);
        //主副表
        for (FieLdsModel field : fields) {
            try {
                String vModel = field.getVModel();
                String format = DateTimeFormatConstant.getFormat(field.getFormat());
                ConfigModel config = field.getConfig();
                if (map.get(vModel) != null) {
                    String s = map.get(vModel).toString();
                    if (StringUtil.isBlank(s) || "[]".equals(s) || "[[]]".equals(s)) {
                        map.replace(vModel, null);
                    }
                }

                //SQL Server text字段先这样处理。
                if (map.get(vModel) == null) {
                    String dbType = "";
                    try {
                        @Cleanup Connection connection = DynamicDataSourceUtil.getCurrentConnection();
                        dbType = connection.getMetaData().getDatabaseProductName().trim();
                    } catch (Exception e) {
                    }
                    if (ProjectKeyConsts.getTextField().contains(config.getProjectKey()) && "Microsoft SQL Server".equals(dbType)) {
                        map.put(vModel, "");
                    }
                }
                if (ProjectKeyConsts.DATE.equals(config.getProjectKey()) && map.get(vModel) != null) {
                    Date date = new Date(Long.parseLong(String.valueOf(map.get(vModel))));
                    String completionStr = "";
                    switch (format) {
                        case "yyyy":
                            completionStr = "-01-01 00:00:00";
                            break;
                        case "yyyy-MM":
                            completionStr = "-01 00:00:00";
                            break;
                        case "yyyy-MM-dd":
                            completionStr = " 00:00:00";
                            break;
                        case "yyyy-MM-dd HH":
                            completionStr = ":00:00";
                            break;
                        case "yyyy-MM-dd HH:mm":
                            completionStr = ":00";
                            break;
                        default:
                            break;
                    }
                    String datestr = com.linzen.util.DateUtil.dateToString(date, format);
                    long time = com.linzen.util.DateUtil.stringToDate(datestr + completionStr).getTime();
                    map.replace(vModel, time);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //子表
        for (FieLdsModel field : fields) {
            if (field.getVModel().toLowerCase().startsWith(ProjectKeyConsts.CHILD_TABLE_PREFIX)) {
                List<FieLdsModel> children = field.getConfig().getChildren();
                if (CollectionUtils.isNotEmpty(children)) {
                    String tableKey = field.getConfig().getTableName() + "List";
                    if (map.get(tableKey) != null) {
                        List<Object> listObj = (List) map.get(tableKey);
                        if (CollectionUtils.isEmpty(listObj)) continue;
                        List<Object> listObjNew = new ArrayList<>();
                        for (Object o : listObj) {
                            Map<String, Object> stringObjectMap = JsonUtil.entityToMap(o);
                            swapDatetime(children, stringObjectMap);
                            listObjNew.add(stringObjectMap);
                        }
                        if (CollectionUtils.isNotEmpty(listObjNew)) {
                            map.replace(tableKey, listObjNew);
                        }
                    }
                    String tableFieldKey = field.getVModel();
                    if (map.get(tableFieldKey) != null) {
                        List<Object> listObj = (List) map.get(tableFieldKey);
                        if (CollectionUtils.isEmpty(listObj)) continue;
                        List<Object> listObjNew = new ArrayList<>();
                        for (Object o : listObj) {
                            Map<String, Object> stringObjectMap = JsonUtil.entityToMap(o);
                            swapDatetime(children, stringObjectMap);
                            listObjNew.add(stringObjectMap);
                        }
                        if (CollectionUtils.isNotEmpty(listObjNew)) {
                            map.replace(tableFieldKey, listObjNew);
                        }
                    }
                }
            }

        }
    }

    /**
     * 判断组织层级是否正确
     *
     * @param comTwoList
     * @param comOneList
     * @return
     */
    private boolean orgListRight(List<List<String>> comTwoList, List<String> comOneList) {
        if (CollectionUtils.isNotEmpty(comOneList)) {
            String one = comOneList.get(comOneList.size() - 1);
            List<String> realOrgs = new ArrayList<>();
            SysOrganizeEntity organizeEntity = organizeApi.getInfo(one);
            if (organizeEntity != null) {
                if (StringUtil.isNotEmpty(organizeEntity.getOrganizeIdTree())) {
                    String[] split = organizeEntity.getOrganizeIdTree().split(",");
                    if (split.length > 0) {
                        realOrgs = Arrays.asList(split);
                    }
                    if (!JsonUtil.createObjectToString(comOneList).equals(JsonUtil.createObjectToString(realOrgs))) {
                        return true;
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(comTwoList) && comTwoList.size() > 0) {
            for (List<String> two : comTwoList) {
                if (two.size() <= 0) {
                    return false;
                }
                String one = two.get(two.size() - 1);
                List<String> realOrgs = new ArrayList<>();
                SysOrganizeEntity organizeEntity = organizeApi.getInfo(one);
                if (organizeEntity != null) {
                    if (StringUtil.isNotEmpty(organizeEntity.getOrganizeIdTree())) {
                        String[] split = organizeEntity.getOrganizeIdTree().split(",");
                        if (split.length > 0) {
                            realOrgs = Arrays.asList(split);
                        }
                        if (!JsonUtil.createObjectToString(comOneList).equals(JsonUtil.createObjectToString(realOrgs))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 视图条件组装
     *
     * @param paginationModel
     * @param columnDataModel
     * @return
     */
    public List<FieLdsModel> getQueryCondition(PaginationModel paginationModel, ColumnDataModel columnDataModel) {
        List<FieLdsModel> searchVOList = JsonUtil.createJsonToList(columnDataModel.getSearchList(), FieLdsModel.class);
        Map<String, Object> keyJsonMap = JsonUtil.stringToMap(paginationModel.getQueryJson());

        List<FieLdsModel> searchResList = new ArrayList<>();
        if (keyJsonMap == null) {
            return searchResList;
        }
        for (String key : keyJsonMap.keySet()) {
            for (FieLdsModel item : searchVOList) {
                String vModel = item.getVModel();
                if (key.equals(vModel)) {
                    if (!item.getConfig().getIsFromParam()) {//非接口参数的条件
                        FieLdsModel model = BeanUtil.copyProperties(item, FieLdsModel.class);
                        String projectKey = model.getConfig().getProjectKey();
                        switch (projectKey) {
                            case ProjectKeyConsts.COM_INPUT:
                                if (Objects.equals(model.getType(), 3)) {
                                    model.setSearchType(2);//单行输入范围调整为模糊
                                }
                                model.setFieldValue(String.valueOf(keyJsonMap.get(vModel)));
                                break;
                            case ProjectKeyConsts.NUM_INPUT:
                                model.setSearchType(3);//定义为between
                                List<Long> integerList = JsonUtil.createJsonToList(keyJsonMap.get(vModel), Long.class);
                                model.setFieldValueOne(integerList.get(0));
                                model.setFieldValueTwo(integerList.get(1));
                                break;
                            case ProjectKeyConsts.DATE:
                                model.setSearchType(3);//定义为between
                                List<Long> dateList = JsonUtil.createJsonToList(keyJsonMap.get(vModel), Long.class);
                                String timeOne = FormPublicUtils.getTimeFormat(com.linzen.util.DateUtil.dateToString(new Date(dateList.get(0)), model.getFormat()));
                                String timeTwo = FormPublicUtils.getTimeFormat(com.linzen.util.DateUtil.dateToString(new Date(dateList.get(1)), model.getFormat()));
                                model.setFieldValueOne(timeOne);
                                model.setFieldValueTwo(timeTwo);
                                break;
                            case ProjectKeyConsts.TIME:
                                model.setSearchType(3);//定义为between
                                List<String> stringList = JsonUtil.createJsonToList(keyJsonMap.get(vModel), String.class);
                                model.setFieldValueOne(stringList.get(0));
                                model.setFieldValueTwo(stringList.get(1));
                                break;
                            case ProjectKeyConsts.SELECT:
                            case ProjectKeyConsts.POSSELECT:
                            case ProjectKeyConsts.ROLESELECT:
                            case ProjectKeyConsts.GROUPSELECT:
                                model.setSearchType(4);
                                List<String> dataList = new ArrayList<>();
                                try {
                                    List<String> list = JsonUtil.createJsonToList(keyJsonMap.get(vModel), String.class);
                                    dataList.addAll(list);
                                } catch (Exception e1) {
                                    dataList.add(String.valueOf(keyJsonMap.get(vModel)));
                                }
                                model.setDataList(dataList);
                                break;
                            case ProjectKeyConsts.COMSELECT:
                                model.setSearchType(4);
                                List<String> listOrg = new ArrayList<>();
                                List<String> orgListRes = new ArrayList<>();
                                if (model.getSearchMultiple()) {
                                    List<List<String>> list = JsonUtil.createJsonToBean(keyJsonMap.get(vModel), List.class);
                                    for (List<String> a : list) {
                                        listOrg.add(a.get(a.size() - 1));
                                        orgListRes.add(JSONArray.toJSONString(a));
                                    }
                                } else {
                                    List<String> list = JsonUtil.createJsonToBean(keyJsonMap.get(vModel), List.class);
                                    listOrg.add(list.get(list.size() - 1));
                                    orgListRes.addAll(list);
                                }
                                //包含子组织
                                if (model.getIsIncludeSubordinate()) {
                                    for (String org : listOrg) {
                                        //获取子组织
                                        List<String> underOrganizations = organizeApi.getUnderOrganizations(org, false);
                                        //获取组织全路径
                                        for (String itemOrg : underOrganizations) {
                                            SysOrganizeEntity organizeEntity = organizeApi.getInfo(itemOrg);
                                            if (organizeEntity != null) {
                                                if (StringUtil.isNotEmpty(organizeEntity.getOrganizeIdTree())) {
                                                    String[] split = organizeEntity.getOrganizeIdTree().split(",");
                                                    if (split.length > 0) {
                                                        orgListRes.add(JSONArray.toJSONString(Arrays.asList(split)));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    model.setDataList(orgListRes);
                                } else {
                                    model.setDataList(orgListRes);
                                }
                                break;
                            case ProjectKeyConsts.DEPSELECT:
                                model.setSearchType(4);
                                List<String> listDep = new ArrayList<>();
                                List<String> depListRes = new ArrayList<>();
                                if (model.getSearchMultiple()) {
                                    List<String> list = BeanUtil.toBean(keyJsonMap.get(vModel), List.class);
                                    listDep.addAll(list);
                                    depListRes.addAll(list);
                                } else {
                                    listDep.add(String.valueOf(keyJsonMap.get(vModel)));
                                    depListRes.add(String.valueOf(keyJsonMap.get(vModel)));
                                }
                                //包含当前部门及子部门
                                if (model.getIsIncludeSubordinate()) {
                                    for (String org : listDep) {
                                        //获取子部门
                                        List<String> underOrganizations = organizeApi.getUnderOrganizations(org, false);
                                        depListRes.addAll(underOrganizations);
                                    }
                                    model.setDataList(depListRes);
                                } else {
                                    model.setDataList(depListRes);
                                }
                                break;
                            case ProjectKeyConsts.USERSELECT:
                                model.setSearchType(4);
                                List<String> listUser = new ArrayList<>();
                                List<String> userListRes = new ArrayList<>();
                                if (model.getSearchMultiple()) {
                                    List<String> list = BeanUtil.toBean(keyJsonMap.get(vModel), List.class);
                                    listUser.addAll(list);
                                    userListRes.addAll(list);
                                } else {
                                    listUser.add(String.valueOf(keyJsonMap.get(vModel)));
                                    userListRes.add(String.valueOf(keyJsonMap.get(vModel)));
                                }
                                //包含当前用户及下属
                                if (model.getIsIncludeSubordinate()) {
                                    for (String userId : listUser) {
                                        //获取下属用户
                                        List<SysUserEntity> underOrganizations = userService.getListByManagerId(userId, null);
                                        List<String> collect = underOrganizations.stream().map(SysUserEntity::getId).collect(Collectors.toList());
                                        userListRes.addAll(collect);
                                    }
                                    model.setDataList(userListRes);
                                } else {
                                    model.setDataList(userListRes);
                                }
                                break;
                            default:
                                model.setFieldValue(String.valueOf(keyJsonMap.get(vModel)));
                                break;
                        }
                        searchResList.add(model);
                    }
                }
            }
        }
        return searchResList;
    }

    /**
     * 关联表单获取原字段数据（数据类型也要转换）
     *
     * @param dataMap
     * @param projectKey
     * @param obj
     * @param vModel
     */
    private void relationGetLinzenId(Map<String, Object> dataMap, String projectKey, Object obj, String vModel) {
        String vModellinzenId = vModel + "_linzenId";
        switch (projectKey) {
            // 时间格式
            case ProjectKeyConsts.CREATETIME:
            case ProjectKeyConsts.MODIFYTIME:
            case ProjectKeyConsts.DATE:
                Long dateTime = DateTimeFormatConstant.getDateObjToLong(dataMap.get(vModel));
                dataMap.put(vModellinzenId, dateTime != null ? dateTime : dataMap.get(vModel));
                break;
            // 子表操作
            case ProjectKeyConsts.CHILD_TABLE:
                break;
            case ProjectKeyConsts.SWITCH:
            case ProjectKeyConsts.SLIDER:
            case ProjectKeyConsts.RATE:
            case ProjectKeyConsts.CALCULATE:
            case ProjectKeyConsts.NUM_INPUT:
                dataMap.put(vModellinzenId, ObjectUtil.isNotEmpty(obj) ? new BigDecimal(String.valueOf(obj)) : dataMap.get(vModel));
                break;
            default:
                dataMap.put(vModellinzenId, obj);
                break;
        }
        if (ProjectKeyConsts.getArraysKey().contains(projectKey) && obj != null) {
            String o = String.valueOf(obj);
            try {
                List<List> jsonToList = JsonUtil.createJsonToList(o, List.class);
                List<Object> res = new ArrayList<>();
                for (List listChild : jsonToList) {
                    List<Object> res2 = new ArrayList<>();
                    for (Object object : listChild) {
                        if (object != null && StringUtil.isNotEmpty(String.valueOf(object))) {
                            res2.add(object);
                        }
                    }
                    res.add(res2);
                }
                dataMap.put(vModellinzenId, res);
            } catch (Exception e) {
                try {
                    List<Object> jsonToList = JsonUtil.createJsonToList(o, Object.class);
                    dataMap.put(vModellinzenId, jsonToList);
                } catch (Exception e1) {
                }
            }
        }
    }
}
