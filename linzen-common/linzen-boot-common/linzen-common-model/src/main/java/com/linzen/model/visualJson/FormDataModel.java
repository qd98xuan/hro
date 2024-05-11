package com.linzen.model.visualJson;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class FormDataModel {
    /**
     * 模块
     */
    private String areasName;
    /**
     * 功能名称
     */
    private String className;
    /**
     * 后端目录
     */
    private String serviceDirectory;
    /**
     * 所属模块
     */
    private String module;
    /**
     * 子表名称集合
     */
    private String subClassName;


    private String formRef;
    private String formModel;
    private String size;
    private String labelPosition;
    private Integer labelWidth;
    private String formRules;
    private String drawerWidth;
    private Integer gutter;
    private Boolean disabled;
    private String span;
    private Boolean formBtns;
    private Integer idGlobal;
    private String fields;
    private String popupType;
    private String fullScreenWidth;
    private String formStyle;
    private String generalWidth;
    private Boolean hasCancelBtn;
    private String cancelButtonText;
    private Boolean hasConfirmBtn;
    private String confirmButtonText;
    private Boolean hasPrintBtn;
    private String printButtonText;
    private Boolean hasConfirmAndAddBtn;
    private String confirmAndAddText;
    private String labelSuffix;

    private String[] printId;

    private FieLdsModel children;

    //主键策略 默认 雪花
    private Integer primaryKeyPolicy = 1;
    //并发锁
    private Boolean concurrencyLock = false;
    // 过滤规则
    private String ruleList;
    // 过滤规则app
    private String ruleListApp;
    //逻辑删除
    private Boolean logicalDelete = false;

    public void setPrimaryKeyPolicy(Integer primaryKeyPolicy) {
        if (primaryKeyPolicy == null) {
            this.primaryKeyPolicy = 1;
        } else {
            this.primaryKeyPolicy = primaryKeyPolicy;
        }
    }
}
