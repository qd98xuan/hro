package com.linzen.hro.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.linzen.hro.entity.EmployeeSettingEntity;
import com.linzen.hro.model.employeesetting.vo.EmployeeSettingEntityVO;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class FiledConvertToSchemasUtils {

    public static void convertToSchemas(EmployeeSettingEntityVO entityVO) {

        JSONArray schemas = new JSONArray();


        List<EmployeeSettingEntity> fieldList = entityVO.getFieldList();

        for (EmployeeSettingEntity filedEntity : fieldList) {
            JSONObject schema = new JSONObject();
            schema.putOnce("field", filedEntity.getFieldCode());
            schema.putOnce("'label'", filedEntity.getFieldName());

            JSONObject componentProps = new JSONObject();
            if ("1".equals(filedEntity.getFieldType())) {
                schema.putOnce("'component'", "Select");
                componentProps.putOnce("'placeholder'", "请选择" + filedEntity.getFieldName());

            } else if ("2".equals(filedEntity.getFieldType())) {
                schema.putOnce("'component'", "Input");
                componentProps.putOnce("'placeholder'", "请输入" + filedEntity.getFieldName());

            } else if ("3".equals(filedEntity.getFieldType())) {
                schema.putOnce("'component'", "DatePicker");
                componentProps.putOnce("'placeholder'", "请选择" + filedEntity.getFieldName());

            } else if ("4".equals(filedEntity.getFieldType())) {
                schema.putOnce("'component'", "Textarea");
                componentProps.putOnce("'placeholder'", "请输入" + filedEntity.getFieldName());

            } else if ("5".equals(filedEntity.getFieldType())) {
                schema.putOnce("'component'", "InputNumber");
                componentProps.putOnce("'placeholder'", "请输入" + filedEntity.getFieldName());

            } else if ("6".equals(filedEntity.getFieldType())) {
                schema.putOnce("'component'", "AreaSelect");
                componentProps.putOnce("'placeholder'", "请输入" + filedEntity.getFieldName());
            }

            if (filedEntity.getTextLength() == 0) {
                componentProps.putOnce("'maxlength'", 50);
            } else {
                componentProps.putOnce("'maxlength'", filedEntity.getTextLength());
            }

            schema.putOnce("'componentProps'", componentProps);

            // 校验
            JSONArray rules = new JSONArray();

            if("true".equals(filedEntity.getIsNecessary()))  {
                JSONObject rule = new JSONObject();
                rule.putOnce("required", true);
                rule.putOnce("trigger", "blur");
                rule.putOnce("message", filedEntity.getFieldName() + "不能为空！");
                rules.add(rule);
                schema.putOnce("'rules'", rules);
            }

            // ,{ required: true, trigger: 'blur', message: '必填' }

            // schema.putOnce("'pattern'", "");
            // rule.putOnce("message", filedEntity.getFieldName() + "不能为空！");

            schemas.add(schema);

        }

    }
}
