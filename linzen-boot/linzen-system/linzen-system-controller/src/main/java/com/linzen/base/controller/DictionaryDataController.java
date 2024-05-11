package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.entity.DictionaryTypeEntity;
import com.linzen.base.model.dictionarydata.*;
import com.linzen.base.model.dictionarytype.DictionaryExportModel;
import com.linzen.base.model.dictionarytype.DictionaryTypeSelectModel;
import com.linzen.base.model.dictionarytype.DictionaryTypeSelectVO;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.service.DictionaryTypeService;
import com.linzen.base.vo.DownloadVO;
import com.linzen.base.vo.ListVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.MsgCode;
import com.linzen.emnus.ModuleTypeEnum;
import com.linzen.exception.DataBaseException;
import com.linzen.util.FileUtil;
import com.linzen.util.JsonUtil;
import com.linzen.util.JsonUtilEx;
import com.linzen.util.StringUtil;
import com.linzen.util.treeutil.ListToTreeUtil;
import com.linzen.util.treeutil.SumTree;
import com.linzen.util.treeutil.newtreeutil.TreeDotUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典数据
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "数据字典", description = "DictionaryData")
@RestController
@RequestMapping("/api/system/DictionaryData")
public class DictionaryDataController extends SuperController<DictionaryDataService, DictionaryDataEntity> {

    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private DictionaryTypeService dictionaryTypeService;
    @Autowired
    private ConfigValueUtil configValueUtil;

    /**
     * 获取数据字典列表
     *
     * @param dictionaryTypeId   数据字典id
     * @param pageDictionaryData 分页参数
     * @return ignore
     */
    @Operation(summary = "获取数据字典列表")
    @Parameters({
            @Parameter(name = "dictionaryTypeId", description = "数据分类id", required = true)
    })
    @GetMapping("/{dictionaryTypeId}")
    public ServiceResult bindDictionary(@PathVariable("dictionaryTypeId") String dictionaryTypeId, PageDictionaryData pageDictionaryData) {
        List<DictionaryDataEntity> data = dictionaryDataService.getList(dictionaryTypeId);
        List<DictionaryDataEntity> dataAll = data;
        if (StringUtil.isNotEmpty(pageDictionaryData.getKeyword())) {
            data = data.stream().filter(t -> t.getFullName().contains(pageDictionaryData.getKeyword()) || t.getEnCode().contains(pageDictionaryData.getKeyword())).collect(Collectors.toList());
        }
        if (pageDictionaryData.getIsTree() != null && "1".equals(pageDictionaryData.getIsTree())) {
            List<DictionaryDataEntity> treeData = JsonUtil.createJsonToList(ListToTreeUtil.treeWhere(data, dataAll), DictionaryDataEntity.class);
            List<DictionaryDataModel> voListVO = JsonUtil.createJsonToList(treeData, DictionaryDataModel.class);
            List<SumTree<DictionaryDataModel>> sumTrees = TreeDotUtils.convertListToTreeDot(voListVO);
            List<DictionaryDataListVO> list = JsonUtil.createJsonToList(sumTrees, DictionaryDataListVO.class);
            ListVO<DictionaryDataListVO> treeVo = new ListVO<>();
            treeVo.setList(list);
            return ServiceResult.success(treeVo);
        }
        List<DictionaryDataModel> voListVO = JsonUtil.createJsonToList(data, DictionaryDataModel.class);
        ListVO<DictionaryDataModel> treeVo = new ListVO<>();
        treeVo.setList(voListVO);
        return ServiceResult.success(treeVo);
    }


