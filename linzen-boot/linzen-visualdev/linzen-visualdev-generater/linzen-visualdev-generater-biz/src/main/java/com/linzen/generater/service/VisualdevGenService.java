package com.linzen.generater.service;


import com.linzen.base.entity.VisualdevEntity;
import com.linzen.base.model.DownloadCodeForm;
import com.linzen.base.service.SuperService;

/**
 *
 * 可视化开发功能表
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface VisualdevGenService extends SuperService<VisualdevEntity> {

    String codeGengerate(String id, DownloadCodeForm downloadCodeForm) throws Exception;

    /**
     * 代码生成v3
     * @param visualdevEntity 可视化开发功能
     * @param downloadCodeForm 下载相关信息
     * @return 下载文件名
     * @throws Exception ignore
     */
    String codeGengerateV3(VisualdevEntity visualdevEntity, DownloadCodeForm downloadCodeForm) throws Exception;
}

