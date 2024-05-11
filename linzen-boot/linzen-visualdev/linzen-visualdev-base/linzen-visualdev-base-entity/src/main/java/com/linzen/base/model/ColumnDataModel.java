package com.linzen.base.model;

import com.linzen.base.model.Template6.BtnData;
import com.linzen.database.model.superQuery.SuperJsonModel;
import com.linzen.model.visualJson.config.HeaderModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Schema(description="")
public class ColumnDataModel {
    private String searchList;
    private String printIds;
    private Boolean hasDefaultValue = false;
    private Boolean hasSuperQuery = false;
    /**
     * 合计配置
     */
    private boolean showSummary;
    /**
     * 合计字段
     */
    private List<String> summaryField = new ArrayList<>();
    /**
     * 子表展示样式
     */
    private Integer childTableStyle =1;
    private String columnOptions;
    private String columnList;
    private String defaultColumnList;
    private String sortList;
    private Integer type;
    private String defaultSidx;
    private String sort;
    private Boolean hasPage;
    private Integer pageSize;
    private String treeTitle;
    private String treeDataSource;
    private String treeDictionary;
    private String treeRelation;
    private String treePropsUrl;
    private String treePropsValue;
    private String treePropsChildren;
    private String treePropsLabel;
    private String isLeaf;
    private String groupField;
    private List<BtnData> btnsList = new ArrayList<>();
    private List<BtnData> columnBtnsList = new ArrayList<>();
    private String uploaderTemplateJson;
    /**
     * 自定义按钮区
     */
    private String customBtnsList;
    /**
     * 列表权限
     */
    private Boolean useColumnPermission;
    /**
     * 表单权限
     */
    private Boolean useFormPermission;
    /**
     * 按钮权限
     */
    private Boolean useBtnPermission;
    /**
     * 数据权限
     */
    private Boolean useDataPermission;

    //以下树形列表属性，type=5的情况
    /**
     * 同步异步（0：同步，1：异步）
     */
    private Integer treeLazyType=0;

    /**
     * 父级字段
     */
    private String parentField;
    /**
     * 子级字段
     */
    private String subField;

    /**
     * 左侧树同步异步
     */
    private Integer treeSynType;
    /**
     * 左侧树查询
     */
    private Boolean hasTreeQuery;

    private String treeInterfaceId;
    private String treeTemplateJson;


    private SuperJsonModel ruleList = new SuperJsonModel();
    private SuperJsonModel ruleListApp = new SuperJsonModel();
    private List<HeaderModel> complexHeaderList = new ArrayList<>();

    /**
     * 千分位开关
     */
    private boolean thousands=false;
    /**
     * 千分位字段列表
     */
    private List<String> thousandsField = new ArrayList<>();
    /**
     * 默认排序列表
     */
    private Object defaultSortConfig;
}
