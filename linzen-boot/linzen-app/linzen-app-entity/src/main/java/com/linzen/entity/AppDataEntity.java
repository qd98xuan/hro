package com.linzen.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * app常用数据
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("base_app_data")
public class AppDataEntity extends SuperExtendEntity.SuperExtendDEEntity<String> {

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

    /**
     * 数据
     */
    @TableField("f_object_data")
    private String objectData;

    /**
     * 关联系统id
     */
    @TableField("F_SYSTEM_ID")
    private String systemId;

}
