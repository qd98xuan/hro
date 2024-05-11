package com.linzen.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * 类功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PermissionBaseEntity extends SuperExtendEntity.SuperExtendDEEntity<String> {

    /**
     * 名称
     */
    @TableField("F_FULL_NAME")
    private String fullName;

    /**
     * 编码
     */
    @TableField("F_EN_CODE")
    private String enCode;

    /**
     * 扩展属性
     */
    @TableField("F_PROPERTY_JSON")
    private String propertyJson;

}

