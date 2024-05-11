package com.linzen.emnus;


/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public enum TimetaskTypes  {

    /**
     * 执行一次
     */
    One(1, "执行一次"),
    /**
     * 重复执行
     */
    Two(2, "重复执行"),
    /**
     * 调度明细
     */
    Three(3, "调度明细"),
    /**
     * 调度任务
     */
    Four(4, "调度任务");

    private int code;
    private String message;

    TimetaskTypes(int code, String message) {
        this.code = code;
        this.message = message;
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
