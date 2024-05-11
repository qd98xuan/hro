package com.linzen.model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 *
 */
@Data
public class EmployeeModel {
    @Excel(name = "工号", isImportField = "true")
    @Schema(description ="工号")
    private String enCode;
    @Excel(name = "姓名", isImportField = "true")
    @Schema(description ="姓名")
    private String fullName;
    @Excel(name = "性别", isImportField = "true")
    @Schema(description ="性别")
    private String gender;
    @Excel(name = "部门", isImportField = "true")
    @Schema(description ="部门")
    private String departmentName;
    @Excel(name = "职务", isImportField = "true")
    @Schema(description ="职务")
    private String positionName;
    @Excel(name = "用工性质", isImportField = "true")
    @Schema(description ="用工性质")
    private String workingNature;
    @Excel(name = "身份证号", isImportField = "true")
    @Schema(description ="身份证号")
    private String idNumber;
    @Excel(name = "联系电话", isImportField = "true")
    @Schema(description ="联系电话")
    private String telephone;
    @Excel(name = "参加工作", isImportField = "true")
    @Schema(description ="参加工作")
    private String attendWorkTime;
    @Excel(name = "出生年月", isImportField = "true")
    @Schema(description ="出生年月")
    private String birthday;
    @Excel(name = "最高学历", isImportField = "true")
    @Schema(description ="最高学历")
    private String education;
    @Excel(name = "所学专业", isImportField = "true")
    @Schema(description ="所学专业")
    private String major;
    @Excel(name = "毕业院校", isImportField = "true")
    @Schema(description ="毕业院校")
    private String graduationAcademy;
    @Excel(name = "毕业时间", isImportField = "true")
    @Schema(description ="毕业时间")
    private String graduationTime;
    @Schema(description ="字表数据")
    private List<EmployeeModel> list;
    @Schema(description ="创建时间")
    private String creatorTime;
}
