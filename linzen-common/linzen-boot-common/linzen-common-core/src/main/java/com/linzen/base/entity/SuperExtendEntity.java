package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode
public abstract class SuperExtendEntity<T> extends SuperEntity<T>{

    /**
     * 排序码
     */
    @TableField("f_sort_code")
    private Long sortCode;


    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode
    public static abstract class SuperExtendDescriptionEntity<T> extends SuperExtendEntity<T> {


        /**
         * 描述
         */
        @TableField("f_description")
        private String description;

    }


    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode
    public static abstract class SuperExtendEnabledEntity<T> extends SuperExtendEntity<T> {

        /**
         * 有效标志 (0-默认，禁用，1-启用)
         */
        @TableField(value ="f_enabled_mark",fill = FieldFill.INSERT)
        private Integer enabledMark;


    }


    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode
    public static abstract class SuperExtendDEEntity<T> extends SuperExtendEntity<T> {


        /**
         * 描述
         */
        @TableField("f_description")
        private String description;

        /**
         * 有效标志 (0-默认，禁用，1-启用)
         */
        @TableField(value ="f_enabled_mark",fill = FieldFill.INSERT)
        private Integer enabledMark;


    }



}
