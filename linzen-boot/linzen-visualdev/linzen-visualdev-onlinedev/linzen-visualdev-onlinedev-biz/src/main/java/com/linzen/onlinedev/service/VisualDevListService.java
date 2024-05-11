package com.linzen.onlinedev.service;

import com.linzen.base.UserInfo;
import com.linzen.base.model.VisualDevJsonModel;
import com.linzen.exception.WorkFlowException;
import com.linzen.onlinedev.model.OnlineDevListModel.VisualColumnSearchVO;
import com.linzen.onlinedev.model.PaginationModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *列表临时接口
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public interface VisualDevListService {


	/**
	 * 无表数据
	 *
	 * @param modelId
	 * @return
	 */
	List<Map<String,Object>> getWithoutTableData(String modelId);

	/**
	 * 有表查询
	 *
	 * @param visualDevJsonModel
	 * @param paginationModel
	 * @return
	 */
	List<Map<String, Object>> getListWithTable(VisualDevJsonModel visualDevJsonModel, PaginationModel paginationModel, UserInfo userInfo, String moduleId, List<String> columnPropList);

	/**
	 * 列表数据
	 *
	 * @param visualDevJsonModel
	 * @param paginationModel
	 * @return
	 */
	List<Map<String, Object>> getDataList(VisualDevJsonModel visualDevJsonModel, PaginationModel paginationModel) throws WorkFlowException;

	/**
	 * 外链列表数据
	 *
	 * @param visualDevJsonModel
	 * @param paginationModel
	 * @return
	 */
	List<Map<String, Object>> getDataListLink(VisualDevJsonModel visualDevJsonModel, PaginationModel paginationModel) throws WorkFlowException;


	/**
	 * 无表数据处理
	 *
	 * @param list
	 * @param searchVOList
	 * @param paginationModel
	 * @return
	 */
	List<Map<String, Object>> getList(List<Map<String, Object>> list, List<VisualColumnSearchVO> searchVOList, PaginationModel paginationModel);

	/**
	 * 关联表单列表数据
	 *
	 * @param visualDevJsonModel
	 * @param paginationModel
	 * @return
	 */
	List<Map<String, Object>> getRelationFormList(VisualDevJsonModel visualDevJsonModel, PaginationModel paginationModel);
}
