package com.linzen.engine.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * 流程引擎
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("flow_template_json")
public class FlowTemplateJsonEntity extends SuperExtendEntity.SuperExtendEnabledEntity<String> {

    /**
     * 流程模板id
     */
    @TableField("F_TEMPLATE_ID")
    private String templateId;

    /**
     * 流程名称
     */
    @TableField("F_FULL_NAME")
    private String fullName;

    /**
     * 可见类型 0-全部可见、1-指定经办
     */
    @TableField("F_VISIBLE_TYPE")
    private Integer visibleType;

    /**
     * 流程模板
     */
    @TableField("F_FLOW_TEMPLATE_JSON")
    private String flowTemplateJson;

    /**
     * 流程版本
     */
    @TableField("F_VERSION")
    private String version;

    /**
     * 分组id
     */
    @TableField("F_GROUP_ID")
    private String groupId;

    /**
     * 发送配置
     */
    @TableField("F_SEND_CONFIG_IDS")
    private String sendConfigIds;

}
