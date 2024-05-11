package com.linzen.database.model.superQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 高级查询
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ConditionJsonModel {
	private String field;
	private String fieldValue;
	private String symbol;
	private String tableName;
	private String projectKey;
	private String defaultValue;
	private String attr;
	/**
	 * 表单字段是否多选
	 */
	private boolean formMultiple;
}
