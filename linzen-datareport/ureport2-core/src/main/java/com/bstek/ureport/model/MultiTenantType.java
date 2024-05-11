package com.bstek.ureport.model;

import lombok.Getter;

/**
 * @author FHNP
 * @user N
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Getter
public enum MultiTenantType {

    /**
     * 表中字段过滤租户数据
     */
    COLUMN("字段模式"),

    /**
     * 动态替换SQL
     * {租户ID}.表名
     */
    SCHEMA("SCHEMA模式");


    private String directions;

    MultiTenantType(String directions) {
        this.directions = directions;
    }


    public boolean eq(String val) {
        return this.name().equalsIgnoreCase(val);
    }

    public boolean eq(MultiTenantType val) {
        if (val == null) {
            return false;
        }
        return eq(val.name());
    }
}
