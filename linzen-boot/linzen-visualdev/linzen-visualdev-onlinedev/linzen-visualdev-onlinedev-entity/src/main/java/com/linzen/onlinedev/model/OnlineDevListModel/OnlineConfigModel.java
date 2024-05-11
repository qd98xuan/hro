package com.linzen.onlinedev.model.OnlineDevListModel;


import com.linzen.model.visualJson.config.RegListModel;
import lombok.Data;

import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class OnlineConfigModel {
    private String label;
    private String labelWidth;
    private Boolean showLabel;
    private Boolean changeTag;
    private Boolean border;
    private String tag;
    private String tagIcon;
    private Boolean required;
    private String layout;
    private String dataType;
    private Integer span;
    private String projectKey;
    private String dictionaryType;
    private Integer formId;
    private Long renderKey;
    private Integer columnWidth;
    private List<RegListModel> regList;
    private Object defaultValue;
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
    private String showTitle;
    private String tableName;
    private List<OnlineFieldsModel> children;
    /**
     * 单据规则使用
     */
    private String rule;

    /**
     * 验证规则触发方式
     */
    private String trigger = "blur" ;
    /**
     * 隐藏
     */
    private Boolean noShow = false;
    private String projectKeyName;
}
