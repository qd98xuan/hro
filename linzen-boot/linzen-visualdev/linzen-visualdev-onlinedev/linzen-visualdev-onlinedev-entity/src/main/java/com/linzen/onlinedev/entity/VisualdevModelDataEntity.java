package com.linzen.onlinedev.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

import java.util.Date;

/**
 * 0代码功能数据表
 *
 * @author FHNP管理员/admin
 * @version V0.0.1
 * @copyright 领致信息
 * @date 2023-04-01
 */
@Data
public class VisualdevModelDataEntity extends SuperExtendEntity<String> {

    private String visualDevId;

    private Long sortcode;

    private Integer enabledMark;

    private Date creatorTime;

    private String creatorUserId;

    private Date updateTime;

    private String updateUserId;

    private Integer delFlag;

    private Date deleteTime;

    private String deleteUserId;

    private String data;

}

