package com.linzen.message.model.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 第三方工具的对象同步表
 *
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class SynThirdInfoCrForm {

    /**
     * 第三方类型(1:企业微信;2:钉钉)
     */
    @Schema(description = "第三方类型(1:企业微信;2:钉钉)")
    private Integer thirdtype;

    /**
     * 数据类型(1:公司;2:部门;3:用户)
     */
    @Schema(description = "数据类型(1:公司;2:部门;3:用户)")
    private Integer datatype;

    /**
     * 本地对象ID(公司ID、部门ID、用户ID)
     */
    @Schema(description = "本地对象ID(公司ID、部门ID、用户ID)")
    private String sysObjId;

    /**
     * 第三方对象ID(公司ID、部门ID、用户ID)
     */
    @Schema(description = "第三方对象ID(公司ID、部门ID、用户ID)")
    private String thirdObjId;

    /**
     * 同步状态(0:未同步;1:同步成功;2:同步失败)
     */
    @Schema(description = "同步状态(0:未同步;1:同步成功;2:同步失败)")
    private Integer synstate;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;

//    /**
//     * 创建时间
//     */
//    @TableField(value = "F_CREATORTIME",fill = FieldFill.INSERT)
//    private Date creatorTime;
//
//    /**
//     * 创建用户
//     */
//    @TableField(value = "F_CREATORUSERID",fill = FieldFill.INSERT)
//    private String creatorUserId;
//
//    /**
//     * 修改用户
//     */
//    @TableField(value = "F_UPDATEUSERID",fill = FieldFill.UPDATE)
//    private String updateUserId;
//
//    /**
//     * 修改时间
//     */
//    @TableField(value = "F_UPDATETIME",fill = FieldFill.UPDATE)
//    @JSONField(name = "F_UpdateTime")
//    private Date updateTime;

}
