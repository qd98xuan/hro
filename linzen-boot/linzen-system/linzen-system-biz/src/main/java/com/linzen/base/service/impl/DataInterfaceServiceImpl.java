package com.linzen.base.service.impl;

import cn.dev33.satoken.context.SaHolder;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.ServiceResult;
import com.linzen.base.ServiceResultCode;
import com.linzen.base.Pagination;
import com.linzen.base.UserInfo;
import com.linzen.base.entity.DataInterfaceEntity;
import com.linzen.base.entity.DataInterfaceVariateEntity;
import com.linzen.base.entity.InterfaceOauthEntity;
import com.linzen.base.mapper.DataInterfaceMapper;
import com.linzen.base.model.datainterface.*;
import com.linzen.base.service.*;
import com.linzen.base.util.DataInterfaceParamUtil;
import com.linzen.base.util.interfaceUtil.InterfaceUtil;
import com.linzen.base.vo.PaginationVO;
import com.linzen.config.OauthConfigration;
import com.linzen.constant.LinzenConst;
import com.linzen.constant.MsgCode;
import com.linzen.database.model.dto.PrepSqlDTO;
import com.linzen.database.util.DataSourceUtil;
import com.linzen.database.util.JdbcUtil;
import com.linzen.exception.DataBaseException;
import com.linzen.permission.model.datainterface.DataInterfaceConst;
import com.linzen.permission.service.OrganizeAdministratorService;
import com.linzen.permission.service.OrganizeService;
import com.linzen.util.*;
import com.linzen.util.wxutil.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
@Slf4j
public class DataInterfaceServiceImpl extends SuperServiceImpl<DataInterfaceMapper, DataInterfaceEntity> implements DataInterfaceService {
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private DbLinkService dblinkService;
    @Autowired
    private DataSourceUtil dataSourceUtils;
    @Autowired
    private DataInterfaceLogService dataInterfaceLogService;
    @Autowired
    private DataInterfaceVariateService dataInterfaceVariateService;
    @Autowired
    private OauthConfigration oauthConfigration;
    @Autowired
    private InterfaceOauthService interfaceOauthService;
    @Autowired
    private OrganizeService organizeApi;

    @Autowired
    private OrganizeAdministratorService organizeAdminTratorApi;
    @Autowired
    private DataInterfaceUserService dataInterfaceUserService;


