package com.linzen.model.form;

import com.alibaba.fastjson2.JSONArray;
import com.linzen.model.visualJson.analysis.FormAllModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisualTableModel {
    private  JSONArray jsonArray;
    private List<FormAllModel> formAllModel=new ArrayList<>();
    private String table;
    private String linkId;
    private String fullName;
    private boolean concurrency = false;
    private Integer primaryKey = 1;
    //逻辑删除
    private Boolean logicalDelete = false;
}
