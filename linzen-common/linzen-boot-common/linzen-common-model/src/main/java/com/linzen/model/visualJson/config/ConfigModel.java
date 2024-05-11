package com.linzen.model.visualJson.config;

import com.linzen.model.visualJson.FieLdsModel;
import com.linzen.model.visualJson.TemplateJsonModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConfigModel {
    private String label;
    private String labelWidth;
    private Boolean showLabel;
    private Boolean changeTag;
    private Boolean border;
    private String tag;
    private String tagIcon;
    //是否必填
    private boolean required = false;
    //是否唯一
    private Boolean unique = false;
    private String layout;
    private String dataType;
    private Integer span = 24;
    private String projectKey;
    private String dictionaryType;
    private Integer formId;
    private String relationTable;
    private Long renderKey;
    private Integer columnWidth;
    private List<RegListModel> regList;
    private String reg;
    private Object defaultValue;
    private Boolean defaultCurrent;
    private String active;

    /**
     * 提示语
     */
    private String title;
    private String type;
    private Boolean showIcon;
    private Boolean closable;
    /**
     * app静态数据
     */
    private String options;
    /**
     * 判断defaultValue类型
     */
    private String valueType;
    private String propsUrl;
    private String optionType;
    /**
     * 子表添加字段
     */
    private Boolean showTitle;
    private String tableName;
    private String aliasClassName;
    private List<FieLdsModel> children;
    private List<HeaderModel> complexHeaderList = new ArrayList<>();

    /**
     * 多端显示
     */
    private String visibility="[\"app\",\"pc\"]" ;


    private List<TemplateJsonModel> templateJson = new ArrayList();

    /**
     * 单据规则使用
     */
    private String rule;

    /**
     * 验证规则触发方式
     */
    private String trigger="blur";
    /**
     * 隐藏
     */
    private Boolean noShow=false;
    /**
     * app代码生成器
     */
    private int childNum;
    private String model;

    /**
     * 代码生成器多端显示
     */
    private boolean app = true;
    private boolean pc = true;

    /**
     * 高级查询
     */
    private String parentVModel;

    private Boolean merged=false;
    private String colspan;
    private String rowspan;
    private String rowType;

    private String borderType;
    private String borderColor;
    private String borderWidth;
    private String tipLabel = "";
    private String startRelationField;
    private String endRelationField;
    private Boolean startChild = false;
    private Boolean endRChild = false ;

    /**
     * 开始时间开关
     */
    private Boolean startTimeRule = false;
    /**
     * 开始时间类型：1-特定时间,2-表单字段,3-填写当前时间,4-当前时间前,5-当前时间后
     */
    private String startTimeType;
    /**
     * 开始时间单位：1-年,2-月,3-日/1-时,2-分,3-秒
     */
    private String startTimeTarget;
    /**
     * 开始时间值
     */
    private String startTimeValue;
    private Boolean endTimeRule = false;
    private String endTimeType;
    private String endTimeTarget;
    private String endTimeValue;
    /**
     * 取表格td配置中的backgroundColor
     */
    private String backgroundColor;

    /**
     * 是否参数（视图）
     */
    private Boolean isFromParam = false;
    /**
     * 对齐方式
     */
    private String tableAlign;
    /**
     * 冻结方式
     */
    private String tableFixed;
}
