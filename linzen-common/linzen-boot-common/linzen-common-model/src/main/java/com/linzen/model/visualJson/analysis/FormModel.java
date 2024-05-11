package com.linzen.model.visualJson.analysis;

import com.alibaba.fastjson.annotation.JSONField;
import com.linzen.model.visualJson.FieLdsModel;
import com.linzen.model.visualJson.config.ConfigModel;
import lombok.Data;

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
public class FormModel {

    /**
     * 卡片
     */
    private String shadow;
    private String header;

    /**
     * 栅格
     */
    private Integer span;

    /**
     * 标签页
     */
    private String title;
    private String name;
    private String model;
    private Boolean accordion;

    /**
     * 标签页
     */
    private String tabPosition;
    private String type;

    /**
     * 折叠、标签公用
     */
    private String active;
    private String activeIndex;

    /**判断折叠、标签是否最外层 0.不是 1.是**/
    private String outermost;

    /**
     * 折叠、标签公用的子节点
     */
    private List<FieLdsModel> children;

    /**
     * 分组标题
     */
    private String content;
    /**
     * 文本
     */
    private String textStyle;
    private String style;
    @JSONField(name = "__config__")
    private ConfigModel config;

    /**
     * 分组标签、分割线公用
     */
    private String contentPosition;
    /**
     * 标题提示
     */
    private String helpMessage;

    /**
     *按钮
     */
    private String align;
    private String buttonText;

    /**
     * app代码生成器
     */
    private int childNum;

    /**
     * 二维码条形码
     */
    private String dataType ="";

    private String relationField;

    private String visibility;

    private String href;

    private String target;

    private String tipLabel;

    private boolean closable;

    private boolean showIcon;

    private String selectType;

    private boolean merged;
    private String colspan;
    private String rowspan;
    private String rowType;
    private String description;
    private String closeText;
    private String borderType;
    private String borderColor;
    private String borderWidth;

    //自定义
    private String ableDepIds;
    private String ablePosIds;
    private String ableUserIds;
    private String ableRoleIds;
    private String ableGroupIds;
}
