package com.linzen.database.model.superQuery;

import com.linzen.emnus.SearchMethodEnum;
import com.linzen.model.visualJson.FieLdsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 高级查询
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class SuperQueryJsonModel {
	private String logic = SearchMethodEnum.And.getSymbol();
	private List<FieLdsModel> groups = new ArrayList<>();
}
