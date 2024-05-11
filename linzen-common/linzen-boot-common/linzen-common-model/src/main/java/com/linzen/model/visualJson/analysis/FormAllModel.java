package com.linzen.model.visualJson.analysis;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 解析引擎
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FormAllModel {
    /**所有模板的标签 row(栅格)、card(卡片)、table(子表)、mast(主表)、mastTable(主表)、groupTitle(分组标题)**/
    private String projectKey;
    /**是否是结束标签 0.不是 1.是**/
    private String isEnd = "0";
    /**主表数据**/
    private FormColumnModel formColumnModel;
    /**子表的数据**/
    private FormColumnTableModel childList;
    /**栅格和卡片等数据**/
    private FormModel formModel;
    /**主表中有子表数据**/
    private FormMastTableModel formMastTableModel;
}
