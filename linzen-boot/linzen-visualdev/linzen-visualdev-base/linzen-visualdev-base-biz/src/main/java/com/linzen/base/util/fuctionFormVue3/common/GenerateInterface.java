package com.linzen.base.util.fuctionFormVue3.common;

import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface GenerateInterface {

    /**
     * 获取 前端 及 后端模板
     *
     * @param templatePath
     * @param type
     * @param hasImport
     * @return
     */
    List<String> getTemplates(String templatePath, int type, boolean hasImport);

    /**
     * 获取副子表model、list模板
     *
     * @param isChild
     * @return
     */
    List<String> getChildTemps(boolean isChild);
}
