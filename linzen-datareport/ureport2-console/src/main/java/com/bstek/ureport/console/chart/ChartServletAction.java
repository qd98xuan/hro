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
package com.bstek.ureport.console.chart;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bstek.ureport.cache.*;
import com.bstek.ureport.chart.ChartData;
import com.bstek.ureport.console.RenderPageServletAction;
import com.bstek.ureport.console.util.ActionResult;
import com.bstek.ureport.utils.UnitUtils;

/**
 * @author
 * @since 6月30日
 */
public class ChartServletAction extends RenderPageServletAction {
	@Override
	public void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		storeData(req, resp);
		writeObjectToJson(resp, ActionResult.success());
	}

	public void storeData(HttpServletRequest req, HttpServletResponse resp)  {
		String chartId = req.getParameter("_chartId");
		CacheUtils.setChartData(chartId);
	}

	@Override
	public String url() {
		return "/chart";
	}
}
