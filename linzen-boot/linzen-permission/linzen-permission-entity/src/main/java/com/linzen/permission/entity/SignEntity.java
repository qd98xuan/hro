package com.linzen.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperEntity;
import lombok.Data;

/**
 * 个人签名
 *
 * @author FHNP
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("base_sign_img")
public class SignEntity extends SuperEntity<String> {

    /**
     * 签名图片
     */
    @TableField("F_SIGN_IMG")
    private String signImg;

    /**
     * 是否默认
     */
    @TableField("F_IS_DEFAULT")
    private Integer isDefault;
}

