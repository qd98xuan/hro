package com.linzen.util.visiual;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * projectKey类型
 *
 * @author FHNP
 * @create 2018/1/9
 */

public class ProjectKeyConsts {
    /**
     * 单行
     */
    public static final String COM_INPUT = "input";

    /**
     * 多行
     */
    public static final String TEXTAREA = "textarea";

    /**
     * 单选
     */
    public static final String RADIO = "radio";

    /**
     * 开关
     */
    public static final String SWITCH = "switch";

    /**
     * 下拉框
     */
    public static final String SELECT = "select";

    /**
     * 多选框
     */
    public static final String CHECKBOX = "checkbox";
    /**
     * 公司
     */
    public static final String COMSELECT = "organizeSelect";
    /**
     * 部门
     */
    public static final String DEPSELECT = "depSelect";
    /**
     * 數據字典
     */
    public static final String DICSELECT = "dicSelect";
    /**
     * 岗位
     */
    public static final String POSSELECT = "posSelect";
    /**
     * 用户
     */
    public static final String USERSELECT = "userSelect";

    /**
     * 用户
     */
    public static final String CUSTOMUSERSELECT = "usersSelect";

    /**
     * 角色选择
     */
    public static final String ROLESELECT = "roleSelect";

    /**
     * 分组选择
     */
    public static final String GROUPSELECT = "groupSelect";
    /**
     * 行政区划
     */
    public static final String ADDRESS = "areaSelect";
    /**
     * 时间范围
     */
    public static final String TIMERANGE = "timeRange";
    /**
     * 日期范围
     */
    public static final String DATERANGE = "dateRange";
    /**
     * 时间选择
     */
    public static final String TIME = "timePicker";
    /**
     * 日期选择
     */
    public static final String DATE = "datePicker";

    /**
     * 评分
     */
    public static final String RATE = "rate";

    /**
     * 附件
     */
    public static final String UPLOADFZ = "uploadFile";
    /**
     * 图片
     */
    public static final String UPLOADIMG = "uploadImg";
    /**
     * 滑块
     */
    public static final String SLIDER = "slider";
    /**
     * 所属组织
     */
    public static final String CURRORGANIZE = "currOrganize";
    /**
     * 所属部门
     */
    public static final String CURRDEPT = "currDept";
    /**
     * 创建用户
     */
    public static final String CREATEUSER = "createUser";
    /**
     * 创建时间
     */
    public static final String CREATETIME = "createTime";
    /**
     * 修改用户
     */
    public static final String MODIFYUSER = "modifyUser";
    /**
     * 修改时间
     */
    public static final String MODIFYTIME = "modifyTime";
    /**
     * 所属岗位
     */
    public static final String CURRPOSITION = "currPosition";
    /**
     * 单据规则
     */
    public static final String BILLRULE = "billRule";

    /**
     * 功能关联表单
     */
    public static final String RELATIONFORM = "relationForm";

    /**
     * 关联表单属性
     */
    public static final String RELATIONFORM_ATTR = "relationFormAttr";

    /**
     * 工作流关联表单
     */
    public static final String RELATIONFLOW = "relationFlow";

    /**
     * 树形选择
     */
    public static final String TREESELECT = "treeSelect";

    /**
     * 级联选择
     */
    public static final String CASCADER = "cascader";

    /**
     * 子表ProjectKey
     */
    public static final String CHILD_TABLE = "table";

    /**
     * 弹窗选择
     */
    public static final String POPUPSELECT = "popupSelect";
    /**
     * 下拉表格
     */
    public static final String POPUPTABLESELECT = "popupTableSelect";
    /**
     * 弹窗选择属性
     */
    public static final String POPUPSELECT_ATTR = "popupAttr";
    /**
     * 数字输入
     */
    public static final String NUM_INPUT = "inputNumber";
    /**
     * 计算公式
     */
    public static final String CALCULATE = "calculate";
    /**
     * 分组标题
     */
    public static final String GROUP_TITLE = "groupTitle";

    /**
     * 二维码
     */
    public static final String QR_CODE = "qrcode";

    /**
     * 条形码
     */
    public static final String BARCODE = "barcode";

    /**
     * 富文本
     */
    public static final String EDITOR = "editor";

    /**
     * 颜色选择
     */
    public static final String CPLOR_PICKER = "colorPicker";

    /**
     * 分割线
     */
    public static final String DIVIDER = "divider";

    /**
     * 按钮
     */
    public static final String BUTTON = "button";

