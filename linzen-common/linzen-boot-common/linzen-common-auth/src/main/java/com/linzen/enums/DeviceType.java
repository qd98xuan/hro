package com.linzen.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 设备类型枚举
 *
 * @author FHNP
 * @copyright 领致信息技术有限公司
 */
@Getter
@AllArgsConstructor
public enum DeviceType {

    /**
     * PC端
     */
    PC("PC"),

    /**
     * APP端 手机都归为移动 自行扩展
     */
    APP("APP"),

    /**
     * 程序运行中使用的无限制临时用户
     */
    TEMPUSER("TEMPUSER"),

    /**
     * 程序运行中使用的限制临时用户， 不可访问主系统, CurrentUser接口报错
     */
    TEMPORALITIES("TEMPORALITIES");

    private final String device;

}
