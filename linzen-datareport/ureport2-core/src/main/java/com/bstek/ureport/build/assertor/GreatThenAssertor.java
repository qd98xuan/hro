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
package com.bstek.ureport.build.assertor;

import java.math.BigDecimal;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang.StringUtils;

import com.bstek.ureport.Utils;

/**
 * @author 
 * @since 1月12日
 */
public class GreatThenAssertor extends AbstractAssertor {

	@Override
	public boolean eval(Object left, Object right) {
		if(left==null || right==null){
			return false;
		}
		if(StringUtils.isBlank(left.toString()) || StringUtils.isBlank(right.toString())){
			return false;
		}
		if(left instanceof java.sql.Time){
			DateTime datetime = DateUtil.parse(right.toString());
			return ((java.util.Date) left).compareTo(new java.sql.Time(datetime.hour(true), datetime.minute(), datetime.second())) == 1;
		}else if(left instanceof java.util.Date){
			return ((java.util.Date) left).compareTo(DateUtil.parse(right.toString())) == 1;
		}else if(left instanceof String){
			return ((String) left).compareTo(right.toString()) == 1;
		}else {
			BigDecimal leftObj = Utils.toBigDecimal(left);
			right = buildObject(right);
			BigDecimal rightObj = Utils.toBigDecimal(right);
			return leftObj.compareTo(rightObj) == 1;
		}
	}
}
