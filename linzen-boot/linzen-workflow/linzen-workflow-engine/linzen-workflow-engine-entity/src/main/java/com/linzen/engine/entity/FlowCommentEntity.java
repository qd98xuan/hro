package com.linzen.engine.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * 流程评论
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Data
@TableName("flow_comment")
public class FlowCommentEntity extends SuperExtendEntity.SuperExtendEnabledEntity<String> {

    /**
     * 任务主键
     */
    @TableField("F_TASK_ID")
    private String taskId;

    /**
     * 文本
     */
    @TableField("F_TEXT")
    private String text;

    /**
     * 图片
     */
    @TableField("F_IMAGE")
    private String image;

    /**
     * 附件
     */
    @TableField("F_FILE")
    @JSONField(name = "file")
    private String fileName;

}