    /**
     * 获取数据字典列表
     *
     * @return ignore
     */
    @Operation(summary = "获取数据字典列表(分类+内容)")
    @GetMapping("/All")
    public ServiceResult<ListVO<Map<String, Object>>> allBindDictionary() {
        List<DictionaryTypeEntity> dictionaryTypeList = dictionaryTypeService.getList();
        List<Map<String, Object>> list = new ArrayList<>();
        for (DictionaryTypeEntity dictionaryTypeEntity : dictionaryTypeList) {
            List<DictionaryDataEntity> childNodeList = dictionaryDataService.getList(dictionaryTypeEntity.getId(), true);
            if (dictionaryTypeEntity.getIsTree().compareTo(1) == 0) {
                List<Map<String, Object>> selectList = new ArrayList<>();
                for (DictionaryDataEntity item : childNodeList) {
                    Map<String, Object> ht = new HashMap<>(16);
                    ht.put("fullName", item.getFullName());
                    ht.put("enCode", item.getEnCode());
                    ht.put("id", item.getId());
                    ht.put("parentId", item.getParentId());
                    selectList.add(ht);
                }
                List<DictionaryDataAllModel> jsonToList = JsonUtil.createJsonToList(selectList, DictionaryDataAllModel.class);
                //==============转换树
                List<SumTree<DictionaryDataAllModel>> list1 = TreeDotUtils.convertListToTreeDot(jsonToList);
                List<DictionaryDataAllVO> list2 = JsonUtil.createJsonToList(list1, DictionaryDataAllVO.class);
                //==============
                Map<String, Object> htItem = new HashMap<>(16);
                htItem.put("id", dictionaryTypeEntity.getId());
                htItem.put("enCode", dictionaryTypeEntity.getEnCode());
                htItem.put("dictionaryList", list2);
                htItem.put("isTree", 1);
                list.add(htItem);
            } else {
                List<Map<String, Object>> selectList = new ArrayList<>();
                for (DictionaryDataEntity item : childNodeList) {
                    Map<String, Object> ht = new HashMap<>(16);
                    ht.put("enCode", item.getEnCode());
                    ht.put("id", item.getId());
                    ht.put("fullName", item.getFullName());
                    selectList.add(ht);
                }
                Map<String, Object> htItem = new HashMap<>(16);
                htItem.put("id", dictionaryTypeEntity.getId());
                htItem.put("enCode", dictionaryTypeEntity.getEnCode());
                htItem.put("dictionaryList", selectList);
                htItem.put("isTree", 0);
                list.add(htItem);
            }
        }
        ListVO<Map<String, Object>> vo = new ListVO<>();
        vo.setList(list);
        return ServiceResult.success(vo);
    }


    /**
     * 获取数据字典下拉框数据
     *
     * @param dictionaryTypeId 类别主键
     * @param isTree           是否为树
     * @param id               主键
     * @return ignore
     */
    @Operation(summary = "获取数据字典分类下拉框数据")
    @Parameters({
            @Parameter(name = "dictionaryTypeId", description = "数据分类id", required = true),
            @Parameter(name = "isTree", description = "是否树形"),
            @Parameter(name = "id", description = "主键", required = true)
    })
    @GetMapping("{dictionaryTypeId}/Selector/{id}")
    public ServiceResult<ListVO<DictionaryDataSelectVO>> treeView(@PathVariable("dictionaryTypeId") String dictionaryTypeId, String isTree, @PathVariable("id") String id) {
        DictionaryTypeEntity typeEntity = dictionaryTypeService.getInfo(dictionaryTypeId);
        List<DictionaryDataModel> treeList = new ArrayList<>();
        DictionaryDataModel treeViewModel = new DictionaryDataModel();
        treeViewModel.setId("0");
        treeViewModel.setFullName(typeEntity.getFullName());
        treeViewModel.setParentId("-1");
        treeViewModel.setIcon("fa fa-tags");
        treeList.add(treeViewModel);
        if ("1".equals(isTree)) {
            List<DictionaryDataEntity> data = dictionaryDataService.getList(dictionaryTypeId).stream().filter(t -> "1".equals(String.valueOf(t.getEnabledMark()))).collect(Collectors.toList());
            //过滤子集
            if (!"0".equals(id)) {
                data.remove(dictionaryDataService.getInfo(id));
            }
            for (DictionaryDataEntity entity : data) {
                DictionaryDataModel treeModel = new DictionaryDataModel();
                treeModel.setId(entity.getId());
                treeModel.setFullName(entity.getFullName());
                treeModel.setParentId("-1".equals(entity.getParentId()) ? entity.getDictionaryTypeId() : entity.getParentId());
                treeList.add(treeModel);
            }
        }
        List<SumTree<DictionaryDataModel>> sumTrees = TreeDotUtils.convertListToTreeDotFilter(treeList);
        List<DictionaryDataSelectVO> list = JsonUtil.createJsonToList(sumTrees, DictionaryDataSelectVO.class);
        ListVO<DictionaryDataSelectVO> treeVo = new ListVO<>();
        treeVo.setList(list);
        return ServiceResult.success(treeVo);
    }

