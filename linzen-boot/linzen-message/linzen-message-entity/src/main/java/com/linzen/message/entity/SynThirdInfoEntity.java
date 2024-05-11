package com.linzen.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 第三方工具的公司-部门-用户同步表模型
 *
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
@TableName("base_syn_third_info")
public class SynThirdInfoEntity extends SuperExtendEntity.SuperExtendDEEntity<String> implements Serializable {

    /**
     * 第三方类型(1:企业微信;2:钉钉)
     */
    @TableField("F_THIRD_TYPE")
    private Integer thirdType;

    /**
     * 数据类型(1:组织(公司与部门);2:用户)
     */
    @TableField("F_DATA_TYPE")
    private Integer dataType;

    /**
     * 系统对象ID(公司ID、部门ID、用户ID)
     */
    @TableField("F_SYS_OBJ_ID")
    private String sysObjId;

    /**
     * 第三对象ID(公司ID、部门ID、用户ID)
     */
    @TableField("F_THIRD_OBJ_ID")
    private String thirdObjId;

}
