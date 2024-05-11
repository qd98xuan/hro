package com.linzen.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * 操作权限
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("sys_authorize")
public class SysAuthorizeEntity extends SuperExtendEntity<String> {

    /**
     * 项目类型
     */
    @TableField("f_item_type")
    private String itemType;

    /**
     * 项目主键
     */
    @TableField("f_item_id")
    private String itemId;

    /**
     * 对象类型
     */
    @TableField("f_object_type")
    private String objectType;

    /**
     * 对象主键
     */
    @TableField("f_object_id")
    private String objectId;

}
