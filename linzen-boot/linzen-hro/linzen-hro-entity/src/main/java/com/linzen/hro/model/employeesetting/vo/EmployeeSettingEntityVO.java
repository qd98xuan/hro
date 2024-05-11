package com.linzen.hro.model.employeesetting.vo;

import com.linzen.hro.entity.EmployeeSettingEntity;
import lombok.Data;

import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class EmployeeSettingEntityVO {

    private String id;
    /**
     * 模块编码
     **/
    private String moduleCode;
    /**
     * 模块名称
     **/
    private String moduleName;
    /**
     * 模块类型
     **/
    private String moduleType;
    /**
     * 是否是必需的
     **/
    private String necessary;
    /**
     * 是否是必需的
     **/
    private String supportAdditional;
    /**
     * 是否必须添加
     **/
    private String supportNecessaryCheck;


    private List<EmployeeSettingEntity> additionalFieldList;


    private List<EmployeeSettingEntity> fieldList;

}
