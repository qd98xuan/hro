package com.linzen.base.model.Template6;


import com.linzen.model.visualJson.TableModel;
import lombok.Data;

import java.util.List;

/**
 * 多表开发配置
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class Template6Model {
    /**
     * 版本
     */
    private String version = "V3.0.0";
    /**
     * 版权
     */
    private String copyright;
    /**
     * 创建人员
     */
    private String createUser;
    /**
     * 创建日期
     */
    private String createDate;
    /**
     * 功能描述
     */
    private String description;
    /**
     * 子类功能名称
     */
    private String subClassName;
    /**
     * 主类功能名称
     */
    private String className;

    /**
     * tables
     */
    /**
     *  列表主表 - 字段集合
     */
    private List<ColumnListField> columnListFields;


    private String serviceDirectory;


    /**
     *  数据关联 - 集合
     */
    private List<TableModel> dbTableRelation;
}