    @Override
    public List<DataInterfaceEntity> getList(PaginationDataInterface pagination, String type, Integer isSelector) {
        // 定义变量判断是否需要使用修改时间倒序
        boolean flag = false;
        QueryWrapper<DataInterfaceEntity> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(pagination.getEnabledMark())) {
            queryWrapper.lambda().eq(DataInterfaceEntity::getEnabledMark, pagination.getEnabledMark());
        }
        //关键字
        if (!StringUtil.isEmpty(pagination.getKeyword())) {
            flag = true;
            queryWrapper.lambda().and(
                    t -> t.like(DataInterfaceEntity::getFullName, pagination.getKeyword())
                            .or().like(DataInterfaceEntity::getEnCode, pagination.getKeyword())
            );
        }
        // 是否分页
        if (pagination.getHasPage() != null && pagination.getHasPage() == 0) {
            queryWrapper.lambda().eq(DataInterfaceEntity::getHasPage, pagination.getHasPage());
        }
        if (isSelector == 1) {
            queryWrapper.lambda().eq(DataInterfaceEntity::getIsPostPosition, 0);
            if (ObjectUtil.isEmpty(pagination.getEnabledMark())) {
                queryWrapper.lambda().eq(DataInterfaceEntity::getEnabledMark, 1);
            }
        }
        //分类
        queryWrapper.lambda().eq(DataInterfaceEntity::getCategory, pagination.getCategory());
        // 类型
        if (StringUtil.isNotEmpty(type)) {
            if (type.contains(",")) {
                List<Integer> collect = Arrays.stream(type.split(",")).map(Integer::valueOf).collect(Collectors.toList());
                queryWrapper.lambda().in(DataInterfaceEntity::getType, collect);
            } else {
                queryWrapper.lambda().eq(DataInterfaceEntity::getType, Integer.valueOf(type));
            }
        }
        //排序
        queryWrapper.lambda().orderByAsc(DataInterfaceEntity::getSortCode)
                .orderByDesc(DataInterfaceEntity::getCreatorTime);
        if (flag) {
            queryWrapper.lambda().orderByDesc(DataInterfaceEntity::getUpdateTime);
        }
        Page<DataInterfaceEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<DataInterfaceEntity> iPage = this.page(page, queryWrapper);
        return pagination.setData(iPage.getRecords(), iPage.getTotal());
    }

    @Override
    public List<DataInterfaceEntity> getList(boolean filterPage) {
        QueryWrapper<DataInterfaceEntity> queryWrapper = new QueryWrapper<>();
        if (filterPage) {
            queryWrapper.lambda().ne(DataInterfaceEntity::getHasPage, 1);
        }
        queryWrapper.lambda().eq(DataInterfaceEntity::getEnabledMark, 1)
                .orderByAsc(DataInterfaceEntity::getSortCode)
                .orderByDesc(DataInterfaceEntity::getCreatorTime);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public DataInterfaceEntity getInfo(String id) {
        QueryWrapper<DataInterfaceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DataInterfaceEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(DataInterfaceEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreatorUserId(userProvider.get().getUserId());
        entity.setCreatorTime(DateUtil.getNowDate());
        entity.setUpdateTime(null);
        entity.setUpdateUserId(null);
        this.setIgnoreLogicDelete().saveOrUpdate(entity);
        this.clearIgnoreLogicDelete();
    }

    @Override
    public boolean update(DataInterfaceEntity entity, String id) throws DataBaseException {
        entity.setId(id);
        entity.setUpdateUserId(userProvider.get().getUserId());
        entity.setUpdateTime(DateUtil.getNowDate());
        return this.updateById(entity);
    }

    @Override
    public void delete(DataInterfaceEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public boolean isExistByFullNameOrEnCode(String id, String fullName, String enCode) {
        QueryWrapper<DataInterfaceEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(fullName)) {
            queryWrapper.lambda().eq(DataInterfaceEntity::getFullName, fullName);
        }
        if (StringUtil.isNotEmpty(enCode)) {
            queryWrapper.lambda().eq(DataInterfaceEntity::getEnCode, enCode);
        }
        if (StringUtil.isNotEmpty(id)) {
            queryWrapper.lambda().ne(DataInterfaceEntity::getId, id);
        }
        return this.count(queryWrapper) > 0;
    }

    @Override
    public ServiceResult infoToIdPageList(String id, DataInterfacePage page) {
        DataInterfaceEntity entity = this.getInfo(id);
        if (entity == null) {
            return ServiceResult.pageList(new ArrayList<>(), BeanUtil.toBean(new Pagination(), PaginationVO.class));
        }
        if (entity.getHasPage() == 1) {
            Map<String, String> map = null;
            if (page.getParamList() != null) {
                map = new HashMap<>();
                List<DataInterfaceModel> jsonToList = JsonUtil.createJsonToList(page.getParamList(), DataInterfaceModel.class);
                for (DataInterfaceModel dataInterfaceModel : jsonToList) {
                    String defaultValue = dataInterfaceModel.getDefaultValue();
                    map.put(dataInterfaceModel.getField(), defaultValue);
                }
            }
            Pagination pagination = new Pagination();
            pagination.setPageSize(page.getPageSize());
            pagination.setCurrentPage(page.getCurrentPage());
            pagination.setKeyword(page.getKeyword());
            return infoToId(id, null, map, null, null, null, pagination, null);
        } else {
            String dataProcessing = null;
            if (StringUtil.isNotEmpty(entity.getDataJsJson())) {
                dataProcessing = entity.getDataJsJson();
            }
            List<Map<String, Object>> dataList = new ArrayList<>();
            int total = 0;
            Map<String, String> map = null;
            if (page.getParamList() != null) {
                map = new HashMap<>();
                List<DataInterfaceModel> jsonToList = JsonUtil.createJsonToList(page.getParamList(), DataInterfaceModel.class);
                for (DataInterfaceModel dataInterfaceModel : jsonToList) {
                    String defaultValue = dataInterfaceModel.getDefaultValue();
                    map.put(dataInterfaceModel.getField(), defaultValue);
                }
            }
            ServiceResult result = infoToId(id, null, map);
            if (result.getData() != null) {
                if (result.getData() instanceof List) {
                    dataList = (List<Map<String, Object>>) result.getData();
                }
            }
            if (StringUtil.isNotEmpty(page.getKeyword()) && StringUtil.isNotEmpty(page.getRelationField())) {
                dataList = dataList.stream().filter(t -> String.valueOf(t.get(page.getRelationField())).contains(page.getKeyword())).collect(Collectors.toList());
            }
            PaginationVO pagination = new PaginationVO();
            page.setTotal(dataList.size());
            if (StringUtil.isNotEmpty(page.getKeyword()) && StringUtil.isNotEmpty(page.getColumnOptions())) {
                String[] colOptions = page.getColumnOptions().split(",");
                dataList = dataList.stream().filter(t -> {
                    boolean isFit = false;
                    for (String c : colOptions) {
                        if (String.valueOf(t.get(c)).contains(page.getKeyword())) {
                            isFit = true;
                            break;
                        }
                    }
                    return isFit;
                }).collect(Collectors.toList());
            }
            dataList = PageUtil.getListPage((int) page.getCurrentPage(), (int) page.getPageSize(), dataList);
            pagination = BeanUtil.toBean(page, PaginationVO.class);
            return ServiceResult.pageList(dataList, pagination, dataProcessing);
        }
    }

    @Override
    public List<Map<String, Object>> infoToInfo(String id, DataInterfacePage page) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, String> map = null;
        DataInterfaceEntity entity = this.getInfo(id);
        if (entity == null) {
            return new ArrayList<>();
        }
        try {
            if (entity.getHasPage() == 1) {
                if (page.getParamList() != null) {
                    map = new HashMap<>();
                    List<DataInterfaceModel> jsonToList = JsonUtil.createJsonToList(page.getParamList(), DataInterfaceModel.class);
                    for (DataInterfaceModel dataInterfaceModel : jsonToList) {
                        String defaultValue = dataInterfaceModel.getDefaultValue();
                        map.put(dataInterfaceModel.getField(), defaultValue);
                    }
                }
                Map<String, Object> showMap = new HashMap<>();
                if (page.getIds() instanceof List) {
                    List<Object> ids = (List<Object>) page.getIds();
                    Map<String, String> finalMap = map;
                    ids.forEach(t -> {
                        showMap.put(page.getPropsValue(), t);
                        ServiceResult result = infoToId(id, null, finalMap, null, null, null, null, showMap);
                        if (result.getData() instanceof Map) {
                            Map<String, Object> objectMap = (Map<String, Object>) result.getData();
                            if (objectMap.size() > 0) {
                                List<Map> mapList = JsonUtil.createJsonToList(objectMap.get("list"), Map.class);
                                if (mapList != null && mapList.size() > 0) {
                                    list.add(mapList.get(0));
                                } else {
                                    list.add(objectMap);
                                }
                            }
                        } else if (result.getData() instanceof List) {
                            List<Map> list1 = (List<Map>) result.getData();
                            if (list1.size() > 0) {
                                list.add(list1.get(0));
                            }
                        } else {

                        }
                    });
                }
            } else {
                if (page.getIds() != null) {
                    Map<String, Object> dataMap = new HashMap<>();
                    if (page.getParamList() != null) {
                        map = new HashMap<>();
                        List<DataInterfaceModel> jsonToList = JsonUtil.createJsonToList(page.getParamList(), DataInterfaceModel.class);
                        for (DataInterfaceModel dataInterfaceModel : jsonToList) {
                            map.put(dataInterfaceModel.getField(), dataInterfaceModel.getDefaultValue());
                        }
                    }
                    ServiceResult result = infoToId(id, null, map);
                    List<Map<String, Object>> dataList = new ArrayList<>();
                    if (result.getData() instanceof List) {
                        dataList = (List<Map<String, Object>>) result.getData();
                        List<String> ids = (List<String>) page.getIds();
                        List<Map<String, Object>> finalDataList = dataList;
                        ids.forEach(t -> {
                            list.add(finalDataList.stream().filter(data -> t.equals(String.valueOf(data.get(page.getPropsValue())))).findFirst().orElse(new HashMap<>()));
                        });
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
        return list;
    }

    @Override
    public ServiceResult infoToId(String id, String tenantId, Map<String, String> map) {
        return infoToId(id, tenantId, map, null, null, null, null, null);
    }

    @Override
    public ServiceResult infoToId(String id, String tenantId, Map<String, String> map, String token, String appId, String invokType, Pagination pagination, Map<String, Object> showMap) {
        DataInterfaceEntity entity = this.getInfo(id);
        if (entity == null) {
            return ServiceResult.success(new ArrayList<>());
        }
        // 开始调用的时间
        LocalDateTime dateTime = LocalDateTime.now();
        //调用时间
        int invokWasteTime = 0;
        // 有设置默认值的直接赋值
        replaceDefaultVale(entity.getParameterJson(), map);
        // 验证参数必填或类型
        String checkRequestParams = checkRequestParams(entity.getParameterJson(), map, null);
        if (StringUtil.isNotEmpty(checkRequestParams)) {
            return ServiceResult.error(checkRequestParams);
        }
        Object callJs = null;
        try {

            if (pagination == null) {
                pagination = new Pagination();
            }

            // 数据配置
            String dataConfigJson = entity.getDataConfigJson();
            DataConfigJsonModel configJsonModel = BeanUtil.toBean(dataConfigJson, DataConfigJsonModel.class);
            // 如果是静态数据
            if (entity.getType() == 2) {
                String staticData = configJsonModel.getStaticData();
                Object object = callStaticData(staticData);
                handlePostVariate(entity, object);
                return ServiceResult.success(object);
            } else if (entity.getType() == 3) {
                // HTTP调用或HTTPS调用
                JSONObject jsonObject = new JSONObject();
                if (showMap == null) {
                    if (entity.getHasPage() == 0) {
                        pagination = null;
                    }
                    //HTTP调用或HTTPS调用
                    jsonObject = callHTTP(map, token, pagination, null, configJsonModel.getApiData());
                } else {
                    String echoJson = entity.getDataEchoJson();
                    DataConfigJsonModel echoJsonModel = BeanUtil.toBean(echoJson, DataConfigJsonModel.class);
                    jsonObject = callHTTP(map, token, null, showMap, echoJsonModel.getApiData());
                }
                if (Objects.nonNull(jsonObject) && "1".equals(jsonObject.get("errorCode"))) {
                    return ServiceResult.error("接口暂只支持HTTP和HTTPS方式");
                }
                // 判断返回参数长度和key是否跟内置的一致
                if (jsonObject == null) {
                    return ServiceResult.error("接口请求失败");
                }
                handlePostVariate(entity, jsonObject);
                Object js = JScriptUtil.callJs(entity.getDataExceptionJson(), jsonObject.get("data") == null ? new ArrayList<>() : jsonObject.get("data"));
                if ((js instanceof Boolean && !BooleanUtil.toBoolean(String.valueOf(js)))) {
                    // 继续执行接口
                    if (showMap == null) {
                        // 处理变量
                        handlerVariate(configJsonModel.getApiData());
                        jsonObject = callHTTP(map, token, pagination, null, configJsonModel.getApiData());
                    } else {
                        String echoJson = entity.getDataEchoJson();
                        DataConfigJsonModel echoJsonModel = BeanUtil.toBean(echoJson, DataConfigJsonModel.class);
                        // 处理变量
                        handlerVariate(echoJsonModel.getApiData());
                        jsonObject = callHTTP(map, token, null, showMap, echoJsonModel.getApiData());
                    }
                }
                if (isInternal(jsonObject)) {
                    callJs = JScriptUtil.callJs(entity.getDataJsJson(), jsonObject.get("data") == null ? new ArrayList<>() : jsonObject.get("data"));
                } else {
                    callJs = JScriptUtil.callJs(entity.getDataJsJson(), jsonObject);
                }
            } else if (entity.getType() == 1) {
                UserInfo oldUser = null;
                if (token != null) {
                    oldUser = UserProvider.getUser();
                    UserInfo userInfo = UserProvider.getUser(token);
                    UserProvider.setLocalLoginUser(userInfo);
                }
                try {
                    if (showMap == null) {
                        List<Map<String, Object>> sqlMapList = executeSql(entity, 0, map, pagination, null, configJsonModel.getSqlData());
                        handlePostVariate(entity, sqlMapList);
                        callJs = JScriptUtil.callJs(entity.getDataJsJson(), sqlMapList == null ? new ArrayList<>() : sqlMapList);
                        if (entity.getHasPage() == 1) {
                            DataConfigJsonModel pageJsonModel = BeanUtil.toBean(entity.getDataConfigJson(), DataConfigJsonModel.class);
                            List<Map<String, Object>> maps = executeSql(entity, 1, map, pagination, null, pageJsonModel.getSqlData());
                            if (maps.get(0) != null) {
                                pagination.setTotal(Long.parseLong(String.valueOf(maps.get(0).values().iterator().next())));
                            }
                            return ServiceResult.pageList(sqlMapList, BeanUtil.toBean(pagination, PaginationVO.class));
                        }
                    } else {
                        DataConfigJsonModel echoJsonModel = BeanUtil.toBean(entity.getDataEchoJson(), DataConfigJsonModel.class);
                        List<Map<String, Object>> sqlMapList = executeSql(entity, 2, map, pagination, showMap, echoJsonModel.getSqlData());
                        callJs = JScriptUtil.callJs(entity.getDataJsJson(), sqlMapList == null || sqlMapList.size() == 0 ? new ArrayList<>() : sqlMapList.get(0));
                    }
                } finally {
                    if (oldUser != null) {
                        UserProvider.setLocalLoginUser(oldUser);
                    }
                }
            }
            if (callJs instanceof Exception) {
                return ServiceResult.success("接口请求失败", "JS调用失败,错误：" + ((Exception) callJs).getMessage());
            }
            return ServiceResult.success(callJs);
        } catch (Exception e) {
            log.error("错误提示:" + e.getMessage());
            // 本地调试时打印出问题
            e.printStackTrace();
            return ServiceResult.error("接口请求失败");
        } finally {
            // 调用时间
            invokWasteTime = invokTime(dateTime);
            // 添加调用日志
            dataInterfaceLogService.create(id, invokWasteTime, appId, invokType);
        }
    }

    /**
     * 预览时赋值变量
     *
     * @param entity
     * @param object
     */
    private void handlePostVariate(DataInterfaceEntity entity, Object object) {
        // 如果是鉴权的话，需要赋值value
        if (entity.getIsPostPosition() == 1) {
            List<DataInterfaceVariateEntity> list = dataInterfaceVariateService.getList(entity.getId(), null);
            list.forEach(t -> {
                try {
                    Object o = JScriptUtil.callJs(t.getExpression(), object);
                    if (o != null) {
                        t.setValue(o.toString());
                        dataInterfaceVariateService.update(t);
                    }
                } catch (ScriptException e) {

                }
            });
        }
    }

    @Override
    public List<DataInterfaceEntity> getList(List<String> ids) {
        if (ids.size() == 0) {
            return new ArrayList<>();
        }
        QueryWrapper<DataInterfaceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(DataInterfaceEntity::getId, ids);
        return this.list(queryWrapper);
    }

    /**
     * 处理静态数据
     *
     * @param staticData
     * @return
     */
    private Object callStaticData(String staticData) {
        Object obj;
        try {
            Object parse = JSON.parse(staticData);
            if (parse instanceof JSONArray) {
                obj = JsonUtil.createJsonToListMap(staticData);
            } else {
                obj = JsonUtil.stringToMap(staticData);
            }
        } catch (Exception e) {
            obj = staticData;
        }
        if (ObjectUtils.isEmpty(obj)) {
            return new ArrayList<>();
        }
        return obj;
    }

    /**
     * 有设置默认值的直接赋值
     *
     * @param parameterJson
     * @param map
     */
    private void replaceDefaultVale(String parameterJson, Map<String, String> map) {
        List<DataInterfaceModel> dataInterfaceModelList = JsonUtil.createJsonToList(parameterJson, DataInterfaceModel.class);
        if (ObjectUtils.isNotEmpty(dataInterfaceModelList)) {
            if (map == null) {
                map = new HashMap<>(16);
            }
            for (DataInterfaceModel dataInterfaceModel : dataInterfaceModelList) {
                String field = dataInterfaceModel.getField();
                String defaultValue = dataInterfaceModel.getDefaultValue();
                String dataType = dataInterfaceModel.getDataType();
                if (!map.containsKey(field) || StringUtil.isEmpty(map.get(field))) {
                    map.put(field, defaultValue == null ? "" : defaultValue);
                }
//                if (dataType.equals("int") && StringUtil.isEmpty(map.get(field))) {
//                    map.put(field, "0");
//                }
            }
        }
    }

    /**
     * 判断是不是内部接口
     *
     * @param jsonObject
     * @return
     */
    private boolean isInternal(JSONObject jsonObject) {
        if (jsonObject != null) {
            if (jsonObject.size() == 3 && jsonObject.get("code") != null && jsonObject.get("msg") != null && jsonObject.get("data") != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查参数是够必填或类型是否正确
     *
     * @param parameterJson
     * @param map
     * @param sql           预留参数
     */
    private String checkRequestParams(String parameterJson, Map<String, String> map, String sql) {
        if (map == null || StringUtil.isEmpty(parameterJson)) {
            return "";
        }
        StringBuilder message = new StringBuilder();
        List<DataInterfaceModel> dataInterfaceModelList = JsonUtil.createJsonToList(parameterJson, DataInterfaceModel.class);
        dataInterfaceModelList.stream().anyMatch(model -> {
            // 验证是否必填
            if (model.getRequired() == 1) {
                String value = map.get(model.getField());
                if (StringUtil.isEmpty(value)) {
                    message.append(model.getField()).append("不能为空");
                }
            }
            if (message.length() == 0) {
                // 验证类型
                if (model.getDataType() != null) {
                    String value = map.get(model.getField());
                    // 判断是整形
                    if (StringUtil.isNotEmpty(value) && "int".equals(model.getDataType())) {
                        try {
                            Integer.parseInt(value);
                        } catch (Exception e) {
                            message.append(model.getField()).append("类型必须为整型");
                        }
                    } else if (StringUtil.isNotEmpty(value) && "datetime".equals(model.getDataType())) {
                        try {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            map.put(model.getField(), DateUtil.dateFormat(formatter.parse(value)));
                        } catch (Exception e) {
                            try {
                                map.put(model.getField(), DateUtil.dateFormat(new Date(Long.valueOf(value))));
                            } catch (Exception ex) {
                                message.append(model.getField() + "类型必须为日期");
                            }
                        }
                    } else if (StringUtil.isNotEmpty(value) && "decimal".equals(model.getDataType())) {
                        try {
                            Double.valueOf(value);
                        } catch (Exception e) {
                            message.append(model.getField()).append("类型必须为浮点型");
                        }
                    }
                }
            }
            return message.length() > 0;
        });
        return message.toString();
    }


    @Override
    public ServiceResult infoToIdNew(String id, String tenantId, DataInterfaceActionModel model) {
        //鉴权验证
        // 获取token
        String authorSignature = ServletUtil.getRequest().getHeader(Constants.AUTHORIZATION);
        String[] authorSignatureArr = authorSignature.split(":");
        if (authorSignatureArr.length != 3) {
            return ServiceResult.error(ServiceResultCode.ValidateError.getMessage());
        }
        String appId = authorSignatureArr[0];
        String author = authorSignatureArr[2];
        Map<String, String> map = model.getMap();
        String interfaceUserToken = null;
        InterfaceOauthEntity infoByAppId = interfaceOauthService.getInfoByAppId(appId);
        //未提供app相关，接口认证失效，接口不在授权列表时无权访问
        if (infoByAppId == null || infoByAppId.getEnabledMark() == 0 || !infoByAppId.getDataInterfaceIds().contains(id)) {
            return ServiceResult.error(MsgCode.WF122.get());
        }
        if (infoByAppId.getVerifySignature() == 1) {//验证开启
            try {
                //验证请求有效期1分钟内
                String ymdateStr = ServletUtil.getRequest().getHeader(InterfaceUtil.YMDATE);
                Date ymdate = new Date(Long.parseLong(ymdateStr));
                Date time = DateUtil.dateAddMinutes(ymdate, 1);
                if (DateUtil.getNowDate().after(time)) {
                    return ServiceResult.error("验证请求超时");
                }
                //验证签名有效性
                boolean flag = InterfaceUtil.verifySignature(infoByAppId.getAppSecret(), author);
                if (!flag) {
                    return ServiceResult.error(ServiceResultCode.ValidateError.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ServiceResult.error(ServiceResultCode.ValidateError.getMessage());
            }
        } else {//验证未开启，直接使用秘钥进行验证
            if (!infoByAppId.getAppSecret().equals(author)) {
                return ServiceResult.error("appSecret错误");
            }
        }
        //验证使用期限
        Date usefulLife = infoByAppId.getUsefulLife();
        if (infoByAppId.getUsefulLife() != null && usefulLife.before(DateUtil.getNowDate())) {//空值无限期
            return ServiceResult.error("appId使用期限已到期");
        }
        try {
            //用户秘钥获取token
            interfaceUserToken = dataInterfaceUserService.getInterfaceUserToken(model.getTenantId(), infoByAppId.getId(), ServletUtil.getRequest().getHeader(InterfaceUtil.USERKEY));
        } catch (Exception e) {
            return ServiceResult.error(e.getMessage());
        }
        //黑白名单验证
        String ipwhiteList = StringUtil.isNotEmpty(infoByAppId.getWhiteList()) ? infoByAppId.getWhiteList() : "";//ip白名单
        String ipwhiteBlackList = StringUtil.isNotEmpty(infoByAppId.getBlackList()) ? infoByAppId.getBlackList() : "";//ip黑名单
        String ipAddr = IpUtil.getIpAddr();
        if (StringUtil.isNotEmpty(ipwhiteList) && !ipwhiteList.contains(ipAddr)) {//不属于白名单
            return ServiceResult.error(MsgCode.LOG010.get());
        }
//        if (StringUtil.isNotEmpty(ipwhiteBlackList) && ipwhiteBlackList.contains(ipAddr)) {//属于黑名单
//            return ServiceResult.error(ServiceResultCode.ValidateError.getMessage());
//        }
        //以下调用接口
        return infoToId(id, null, map, interfaceUserToken, infoByAppId.getAppId(), model.getInvokType(), null, null);
    }


    @Override
    public DataInterfaceActionModel checkParams(Map<String, String> map) {
        String ymDate = ServletUtil.getRequest().getHeader(InterfaceUtil.YMDATE);
        String authorSignature = ServletUtil.getRequest().getHeader(Constants.AUTHORIZATION);
        if (StringUtils.isEmpty(ymDate)) {
            throw new RuntimeException("header参数：YmDate未传值");
        }
        if (StringUtils.isEmpty(authorSignature)) {
            throw new RuntimeException("header参数：" + Constants.AUTHORIZATION + "未传值");
        }
        DataInterfaceActionModel entity = new DataInterfaceActionModel();
        //判断是否多租户，取参数tenantId
        if (InterfaceUtil.checkParam(map, "tenantId")) {
            entity.setTenantId(map.get("tenantId"));
        }
        String tenantId = SaHolder.getRequest().getParam("tenantId");
        if (StringUtil.isNotEmpty(tenantId)) {
            entity.setTenantId(tenantId);
        }
        entity.setMap(map);
        return entity;
    }

    /**
     * 执行SQL
     *
     * @param entity
     * @param sqlType
     * @param map
     * @return
     * @throws DataBaseException
     */
    private List<Map<String, Object>> executeSql(DataInterfaceEntity entity, int sqlType, Map<String, String> map,
                                                 Pagination pagination, Map<String, Object> showMap,
                                                 SqlDateModel sqlDateModel) throws Exception {
        DataSourceUtil dataSourceUtil = dblinkService.getInfo(sqlDateModel.getDbLinkId());
        String sql = sqlDateModel.getSql();
        if (entity.getHasPage() == 1) {
            if (sqlType == 1) {
                DataConfigJsonModel dataConfigJsonModel = JsonUtil.createJsonToBean(entity.getDataCountJson(), DataConfigJsonModel.class);
                if (dataConfigJsonModel != null) {
                    SqlDateModel countSqlDateModel = BeanUtil.toBean(dataConfigJsonModel.getSqlData(), SqlDateModel.class);
                    sql = countSqlDateModel.getSql();
                }
            } else if (sqlType == 2) {
                DataConfigJsonModel dataConfigJsonModel = JsonUtil.createJsonToBean(entity.getDataEchoJson(), DataConfigJsonModel.class);
                if (dataConfigJsonModel != null) {
                    SqlDateModel countSqlDateModel = BeanUtil.toBean(dataConfigJsonModel.getSqlData(), SqlDateModel.class);
                    sql = countSqlDateModel.getSql();
                }
            }
        }
        UserInfo userInfo = userProvider.get();
        if (dataSourceUtil == null) {
            dataSourceUtil = dataSourceUtils;
        }
        // 系统内置参数替换
        Map<Double, DataInterfaceMarkModel> systemParameter = systemParameter(sql, userInfo, pagination, showMap);
        // 自定义参数替换
        sql = customizationParameter(entity.getParameterJson(), sql, map, systemParameter);

        // 处理SQL
        List<Object> values = new ArrayList<>(systemParameter.size());
        // 参数替换为占位符
        sql = getHandleArraysSql(sql, values, systemParameter);
        if (showMap != null) {
            sql = sql.replace(DataInterfaceConst.SHOWKEY, showMap.keySet().iterator().next());
        }

        //封装sql---视图查询 -重新封装sql
        if (StringUtil.isNotEmpty(sql) && Objects.nonNull(map)
                && StringUtil.isNotEmpty(map.get("searchSqlStr")) && Objects.equals(entity.getAction(), 3)) {
            if (sql.trim().endsWith(";")) {
                sql = sql.trim();
                sql = sql.substring(0, sql.length() - 1);
            }
            sql = "SELECT * FROM (" + sql + ") as T WHERE " + map.get("searchSqlStr") + ";";
        }

        log.info("当前执行的SQL语句是：" + sql);
        if (entity.getHasPage() == 1 && (sql.contains(";") && sql.trim().indexOf(";") != sql.trim().length() - 1)) {
            return null;
        }
        if (entity.getAction() != null && entity.getAction() != 3) {
            JdbcUtil.creUpDe(new PrepSqlDTO(sql, values).withConn(dataSourceUtil, null));
            return null;
        }
        String objectToString = JsonUtil.createObjectToStringDate(JdbcUtil.queryList(new PrepSqlDTO(sql, values).withConn(dataSourceUtil, null)).setIsAlias(true).get());
        return JsonUtil.createJsonToListMap(objectToString);
    }

    /**
     * 自定义参数替换
     *
     * @param parameterJson   参数配置
     * @param sql             sql
     * @param map             参数
     * @param systemParameter 参数集合
     */
    private String customizationParameter(String parameterJson, String sql, Map<String, String> map,
                                          Map<Double, DataInterfaceMarkModel> systemParameter) {
        if (StringUtil.isNotEmpty(sql) && Objects.nonNull(map)) {
            Map<String, String> placeholderMap = new HashMap<>();
            for (String key : map.keySet()) {
                // 验证参数key对比
                String tmpValue = map.get(key);
                if (tmpValue != null) {
                    //参数前方 上个参数后方的语句中是否有 in
                    String sqlarr1 = sql.split("\\{" + key + "\\}")[0];
                    String[] sqlarr2 = sqlarr1.split("\\}");
                    String sql1 = sqlarr2.length > 1 ? sqlarr2[sqlarr2.length - 1] : sqlarr2[0];
                    boolean isInSql = sql1.toLowerCase().contains(" in ");
                    List<String> valueList;
                    if (isInSql) {
                        valueList = Arrays.asList(tmpValue.split(","));
                    } else {
                        valueList = Arrays.asList(new String[]{tmpValue});
                    }
                    String placeholder = "?";
                    for (int i = 1; i < valueList.size(); i++) {
                        placeholder += ",?";
                    }
                    String finalSql = sql;
                    valueList.forEach(t -> {
                        DataInterfaceParamUtil.getParamModel(systemParameter, finalSql, "{" + key + "}", t);
                    });
                    placeholderMap.put(key, placeholder);
                } else {
                    DataInterfaceParamUtil.getParamModel(systemParameter, sql, "{" + key + "}", null);
                    placeholderMap.put(key, "?");
                }
            }
            for (String key : placeholderMap.keySet()) {
                sql = sql.replaceAll("\\{" + key + "}", placeholderMap.get(key));
            }
        }
        return sql;
    }

    /**
     * 参数替换为占位符
     *
     * @param sql
     * @param values
     * @param systemParameter
     * @return
     */
    private String getHandleArraysSql(String sql, List<Object> values, Map<Double, DataInterfaceMarkModel> systemParameter) {
        if (StringUtil.isNotEmpty(sql)) {
            for (Double aDouble : systemParameter.keySet()) {
                Object value = systemParameter.get(aDouble).getValue();
                values.add(value);
            }
            for (Double aDouble : systemParameter.keySet()) {
                DataInterfaceMarkModel dataInterfaceMarkModel = systemParameter.get(aDouble);
                if (DataInterfaceConst.ORGANDSUB.equals(dataInterfaceMarkModel.getMarkName())) {
                    if (dataInterfaceMarkModel.getValue() instanceof List) {
                        List<Object> list = (List) dataInterfaceMarkModel.getValue();
                        String placeholder = "?";
                        int index = 0;
                        boolean addOrSet = false;
                        for (Object obj : list) {
                            placeholder += ",?";
                            if (!addOrSet) {
                                // 得到下标
                                int i = values.indexOf(dataInterfaceMarkModel.getValue());
                                values.set(i, obj);
                                addOrSet = true;
                                index = i++;
                            } else {
                                values.add(index, obj);
                            }
                        }
                        sql = sql.replaceAll(DataInterfaceConst.ORGANDSUB, placeholder);
                    }
                }
                if (DataInterfaceConst.USERANDSUB.equals(dataInterfaceMarkModel.getMarkName())) {
                    if (dataInterfaceMarkModel.getValue() instanceof List) {
                        List<Object> list = (List) dataInterfaceMarkModel.getValue();
                        String placeholder = "?";
                        int index = 0;
                        boolean addOrSet = false;
                        for (Object obj : list) {
                            placeholder += ",?";
                            if (!addOrSet) {
                                // 得到下标
                                int i = values.indexOf(dataInterfaceMarkModel.getValue());
                                values.set(i, obj);
                                addOrSet = true;
                                index = i++;
                            } else {
                                values.add(index, obj);
                            }
                        }
                        sql = sql.replaceAll(DataInterfaceConst.USERANDSUB, placeholder);
                    }
                }
                if (DataInterfaceConst.CHARORG.equals(dataInterfaceMarkModel.getMarkName())) {
                    if (dataInterfaceMarkModel.getValue() instanceof List) {
                        List<Object> list = (List) dataInterfaceMarkModel.getValue();
                        String placeholder = "?";
                        int index = 0;
                        boolean addOrSet = false;
                        for (Object obj : list) {
                            placeholder += ",?";
                            if (!addOrSet) {
                                // 得到下标
                                int i = values.indexOf(dataInterfaceMarkModel.getValue());
                                values.set(i, obj);
                                addOrSet = true;
                                index = i++;
                            } else {
                                values.add(index, obj);
                            }
                        }
                        sql = sql.replaceAll(DataInterfaceConst.CHARORG, placeholder);
                    }
                }
            }
            sql = sql.replaceAll(DataInterfaceConst.USER, "?");
            sql = sql.replaceAll(DataInterfaceConst.ORG, "?");
            sql = sql.replaceAll(DataInterfaceConst.KEYWORD, "?");
            sql = sql.replaceAll(DataInterfaceConst.OFFSETSIZE, "?");
            sql = sql.replaceAll(DataInterfaceConst.PAGESIZE, "?");
//            sql = sql.replaceAll(DataInterfaceVarEnum.SHOWKEY, "?");
            sql = sql.replaceAll(DataInterfaceConst.SHOWVALUE, "?");
            sql = sql.replaceAll(DataInterfaceConst.ID, "?");
            sql = sql.replaceAll(DataInterfaceConst.ID_LOT, "?");
        }
        return sql;
    }

    /**
     * HTTP调用
     *
     * @return get
     */
    private JSONObject callHTTP(Map<String, String> map,
                                String token, Pagination pagination, Map<String, Object> showMap,
                                ApiDateModel apiDateModel) throws UnsupportedEncodingException {
        JSONObject get = new JSONObject();
        String path = apiDateModel.getUrl();
        // 请求方法
        String requestMethod = apiDateModel.getMethod() == 1 ? "GET" : "POST";
        // 获取请求头参数
        List<HeadModel> header = apiDateModel.getHeader();
        // 自定义参数
        List<HeadModel> query = apiDateModel.getQuery();
        String body = apiDateModel.getBody();
        int bodyType = apiDateModel.getBodyType() == 1 ? 0 : apiDateModel.getBodyType() == null ? 0 : apiDateModel.getBodyType();
        // Post请求拼接参数
        JSONObject jsonObject = null;
        List<JSONObject> jsonObjects1 = null;
        //判断是否为http或https
        if (StringUtil.isNotEmpty(path) && path.startsWith("/")) {
            path = oauthConfigration.getLinzenDomain() + path;
        }
        if (path.startsWith("http")) {
            String showKey = null;
            Object showValue = null;
            if (showMap != null) {
                if (showMap.size() > 0) {
                    showKey = showMap.keySet().iterator().next();
                    showValue = showMap.values().iterator().next();
                }
            }
            // 替换url上的回显参数
            path = path.replace("{" + DataInterfaceConst.SHOWKEY.replaceAll("@", "") + "}", showKey != null ? showKey : "");
            path = path.replace("{" + DataInterfaceConst.SHOWVALUE.replaceAll("@", "") + "}", showValue != null ? URLEncoder.encode(String.valueOf(showValue), "UTF-8") : "");
            //请求参数解析
            if (query != null) {
                // 判断是否为get，get从url上拼接
                path += !path.contains("?") ? "?" : "&";
                for (HeadModel headModel : query) {
                    if ("1".equals(headModel.getSource())) {
                        if (map != null && map.containsKey(headModel.getDefaultValue())) {
                            String value = map.get(headModel.getDefaultValue());
                            path += headModel.getField() + "=" + URLEncoder.encode(StringUtil.isNotEmpty(value) ? value : ""
//                                    .replaceAll("'", "")
                                    , "UTF-8") + "&";
                        } else {
                            path += headModel.getField() + "=" + URLEncoder.encode(StringUtil.isNotEmpty(map.get(headModel.getDefaultValue())) ? map.get(headModel.getDefaultValue()) : ""
//                                    .replaceAll("'", "")
                                    , "UTF-8") + "&";
                        }
                    }
                    if ("2".equals(headModel.getSource())) {
                        DataInterfaceVariateEntity variateEntity = dataInterfaceVariateService.getInfo(headModel.getDefaultValue());
                        path += headModel.getField() + "=" + variateEntity.getValue() + "&";
                    }
                    if ("3".equals(headModel.getSource())) {
                        path += headModel.getField() + "=" + URLEncoder.encode(StringUtil.isNotEmpty(headModel.getDefaultValue()) ? headModel.getDefaultValue() : ""
//                                .replaceAll("'", "")
                                , "UTF-8") + "&";
                    }
                    // 分页参数
                    if ("4".equals(headModel.getSource())) {
                        Map<String, Object> map1 = JsonUtil.entityToMap(pagination);

                        Object urlValue = map1.get(headModel.getDefaultValue());
                        if (urlValue instanceof String && ObjectUtil.isNotNull(urlValue)) {
                            path += headModel.getField() + "=" + URLEncoder.encode(String.valueOf(urlValue), "UTF-8") + "&";
                        } else {
                            path += headModel.getField() + "=" + urlValue + "&";
                        }
                    }
                    // 回显参数
                    if ("5".equals(headModel.getSource())) {
                        if (DataInterfaceConst.SHOWKEY.equals(headModel.getDefaultValue())) {
                            if (showKey != null) {
                                path += headModel.getField() + "=" + URLEncoder.encode(showKey, "UTF-8") + "&";
                            }
                        } else {
                            if (showValue != null) {
                                path += headModel.getField() + "=" + URLEncoder.encode(String.valueOf(showValue), "UTF-8") + "&";
                            } else {
                                path += headModel.getField() + "&";
                            }
                        }
                    }
                }
            }

            String jsonObjects = null;
            if (bodyType == 1 || bodyType == 2) {
                List<HeadModel> bodyJson = JsonUtil.createJsonToList(body, HeadModel.class);
                for (HeadModel headModel : bodyJson) {
                    if ("1".equals(headModel.getSource())) {
                        if (map != null && map.containsKey(headModel.getDefaultValue())) {
                            String value = StringUtil.isNotEmpty(map.get(headModel.getDefaultValue())) ? map.get(headModel.getDefaultValue()) : "";
                            jsonObjects += "&" + headModel.getField() + "=" + URLEncoder.encode(value
//                                    .replaceAll("'", "")
                                    , "UTF-8");
                        } else {
                            String value = StringUtil.isNotEmpty(map.get(headModel.getDefaultValue())) ? map.get(headModel.getDefaultValue()) : "";
                            jsonObjects += "&" + headModel.getField() + "=" + URLEncoder.encode(value
//                                    .replaceAll("'", "")
                                    , "UTF-8");
                        }
                    }
                    if ("2".equals(headModel.getSource())) {
                        DataInterfaceVariateEntity variateEntity = dataInterfaceVariateService.getInfo(headModel.getDefaultValue());
                        jsonObjects += "&" + headModel.getField() + "=" + variateEntity.getValue();
                    }
                    if ("3".equals(headModel.getSource())) {
                        jsonObjects += "&" + headModel.getField() + "=" + headModel.getDefaultValue();
                    }
                }
            } else if (bodyType == 3 || bodyType == 4) {
                // 优先替换变量
                Pattern compile = Pattern.compile("\\{@\\w+}");
                Matcher matcher = compile.matcher(body);
                while (matcher.find()) {
                    // 得到参数
                    String group = matcher.group();
                    String variate = group.replace("{", "").replace("}", "").replace("@", "");
                    DataInterfaceVariateEntity dataInterfaceVariateEntity = dataInterfaceVariateService.getInfoByFullName(variate);
                    if (dataInterfaceVariateEntity != null) {
                        body = body.replace(group, dataInterfaceVariateEntity.getValue());
                    }
                }
                Pattern compile1 = Pattern.compile("\\{\\w+}");
                Matcher matcher1 = compile1.matcher(body);
                while (matcher1.find()) {
                    // 得到参数
                    String group = matcher1.group();
                    String param = group.replace("{", "").replace("}", "");
                    body = body.replace(group, map.get(param));
                }
                jsonObjects = body;
            }
            //获取token
            if (StringUtil.isEmpty(token)) {
                HeadModel headModel = header.stream().filter(t -> Constants.AUTHORIZATION.equals(t.getField())).findFirst().orElse(null);
                if (headModel != null) {
                    token = headModel.getDefaultValue();
                } else {
                    token = UserProvider.getToken();
                }
            }
            if (jsonObject == null && jsonObjects1 != null) {
                jsonObjects = jsonObjects1.toString();
            } else {
                jsonObjects = StringUtil.isEmpty(jsonObjects) ? jsonObject != null && jsonObject.size() > 0 ? jsonObject.toJSONString() : null : jsonObjects;
            }
            if (apiDateModel.getMethod() == 1) {
                jsonObjects = "";
            }
            JSONObject headerJson = new JSONObject();
            // 请求头
            for (HeadModel headModel : header) {
                if ("1".equals(headModel.getSource())) {
                    if (map != null && map.containsKey(headModel.getDefaultValue())) {
                        String value = map.get(headModel.getDefaultValue());
                        headerJson.put(headModel.getField(), value
//                                .replaceAll("'", "")
                        );
                    } else {
                        headerJson.put(headModel.getField(), map.get(headModel.getDefaultValue()));
                    }
                }
                if ("2".equals(headModel.getSource())) {
                    DataInterfaceVariateEntity variateEntity = dataInterfaceVariateService.getInfo(headModel.getDefaultValue());
                    headerJson.put(headModel.getField(), variateEntity.getValue());
                }
                if ("3".equals(headModel.getSource())) {
                    headerJson.put(headModel.getField(), headModel.getDefaultValue());
                }
                // 分页参数
                if ("4".equals(headModel.getSource())) {
                    Map<String, Object> map1 = JsonUtil.entityToMap(pagination);

                    Object urlValue = map1.get(headModel.getDefaultValue());
                    headerJson.put(headModel.getField(), urlValue);
                }
                // 回显参数
                if ("5".equals(headModel.getSource())) {
                    if (DataInterfaceConst.SHOWKEY.equals(headModel.getDefaultValue())) {
                        headerJson.put(headModel.getField(), showKey);
                    } else {
                        headerJson.put(headModel.getField(), showValue);
                    }
                }
            }
            get = HttpUtil.httpRequest(path, requestMethod, jsonObjects, token, headerJson.size() > 0 ? JsonUtil.createObjectToString(headerJson) : null, String.valueOf(bodyType));
            return get;
        } else {
            get.put("errorCode", "1");
            return get;
        }
    }

    /**
     * 处理变量
     *
     * @param apiDateModel
     */
    public void handlerVariate(ApiDateModel apiDateModel) {
        Set<String> variate = new HashSet<>();
        // 获取请求头参数
        List<HeadModel> header = apiDateModel.getHeader();
        header.forEach(headModel -> {
            if ("2".equals(headModel.getSource())) {
                variate.add(headModel.getDefaultValue());
            }
        });
        // 自定义参数
        List<HeadModel> query = apiDateModel.getQuery();
        query.forEach(headModel -> {
            if ("2".equals(headModel.getSource())) {
                variate.add(headModel.getDefaultValue());
            }
        });
        List<HeadModel> bodyJson = JsonUtil.createJsonToList(apiDateModel.getBody(), HeadModel.class);
        if (bodyJson != null) {
            bodyJson.forEach(headModel -> {
                if ("2".equals(headModel.getSource())) {
                    variate.add(headModel.getDefaultValue());
                }
            });
        }
        List<DataInterfaceVariateEntity> variateEntities = dataInterfaceVariateService.getListByIds(new ArrayList<>(variate));
        List<String> collect = variateEntities.stream().map(DataInterfaceVariateEntity::getInterfaceId).collect(Collectors.toList());
        List<DataInterfaceEntity> list = this.getList(collect);
        Map<String, String> map = new HashMap<>();
        list.forEach(t -> {
            try {
                DataConfigJsonModel dataConfigJsonModel = BeanUtil.toBean(t.getDataConfigJson(), DataConfigJsonModel.class);
                JSONObject jsonObject = callHTTP(null, UserProvider.getToken(), new Pagination(), null, BeanUtil.toBean(dataConfigJsonModel.getApiData(), ApiDateModel.class));
                if (Objects.nonNull(jsonObject) && "1".equals(jsonObject.get("errorCode"))) {
                    log.error("接口暂只支持HTTP和HTTPS方式");
                    return;
                }
                // 判断返回参数长度和key是否跟内置的一致
                if (jsonObject == null) {
                    log.error("接口请求失败");
                    return;
                }
                Object js = JScriptUtil.callJs(t.getDataExceptionJson(), jsonObject.get("data") == null ? new ArrayList<>() : jsonObject.get("data"));
                if (isInternal(jsonObject)) {
                    map.put(t.getId(), String.valueOf(JScriptUtil.callJs(t.getDataJsJson(), jsonObject.get("data") == null ? new ArrayList<>() : jsonObject.get("data"))));
                } else {
                    map.put(t.getId(), String.valueOf(JScriptUtil.callJs(t.getDataJsJson(), jsonObject)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        dataInterfaceVariateService.update(map, variateEntities);
    }

    /**
     * 处理系统参数
     *
     * @param sql
     * @return
     */
    private Map<Double, DataInterfaceMarkModel> systemParameter(String sql, UserInfo userInfo, Pagination pagination, Map<String, Object> showMap) {
        Map<Double, DataInterfaceMarkModel> paramValue = new TreeMap<>();
        //当前组织及子组织
        if (sql.contains(DataInterfaceConst.ORGANDSUB)) {
            String orgId = userInfo.getOrganizeId();
            if (StringUtil.isNotEmpty(userInfo.getDepartmentId())) {
                orgId = userInfo.getDepartmentId();
            }
            List<String> underOrganizations = organizeApi.getUnderOrganizations(orgId, false);
            underOrganizations.add(orgId);
            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceConst.ORGANDSUB, underOrganizations);
        }
        //当前用户及下属
        if (sql.contains(DataInterfaceConst.USERANDSUB)) {
            List<String> subOrganizeIds = new ArrayList<>();
            if (userInfo.getSubordinateIds().size() > 0) {
                subOrganizeIds = userInfo.getSubordinateIds();
            }
            subOrganizeIds.add(userInfo.getUserId());
            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceConst.USERANDSUB, subOrganizeIds);
        }
        //当前分管组织
        if (sql.contains(DataInterfaceConst.CHARORG) && StringUtil.isNotEmpty(userInfo.getUserId())) {
            List<String> orgIds = organizeAdminTratorApi.getOrganizeUserList(LinzenConst.CURRENT_ORG_SUB);
            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceConst.CHARORG, orgIds);
        }
        //当前组织
        if (sql.contains(DataInterfaceConst.ORG)) {
            String orgId = userInfo.getOrganizeId();
            if (StringUtil.isNotEmpty(userInfo.getDepartmentId())) {
                orgId = userInfo.getDepartmentId();
            }
            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceConst.ORG, orgId);
        }
        //关键字
        if (sql.contains(DataInterfaceConst.KEYWORD)) {
            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceConst.KEYWORD, pagination.getKeyword());
        }
        // 当前页数
        if (sql.contains(DataInterfaceConst.OFFSETSIZE)) {
            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceConst.OFFSETSIZE, pagination.getPageSize() * (pagination.getCurrentPage() - 1));
        }
        // 每页行数
        if (sql.contains(DataInterfaceConst.PAGESIZE)) {
            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceConst.PAGESIZE, pagination.getPageSize());
        }
        //当前用户
        if (sql.contains(DataInterfaceConst.USER)) {
            String userId = userInfo.getUserId();
            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceConst.USER, userId);
        }
//        // 每页行数
//        if (sql.contains(DataInterfaceVarEnum.SHOWKEY)) {
//            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceVarEnum.SHOWKEY, showMap.keySet().iterator().next());
//        }
        // 每页行数
        if (sql.contains(DataInterfaceConst.SHOWVALUE)) {
            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceConst.SHOWVALUE, showMap.values().iterator().next());
        }
        // 生成雪花id
        if (sql.contains(DataInterfaceConst.ID)) {
            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceConst.ID, RandomUtil.uuId());
        }
        if (sql.contains(DataInterfaceConst.ID_LOT)) {
            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceConst.ID_LOT, null);
        }
        return paramValue;
    }

    /**
     * 计算执行时间
     *
     * @param dateTime
     * @return
     */
    public int invokTime(LocalDateTime dateTime) {
        //调用时间
        int invokWasteTime = Integer.valueOf((int) (System.currentTimeMillis() - dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli()));
        return invokWasteTime;
    }

}
