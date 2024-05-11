package com.linzen.enums;


/**
 * 日志等级
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public enum LogLevelEnum {

    /**
     * 错误
     */
    Error(0,"错误"),
    /**
     * 成功
     */
    Success(1,"成功"),
    /**
     * 警告
     */
    Warning(2,"警告");

    private int code;
    private String message;

    LogLevelEnum(int code, String message){
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

    /**
     * 根据状态code获取枚举名称
     * @return
     */
    public static String getMessageByCode(Integer code) {
        for (LogLevelEnum status : LogLevelEnum.values()) {
            if (status.getCode().equals(code)) {
                return status.message;
            }
        }
        return null;
    }

    /**
     * 根据状态code获取枚举值
     * @return
     */
    public static LogLevelEnum getByCode(Integer code) {
        for (LogLevelEnum status : LogLevelEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
