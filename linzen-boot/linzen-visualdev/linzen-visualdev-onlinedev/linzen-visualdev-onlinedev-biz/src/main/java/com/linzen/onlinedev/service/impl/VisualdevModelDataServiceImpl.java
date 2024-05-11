package com.linzen.onlinedev.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.base.entity.VisualdevEntity;
import com.linzen.base.entity.VisualdevReleaseEntity;
import com.linzen.base.model.ColumnDataModel;
import com.linzen.base.model.FormDataField;
import com.linzen.base.model.VisualDevJsonModel;
import com.linzen.base.service.DbLinkService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.base.service.VisualdevReleaseService;
import com.linzen.base.service.VisualdevService;
import com.linzen.constant.MsgCode;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.engine.entity.FlowTaskEntity;
import com.linzen.engine.service.FlowTaskService;
import com.linzen.exception.DataBaseException;
import com.linzen.exception.WorkFlowException;
import com.linzen.model.flow.DataModel;
import com.linzen.model.visualJson.FieLdsModel;
import com.linzen.model.visualJson.FormCloumnUtil;
import com.linzen.model.visualJson.FormDataModel;
import com.linzen.model.visualJson.TableModel;
import com.linzen.model.visualJson.analysis.FormAllModel;
import com.linzen.model.visualJson.analysis.FormModel;
import com.linzen.model.visualJson.analysis.RecursionForm;
import com.linzen.onlinedev.entity.VisualdevModelDataEntity;
import com.linzen.onlinedev.mapper.VisualdevModelDataMapper;
import com.linzen.onlinedev.model.OnlineDevData;
import com.linzen.onlinedev.model.OnlineDevListModel.VisualColumnSearchVO;
import com.linzen.onlinedev.model.PaginationModel;
import com.linzen.onlinedev.model.PaginationModelExport;
import com.linzen.onlinedev.model.VisualdevModelDataInfoVO;
import com.linzen.onlinedev.service.VisualDevListService;
import com.linzen.onlinedev.service.VisualdevModelDataService;
import com.linzen.onlinedev.util.OnlineDevDbUtil;
import com.linzen.onlinedev.util.onlineDevUtil.OnlineDevInfoUtils;
import com.linzen.onlinedev.util.onlineDevUtil.OnlineProductSqlUtils;
import com.linzen.onlinedev.util.onlineDevUtil.OnlinePublicUtils;
import com.linzen.onlinedev.util.onlineDevUtil.OnlineSwapDataUtils;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.util.*;
import com.linzen.util.context.RequestContext;
import com.linzen.util.visiual.ProjectKeyConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class VisualdevModelDataServiceImpl extends SuperServiceImpl<VisualdevModelDataMapper, VisualdevModelDataEntity> implements VisualdevModelDataService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private DbLinkService dblinkService;
    @Autowired
    private VisualdevService visualdevService;
    @Autowired
    private VisualdevReleaseService visualdevReleaseService;
    @Autowired
    private VisualDevListService visualDevListService;
    @Autowired
    private OnlineSwapDataUtils onlineSwapDataUtils;
    @Autowired
    private OnlineDevInfoUtils onlineDevInfoUtils;
    @Autowired
    private OnlineDevDbUtil onlineDevDbUtil;
    @Autowired
    private FlowFormDataUtil flowFormDataUtil;
    @Autowired
    private FormCheckUtils formCheckUtils;
    @Autowired
    private FlowTaskService flowTaskApi;


    @Override
    public List<VisualdevModelDataEntity> getList(String modelId) {
        QueryWrapper<VisualdevModelDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualdevModelDataEntity::getVisualDevId, modelId);
        return this.list(queryWrapper);
    }

    /**
     * 表单字段
     * @param id
     * @param filterType  过滤类型，0或者不传为默认过滤子表和关联表单，1-弹窗配置需要过滤掉的类型
     * @return
     */
    @Override
    public List<FormDataField> fieldList(String id, Integer filterType) {
        VisualdevReleaseEntity entity = visualdevReleaseService.getById(id);
        FormDataModel formData = JsonUtil.createJsonToBean(entity.getFormData(), FormDataModel.class);

        List<FieLdsModel> fieLdsModelList = JsonUtil.createJsonToList(formData.getFields(), FieLdsModel.class);
        List<FieLdsModel> mainFieldModelList = new ArrayList<>();

        OnlinePublicUtils.recursionFields(mainFieldModelList,fieLdsModelList);
        //过滤掉无法传递的组件
        List<String> notInList=new ArrayList<>();
        notInList.add(ProjectKeyConsts.RELATIONFORM);
        notInList.add(ProjectKeyConsts.CHILD_TABLE);
        if (Objects.equals(filterType,1)) {
            notInList.add("link");
            notInList.add("button");
            notInList.add("LINZENText");
            notInList.add("alert");
            notInList.add(ProjectKeyConsts.POPUPSELECT);
            notInList.add(ProjectKeyConsts.QR_CODE);
            notInList.add(ProjectKeyConsts.BARCODE);
            notInList.add(ProjectKeyConsts.BILLRULE);
            notInList.add(ProjectKeyConsts.CREATEUSER);
            notInList.add(ProjectKeyConsts.CREATETIME);
            notInList.add(ProjectKeyConsts.UPLOADIMG);
            notInList.add(ProjectKeyConsts.UPLOADFZ);
            notInList.add(ProjectKeyConsts.MODIFYUSER);
            notInList.add(ProjectKeyConsts.MODIFYTIME);

            notInList.add(ProjectKeyConsts.CURRORGANIZE);
            notInList.add(ProjectKeyConsts.CURRPOSITION);
            notInList.add(ProjectKeyConsts.IFRAME);
            notInList.add(ProjectKeyConsts.RELATIONFORM_ATTR);
            notInList.add(ProjectKeyConsts.POPUPSELECT_ATTR);
        }

        List<FormDataField> formDataFieldList = mainFieldModelList.stream().filter(fieLdsModel ->
                !"".equals(fieLdsModel.getVModel())
                        && StringUtil.isNotEmpty(fieLdsModel.getVModel())
                        && !notInList.contains(fieLdsModel.getConfig().getProjectKey())
        ).map(fieLdsModel -> {
            FormDataField formDataField = new FormDataField();
            formDataField.setLabel(fieLdsModel.getConfig().getLabel());
            formDataField.setVModel(fieLdsModel.getVModel());
            return formDataField;
        }).collect(Collectors.toList());

        return formDataFieldList;
    }

    @Override
    public List<Map<String, Object>> getPageList(VisualdevEntity entity, PaginationModel paginationModel) {
//        String json = null;
//        if (StringUtil.isNotEmpty(paginationModel.getKeyword())) {
//            Map<String, Object> map = new HashMap<>();
//            map.put(paginationModel.getRelationField(), paginationModel.getKeyword());
//            json = JsonUtil.createObjectToString(map);
//        }
//        paginationModel.setQueryJson(json);
        VisualDevJsonModel visualJsonModel = OnlinePublicUtils.getVisualJsonModel(entity);

        //判断请求客户端来源
        if (!RequestContext.isOrignPc()){
            visualJsonModel.setColumnData(visualJsonModel.getAppColumnData());
        }
        List<Map<String, Object>> dataList = visualDevListService.getRelationFormList(visualJsonModel, paginationModel);
        return dataList;
    }

    @Override
    public List<Map<String, Object>> exportData(String[] keys, PaginationModelExport paginationModelExport, VisualDevJsonModel visualDevJsonModel) {
        PaginationModel paginationModel =new PaginationModel();
        BeanUtil.copyProperties(paginationModelExport,paginationModel);
        List<String> keyList = Arrays.asList(keys);
        List<Map<String,Object>> noSwapDataList;
        ColumnDataModel columnDataModel = visualDevJsonModel.getColumnData();
        List<VisualColumnSearchVO> searchVOList = new ArrayList<>();
        List<TableModel> visualTables = visualDevJsonModel.getVisualTables();
        TableModel mainTable = visualTables.stream().filter(vi -> vi.getTypeId().equals("1")).findFirst().orElse(null);
        //解析控件
        FormDataModel formDataModel = visualDevJsonModel.getFormData();
        List<FieLdsModel> fieLdsModels = JsonUtil.createJsonToList(formDataModel.getFields(), FieLdsModel.class);
        RecursionForm recursionForm = new RecursionForm(fieLdsModels, visualTables);
        List<FormAllModel> formAllModel = new ArrayList<>();
        FormCloumnUtil.recursionForm(recursionForm, formAllModel);
        //封装查询条件
        if (StringUtil.isNotEmpty(paginationModel.getQueryJson())){
            Map<String, Object> keyJsonMap = JsonUtil.stringToMap(paginationModel.getQueryJson());
            searchVOList= JsonUtil.createJsonToList(columnDataModel.getSearchList(),VisualColumnSearchVO.class);
            searchVOList =	searchVOList.stream().map(searchVO->{
                searchVO.setValue(keyJsonMap.get(searchVO.getId()));
                return searchVO;
            }).filter(vo->vo.getValue()!=null && StringUtil.isNotEmpty(String.valueOf(vo.getValue()))).collect(Collectors.toList());
            //左侧树查询
            boolean b =false;
            if (columnDataModel.getTreeRelation()!=null){
                b = keyJsonMap.keySet().stream().anyMatch(t -> t.equalsIgnoreCase(String.valueOf(columnDataModel.getTreeRelation())));
            }
            if (b && keyJsonMap.size()>searchVOList.size()){
                String relation =String.valueOf(columnDataModel.getTreeRelation());
                VisualColumnSearchVO vo =new VisualColumnSearchVO();
                vo.setSearchType("1");
                vo.setVModel(relation);
                vo.setValue(keyJsonMap.get(relation));
                searchVOList.add(vo);
            }
        }
        //判断有无表
        List<VisualColumnSearchVO> searchVOS = new ArrayList<>();
        if (visualDevJsonModel.getVisualTables().size()>0){
            //当前用户信息
            UserInfo userInfo = userProvider.get();
            //菜单id
            String moduleId = paginationModel.getMenuId();
            //封装搜索数据
            OnlineProductSqlUtils.queryList(formAllModel,visualDevJsonModel,paginationModel);
            noSwapDataList =visualDevListService.getListWithTable(visualDevJsonModel,paginationModel,userInfo,moduleId,keyList);
        }else{
            noSwapDataList =visualDevListService.getWithoutTableData(visualDevJsonModel.getId());
            noSwapDataList = visualDevListService.getList(noSwapDataList, searchVOList, paginationModel);
        }

        //数据转换
        List<FieLdsModel> fields = new ArrayList<>();
        OnlinePublicUtils.recursionFields(fields, fieLdsModels);
        noSwapDataList = onlineSwapDataUtils.getSwapList(noSwapDataList, fields,visualDevJsonModel.getId(),false,new ArrayList<>());

        return noSwapDataList;
    }


    @Override
    public VisualdevModelDataEntity getInfo(String id) {
        QueryWrapper<VisualdevModelDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualdevModelDataEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public VisualdevModelDataInfoVO infoDataChange(String id, VisualdevEntity visualdevEntity) throws IOException, ParseException, DataBaseException, SQLException {
        FormDataModel formDataModel = JsonUtil.createJsonToBean(visualdevEntity.getFormData(), FormDataModel.class);
        List<FieLdsModel> modelList = JsonUtil.createJsonToList(formDataModel.getFields(), FieLdsModel.class);

        VisualdevModelDataEntity visualdevModelDataEntity = this.getInfo(id);

        List<FieLdsModel> childFieldModelList = new ArrayList<>();
        List<FieLdsModel> mainFieldModelList = new ArrayList<>();
        //二维码 条形码
        List<FormModel> models = new ArrayList<>();
        OnlinePublicUtils.recurseFiled(modelList, mainFieldModelList,childFieldModelList,models);

        if (visualdevModelDataEntity != null) {
            Map<String, Object>  DataMap = JsonUtil.stringToMap(visualdevModelDataEntity.getData());
            Map<String, Object> childTableMap = DataMap.entrySet().stream().filter(m -> m.getKey().contains("tableField"))
                .collect(Collectors.toMap((e) -> (String) e.getKey(),
                (e) -> ObjectUtil.isNotEmpty(e.getValue()) ? e.getValue() : ""));
            Map<String, Object> mainTableMap = DataMap.entrySet().stream().filter(m -> !m.getKey().contains("tableField"))
                .collect(Collectors.toMap((e) -> (String) e.getKey(),
                    (e) -> ObjectUtil.isNotEmpty(e.getValue()) ? e.getValue() : ""));
            mainTableMap = onlineDevInfoUtils.swapChildTableDataInfo(mainFieldModelList, mainTableMap,models);

            for (Map.Entry<String,Object> entry : childTableMap.entrySet()){
                List<Map<String, Object>> listMap = JsonUtil.createJsonToListMap(String.valueOf(entry.getValue()));
                FieLdsModel fieLdsModel = childFieldModelList.stream().filter(child -> child.getVModel().equalsIgnoreCase(entry.getKey())).findFirst().orElse(null);
                if (ObjectUtil.isNotEmpty(fieLdsModel)){
                    List<Map<String,Object>> tableValueList = new ArrayList<>();
                    if (Objects.nonNull(listMap)){
                        for (Map<String, Object> map : listMap){
                            Map<String,Object> childFieldMap  = onlineDevInfoUtils.swapChildTableDataInfo(fieLdsModel.getConfig().getChildren(),map,models);
                            tableValueList.add(childFieldMap);
                        }
                    }
                    Map<String,Object> childFieldsMap = new HashMap<>();
                    childFieldsMap.put(entry.getKey(),tableValueList);
                    mainTableMap.putAll(childFieldsMap);
                }
            }
            String objectToString = JsonUtilEx.getObjectToString(mainTableMap);
            VisualdevModelDataInfoVO vo = new VisualdevModelDataInfoVO();
            vo.setData(objectToString);
            vo.setId(id);
            return vo;
        }
        return null;
    }

    @Override
    public DataModel visualCreate(VisualdevEntity visualdevEntity, Map<String, Object> map, boolean isLink, boolean isUpload) throws Exception {
        FormDataModel formData = JsonUtil.createJsonToBean(visualdevEntity.getFormData(), FormDataModel.class);
        List<FieLdsModel> list = JsonUtil.createJsonToList(formData.getFields(), FieLdsModel.class);
        List<TableModel> tableModels = JsonUtil.createJsonToList(visualdevEntity.getVisualTables(), TableModel.class);
        DbLinkEntity linkEntity = StringUtil.isNotEmpty(visualdevEntity.getDbLinkId()) ? dblinkService.getInfo(visualdevEntity.getDbLinkId()) : null;
        //是否开启并发锁
        Boolean concurrency = false;
        Integer primaryKeyPolicy = formData.getPrimaryKeyPolicy();
        if (formData.getConcurrencyLock()) {
            //初始化version值
            map.put(TableFeildsEnum.VERSION.getField() , 0);
            concurrency = true;
        }
        //单行唯一校验
        if(!isUpload) {
            String b = formCheckUtils.checkForm(list, map, linkEntity, tableModels, primaryKeyPolicy, formData.getLogicalDelete(), null);
            if (StringUtil.isNotEmpty(b)) {
                throw new WorkFlowException(b + "不能重复" );
            }
        }

        OnlineSwapDataUtils.swapDatetime(list,map);
        String mainId = RandomUtil.uuId();
        UserInfo userInfo = userProvider.get();
        SysUserEntity info = userService.getInfo(userInfo.getUserId());
        DataModel dataModel = DataModel.builder().dataNewMap(map).fieLdsModelList(list).tableModelList(tableModels)
                .mainId(mainId).link(linkEntity).userEntity(info).concurrencyLock(concurrency)
                .primaryKeyPolicy(primaryKeyPolicy).flowEnable(Objects.equals(visualdevEntity.getEnableFlow(),1))
                .linkOpen(isLink).build();
//        if (!StringUtil.isEmpty(visualdevEntity.getVisualTables()) && !OnlineDevData.TABLE_CONST.equals(visualdevEntity.getVisualTables())) {
            flowFormDataUtil.create(dataModel);
//        } else {
//            VisualdevModelDataEntity entity = new VisualdevModelDataEntity();
//            entity.setData(JsonUtilEx.getObjectToString(dataModel.getDataNewMap()));
//            entity.setVisualDevId(visualdevEntity.getId());
//            entity.setId(dataModel.getMainId());
//            entity.setSortcode(RandomUtil.parses());
//            entity.setCreatortime(new Date());
//            entity.setCreatoruserid(userProvider.get().getUserId());
//            entity.setEnabledMark(1);
//            this.save(entity);
//        }
        return dataModel;
    }

    @Override
    public DataModel visualUpdate(VisualdevEntity visualdevEntity, Map<String, Object> map,String id,boolean isUpload) throws WorkFlowException {
        ColumnDataModel columnDataModel = JsonUtil.createJsonToBean(visualdevEntity.getColumnData(), ColumnDataModel.class);
        FormDataModel formData = JsonUtil.createJsonToBean(visualdevEntity.getFormData(), FormDataModel.class);
        List<FieLdsModel> list = JsonUtil.createJsonToList(formData.getFields(), FieLdsModel.class);
        boolean inlineEdit = columnDataModel.getType() != null && columnDataModel.getType() == 4;
        if (inlineEdit) {
            list = JsonUtil.createJsonToList(columnDataModel.getColumnList(), FieLdsModel.class);
            list = list.stream().filter(f -> !f.getId().toLowerCase().contains(ProjectKeyConsts.CHILD_TABLE_PREFIX)).collect(Collectors.toList());
        }
        List<TableModel> tableModels = JsonUtil.createJsonToList(visualdevEntity.getVisualTables(), TableModel.class);
        TableModel mainT = tableModels.stream().filter(t -> t.getTypeId().equals("1" )).findFirst().orElse(null);
        DbLinkEntity linkEntity = StringUtil.isNotEmpty(visualdevEntity.getDbLinkId()) ? dblinkService.getInfo(visualdevEntity.getDbLinkId()) : null;
        //是否开启并发锁
        Boolean isConcurrencyLock = false;
        Integer primaryKeyPolicy = formData.getPrimaryKeyPolicy();
        if (formData.getConcurrencyLock()) {
            if (map.get(TableFeildsEnum.VERSION.getField() ) == null) {
                map.put(TableFeildsEnum.VERSION.getField() , 0);
            } else {
                boolean version = true;
                try {
                    version = onlineDevDbUtil.getVersion(mainT.getTable(), linkEntity, map, id, primaryKeyPolicy);
                }catch (Exception e){
                    throw new WorkFlowException(e.getMessage());
                }
                if (!version) {
                    throw new WorkFlowException(MsgCode.VS405.get());
                } else {
                    Integer vs = Integer.valueOf(String.valueOf(map.get(TableFeildsEnum.VERSION.getField() )));
                    map.put(TableFeildsEnum.VERSION.getField() , vs + 1);
                }
            }
            isConcurrencyLock = true;
        }
        //单行唯一校验
        if(!isUpload) {
            String b = formCheckUtils.checkForm(list, map, linkEntity, tableModels, primaryKeyPolicy, formData.getLogicalDelete(), id);
            if (StringUtil.isNotEmpty(b)) {
                throw new WorkFlowException(b + "不能重复" );
            }
        }
        OnlineSwapDataUtils.swapDatetime(list,map);
        UserInfo userInfo = userProvider.get();
        SysUserEntity info = userService.getInfo(userInfo.getUserId());
        DataModel dataModel = DataModel.builder().dataNewMap(map).fieLdsModelList(list).tableModelList(tableModels)
                .mainId(id) .link(linkEntity).userEntity(info).concurrencyLock(isConcurrencyLock)
                .primaryKeyPolicy(primaryKeyPolicy) .flowEnable(Objects.equals(visualdevEntity.getEnableFlow(),1)).build();
//        if (StringUtil.isEmpty(visualdevEntity.getVisualTables()) || OnlineDevData.TABLE_CONST.equals(visualdevEntity.getVisualTables())) {
//            VisualdevModelDataEntity entity = new VisualdevModelDataEntity();
//            entity.setData(JsonUtilEx.getObjectToString(dataModel.getDataNewMap()));
//            entity.setVisualDevId(visualdevEntity.getId());
//            entity.setId(dataModel.getMainId());
//            entity.setUpdatetime(new Date());
//            entity.setUpdateuserid(userProvider.get().getUserId());
//            this.updateById(entity);
//        } else {
            flowFormDataUtil.update(dataModel);
//        }
        return dataModel;
    }

    @Override
    public DataModel visualCreate(VisualdevEntity visualdevEntity, Map<String, Object> map, boolean isLink) throws Exception {
        return visualCreate(visualdevEntity,map,isLink,false);
    }

    @Override
    public DataModel visualCreate(VisualdevEntity visualdevEntity, Map<String, Object> map) throws Exception {
        return visualCreate(visualdevEntity,map,false);
    }

    @Override
    public DataModel visualUpdate(VisualdevEntity visualdevEntity, Map<String, Object> map, String id) throws Exception {
        return visualUpdate(visualdevEntity,map,id,false);
    }

    @Override
    public void visualDelete(VisualdevEntity visualdevEntity,List<String> idsVoList) throws Exception{
        VisualDevJsonModel visualJsonModel = OnlinePublicUtils.getVisualJsonModel(visualdevEntity);
        //判断请求客户端来源
        if (!RequestContext.isOrignPc()) {
            visualJsonModel.setColumnData(visualJsonModel.getAppColumnData());
        }
        List<String> idsList = new ArrayList<>();
        StringJoiner  errMess=new StringJoiner(",");
        if (visualdevEntity.getEnableFlow() == 1) {
            for (String id : idsVoList) {
                FlowTaskEntity taskEntity = flowTaskApi.getInfoSubmit(id);
                if (taskEntity != null) {
                    if (taskEntity.getStatus().equals(0) || taskEntity.getStatus().equals(4)) {
                        try {
                            flowTaskApi.delete(taskEntity);
                            idsList.add(id);
                        }catch (Exception e){
                            errMess.add(e.getMessage());
                        }
                    }
                } else {
                    idsList.add(id);
                }
            }
        } else {
            idsList = idsVoList;
        }
        if (idsList.size() == 0) {
            throw new WorkFlowException(errMess.toString());
        }
        if (!StringUtil.isEmpty(visualdevEntity.getVisualTables()) && !OnlineDevData.TABLE_CONST.equals(visualdevEntity.getVisualTables())) {
            for(String id:idsList){
                try {
                    tableDelete(id, visualJsonModel);
                }catch (Exception e){
                    throw new WorkFlowException(e.getMessage());
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(VisualdevModelDataEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }

    @Override
    public boolean tableDelete(String id,VisualDevJsonModel visualDevJsonModel) throws Exception {
        DbLinkEntity linkEntity = dblinkService.getInfo(visualDevJsonModel.getDbLinkId());
        VisualDevJsonModel model = BeanUtil.copyProperties(visualDevJsonModel, VisualDevJsonModel.class);
        return onlineDevDbUtil.deleteTable(id, model, linkEntity);
    }

    @Override
    public ServiceResult tableDeleteMore(List<String> ids, VisualDevJsonModel visualDevJsonModel) throws Exception {
        List<String> dataInfoVOList = new ArrayList<>();
        for (String id : ids) {
            boolean isDel = tableDelete(id, visualDevJsonModel);
            if(isDel){
                dataInfoVOList.add(id);
            }
        }
        visualDevJsonModel.setDataIdList(dataInfoVOList);
        return ServiceResult.success(MsgCode.SU003.get());
    }

}
