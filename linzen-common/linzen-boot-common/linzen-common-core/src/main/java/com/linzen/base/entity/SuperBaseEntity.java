package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode
public abstract class SuperBaseEntity implements Serializable {


    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = false)
    public static abstract class SuperIBaseEntity<T> extends SuperBaseEntity {

        /**
         * 主键
         */
        @TableId("f_id")
        public T id;

    }


    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = false)
    public static abstract class SuperTBaseEntity<T> extends SuperIBaseEntity<T> {

        /**
         * 租户id
         */
        @TableField("f_tenant_id")
        private String tenantId;

    }

    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = false)
    public static abstract class SuperCBaseEntity<T> extends SuperTBaseEntity<T> {

        /**
         * 创建时间
         */
        @TableField(value = "f_creator_time", fill = FieldFill.INSERT)
        private Date creatorTime;

        /**
         * 创建用户
         */
        @TableField(value = "f_creator_user_id", fill = FieldFill.INSERT)
        private String creatorUserId;

    }


    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = false)
    public static abstract class SuperCUBaseEntity<T> extends SuperCBaseEntity<T> {

        /**
         * 修改时间
         */
        @TableField(value = "f_update_time", fill = FieldFill.INSERT_UPDATE)
        private Date updateTime;

        /**
         * 修改用户
         */
        @TableField(value = "f_update_user_id", fill = FieldFill.INSERT_UPDATE)
        private String updateUserId;

    }


    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = false)
    public static abstract class SuperCUDBaseEntity<T> extends SuperCUBaseEntity<T> {

        /**
         * 删除标志
         */
        @TableField(value = "f_del_flag", fill = FieldFill.INSERT_UPDATE)
        @TableLogic(value = "0", delval = "1")
        private Integer delFlag;

        /**
         * 删除时间
         */
        @TableField(value = "f_delete_time", fill = FieldFill.UPDATE)
        private Date deleteTime;

        /**
         * 删除用户
         */
        @TableField(value = "f_delete_user_id", fill = FieldFill.UPDATE)
        private String deleteUserId;
    }

    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = false)
    public static abstract class SuperCDBaseEntity<T> extends SuperCBaseEntity<T> {

        /**
         * 删除标志
         */
        @TableField(value = "f_del_flag", fill = FieldFill.INSERT_UPDATE)
        @TableLogic(value = "0", delval = "1")
        private Integer delFlag;

        /**
         * 删除时间
         */
        @TableField(value = "f_delete_time", fill = FieldFill.UPDATE)
        private Date deleteTime;

        /**
         * 删除用户
         */
        @TableField(value = "f_delete_user_id", fill = FieldFill.UPDATE)
        private String deleteUserId;
    }


}

