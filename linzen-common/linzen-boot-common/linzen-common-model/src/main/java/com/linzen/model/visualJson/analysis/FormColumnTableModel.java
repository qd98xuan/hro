package com.linzen.model.visualJson.analysis;
import com.linzen.model.visualJson.FooterBtnsModel;
import com.linzen.model.visualJson.config.HeaderModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析引擎
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FormColumnTableModel {

    /**json原始名称**/
    private String tableModel;
    /**表名称**/
    private String tableName;
    /**标题**/
    private String label;
    private String tipLabel;
    /**宽度**/
    private Integer span;
    /**是否显示标题**/
    private boolean showTitle;
    /**按钮名称**/
    private String actionText;
    /**子表的属性**/
    private List<FormColumnModel> childList;
    private List<HeaderModel> complexHeaderList;
    private List<FormColumnModel> childFieldList;
    /**app子表属性**/
    private String fieLdsModel;

    /**
     * 子表是否合计
     */
    private Boolean showSummary;

    /**
     * 子表合计字段
     */
    private String summaryField;

    /**
     * app子表合计名称
     */
    private String summaryFieldName;

    /**
     * 代码生成器多端显示
     */
    private boolean app = true;
    private boolean pc = true;

    private String visibility;
    private boolean required = false;
    /**
     * 别名
     */
    private String aliasClassName;
    /**
     * 别名首字母小写
     */
    private String aliasLowName;

    /**
     * 别名首字母大写
     */
    private String aliasUpName;
    /**
     * 千分位开关
     */
    private boolean thousands=false;
    /**
     * 千分位字段列表
     */
    private List<String> thousandsField=new ArrayList<>();

    /**
     * 设计子表底部按钮
     */
    private List<FooterBtnsModel> footerBtnsList = new ArrayList<>();

    /**
     * 设计子表顶部按钮
     */
    private List<FooterBtnsModel> columnBtnsList = new ArrayList<>();
}
