package com.linzen.emnus;


/**
 * 查询功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public enum SearchMethodEnum {
    /**
     * 等于
     */
    Equal("==", "等于"),
    /**
     * 介于
     */
    Between("between", "介于"),
    /**
     * 不等于
     */
    NotEqual("<>", "不等于"),
    /**
     * 大于
     */
    GreaterThan(">", "大于"),
    /**
     * 大于等于
     */
    GreaterThanOrEqual(">=", "大于等于"),
    /**
     * 小于
     */
    LessThan("<", "小于"),
    /**
     * 小于等于
     */
    LessThanOrEqual("<=", "小于等于"),
    /**
     * 包含任意一个
     */
    Included("in", "包含任意一个"),
    /**
     * 不包含任意一个
     */
    NotIncluded("notIn", "不包含任意一个"),
    /**
     * 为空
     */
    IsNull("null", "为空"),
    /**
     * 不为空
     */
    IsNotNull("notNull", "不为空"),
    /**
     * 包含
     */
    Like("like", "包含"),
    /**
     * 不包含
     */
    NotLike("notLike", "不包含"),
    /**
     * 并且
     */
    And("and", "并且"),
    /**
     * 或者
     */
    Or("or", "或者");


    SearchMethodEnum(String symbol, String message) {
        this.symbol = symbol;
        this.message = message;
    }

    private String symbol;
    private String message;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static SearchMethodEnum getSearchMethod(String symbol) {
        for (SearchMethodEnum status : SearchMethodEnum.values()) {
            if (status.getSymbol().equals(symbol)) {
                return status;
            }
        }
        return Equal;
    }

}
