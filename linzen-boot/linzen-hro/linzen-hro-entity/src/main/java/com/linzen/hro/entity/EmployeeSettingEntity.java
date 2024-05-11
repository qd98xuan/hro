package com.linzen.hro.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.linzen.base.entity.SuperExtendEntity;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
/**
 * 花名册员工设置
 *
 * @版本： V0.0.1
 * @版权： 领致信息技术有限公司
 * @作者： FHNP
 * @日期： 2024-05-10
 */
@Data
@TableName("employee_setting")
public class EmployeeSettingEntity extends SuperExtendEntity.SuperExtendDEEntity<String> implements Serializable {
    @TableId(value ="F_ID"  )
	private String id;
    /**
	 * 可看
	 **/
    @TableField("F_CAN_SEE")
	private String canSee;
    /**
	 * 公司ID
	 **/
    @TableField("F_COMPANY_ID")
	private String companyId;
    /**
	 * 下拉的元素
	 **/
    @TableField("F_DROP_DOWN_ARRAY")
	private String dropDownArray;
    /**
	 * 字段编码
	 **/
    @TableField("F_FIELD_CODE")
	private String fieldCode;
    /**
	 * 字段名称
	 **/
    @TableField("F_FIELD_NAME")
	private String fieldName;
    /**
	 * 字段类型
	 **/
    @TableField("F_FIELD_TYPE")
	private String fieldType;
    /**
	 * 创建时间
	 **/
    @TableField("F_GMT_CREATE")
	private Long gmtCreate;
    /**
	 * 修改时间
	 **/
    @TableField("F_GMT_MODIFIED")
	private Long gmtModified;
    /**
	 * 忽略
	 **/
    @TableField("F_IGNORE")
	private String ignore;
    /**
	 * 是否禁用
	 **/
    @TableField("F_IS_DISABLED")
	private String isDisabled;
    /**
	 * 能否编辑
	 **/
    @TableField("F_IS_EDIT")
	private String isEdit;
    /**
	 * 是否必须
	 **/
    @TableField("F_IS_NECESSARY")
	private String isNecessary;
    /**
	 * 是否公开
	 **/
    @TableField("F_IS_OPEN")
	private String isOpen;
    /**
	 * 模块编码
	 **/
    @TableField("F_MODULE_CODE")
	private String moduleCode;
    /**
	 * 模块类型
	 **/
    @TableField("F_MODULE_TYPE")
	private String moduleType;
    /**
	 * 下拉的元素字符串
	 **/
    @TableField("F_OPTIONS")
	private String options;
    /**
	 * 来源ID
	 **/
    @TableField("F_SOURCE_ID")
	private String sourceId;
    /**
	 * 排序
	 **/
    @TableField("F_SORT")
	private Long sort;
    /**
	 * 模板ID
	 **/
    @TableField("F_TEMPLATE_ID")
	private String templateId;
    /**
	 * 最大长度
	 **/
    @TableField("F_TEXT_LENGTH")
	private Integer textLength;
    /**
	 * 枚举列表
	 **/
    @TableField("F_DROP_DOWN_ENUM_LIST")
	private String dropDownEnumList;
    /**
	 * 是否分组字段
	 **/
    @TableField("F_GROUP_FIELD")
	private String groupField;
    /**
	 * 显示列表
	 **/
    @TableField("F_LIST_OF_SHOW")
	private String listOfShow;
    /**
	 * 模块名称
	 **/
    @TableField("F_MODULE_NAME")
	private String moduleName;
    /**
	 * 自定义（additionalFieldList）还是系统字段（fieldList）
	 **/
    @TableField("F_ENTITY_FLAG")
	private String entityFlag;













}
