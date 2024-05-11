package com.linzen.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 *
 * @author FHNP
 * @copyright 领致信息技术有限公司
 */
@Getter
@AllArgsConstructor
public enum LoginTicketStatus {

    /**
     * 登录成功
     */
    Success(1),
    /**
     * 未登录
     */
    UnLogin(2),
    /**
     * 登录失败
     */
    ErrLogin(3),
    /**
     * 未绑定
     */
    UnBind(4),
    /**
     * 失效
     */
    Invalid(5),
    /**
     * 多租户
     */
    Multitenancy(6),;


    private int status;
}
