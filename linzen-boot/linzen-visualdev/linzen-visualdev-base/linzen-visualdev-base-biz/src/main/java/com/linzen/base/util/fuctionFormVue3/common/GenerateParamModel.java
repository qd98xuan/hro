package com.linzen.base.util.fuctionFormVue3.common;

import com.linzen.base.UserInfo;
import com.linzen.base.entity.VisualdevEntity;
import com.linzen.base.model.DownloadCodeForm;
import com.linzen.base.model.Template7.Template7Model;
import com.linzen.config.ConfigValueUtil;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.util.DataSourceUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenerateParamModel {
    private DataSourceUtil dataSourceUtil;
    private String path;
    private String fileName;
    private String templatesPath;
    private DownloadCodeForm downloadCodeForm;
    private VisualdevEntity entity;
    private UserInfo userInfo;
    private ConfigValueUtil configValueUtil;
    private DbLinkEntity linkEntity;
    /**
     * 当前表名
     */
    private String table;
    /**
     * 主表主键
     */
    private String pKeyName;
    /**
     * 当前表类名
     */
    private String className;
    /**
     * 代码生成基础信息
     */
    private Template7Model template7Model;

    /**
     * 乐观锁
     */
    private boolean concurrencyLock;
    /**
     * 是否主表
     */
    private boolean isMainTable;

}
