package com.bstek.ureport.console.ureport.model;

import lombok.Data;

@Data
public class ReportListVO {
    private String id;
    private String fullName;
    private String enCode;
    private String creatorUser;
    private Long creatorTime;
    private String categoryId;
    private String updateUser;
    private Long updateTime;
    private Integer delFlag;
    private Long sortCode;
}
