package com.linzen.permission.model.user.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class UserExportExceptionVO implements Serializable {
    @Excel(name = "账号", isImportField = "true")
    private String account;
    @Excel(name = "姓名", isImportField = "true")
    private String realName;
    /**
     * 性别
     */
    @Excel(name = "性别", isImportField = "true")
    private String gender;
    @Excel(name = "电子邮箱", isImportField = "true")
    private String email;
    /**
     * 组织
     */
    @Excel(name = "所属组织", isImportField = "true")
    private String organizeId;
    /**
     * 主管
     */
    @Excel(name = "直属主管", isImportField = "true")
    private String managerId;
    /**
     * 岗位
     */
    @Excel(name = "岗位", isImportField = "true")
    private String positionId;
    /**
     * 角色
     */
    @Excel(name = "角色", isImportField = "true")
    private String roleId;
    @Excel(name = "排序", isImportField = "true")
    private Long sortCode;
    @Excel(name = "状态", isImportField = "true")
    private String delFlag;
    @Excel(name = "说明", isImportField = "true")
    private String description;
    @Excel(name = "民族", isImportField = "true")
    private String nation;
    @Excel(name = "籍贯", isImportField = "true")
    private String nativePlace;
    /**
     * 入职时间
     */
    @Excel(name = "入职时间", isImportField = "true")
    private String entryDate;
    @Excel(name = "证件类型", isImportField = "true")
    private String certificatesType;
    @Excel(name = "证件号码", isImportField = "true")
    private String certificatesNumber;
    @Excel(name = "文化程度", isImportField = "true")
    private String education;
    @Excel(name = "出生年月", isImportField = "true")
    private String birthday;
    @Excel(name = "办公电话", isImportField = "true")
    private String telePhone;
    @Excel(name = "办公座机", isImportField = "true")
    private String landline;
    @Excel(name = "手机号码", isImportField = "true")
    private String mobilePhone;
    @Excel(name = "紧急联系", isImportField = "true")
    private String urgentContacts;
    @Excel(name = "紧急电话", isImportField = "true")
    private String urgentTelePhone;
    @Excel(name = "通讯地址", isImportField = "true")
    private String postalAddress;
    @Excel(name = "职级", isImportField = "true")
    private String ranks;
    @Excel(name = "异常原因")
    private String errorsInfo;

    private List<UserExportExceptionVO> list;
}
