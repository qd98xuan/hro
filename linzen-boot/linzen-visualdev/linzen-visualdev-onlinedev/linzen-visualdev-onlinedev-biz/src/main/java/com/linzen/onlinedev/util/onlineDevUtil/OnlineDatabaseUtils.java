package com.linzen.onlinedev.util.onlineDevUtil;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.linzen.database.model.dbfield.JdbcColumnModel;
import com.linzen.database.model.dto.PrepSqlDTO;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.util.JdbcUtil;
import com.linzen.onlinedev.model.OnlineDevListModel.OnlineColumnFieldModel;
import com.linzen.onlinedev.model.OnlineDevListModel.OnlineDevListDataVO;
import com.linzen.onlinedev.model.OnlineDevListModel.VisualColumnSearchVO;
import com.linzen.util.FormPublicUtils;
import com.linzen.util.StringUtil;
import com.linzen.util.visiual.ProjectKeyConsts;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class OnlineDatabaseUtils {
	public static List<OnlineDevListDataVO> getTableDataList(DbLinkEntity dbLinkEntity, String sql, String pKeyName, List<OnlineColumnFieldModel> childFieldList)  {
		List<OnlineDevListDataVO> list = new ArrayList<>();
		try {
			List<List<JdbcColumnModel>> fieldMods = JdbcUtil.queryJdbcColumns(new PrepSqlDTO(sql).withConn(dbLinkEntity)).setIsValue(true).get();
			List<Map<String,Object>> dataList = new ArrayList<>();

			for (List<JdbcColumnModel> dblist : fieldMods){
				Map<String,Object> dataMap =new HashMap<>();
				HashMap<String, Object> collect = dblist.stream().collect(Collectors.toMap(s -> Optional.ofNullable(s.getField()).orElse(""), s -> Optional.ofNullable(s.getValue()).orElse(""), (a, b) -> b, HashMap::new));
				dataMap.putAll(collect);
				for (OnlineColumnFieldModel on : childFieldList){
					//需要替换的子表字段
					JdbcColumnModel fieldMod = dblist.stream()
							.filter(dbMod -> dbMod.getTable().equalsIgnoreCase(on.getTableName())
									&& dbMod.getField().equalsIgnoreCase(on.getField())).findFirst().orElse(null);
					//将数据转成map格式
					if (ObjectUtil.isNotEmpty(fieldMod)){
						dataMap.remove(fieldMod.getField());
						dataMap.put(on.getOriginallyField(),fieldMod.getValue());
					}
				}
				dataList.add(dataMap);
			}

			for (Map<String, Object> dataMap : dataList) {
				OnlineDevListDataVO dataVo = new OnlineDevListDataVO();
				dataMap = toLowerKey(dataMap);
				dataVo.setData(dataMap);
				if (dataMap.containsKey(pKeyName.toUpperCase())) {
					dataVo.setId(String.valueOf(dataMap.get(pKeyName.toUpperCase())));
				}
				list.add(dataVo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static Map<String, Object> toLowerKey(Map<String, Object> map) {
		Map<String, Object> resultMap = new HashMap<>(16);
		Set<String> sets = map.keySet();
		for (String key : sets) {
			resultMap.put(key.toLowerCase(), map.get(key));
		}
		return resultMap;
	}

	public static Boolean existKey(List<String> feilds, String pKeyName) {
		if (feilds.size() > 0) {
			for (String feild : feilds) {
				if (feild.equals(pKeyName)) {
					return true;
				}
			}
		}
		return false;
	}

	public static List<OnlineDevListDataVO> setDataId(String keyName, List<OnlineDevListDataVO> DevList) {
		keyName = keyName.toLowerCase();
		for (OnlineDevListDataVO dataVo : DevList) {
			Map<String, Object> dataMap = dataVo.getData();
			if (dataMap.get(keyName) != null) {
				dataVo.setId(String.valueOf(dataMap.get(keyName)));
			}
		}
		return DevList;
	}

	public static List<Object> getValueList(List<VisualColumnSearchVO> searchVOList){
		List<Object> valueList = new LinkedList<>();
		for (VisualColumnSearchVO vo : searchVOList){
			String projectKey = vo.getConfig().getProjectKey();
			String format;
			switch (projectKey){
				case ProjectKeyConsts.MODIFYTIME:
				case ProjectKeyConsts.CREATETIME:
				case ProjectKeyConsts.DATE:
					JSONArray timeStampArray = (JSONArray) vo.getValue();
					Long o1 =(Long) timeStampArray.get(0);
					Long o2 = (Long) timeStampArray.get(1);
					format = StringUtil.isEmpty(vo.getFormat()) ? "yyyy-MM-dd HH:mm:ss" : vo.getFormat();
					//时间戳转string格式
					String startTime = OnlinePublicUtils.getDateByFormat(o1,format);
					String endTime = OnlinePublicUtils.getDateByFormat(o2,format);
					//处理时间查询条件范围
					endTime=endTime.substring(0,10);
					String firstTimeDate = OnlineDatabaseUtils.getTimeFormat(startTime);
					String lastTimeDate =  OnlineDatabaseUtils.getLastTimeFormat(endTime);
					valueList.add(firstTimeDate);
					valueList.add(lastTimeDate);
					break;
				case ProjectKeyConsts.TIME:
					JSONArray timeArray =(JSONArray)vo.getValue();
					String start = String.valueOf(timeArray.get(0));
					String end =String.valueOf(timeArray.get(1));
					valueList.add(start);
					valueList.add(end);
					break;
				case ProjectKeyConsts.NUM_INPUT:
				case ProjectKeyConsts.CALCULATE:
					List<String> searchArray = (List<String>)vo.getValue();
					Integer firstValue = null;
					Integer secondValue = null;
					for(int i=0;i<searchArray.size();i++){
						String name = searchArray.get(i);
						if(StringUtil.isNotEmpty(name)) {
							if (i == 0) {
								firstValue =Integer.valueOf(name);
							} else {
								secondValue =Integer.valueOf(name);
							}
						}
					}
					if (firstValue != null){
						valueList.add(firstValue);
					}
					if (secondValue != null){
						valueList.add(secondValue);
					}
					break;
				default:
					valueList.add(vo.getValue());
					break;
			}
		}
		return valueList;
	}

	/**
	 * 转换时间格式
	 * @param time
	 * @return
	 */
	public static String getTimeFormat(String time){
		return FormPublicUtils.getTimeFormat(time);
	}

	public static String getLastTimeFormat(String time){
		return FormPublicUtils.getLastTimeFormat(time);
	}

}
