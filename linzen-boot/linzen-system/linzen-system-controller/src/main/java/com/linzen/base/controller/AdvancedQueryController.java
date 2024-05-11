package com.linzen.base.controller;

import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.base.entity.AdvancedQueryEntity;
import com.linzen.base.model.advancedquery.AdvancedQueryListVO;
import com.linzen.base.model.advancedquery.AdvancedQuerySchemeForm;
import com.linzen.base.service.AdvancedQueryService;
import com.linzen.base.vo.ListVO;
import com.linzen.constant.MsgCode;
import com.linzen.exception.DataBaseException;
import com.linzen.util.JsonUtil;
import com.linzen.util.JsonUtilEx;
import com.linzen.util.UserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 高级查询方案管理
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "高级查询方案管理", description = "AdvancedQuery")
@RestController
@RequestMapping("/api/system/AdvancedQuery")
public class AdvancedQueryController extends SuperController<AdvancedQueryService, AdvancedQueryEntity> {

	@Autowired
	private AdvancedQueryService queryService;
	@Autowired
	private UserProvider userProvider;

	/**
	 * 新建
	 *
	 * @param advancedQuerySchemeForm 实体对象
	 * @return
	 */
	@Operation(summary = "新建方案")
	@Parameters({
			@Parameter(name = "advancedQuerySchemeForm", description = "实体对象", required = true)
	})
	@PostMapping
	public ServiceResult create(@RequestBody @Valid AdvancedQuerySchemeForm advancedQuerySchemeForm) {
		AdvancedQueryEntity entity = BeanUtil.toBean(advancedQuerySchemeForm, AdvancedQueryEntity.class);
		queryService.create(entity);
		return ServiceResult.success(MsgCode.SU001.get());
	}

	/**
	 * 修改方案
	 *
	 * @param id 主键
	 * @param advancedQuerySchemeForm 实体对象
	 * @return
	 */
	@Operation(summary = "修改方案")
	@Parameters({
			@Parameter(name = "id", description = "主键", required = true),
			@Parameter(name = "advancedQuerySchemeForm", description = "实体对象", required = true)
	})
	@PutMapping("/{id}")
	public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid AdvancedQuerySchemeForm advancedQuerySchemeForm) {
		AdvancedQueryEntity entity = BeanUtil.toBean(advancedQuerySchemeForm, AdvancedQueryEntity.class);
		entity.setId(id);
		queryService.updateById(entity);
		return ServiceResult.success(MsgCode.SU004.get());
	}

	/**
	 * 删除
	 *
	 * @param id 主键值
	 * @return ignore
	 */
	@Operation(summary = "删除方案")
	@Parameters({
			@Parameter(name = "id", description = "主键", required = true)
	})
	@DeleteMapping("/{id}")
	public ServiceResult delete(@PathVariable("id") String id) {
		UserInfo userInfo = userProvider.get();
		AdvancedQueryEntity entity = queryService.getInfo(id,userInfo.getUserId());
		if (entity != null) {
			queryService.removeById(entity);
			return ServiceResult.success(MsgCode.SU003.get());
		}
		return ServiceResult.error(MsgCode.FA003.get());
	}

	/**
	 * 列表
	 *
	 * @param moduleId 功能主键
	 * @return ignore
	 */
	@Operation(summary = "方案列表")
	@Parameters({
			@Parameter(name = "moduleId", description = "功能主键", required = true)
	})
	@GetMapping("/{moduleId}/List")
	public ServiceResult<ListVO<AdvancedQueryListVO>> list(@PathVariable("moduleId") String moduleId) {
		UserInfo userInfo = userProvider.get();
		List<AdvancedQueryEntity> data = queryService.getList(moduleId,userInfo);
		List<AdvancedQueryListVO> list = JsonUtil.createJsonToList(data, AdvancedQueryListVO.class);
		ListVO<AdvancedQueryListVO> vo = new ListVO<>();
		vo.setList(list);
		return ServiceResult.success(vo);
	}
	/**
	 * 信息
	 *
	 * @param id 主键值
	 * @return ignore
	 * @throws DataBaseException ignore
	 */
	@Operation(summary = "获取方案信息")
	@Parameters({
			@Parameter(name = "id", description = "主键值", required = true)
	})
	@GetMapping("/{id}")
	public ServiceResult<AdvancedQuerySchemeForm> info(@PathVariable("id") String id) throws DataBaseException {
		UserInfo userInfo = userProvider.get();
		AdvancedQueryEntity entity = queryService.getInfo(id,userInfo.getUserId());
		AdvancedQuerySchemeForm vo = JsonUtilEx.getJsonToBeanEx(entity, AdvancedQuerySchemeForm.class);
		return ServiceResult.success(vo);
	}

}
