package com.linzen.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperBaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * Contract
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
@TableName("test_contract")
public class ContractEntity extends SuperBaseEntity.SuperTBaseEntity<String> implements Serializable {

    @TableField("F_CONTRACTNAME")
    private String contractName;

    @TableField("F_MYTELEPHONE")
    private String mytelePhone;

    @TableField("F_FILEJSON")
    private String fileJson;

}
