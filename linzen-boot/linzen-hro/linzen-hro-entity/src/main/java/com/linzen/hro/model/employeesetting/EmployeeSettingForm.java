package com.linzen.hro.model.employeesetting;

import lombok.Data;

import java.util.List;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 雇员字段设置表
 *
 * @版本： V0.0.1
 * @版权： 领致信息技术有限公司
 * @作者： FHNP
 * @日期： 2024-05-10
 */
@Data
public class EmployeeSettingForm {
    /**
     * 主键
     */
    private String id;

    /**
     * 可看
     **/
    @JsonProperty("canSee")
    private Integer canSee;
    /**
     * 公司ID
     **/
    @JsonProperty("companyId")
    private String companyId;
    /**
     * 下拉的元素
     **/
    @JsonProperty("dropDownArray")
    private String dropDownArray;
    /**
     * 字段编码
     **/
    @JsonProperty("fieldCode")
    private String fieldCode;
    /**
     * 字段名称
     **/
    @JsonProperty("fieldName")
    private String fieldName;
    /**
     * 字段类型
     **/
    @JsonProperty("fieldType")
    private Object fieldType;
    /**
     * 忽略
     **/
    @JsonProperty("ignore")
    private Integer ignore;
    /**
     * 是否禁用
     **/
    @JsonProperty("isDisabled")
    private Integer isDisabled;
    /**
     * 能否编辑
     **/
    @JsonProperty("isEdit")
    private Integer isEdit;
    /**
     * 是否必须
     **/
    @JsonProperty("isNecessary")
    private Integer isNecessary;
    /**
     * 是否公开
     **/
    @JsonProperty("isOpen")
    private Integer isOpen;
    /**
     * 模块编码
     **/
    @JsonProperty("moduleCode")
    private String moduleCode;
    /**
     * 下拉的元素字符串
     **/
    @JsonProperty("options")
    private String options;
    /**
     * 模块类型
     **/
    @JsonProperty("moduleType")
    private String moduleType;
    /**
     * 来源ID
     **/
    @JsonProperty("sourceId")
    private String sourceId;
    /**
     * 排序
     **/
    @JsonProperty("sort")
    private BigDecimal sort;
    /**
     * 模板ID
     **/
    @JsonProperty("templateId")
    private String templateId;
    /**
     * 最大长度
     **/
    @JsonProperty("textLength")
    private BigDecimal textLength;
    /**
     * 枚举列表
     **/
    @JsonProperty("dropDownEnumList")
    private String dropDownEnumList;
    /**
     * 是否分组字段
     **/
    @JsonProperty("groupField")
    private Integer groupField;
    /**
     * 显示列表
     **/
    @JsonProperty("listOfShow")
    private String listOfShow;
    /**
     * 模块名称
     **/
    @JsonProperty("moduleName")
    private String moduleName;
    /**
     * 描述或说明
     **/
    @JsonProperty("description")
    private String description;
    /**
     * 字段来源标识
     **/
    @JsonProperty("entityFlag")
    private Object entityFlag;

    /**
     * 提交的JSON字符串
     **/
    @JsonProperty("content")
    private String content;
}
