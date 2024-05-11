package com.linzen.service;

import com.linzen.base.ServiceResult;
import com.linzen.exception.DataBaseException;
import com.linzen.exception.WorkFlowException;
import com.linzen.model.flow.FlowFormDataModel;

import java.sql.SQLException;
import java.util.Map;

/**
 * 表单数据操作
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface FormDataService{

	/**
	 * 新增
	 *
	 * @param formId 表单id
	 * @param id     主键id
	 * @param map 数据
	 * @return ignore
	 */
	void create(String formId, String id, Map<String, Object> map) throws Exception;

	/**
	 * 修改
	 *
	 * @param formId 表单id
	 * @param id     主键id
	 * @param map 数据
	 * @return ignore
	 */
	void update(String formId, String id, Map<String, Object> map) throws WorkFlowException, SQLException, DataBaseException;


	void saveOrUpdate(FlowFormDataModel flowFormDataModel) throws WorkFlowException;

	/**
	 * 删除
	 *
	 * @param formId 表单id
	 * @param id     主键id
	 * @return ignore
	 */
	boolean delete(String formId, String id) throws Exception;

	/**
	 * 信息
	 *
	 * @param formId 表单id
	 * @param id     主键id
	 * @return ignore
	 */
	ServiceResult info(String formId, String id);
}
