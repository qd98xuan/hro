package com.linzen.base.model;

import com.alibaba.fastjson2.annotation.JSONField;
import com.linzen.util.treeutil.SumTree;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 打印模板数树形视图对象
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PrintDevTreeModel extends SumTree {

    /**
     * 分类下模板数量
     */
    private Integer num;

    /**
     * 主键_id
     */
    private String id;

    /**
     * 名称
     */
    private String fullName;

    /**
     * 编码
     */
    private String enCode;

    /**
     * 分类
     */
    private String category;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 描述
     */
    private String description;

    /**
     * 排序码
     */
    private Integer sortCode;

    /**
     * 有效标识
     */
    private Integer enabledMark;

    /**
     * 创建时间
     */
    private Long creatorTime;

    /**
     * 创建用户_id
     */
    @JSONField(name = "creatorUserId")
    private String creatorUser;

    /**
     * 修改时间
     */
    private Long updateTime;

    /**
     * 修改用户_id
     */
    @JSONField(name = "updateUserId")
    private String updateUser;

    /**
     * 删除标志
     */
    private Integer delFlag;

    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;

    /**
     * 删除用户_id
     */
    private String deleteUserId;

    /**
     * 连接数据 _id
     */
    private String dbLinkId;

    /**
     * sql语句
     */
    private String sqlTemplate;

    /**
     * 左侧字段
     */
    private String leftFields;

    /**
     * 打印模板
     */
    private String printTemplate;


}
