package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.base.CaseFormat;
import com.linzen.base.ServiceResult;
import com.linzen.base.Pagination;
import com.linzen.base.entity.ModuleDataAuthorizeLinkEntity;
import com.linzen.base.entity.ModuleEntity;
import com.linzen.base.model.dbtable.vo.DbFieldVO;
import com.linzen.base.model.module.PropertyJsonModel;
import com.linzen.base.model.moduledataauthorize.DataAuthorizeLinkForm;
import com.linzen.base.model.moduledataauthorize.DataAuthorizeTableNameVO;
import com.linzen.base.service.DbTableService;
import com.linzen.base.service.ModuleDataAuthorizeLinkDataService;
import com.linzen.base.service.ModuleService;
import com.linzen.base.vo.PaginationVO;
import com.linzen.database.model.dbfield.DbFieldModel;
import com.linzen.model.visualJson.TableModel;
import com.linzen.util.*;
import com.linzen.util.context.SpringContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据权限字段管理 数据连接
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "数据权限字段管理数据连接" , description = "ModuleDataAuthorizeLink")
@RestController
@RequestMapping("/api/system/ModuleDataAuthorizeLink")
public class ModuleDataAuthorizeLinkController {

	@Autowired
	private ModuleDataAuthorizeLinkDataService linkDataService;
	@Autowired
	private ModuleService moduleService;
	@Autowired
	private DbTableService dbTableService;

	/**
	 * 页面参数
	 *
	 * @param linkForm 页面参数
	 * @return
	 */
	@Operation(summary = "保存编辑数据连接")
	@Parameters({
			@Parameter(name = "linkForm", description = "页面参数", required = true)
	})
	@SaCheckPermission("system.menu")
	@PostMapping("/saveLinkData")
	public ServiceResult saveLinkData(@RequestBody @Valid DataAuthorizeLinkForm linkForm) {
		ModuleDataAuthorizeLinkEntity linkDataEntity = BeanUtil.toBean(linkForm, ModuleDataAuthorizeLinkEntity.class);
		if (StringUtil.isEmpty(linkDataEntity.getId())) {
			linkDataEntity.setId(RandomUtil.uuId());
			linkDataService.save(linkDataEntity);
			return ServiceResult.success("保存成功");
		} else {
			linkDataService.updateById(linkDataEntity);
			return ServiceResult.success("更新成功");
		}
	}

	/**
	 * 获取表名
	 *
	 * @param menuId 菜单id
	 * @param type 分类
	 * @return
	 */
	@Operation(summary = "获取表名")
	@Parameters({
			@Parameter(name = "menuId", description = "菜单id", required = true),
			@Parameter(name = "type", description = "分类", required = true)
	})
	@SaCheckPermission("system.menu")
	@GetMapping("/getVisualTables/{menuId}/{type}")
	public ServiceResult<DataAuthorizeTableNameVO> getVisualTables(@PathVariable("menuId") String menuId, @PathVariable("type") Integer type) {
		ModuleEntity info = moduleService.getInfo(menuId);
		DataAuthorizeTableNameVO vo = null;
		if (ObjectUtil.isNotNull(info)) {
			PropertyJsonModel model = JsonUtil.createJsonToBean(info.getPropertyJson(), PropertyJsonModel.class);
			if (model == null) {
				model = new PropertyJsonModel();
			}
			//功能
			if (info.getType() == 3) {
				// 得到bean
				Object bean = SpringContext.getBean("visualdevServiceImpl");
				Object method = ReflectionUtil.invokeMethod(bean, "getInfo" , new Class[]{String.class}, new Object[]{model.getModuleId()});
				Map<String, Object> map = JsonUtil.entityToMap(method);
				if (map != null) {
					List<TableModel> tables = JsonUtil.createJsonToList(String.valueOf(map.get("tables")), TableModel.class);
					List<String> collect = tables.stream().map(t -> t.getTable()).collect(Collectors.toList());
					vo = DataAuthorizeTableNameVO.builder().linkTables(collect).linkId(String.valueOf(map.get("dbLinkId"))).build();
				}
			} else {
				ModuleDataAuthorizeLinkEntity linkDataEntity = linkDataService.getLinkDataEntityByMenuId(menuId,type);
				String linkTables = linkDataEntity.getLinkTables();
				List<String> tables = StringUtil.isNotEmpty(linkTables) ? Arrays.asList(linkTables.split(",")) : new ArrayList<>();
				vo = DataAuthorizeTableNameVO.builder().linkTables(tables).linkId(linkDataEntity.getLinkId()).build();
			}
		}
		return ServiceResult.success(vo);
	}

	/**
	 * 数据连接信息
	 *
	 * @param menudId 菜单id
	 * @param type 分类
	 * @return
	 */
	@Operation(summary = "数据连接信息")
	@Parameters({
			@Parameter(name = "menudId", description = "菜单id", required = true),
			@Parameter(name = "type", description = "分类", required = true)
	})
	@SaCheckPermission("system.menu")
	@GetMapping("/getInfo/{menudId}/{type}")
	public ServiceResult getInfo(@PathVariable("menudId") String menudId,@PathVariable("type") Integer type) {
		ModuleDataAuthorizeLinkEntity linkDataEntity = linkDataService.getLinkDataEntityByMenuId(menudId,type);
		DataAuthorizeLinkForm linkForm = BeanUtil.toBean(linkDataEntity, DataAuthorizeLinkForm.class);
		return ServiceResult.success(linkForm);
	}

	/**
	 * 表名获取数据表字段
	 *
	 * @param linkId 连接id
	 * @param tableName 表名
	 * @param menuType 菜单类型
	 * @param dataType 数据类型
	 * @param pagination 分页模型
	 * @return
	 * @throws Exception
	 */
	@Operation(summary = "表名获取数据表字段")
	@Parameters({
			@Parameter(name = "linkId", description = "连接id", required = true),
			@Parameter(name = "tableName", description = "表名", required = true),
			@Parameter(name = "menuType", description = "菜单类型", required = true),
			@Parameter(name = "dataType", description = "数据类型", required = true)
	})
	@SaCheckPermission("system.menu")
	@GetMapping("/{linkId}/Tables/{tableName}/Fields/{menuType}/{dataType}")
	public ServiceResult getTableInfoByTableName(@PathVariable("linkId") String linkId, @PathVariable("tableName") String tableName, @PathVariable("menuType") Integer menuType,@PathVariable("dataType") Integer dataType, Pagination pagination) throws Exception {
		List<DbFieldModel> data = dbTableService.getFieldList(linkId, tableName);
		List<DbFieldVO> vos = JsonUtil.createJsonToList(data, DbFieldVO.class);
		if (StringUtil.isNotEmpty(pagination.getKeyword())) {
			vos = vos.stream().filter(vo -> {
				boolean ensure;
				String fieldName = vo.getFieldName();
				fieldName = Optional.ofNullable(fieldName).orElse("");
				ensure = fieldName.toLowerCase().contains(pagination.getKeyword().toLowerCase()) || vo.getField().toLowerCase().contains(pagination.getKeyword().toLowerCase());
				return ensure;
			}).collect(Collectors.toList());
		}
		if (menuType==2 && dataType!=3){
			for (DbFieldVO vo : vos){
				String name = vo.getField().toLowerCase().replaceAll("f_", "");
				vo.setField(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name));
			}
		}
		List listPage = PageUtil.getListPage((int) pagination.getCurrentPage(), (int) pagination.getPageSize(), vos);
		PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
		paginationVO.setTotal((long) vos.size());
		return ServiceResult.pageList(listPage,paginationVO);
	}
}

