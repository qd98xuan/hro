package com.linzen.database.model.superQuery;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 高级查询（代码生成器）
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@AllArgsConstructor
public class SuperQueryConditionModel<T> {
	private QueryWrapper<T> obj;
	private List<ConditionJsonModel> conditionList;
	private String matchLogic;
	private String tableName;
}
