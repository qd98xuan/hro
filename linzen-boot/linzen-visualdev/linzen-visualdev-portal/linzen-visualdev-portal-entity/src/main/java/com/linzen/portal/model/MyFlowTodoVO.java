package com.linzen.portal.model;
import lombok.Data;

@Data
public class MyFlowTodoVO {
    private String id;
    private Integer delFlag;
    private Long startTime;
    private Long endTime;
    private String content;
}
