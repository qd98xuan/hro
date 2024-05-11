package com.linzen.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 审批常用语 Entity
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息
 * @date 2023-04-01
 */
@TableName("base_common_words")
@Schema(description = "CommonWords对象", name = "审批常用语")
@Data
public class CommonWordsEntity extends SuperExtendEntity.SuperExtendEnabledEntity<String> {

    /**
     * 应用id
     */
    @TableField("f_system_ids")
    private String systemIds;

    /**
     * 常用语
     */
    @TableField("f_common_words_text")
    private String commonWordsText;

    /**
     * 常用语类型(0:系统,1:个人)
     */
    @TableField("f_common_words_type")
    private Integer commonWordsType;


}
