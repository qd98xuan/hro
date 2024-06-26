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
package com.bstek.ureport.parser.impl.searchform;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import com.bstek.ureport.definition.searchform.CheckboxInputComponent;
import com.bstek.ureport.definition.searchform.LabelPosition;
import com.bstek.ureport.definition.searchform.Option;

/**
 * @author
 * @since 10月24日
 */
public class CheckboxParser implements FormParser<CheckboxInputComponent> {
	@Override
	public CheckboxInputComponent parse(Element element) {
		CheckboxInputComponent component=new CheckboxInputComponent();
		component.setBindParameter(element.attributeValue("bind-parameter"));
		component.setOptionsInline(Boolean.valueOf(element.attributeValue("options-inline")));
		component.setLabel(element.attributeValue("label"));
		component.setType(element.attributeValue("type"));
		String useDataset=element.attributeValue("use-dataset");
		component.setShowValue(element.attributeValue("show-value"));
		if(StringUtils.isNotBlank(useDataset)){
			component.setUseDataset(Boolean.valueOf(useDataset));
			component.setDataset(element.attributeValue("dataset"));
			component.setLabelField(element.attributeValue("label-field"));
			component.setValueField(element.attributeValue("value-field"));
			component.setDefaultType(element.attributeValue("default-type"));
		}
		component.setDefaultValue(element.attributeValue("show-value"));
		component.setLabelPosition(LabelPosition.valueOf(element.attributeValue("label-position")));
		List<Option> options=new ArrayList<Option>();
		for(Object obj:element.elements()){
			if(obj==null || !(obj instanceof Element)){
				continue;
			}
			Element ele=(Element)obj;
			if(!ele.getName().equals("option")){
				continue;
			}
			Option option=new Option();
			options.add(option);
			option.setLabel(ele.attributeValue("label"));
			option.setValue(ele.attributeValue("value"));
		}
		component.setOptions(options);
		return component;
	}
	@Override
	public boolean support(String name) {
		return name.equals("input-checkbox");
	}
}
