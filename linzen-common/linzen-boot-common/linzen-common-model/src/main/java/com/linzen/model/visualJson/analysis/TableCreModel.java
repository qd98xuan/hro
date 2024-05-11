package com.linzen.model.visualJson.analysis;

import com.alibaba.fastjson2.JSONArray;
import lombok.Data;

import java.util.List;

/**
 * 无表生成有表模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class TableCreModel {
	private JSONArray jsonArray;
	private List<FormAllModel> formAllModel;
	private String table;
	private String linkId;
}
