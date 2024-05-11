package com.linzen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.service.DbLinkService;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.entity.FlowFormEntity;
import com.linzen.exception.DataBaseException;
import com.linzen.exception.WorkFlowException;
import com.linzen.model.flow.FlowFormDataModel;
import com.linzen.model.visualJson.FormDataModel;
import com.linzen.model.visualJson.TableModel;
import com.linzen.permission.service.OrganizeService;
import com.linzen.permission.service.UserService;
import com.linzen.service.FlowFormService;
import com.linzen.service.FormDataService;
import com.linzen.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class FlowDataServiceImpl implements FormDataService {
	@Autowired
	private FlowFormCustomUtils flowFormCustomUtils;
	@Autowired
	private FlowFormHttpReqUtils flowFormHttpReqUtils;

	@Autowired
	private FlowFormService flowFormService;
	@Autowired
	private DbLinkService dblinkService;
	@Autowired
	private FlowFormDataUtil flowDataUtil;
	@Autowired
	private UserService userService;
	@Autowired
	private OrganizeService organizeService;
	@Autowired
	private UserProvider userProvider;

    @Override
    public void create(String formId, String id, Map<String, Object> map) throws WorkFlowException {
        FlowFormEntity flowFormEntity = flowFormService.getById(formId);
        //判断是否为系统表单
        boolean b = flowFormEntity.getFormType() == 1;
        if (b) {
            flowFormHttpReqUtils.create(flowFormEntity, id, UserProvider.getToken(), map);
        } else {
            flowFormCustomUtils.create(flowFormEntity, id, map, null, null);
        }
    }

    @Override
    public void update(String formId, String id, Map<String, Object> map) throws WorkFlowException, SQLException, DataBaseException {
        FlowFormEntity flowFormEntity = flowFormService.getById(formId);
        //判断是否为系统表单
        boolean b = flowFormEntity.getFormType() == 1;
        if (b) {
            flowFormHttpReqUtils.update(flowFormEntity, id, UserProvider.getToken(), map);
        } else {
            flowFormCustomUtils.update(flowFormEntity, id, map, null);
        }
    }

	@Override
	public void saveOrUpdate(FlowFormDataModel flowFormDataModel) throws WorkFlowException {
		String id = flowFormDataModel.getId();
		String formId = flowFormDataModel.getFormId();
		Map<String, Object> map = flowFormDataModel.getMap();
		List<Map<String, Object>> formOperates = flowFormDataModel.getFormOperates();
		FlowFormEntity flowFormEntity = flowFormService.getById(formId);
		Integer formType = flowFormEntity.getFormType();
		if(map.get(TableFeildsEnum.VERSION.getField().toUpperCase())!=null){//针对Oracle数据库大小写敏感，出现大写字段补充修复
			map.put(TableFeildsEnum.VERSION.getField(),map.get(TableFeildsEnum.VERSION.getField().toUpperCase()));
		}
		//系统表单
		if (formType == 1){
			map.put("formOperates",formOperates);
			flowFormHttpReqUtils.saveOrUpdate(flowFormEntity,id,UserProvider.getToken(),map);
		} else {
			try {
				flowFormCustomUtils.saveOrUpdate(flowFormEntity,flowFormDataModel);
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
			} catch (DataBaseException e) {
				e.printStackTrace();
			}
		}
	}

    @Override
    public boolean delete(String formId, String id) throws Exception {
        FlowFormEntity flowFormEntity = flowFormService.getById(formId);
        List<TableModel> tableModels = JsonUtil.createJsonToList(flowFormEntity.getTableJson(), TableModel.class);
        FormDataModel formData = JsonUtil.createJsonToBean(flowFormEntity.getPropertyJson(), FormDataModel.class);
        Integer primaryKeyPolicy = formData.getPrimaryKeyPolicy();
        DbLinkEntity linkEntity = StringUtil.isNotEmpty(flowFormEntity.getDbLinkId()) ? dblinkService.getInfo(flowFormEntity.getDbLinkId()) : null;
        flowDataUtil.deleteTable(id, primaryKeyPolicy, tableModels, linkEntity);
        return true;
    }

    @Override
    public ServiceResult info(String formId, String id){
        ServiceResult result = new ServiceResult();
        Map<String, Object> allDataMap = new HashMap();
        FlowFormEntity flowFormEntity = flowFormService.getById(formId);
        result.setCode(flowFormEntity==null?400:200);
        result.setMsg(flowFormEntity==null?"表单信息不存在":"");
        if(flowFormEntity!=null){
            //判断是否为系统表单
            boolean b = flowFormEntity.getFormType() == 1;
            if (b) {
                allDataMap.putAll(flowFormHttpReqUtils.info(flowFormEntity, id, UserProvider.getToken()));
            } else {
                allDataMap.putAll(flowFormCustomUtils.info(flowFormEntity, id));
            }
        }
        result.setData(allDataMap);
        return result;
    }
}
