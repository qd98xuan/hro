package com.linzen.onlinedev.model.OnlineImport;
import com.linzen.onlinedev.model.VisualdevModelDataInfoVO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * 在线开发导入数据结果集
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ExcelImportModel {
	/**
	 * 导入成功条数
	 */
	private int snum;
	/**
	 * 导入失败条数
	 */
	private int fnum;
	/**
	 * 导入结果状态(0,成功  1，失败)
	 */
	private int resultType;

	/**
	 * 失败结果
	 */
	private List<Map<String, Object>> failResult;

	/**
	 * 集成调用哦个
	 */
	private List<VisualdevModelDataInfoVO> dataInfoList = new ArrayList<>();
}
