/*******************************************************************************
 * Copyright 2017 Bstek
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.bstek.ureport.definition.searchform;


import cn.hutool.system.UserInfo;
import com.alibaba.fastjson.JSONObject;
import com.bstek.ureport.Utils;
import com.bstek.ureport.build.Dataset;
import com.bstek.ureport.exception.DatasetUndefinitionException;
import com.bstek.ureport.utils.HttpUtil;
import com.bstek.ureport.utils.ReportConfig;
import com.linzen.util.ServletUtil;
import com.linzen.util.context.SpringContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * @author
 * @since 10月23日
 */
public class RadioInputComponent extends InputComponent {

	private ReportConfig reportConfig = SpringContext.getBean(ReportConfig.class);

	/**
	 * 数据来源
	 */
	private boolean useDataset;

	public boolean isUseDataset() {
		return useDataset;
	}

	public void setUseDataset(boolean useDataset) {
		this.useDataset = useDataset;
	}

	/**
	 * 数据集
	 */
	private String dataset;

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	/**
	 * 显示字段
	 */
	private String labelField;

	public String getLabelField() {
		return labelField;
	}

	public void setLabelField(String labelField) {
		this.labelField = labelField;
	}
	/**
	 * 实际字段
	 */
	private String valueField;

	public String getValueField() {
		return valueField;
	}

	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	/**
	 * 默认类型 1- 自定义 2- 系统
	 */
	private String defaultType;

	public String getDefaultType() {
		return defaultType;
	}

	public void setDefaultType(String defaultType) {
		this.defaultType = defaultType;
	}


	private String showValue;


	public String getShowValue() {
		return showValue;
	}

	public void setShowValue(String showValue) {
		this.showValue = showValue;
	}

	/**
	 * 默认值
	 */
	private String defaultValue;

	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * set方法返回默认值
	 *
	 * @param defaultValue
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		if (useDataset) {
			if ("2".equals(defaultType)) {
				String token = ServletUtil.getRequest().getParameter("token");
				JSONObject object = HttpUtil.httpRequest(reportConfig.getUserUrl(), "GET", null, token);
				if ("1".equals(defaultValue)) {
					this.defaultValue = object!=null?object.getString("userId"):"";
				} else if ("2".equals(defaultValue)) {
					this.defaultValue =  object!=null?object.getString("departmentId"):"";
				} else if ("3".equals(defaultValue)) {
					this.defaultValue =  object!=null?object.getString("organizeId"):"";
				} else if ("4".equals(defaultValue)) {
					this.defaultValue =  object!=null?object.getString("positionId"):"";
				} else if ("5".equals(defaultValue)) {
					this.defaultValue =  object!=null?object.getString("roleId"):"";
				} else if ("6".equals(defaultValue)) {
					this.defaultValue =  object!=null?object.getString("managerId"):"";
				}
			}
		}
	}

	private boolean optionsInline;
	private List<Option> options;
	@Override
	String inputHtml(RenderContext context) {
		StringBuilder sb=new StringBuilder();
		String name=getBindParameter();
		Object pvalue=context.getParameter(name)==null ? defaultValue!=null? defaultValue:"" : context.getParameter(name);
		String[] data=pvalue.toString().split(",");
		List<String> list=Arrays.asList(data);
		sb.append("<div>");
		Object value = "";
		Object label = "";
		if (useDataset) {
			Dataset ds=context.getDataset(dataset);
			if(ds==null){
				throw new DatasetUndefinitionException(dataset);
			}
			for(Object obj:ds.getData()){
				label= Utils.getProperty(obj, labelField);
				value=Utils.getProperty(obj, valueField);
				String checked = String.valueOf(value).equals(defaultValue) ? "checked" : "";
				if(this.optionsInline){
					sb.append("<span class='checkbox-inline' style='padding-top:0px;padding-left:2px;padding-top:0px'><input value='"+value+"' "+checked+" type='radio' name='"+name+"'" +
							" use-dataset = ' " + useDataset +  " '" +
							" dataset = ' " + dataset +  " '" +
							" label-field = ' " + labelField +  " '" +
							" value-field = ' " + valueField +  " '" +
							" default-type = ' " + defaultType +  " '" +
							" default-value = ' " + defaultValue +  " '" +
							"> "+label+"</span>");
				}else{
					sb.append("<span class='checkbox'><input value='"+value+"' type='radio' "+checked+" name='"+name+"' style='margin-left: auto'" +
							" use-dataset = ' " + useDataset +  " '" +
							" dataset = ' " + dataset +  " '" +
							" label-field = ' " + labelField +  " '" +
							" value-field = ' " + valueField +  " '" +
							" default-type = ' " + defaultType +  " '" +
							" default-value = ' " + defaultValue +  " '" +
							"> <span style=\"margin-left:15px\">"+label+"</span></span>");
				}
			}
		} else {
			for(Option option:options){
				value=option.getValue();
				label=option.getLabel();
				String checked = list.contains(String.valueOf(value)) ? "checked" : "";
				if(this.optionsInline){
					sb.append("<span class='checkbox-inline' style='padding-top:0px;padding-left:2px;padding-top:0px'><input value='"+value+"' "+checked+" type='radio' name='"+name+"'" +
							" use-dataset = ' " + useDataset +  " '" +
							" dataset = ' " + dataset +  " '" +
							" label-field = ' " + labelField +  " '" +
							" value-field = ' " + valueField +  " '" +
							" default-type = ' " + defaultType +  " '" +
							" default-value = ' " + defaultValue +  " '" +
							"> "+label+"</span>");
				}else{
					sb.append("<span class='checkbox'><input value='"+value+"' type='radio' "+checked+" name='"+name+"' style='margin-left: auto'" +
							" use-dataset = ' " + useDataset +  " '" +
							" dataset = ' " + dataset +  " '" +
							" label-field = ' " + labelField +  " '" +
							" value-field = ' " + valueField +  " '" +
							" default-type = ' " + defaultType +  " '" +
							" default-value = ' " + defaultValue +  " '" +
							"> <span style=\"margin-left:15px\">"+label+"</span></span>");
				}
			}
//			if(pvalue.equals("")){
//				sb.append("<span class='checkbox'><input value='"+pvalue+"' type='radio' checked name='"+name+"' style='margin-left: auto'" +
//						" use-dataset = ' '" +
//						" dataset = ' '" +
//						" label-field = ' '" +
//						" value-field = ' '" +
//						" default-type = ' '" +
//						" default-value = ' '" +
//						"> <span style=\"margin-left:15px\"></span></span>");
//			}
		}
		sb.append("</div>");
		return sb.toString();
	}
	@Override
	public String initJs(RenderContext context) {
		String name=getBindParameter();
		StringBuilder sb=new StringBuilder();
		sb.append("formElements.push(");
		sb.append("function(){");
		sb.append("if(''==='"+name+"'){");
		sb.append("alert('单选框未绑定查询参数名，不能进行查询操作!');");
		sb.append("throw '单选框未绑定查询参数名，不能进行查询操作!'");
		sb.append("}");
		sb.append("return {");
		sb.append("\""+name+"\":");
		sb.append("$(\"input[name='"+getBindParameter()+"']:checked\").val()");
		sb.append("}");
		sb.append("}");
		sb.append(");");
		return sb.toString();
	}
	public void setOptionsInline(boolean optionsInline) {
		this.optionsInline = optionsInline;
	}
	public boolean isOptionsInline() {
		return optionsInline;
	}
	public void setOptions(List<Option> options) {
		this.options = options;
	}
	public List<Option> getOptions() {
		return options;
	}
}
