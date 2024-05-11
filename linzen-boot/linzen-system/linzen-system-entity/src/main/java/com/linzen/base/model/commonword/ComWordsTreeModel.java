package com.linzen.base.model.commonword;

import com.linzen.util.treeutil.SumTree;
import lombok.Data;

import java.util.List;

/**
 * 类功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息
 * @date 2023-04-01
 */
@Data
public class ComWordsTreeModel extends SumTree {

    /**
     * 分类下模板数量
     */
    private Integer num;

    /**
     * 显示名
     */
    private String fullName;

    /**
     * 自然主键
     */
    private String id;

    /**
     * 应用id
     */
    private List<String> systemIds;

    /**
     * 应用名称
     */
    private String systemNames;

    /**
     * 常用语
     */
    private String commonWordsText;

    /**
     * 常用语类型(0:系统,1:个人)
     */
    private Integer commonWordsType;

    /**
     * 排序
     */
    private Long sortCode;

    /**
     * 有效标志
     */
    private Integer enabledMark;

}
