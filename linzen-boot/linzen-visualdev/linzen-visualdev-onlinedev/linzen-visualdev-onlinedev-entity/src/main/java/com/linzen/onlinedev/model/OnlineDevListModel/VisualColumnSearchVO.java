package com.linzen.onlinedev.model.OnlineDevListModel;

import com.linzen.model.visualJson.TemplateJsonModel;
import com.linzen.model.visualJson.config.ConfigModel;
import com.linzen.model.visualJson.props.PropsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class VisualColumnSearchVO {
    /**
     * 查询字段全key：如tableField113-datePickerField117
     */
    private String id;
    /**
     * 查询字段全名：如设计子表-子表年月日
     */
    private String fullName;
    /**
     * 查询条件类型 1.等于 2.模糊 3.范围
     */
    private String searchType;
    private String vModel;
    /**
     * 查询值
     */
    private Object value;
    /**
     * 是否多选
     */
    private Boolean multiple;

    private Boolean searchMultiple;

    private ConfigModel config;
    /**
     * 省市区
     */
    private Integer level;
    /**
     * 时间类型格式
     */
    private String format;
    private String type;

    /**
     * 数据库字段
     */
    private String field;
    private String table;

    private PropsModel props;
    private SlotModel slot;
    private String options;

    private List<TemplateJsonModel> templateJson = new ArrayList();
    private String interfaceId;

    private String selectType;
    private String ableDepIds;
    private String ableIds;
    private String ablePosIds;
    private String ableUserIds;
    private String ableRoleIds;
    private String ableGroupIds;

    /**
     * 列表字段是否关键词
     */
    private Boolean isKeyword = false;

    /**
     * 是否选中数据及子信息(只针对视图)
     */
    private Boolean isIncludeSubordinate = false;
}
