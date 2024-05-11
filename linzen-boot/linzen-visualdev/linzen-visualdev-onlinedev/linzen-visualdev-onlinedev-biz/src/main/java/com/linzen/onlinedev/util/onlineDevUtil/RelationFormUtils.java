package com.linzen.onlinedev.util.onlineDevUtil;

import com.linzen.onlinedev.model.OnlineDevListModel.VisualColumnSearchVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * 关联表单工具类
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class RelationFormUtils {

	/**
	 * 转后后查询
	 * @param dataVOList
	 * @param searchVOList
	 * @return
	 */
	public static List<Map<String, Object>> getRelationListByKeyword(List<Map<String, Object>> dataVOList, List<VisualColumnSearchVO> searchVOList){
		List<Map<String, Object>> passDataList = new ArrayList<>();
		for (Map<String, Object> dataMap : dataVOList){
			int i =0;
			for (VisualColumnSearchVO searchVO : searchVOList){
				String s = String.valueOf(dataMap.get(searchVO.getVModel()));
				if (s.contains(String.valueOf(searchVO.getValue()))){
					i++;
				}
			}
			if (i>0){
				passDataList.add(dataMap);
			}
		}
		return passDataList;
	}
}
