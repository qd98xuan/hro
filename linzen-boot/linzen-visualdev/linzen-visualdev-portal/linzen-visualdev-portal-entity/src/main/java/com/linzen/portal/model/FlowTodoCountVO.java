package com.linzen.portal.model;
import lombok.Data;

@Data
public class FlowTodoCountVO {
    private Long toBeReviewed = 0L;
    private Long entrust = 0L;
    private Long flowDone = 0L;
    private Long flowCirculate = 0L;
}
