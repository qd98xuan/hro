package com.linzen.base.util.fuctionFormVue3;


import com.linzen.base.util.fuctionFormVue3.common.GenerateInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class GenerateFormFlow implements GenerateInterface {
    @Override
    public List<String> getTemplates(String templatePath, int type, boolean hasImport) {
        List<String> templates = new ArrayList<>();
        //前端
        templates.add(templatePath + File.separator + "html" + File.separator + "index.vue.vm");
        templates.add(templatePath + File.separator + "html" + File.separator + "Form.vue.vm");
        //api接口
        templates.add(File.separator + "helper" + File.separator + "api.ts.vm");
        //后端
        templates.add(File.separator + "java" + File.separator + "Form.java.vm");
        templates.add(File.separator + "java" + File.separator + "Constant.java.vm");
        return templates;
    }

    @Override
    public List<String> getChildTemps(boolean isChild) {
        List<String> templates = new ArrayList<>();
        if (isChild) {
            templates.add(File.separator + "java" + File.separator + "Model.java.vm");
        }
        return templates;
    }

}
