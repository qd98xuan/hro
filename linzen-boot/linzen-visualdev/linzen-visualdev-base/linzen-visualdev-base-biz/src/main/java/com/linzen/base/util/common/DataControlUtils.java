package com.linzen.base.util.common;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.linzen.model.visualJson.FieLdsModel;
import com.linzen.util.JsonUtil;
import com.linzen.util.visiual.ProjectKeyConsts;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 代码生成器数据处理工具类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class DataControlUtils {

	/**
	 * 将字符串的首字母转大写
	 * @param name 需要转换的字符串
	 * @return
	 */
	public static String captureName(String name) {
		char[] ch = name.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			if (i == 0) {
				ch[0] = Character.toUpperCase(ch[0]);
			}
		}
		StringBuffer a = new StringBuffer();
		a.append(ch);
		return a.toString();

	}

	public static String initialLowercase(String name) {
		char[] ch = name.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			if (i == 0) {
				ch[0] = Character.toLowerCase(ch[0]);
			}
		}
		StringBuffer a = new StringBuffer();
		a.append(ch);
		return a.toString();
	}

	 public static String getPlaceholder(String projectKey){
			String placeholderName = "请选择";
			switch (projectKey){
				case ProjectKeyConsts.BILLRULE:
				case ProjectKeyConsts.MODIFYUSER:
				case ProjectKeyConsts.CREATEUSER:
				case ProjectKeyConsts.COM_INPUT:
				case ProjectKeyConsts.TEXTAREA:
					placeholderName = "请输入";
					break;
				default:
					break;
			}
			return placeholderName;
	 }

	/**
	 * 去重
	 * @param keyExtractor
	 * @param <T>
	 * @return
	 */
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Map<Object,Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	public static FieLdsModel setAbleIDs(FieLdsModel fieLdsModel){
		if (fieLdsModel.getAbleDepIds()!= null){
			fieLdsModel.setAbleDepIds(JSONObject.toJSONString(fieLdsModel.getAbleDepIds()));
		}
		if (fieLdsModel.getAblePosIds()!= null){
			fieLdsModel.setAblePosIds(JSONObject.toJSONString(fieLdsModel.getAblePosIds()));
		}
		if (fieLdsModel.getAbleUserIds()!= null){
			fieLdsModel.setAbleUserIds(JSONObject.toJSONString(fieLdsModel.getAbleUserIds()));
		}
		if (fieLdsModel.getAbleRoleIds()!= null){
			fieLdsModel.setAbleRoleIds(JSONObject.toJSONString(fieLdsModel.getAbleRoleIds()));
		}
		if (fieLdsModel.getAbleGroupIds()!= null){
			fieLdsModel.setAbleGroupIds(JSONObject.toJSONString(fieLdsModel.getAbleGroupIds()));
		}
		if (fieLdsModel.getAbleIds()!= null){
			fieLdsModel.setAbleIds(JSONObject.toJSONString(fieLdsModel.getAbleIds()));
		}
		//model字段验证reg转换
		if (fieLdsModel.getConfig().getRegList() != null) {
			String o1 = JSONObject.toJSONString(JsonUtil.createObjectToString(fieLdsModel.getConfig().getRegList()));
			fieLdsModel.getConfig().setReg(o1);
		}
		return fieLdsModel;
	}
}
