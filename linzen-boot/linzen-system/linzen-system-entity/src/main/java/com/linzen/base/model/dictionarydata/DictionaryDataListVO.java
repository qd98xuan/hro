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
public class DictionaryDataListVO {
    private String id;
    private String fullName;
    private String enCode;
    private Integer delFlag;
    private Boolean hasChildren;
    private String parentId;
    private List<DictionaryDataListVO> children;
    private Long sortCode;

}
