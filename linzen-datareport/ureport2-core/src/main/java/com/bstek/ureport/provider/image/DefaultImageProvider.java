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
package com.bstek.ureport.provider.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.linzen.util.XSSEscape;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import com.bstek.ureport.exception.ReportComputeException;

/**
 * @author 
 * @since 3月6日
 */
public class DefaultImageProvider implements ImageProvider,ApplicationContextAware {
	private ApplicationContext applicationContext;
	private String baseWebPath;
	private String imgPath;
	@Override
	public InputStream getImage(String path) {
		try {
			if(path.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX) || path.startsWith("/WEB-INF")){
				return applicationContext.getResource(path).getInputStream();				
			}else{
				if(imgPath != null){
					path = imgPath + path;
					path = path.replaceAll("\\\\", "/").replaceAll("/{2,}", "/").replaceAll("\\.{2,}", "\\.");
				}else{
					path=baseWebPath+path;
				}
				return new FileInputStream(XSSEscape.escapePath(path));
			}
		} catch (IOException e) {
			throw new ReportComputeException(e);
		}
	}

	@Override
	public boolean support(String path) {
		if(path.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)){
			return true;
		}else if(baseWebPath!=null && (path.startsWith("/") || path.startsWith("/WEB-INF"))){
			return true;
		}else if(imgPath!=null && !path.matches("\\w+://.*")){
			return true;
		}
		return false;
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if(applicationContext instanceof WebApplicationContext){
			WebApplicationContext context=(WebApplicationContext)applicationContext;
			baseWebPath=context.getServletContext().getRealPath("/");
		}
		if(StringUtils.hasText(applicationContext.getEnvironment().getProperty("config.imgPath"))){
			imgPath = applicationContext.getEnvironment().getProperty("config.imgPath");
			imgPath = imgPath.replaceAll("\\\\", "/");
			if(!imgPath.endsWith("/")){
				imgPath += "/";
			}
			File f = new File(imgPath);
			if(!f.exists()){
				f.mkdirs();
			}
		}
		this.applicationContext=applicationContext;
	}
}
