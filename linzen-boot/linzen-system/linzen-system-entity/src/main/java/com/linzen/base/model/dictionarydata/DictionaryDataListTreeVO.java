package com.linzen.base.model.dictionarydata;

import lombok.Data;

import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class DictionaryDataListTreeVO {
    private String id;
    private String parentId;
    private Boolean hasChildren;
    private List<DictionaryDataListTreeVO> children;
    private String fullName;
    private String enCode;
    private Integer delFlag;
    private Long sortCode;
}
