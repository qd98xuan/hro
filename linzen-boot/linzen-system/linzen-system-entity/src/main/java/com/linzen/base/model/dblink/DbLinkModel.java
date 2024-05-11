package com.linzen.base.model.dblink;

import com.alibaba.fastjson2.annotation.JSONField;
import com.linzen.util.treeutil.SumTree;
import lombok.Data;

@Data
public class DbLinkModel extends SumTree {

    private String id;

    private String image;

    private String fullName;

    private String dbType;

    private String host;

    private String port;

    private Long creatorTime;

    private String creatorUserId;

    private Long updateTime;

    private String updateUserId;

    private Integer delFlag;

    private Long sortCode;

    private Long num;
}
