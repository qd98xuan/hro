package com.linzen.engine.enums;

/**
 * 经办对象
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public enum FlowTaskOperatorEnum {

    /**
     * 发起者主管
     */
    LaunchCharge(1, "发起者主管"),
    /**
     * 部门经理
     */
    DepartmentCharge(2, "部门经理"),
    /**
     * 发起者本人
     */
    InitiatorMe(3, "发起者本人"),
    /**
     * 变量
     */
    Variate(4, "变量"),
    /**
     * 环节
     */
    Tache(5, "环节"),
    /**
     * 指定人
     */
    Nominator(6, "指定人"),
    /**
     * 候选人
     */
    Candidate(7, "候选人"),
    /**
     * 服务
     */
    Serve(9, "服务");


    private Integer code;
    private String message;

    FlowTaskOperatorEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据状态code获取枚举名称
     *
     * @return
     */
    public static String getMessageByCode(String code) {
        for (FlowTaskOperatorEnum status : FlowTaskOperatorEnum.values()) {
            if (status.getCode().equals(code)) {
                return status.message;
            }
        }
        return null;
    }

    /**
     * 根据状态code获取枚举值
     *
     * @return
     */
    public static FlowTaskOperatorEnum getByCode(String code) {
        for (FlowTaskOperatorEnum status : FlowTaskOperatorEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
