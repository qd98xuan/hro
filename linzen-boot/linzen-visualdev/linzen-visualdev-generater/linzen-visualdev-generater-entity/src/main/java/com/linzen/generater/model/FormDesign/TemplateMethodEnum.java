package com.linzen.generater.model.FormDesign;
/**
 *
 * 模板路径
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public enum TemplateMethodEnum {
	T1("TemplateCode1"),
	T2("TemplateCode2"),
	T3("TemplateCode3"),
	T4("TemplateCode4"),
	T5("TemplateCode5");

	TemplateMethodEnum(String method) {
		this.method = method;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	private String method;
}