    /**
     * 获取字典分类
     *
     * @param dictionaryTypeId 分类id、分类编码
     * @return ignore
     */
    @Operation(summary = "获取某个字典数据下拉框列表")
    @Parameters({
            @Parameter(name = "dictionaryTypeId", description = "数据分类id", required = true)
    })
    @GetMapping("/{dictionaryTypeId}/Data/Selector")
    public ServiceResult<ListVO<DictionaryTypeSelectVO>> selectorOneTreeView(@PathVariable("dictionaryTypeId") String dictionaryTypeId) {
        // 用dictionaryTypeId直接获取数据，获取不到的时候，采用enCode获取ID之后，再获取一次。
        List<DictionaryDataEntity> dictionaryDataList = dictionaryDataService.getList(dictionaryTypeId, true);
        if(dictionaryDataList.isEmpty()){
            DictionaryTypeEntity dictionaryTypeEntity = dictionaryTypeService.getInfoByEnCode(dictionaryTypeId);
            if(dictionaryTypeEntity != null){
                dictionaryDataList = dictionaryDataService.getList(dictionaryTypeEntity.getId(), true);
            }

        }
        List<DictionaryTypeSelectModel> voListVO = JsonUtil.createJsonToList(dictionaryDataList, DictionaryTypeSelectModel.class);
        List<SumTree<DictionaryTypeSelectModel>> sumTrees = TreeDotUtils.convertListToTreeDot(voListVO);
        List<DictionaryTypeSelectVO> list = JsonUtil.createJsonToList(sumTrees, DictionaryTypeSelectVO.class);
        ListVO<DictionaryTypeSelectVO> vo = new ListVO<>();
        vo.setList(list);
        return ServiceResult.success(vo);
    }

    /**
     * 获取字典分类根据英文编码
     *
     * @param enCode 分类EnCode、分类编码
     * @return ignore
     */
    @Operation(summary = "获取某个字典数据下拉框列表")
    @Parameters({
            @Parameter(name = "enCode", description = "数据分类EnCode", required = true)
    })
    @GetMapping("/{enCode}/EnCode/Selector")
    public ServiceResult<ListVO<DictionaryTypeSelectVO>> selectorOneTreeViewByEnCode(@PathVariable("enCode") String enCode) {
        List<DictionaryDataEntity> data = dictionaryDataService.getListByTypeDataCode(enCode);
        List<DictionaryTypeSelectModel> voListVO = BeanUtil.copyToList(data, DictionaryTypeSelectModel.class);
        List<SumTree<DictionaryTypeSelectModel>> sumTrees = TreeDotUtils.convertListToTreeDot(voListVO);
        List<DictionaryTypeSelectVO> list = JsonUtil.createJsonToList(sumTrees, DictionaryTypeSelectVO.class);
        ListVO<DictionaryTypeSelectVO> vo = new ListVO<>();
        vo.setList(list);
        return ServiceResult.success(vo);
    }