    /**
     * 链接
     */
    public static final String LINK = "link";

    /**
     * 提示
     */
    public static final String ALERT = "alert";

    /**
     * 卡片容器
     */
    public static final String CARD = "card";

    /**
     * 栅格容器
     */
    public static final String ROW = "row";

    /**
     * 标签面板
     */
    public static final String TAB = "tab";

    /**
     * 折叠面板
     */
    public static final String COLLAPSE = "collapse";

    /**
     * 定位
     */
    public static final String LOCATION = "location";

    /**
     * 手写签名
     */
    public static final String SIGN = "sign";

    /**
     * iframe
     */
    public static final String IFRAME = "iframe";


    /**
     * 列表关键词-key
     */
    public static final String PROJECTKEYWORD = "projectKeyword";

    /**
     * 子表前缀
     */
    public static final String CHILD_TABLE_PREFIX = "tablefield";


    public static final List<String> SplitKey = new ArrayList() {{
        add(ProjectKeyConsts.DATE);
        add(ProjectKeyConsts.TIME);
        add(ProjectKeyConsts.NUM_INPUT);
        add(ProjectKeyConsts.CREATETIME);
        add(ProjectKeyConsts.MODIFYTIME);
    }};

    public static final List<String> BaseSelect = new ArrayList() {{
        add(ProjectKeyConsts.COM_INPUT);
        add(ProjectKeyConsts.TEXTAREA);
        add(ProjectKeyConsts.BILLRULE);
        add(ProjectKeyConsts.POPUPTABLESELECT);
        add(ProjectKeyConsts.RELATIONFORM);
        add(ProjectKeyConsts.RELATIONFORM_ATTR);
        add(ProjectKeyConsts.POPUPSELECT);
        add(ProjectKeyConsts.POPUPSELECT_ATTR);
    }};

    public static final List<String> SelectIgnore = new ArrayList() {{
        add(ProjectKeyConsts.COMSELECT);
        add(ProjectKeyConsts.ADDRESS);
        add(ProjectKeyConsts.CASCADER);
        add(ProjectKeyConsts.CHECKBOX);
        add(ProjectKeyConsts.CURRORGANIZE);
        add(ProjectKeyConsts.CUSTOMUSERSELECT);
    }};

    public static final List<String> NumSelect = new ArrayList() {{
        add(ProjectKeyConsts.CALCULATE);
        add(ProjectKeyConsts.NUM_INPUT);
        add(ProjectKeyConsts.RATE);
        add(ProjectKeyConsts.SLIDER);
    }};

    public static final List<String> DateSelect = new ArrayList() {{
        add(ProjectKeyConsts.DATE);
        add(ProjectKeyConsts.CREATETIME);
        add(ProjectKeyConsts.MODIFYTIME);
    }};

    /**
     * 模板设置-导入不展示的表单控件
     */
    public static final List<String> getUploadMaybeNull() {
        return Arrays.asList(new String[]{UPLOADFZ, UPLOADIMG, CPLOR_PICKER, DIVIDER, BUTTON, LINK, ALERT, POPUPTABLESELECT, RELATIONFORM, POPUPSELECT,
                RELATIONFORM_ATTR, POPUPSELECT_ATTR, QR_CODE, BARCODE, CALCULATE, GROUP_TITLE, CARD, ROW, TAB, COLLAPSE, RATE, SLIDER});
    }

    /**
     * 获取系统控件keylist
     */
    public static final List<String> getSystemKey() {
        return Arrays.asList(new String[]{CREATEUSER, CREATETIME, MODIFYUSER, MODIFYTIME, CURRORGANIZE, CURRDEPT, CURRPOSITION});
    }

    /**
     * 获取text字段（oracle和DM字段需要dbms_lob.substr()）
     */
    public static final List<String> getTextField() {
        return Arrays.asList(new String[]{UPLOADFZ, UPLOADIMG, EDITOR, SIGN});
    }

    /**
     * 存储数据结构可能为数组控件
     */
    public static final List<String> getArraysKey() {
        return Arrays.asList(new String[]{UPLOADFZ, UPLOADIMG, SELECT, DEPSELECT, ROLESELECT, USERSELECT, CUSTOMUSERSELECT, COMSELECT,
                TREESELECT, POSSELECT, GROUPSELECT, ADDRESS, CASCADER, CURRORGANIZE, CHECKBOX, POPUPTABLESELECT});
    }
}
