package com.linzen.model.visualJson;

import com.alibaba.fastjson.annotation.JSONField;
import com.linzen.model.visualJson.config.ConfigModel;
import com.linzen.model.visualJson.options.ColumnOptionModel;
import com.linzen.model.visualJson.props.PropsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FieLdsModel {
    /**
     * 数据配置
     */
    @JSONField(name = "__config__")
    private ConfigModel config;
    /**
     * 设置默认值为空字符串
     */
    @JSONField(name = "__vModel__")
    private String vModel = "";

    private String placeholder;

    private Object style;

    private Boolean clearable;

    private String prefixIcon;

    private Integer precision;

    private String suffixIcon;

    private String maxlength;

    private Boolean showWordLimit;

    private Boolean readonly;

    private Boolean disabled;

    private String label;

    /**
     * 列表拼接字段接收
     */
    private String id = "";

    /**
     * 关联表单id
     */
    private String modelId = "";

    /**
     * 子表表单 按钮
     */
    private List<FooterBtnsModel> footerBtnsList = new ArrayList<>();

    /**
     * 子表表单 列
     */
    private List<FooterBtnsModel> columnBtnsList = new ArrayList<>();

    /**
     * 关联表单 二维码 条形码 字段
     */
    private String relationField;
    private String relationFieldSource;
    private Boolean relationChild = false;
    private String relationModel;
    private Boolean hasPage;
    private String pageSize;
    private String type;
    private Object autoSize;
    private Integer step;
    private Boolean stepstrictly;
    private Object textStyle;
    private Integer lineHeight;
    private Integer fontSize;
    private Boolean showChinese;
    private Boolean showPassword;
    private String tipLabel;

    /**
     * 链接
     */
    private String target;
    private String href;

    /**
     * 大小
     */
    private String size;
    private Boolean filterable;
    /**
     * 关联表单属性
     */
    private String showField;
    /**
     * 多选
     */
    private Boolean multiple = false;

    private Boolean searchMultiple = false;
    private Object value;

    /**
     * 待定
     */
    private PropsModel props;
    /**
     * 待定
     */
    private Boolean showAllLevels;
    private String separator;
    private Boolean isrange;
    private String rangeseparator;
    private String startplaceholder;
    private String endplaceholder;
    private String format;
    private String valueformat;
    private Object pickeroptions;
    /**
     * v2评分-最大值
     */
    private Integer max;
    /**
     * 评分-允许半选
     */
    private Boolean allowhalf;
    /**
     * v3评分-最大值
     */
    private Integer count;
    private Boolean showText;
    private Boolean showScore;
    private Boolean showAlpha;
    private String colorformat;
    private String activecolor;
    private String inactivecolor;
    private String activeValue;
    private String inactiveValue;
    private Integer min;
    private Boolean showStops;
    private Boolean range;
    private String content;
    private String header;
    private Boolean accordion;
    private String tabPosition;
    /**
     * 未找到
     */
    private String accept;
    private Boolean showTip;
    private Integer fileSize;
    private String sizeUnit;
    private Integer limit;
    private String contentPosition;
    /**
     * 标题提示
     */
    private String helpMessage;
    private String buttonText;
    private Integer level;
    private String options;
    private String shadow;
    private String name;
    private String title;

    /**
     * 文件路径类型 默认路径：defaultPath 自定义路径：selfPath
     */
    private String pathType;
    /**
     * 是否分用户存储 1：是 0：否
     */
    private String isAccount;
    /**
     * 文件夹名，子级文件用“/”隔开，如：文件1/文件1-1
     */
    private String folder;

    /**
     * 查询方式 1.eq 2.like 3.between
     */
    private Integer searchType;
    private String interfaceId;
    private List<ColumnOptionModel> columnOptions;
    private String propsValue;

    /**
     * 开关 值
     */
    private String activeTxt;
    private String inactiveTxt;

    /**
     * 条形码 条码颜色
     */
    private String lineColor;
    /**
     * 条形码 背景色
     */
    private String background;
    /**
     * 条形码 宽高
     */
    private Integer width;
    private Integer height;
    /**
     * 条形码 二维码 固定值
     */
    private String staticText;

    private String templateJson = "[]";

    /**
     * 条形码 二维码 类型 （静态,或者组件,当前表单路径） static relation form
     */
    private String dataType = "";

    /**
     * 二维码 条码颜色
     */
    private String colorDark;

    /**
     * 二维码 背景色
     */
    private String colorLight;

    /**
     * 按钮(居中,右,左)
     */
    private String align;

    /**
     * 子表是否合计
     */
    private Boolean showSummary;

    /**
     * 子表合计字段
     */
    private String summaryField;

    /**
     * 所属部门展示内容
     */
    private String showLevel;

    /**
     * 弹窗 样式属性
     */
    private String popupType;
    private String popupTitle;
    private String popupWidth;


    private boolean closable;

    private boolean showIcon;

    private String selectType;

    //自定义
    private String ableDepIds;
    private String ableOrgIds;
    private String ablePosIds;
    private String ableUserIds;
    private String ableRoleIds;
    private String ableGroupIds;
    private String ableIds;
    /**
     * 导入子表字段数量
     */
    private Integer childrenSize;
    /**
     * 是否是需要导入的字段
     */
    private boolean needImport;

    private String relationTableForeign;
    private String mainTableId;
    private String childMainKey;
    /**
     * 0主表 1 副表 2子表
     */
    private Integer tableType;
    private String beforeVmodel;

    private String description;
    private String closeText;

    private String tipText;
    private String direction;
    private String addonAfter;
    private String addonBefore;
    private Boolean isAmountChinese;
    /**
     * 数字输入-是否有加减按键
     */
    private String controls;
    private String startTime;
    private String endTime;

    private String optionType;
    /**
     * 下拉补全展示条数
     */
    private Integer total;

    /**
     * 单选框按键风格
     */
    private String buttonStyle;

    /**
     * 千分位开关
     */
    private boolean thousands = false;

    /**
     * 千分位字段列表
     */
    private List<String> thousandsField = new ArrayList<>();

    /**
     * 展示 存储数据  0-不存储，1-存储
     */
    private Integer isStorage;

    //高级搜索
    private String fieldValue;
    private Object fieldValueOne;
    private Object fieldValueTwo;
    private List<String> dataList = new ArrayList<>();
    private String fieldValueType;
    private String symbol;

    /**
     * 地图属性
     */
    private boolean autoLocation = false;
    private boolean enableLocationScope = false;
    private int adjustmentScope;
    private boolean enableDesktopLocation = false;
    private List<Object> locationScope;

    /**
     * 列表字段是否关键词
     */
    private Boolean isKeyword = false;
    private Boolean sortable = false;

    /**
     * 是否选中数据及子信息(只针对视图)
     */
    private Boolean isIncludeSubordinate = false;

    private Boolean useScan = false;
    private Boolean useMask = false;
    private Object maskConfig;

    /**
     * iframe属性
     * borderType 边框  ，borderColor  边框颜色 ， borderWidth边框宽度
     */
    private String borderType;
    private String borderColor;
    private Integer borderWidth;
}