    /**
     * 获取数据字典信息
     *
     * @param id 主键
     * @return ignore
     * @throws DataBaseException ignore
     */
    @Operation(summary = "获取数据字典信息")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @GetMapping("/{id}/Info")
    public ServiceResult<DictionaryDataInfoVO> info(@PathVariable("id") String id) throws DataBaseException {
        DictionaryDataEntity entity = dictionaryDataService.getInfo(id);
        DictionaryDataInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, DictionaryDataInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 重复验证（名称）
     *
     * @param dictionaryTypeId 类别主键
     * @param fullName         名称
     * @param id               主键值
     * @return ignore
     */
    @Operation(summary = "（待定）重复验证（名称）")
    @GetMapping("/IsExistByFullName")
    public ServiceResult isExistByFullName(String dictionaryTypeId, String fullName, String id) {
        boolean data = dictionaryDataService.isExistByFullName(dictionaryTypeId, fullName, id);
        return ServiceResult.success(data);
    }

    /**
     * 重复验证（编码）
     *
     * @param dictionaryTypeId 类别主键
     * @param enCode           编码
     * @param id               主键值
     * @return ignore
     */
    @Operation(summary = "（待定）重复验证（编码）")
    @GetMapping("/IsExistByEnCode")
    public ServiceResult isExistByEnCode(String dictionaryTypeId, String enCode, String id) {
        boolean data = dictionaryDataService.isExistByEnCode(dictionaryTypeId, enCode, id);
        return ServiceResult.success(data);
    }


    /**
     * 添加数据字典
     *
     * @param dictionaryDataCrForm 实体对象
     * @return ignore
     */
    @Operation(summary = "添加数据字典")
    @Parameters({
            @Parameter(name = "dictionaryDataCrForm", description = "实体对象", required = true)
    })
    @SaCheckPermission("systemData.dictionary")
    @PostMapping
    public ServiceResult create(@RequestBody @Valid DictionaryDataCrForm dictionaryDataCrForm) {
        DictionaryDataEntity entity = BeanUtil.toBean(dictionaryDataCrForm, DictionaryDataEntity.class);
        if (dictionaryDataService.isExistByFullName(entity.getDictionaryTypeId(), entity.getFullName(), entity.getId())) {
            return ServiceResult.error("字典名称不能重复");
        }
        if (dictionaryDataService.isExistByEnCode(entity.getDictionaryTypeId(), entity.getEnCode(), entity.getId())) {
            return ServiceResult.error("字典编码不能重复");
        }
        dictionaryDataService.create(entity);
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 修改数据字典
     *
     * @param id                   主键值
     * @param dictionaryDataUpForm 实体对象
     * @return ignore
     */
    @Operation(summary = "修改数据字典")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true),
            @Parameter(name = "dictionaryDataUpForm", description = "实体对象", required = true)
    })
    @SaCheckPermission("systemData.dictionary")
    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid DictionaryDataUpForm dictionaryDataUpForm) {
        DictionaryDataEntity entity = BeanUtil.toBean(dictionaryDataUpForm, DictionaryDataEntity.class);
        if (dictionaryDataService.isExistByFullName(entity.getDictionaryTypeId(), entity.getFullName(), id)) {
            return ServiceResult.error("字典名称不能重复");
        }
        if (dictionaryDataService.isExistByEnCode(entity.getDictionaryTypeId(), entity.getEnCode(), id)) {
            return ServiceResult.error("字典编码不能重复");
        }
        boolean flag = dictionaryDataService.update(id, entity);
        if (!flag) {
            return ServiceResult.error(MsgCode.FA002.get());
        }
        return ServiceResult.success(MsgCode.SU004.get());

    }

    /**
     * 删除数据字典
     *
     * @param id 主键值
     * @return ignore
     */
    @Operation(summary = "删除数据字典")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("systemData.dictionary")
    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable("id") String id) {
        DictionaryDataEntity entity = dictionaryDataService.getInfo(id);
        if (entity != null) {
            if (dictionaryDataService.isExistSubset(entity.getId())) {
                return ServiceResult.error("字典类型下面有字典值禁止删除");
            }
            dictionaryDataService.delete(entity);
            return ServiceResult.success(MsgCode.SU003.get());
        }
        return ServiceResult.error(MsgCode.FA003.get());
    }

    /**
     * 更新字典状态
     *
     * @param id 主键值
     * @return ignore
     */
    @Operation(summary = "更新字典状态")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("systemData.dictionary")
    @PutMapping("/{id}/Actions/State")
    public ServiceResult update(@PathVariable("id") String id) {
        DictionaryDataEntity entity = dictionaryDataService.getInfo(id);
        if (entity != null) {
            if ("1".equals(String.valueOf(entity.getEnabledMark()))) {
                entity.setEnabledMark(0);
            } else {
                entity.setEnabledMark(1);
            }
            boolean flag = dictionaryDataService.update(entity.getId(), entity);
            if (!flag) {
                return ServiceResult.success(MsgCode.FA002.get());
            }
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 数据字典导出功能
     *
     * @param id 接口id
     * @return ignore
     */
    @Operation(summary = "导出数据字典数据")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("systemData.dictionary")
    @GetMapping("/{id}/Actions/Export")
    public ServiceResult exportFile(@PathVariable("id") String id) {
        DownloadVO downloadVO = dictionaryDataService.exportData(id);
        return ServiceResult.success(downloadVO);
    }

    /**
     * 数据字典导入功能
     *
     * @param multipartFile 文件
     * @return ignore
     * @throws DataBaseException ignore
     */
    @Operation(summary = "数据字典导入功能")
    @SaCheckPermission("systemData.dictionary")
    @PostMapping(value = "/Actions/Import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResult importFile(@RequestPart("file") MultipartFile multipartFile,
                                   @RequestParam("type") Integer type) throws DataBaseException {
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.SYSTEM_DICTIONARYDATA.getTableName())) {
            return ServiceResult.error(MsgCode.IMP002.get());
        }
        try {
            //获取文件内容
            String fileContent = FileUtil.getFileContent(multipartFile);
            DictionaryExportModel exportModel = JsonUtil.createJsonToBean(fileContent, DictionaryExportModel.class);
            List<DictionaryTypeEntity> list = exportModel.getList();
            //父级分类id不存在的话，直接抛出异常
            //如果分类只有一个
            if (list.size() == 1 && !"-1".equals(list.get(0).getParentId()) && dictionaryTypeService.getInfo(list.get(0).getParentId()) == null) {
                return ServiceResult.error("导入失败，查询不到上级分类");
            }
            //如果有多个需要验证分类是否存在
            if (list.stream().filter(t -> "-1".equals(t.getParentId())).count() < 1) {
                boolean exist = false;
                for (DictionaryTypeEntity dictionaryTypeEntity : list) {
                    //判断父级是否存在
                    if (dictionaryTypeService.getInfo(dictionaryTypeEntity.getParentId()) != null) {
                        exist = true;
                    }
                }
                if (!exist) {
                    return ServiceResult.error("导入失败，查询不到上级分类");
                }
            }
            //判断数据是否存在
            return dictionaryDataService.importData(exportModel, type);
        } catch (Exception e) {
            throw new DataBaseException(MsgCode.IMP004.get());
        }
    }

}
