package com.linzen.base.util.app;

import com.linzen.base.UserInfo;
import com.linzen.base.entity.VisualdevEntity;
import com.linzen.base.model.DownloadCodeForm;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.util.DataSourceUtil;
import com.linzen.model.visualJson.FormDataModel;
import lombok.Data;

/**
 *
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class AppGenModel {
    /**
     * 文件夹名字
     */
    private String fileName;
    /**
     * 实体对象
     */
    private VisualdevEntity entity;
    /**
     * 下载对象
     */
    private DownloadCodeForm downloadCodeForm;
    /**
     * 表单对象
     */
    private FormDataModel model;
    /**
     * 模板文件
     */
    private String templatePath;
    /**
     * 主键
     */
    private String pKeyName;
    /**
     * 本地数据源
     */
    private DataSourceUtil dataSourceUtil;
    /**
     * 数据连接
     */
    private DbLinkEntity linkEntity;
    /**
     * 个人信息
     */
    private UserInfo userInfo;
    /**
     * 生成文件名字
     */
    private String className;
    /**
     * 数据库表
     */
    private String table;
    /**
     * 生成路径
     */
    private String serviceDirectory;
    /**
     * 模板路径
     */
    private String templateCodePath;

    private Boolean groupTable;

    private String type;

}
