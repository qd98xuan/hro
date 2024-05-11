package com.linzen.generater.factory;

import com.linzen.base.util.functionForm.*;
import com.linzen.generater.model.FormDesign.TemplateMethodEnum;
import org.springframework.stereotype.Component;

/**
 * 代码生成工厂类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Component
public class CodeGenerateFactory {

	/**
	 * 根据模板路径对应实体
	 * @param templateMethod
	 * @return
	 */
	public CodeGenerateUtil getGenerator(String templateMethod){
		if (templateMethod.equals(TemplateMethodEnum.T2.getMethod())){
			return  FormListUtil.getFormListUtil();
		}else if (templateMethod.equals(TemplateMethodEnum.T4.getMethod())){
			return  FormUtil.getFormUtil();
		}else if (templateMethod.equals(TemplateMethodEnum.T3.getMethod())){
			return  FunctionFlowUtil.getFunctionFlowUtil();
		}else if (templateMethod.equals(TemplateMethodEnum.T5.getMethod())){
			return  FlowFormUtil.getFormUtil();
		}else {
			return null;
		}
	}
}
