package com.linzen.base.model.dictionarytype;

import com.linzen.base.entity.DictionaryTypeEntity;
import com.linzen.base.model.dictionarydata.DictionaryDataExportModel;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据字典导入导出模板
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DictionaryExportModel implements Serializable {

    /**
     * 字典分类
     */
    private List<DictionaryTypeEntity> list = new ArrayList<>();

    /**
     * 数据集合
     */
    private List<DictionaryDataExportModel> modelList = new ArrayList<>();

}
