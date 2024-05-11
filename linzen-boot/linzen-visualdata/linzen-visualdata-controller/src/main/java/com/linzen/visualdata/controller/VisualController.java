package com.linzen.visualdata.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import cn.xuyanwu.spring.file.storage.FileInfo;
import com.google.common.base.Joiner;
import com.linzen.base.ServiceResult;
import com.linzen.base.controller.SuperController;
import com.linzen.base.vo.DownloadVO;
import com.linzen.base.vo.ListVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.MsgCode;
import com.linzen.emnus.ModuleTypeEnum;
import com.linzen.exception.DataBaseException;
import com.linzen.model.FileListVO;
import com.linzen.util.*;
import com.linzen.visualdata.entity.VisualCategoryEntity;
import com.linzen.visualdata.entity.VisualConfigEntity;
import com.linzen.visualdata.entity.VisualEntity;
import com.linzen.visualdata.enums.VisualImgEnum;
import com.linzen.visualdata.model.VisualPageVO;
import com.linzen.visualdata.model.visual.*;
import com.linzen.visualdata.model.visualcategory.VisualCategoryListVO;
import com.linzen.visualdata.model.visualconfig.VisualConfigInfoModel;
import com.linzen.visualdata.model.visualfile.ImageVO;
import com.linzen.visualdata.service.VisualCategoryService;
import com.linzen.visualdata.service.VisualConfigService;
import com.linzen.visualdata.service.VisualService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 大屏基本信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@RestController
@Tag(name = "大屏基本信息", description = "visual")
@RequestMapping("/api/blade-visual/visual")
@Slf4j
public class VisualController extends SuperController<VisualService, VisualEntity> {

    @Autowired
    private FileExport fileExport;
    @Autowired
    private VisualService visualService;
    @Autowired
    private VisualConfigService configService;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private VisualCategoryService categoryService;

    /**
     * 列表
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "分页")
    @GetMapping("/list")
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult<VisualPageVO<VisualListVO>> list(VisualPaginationModel pagination) {
        List<VisualEntity> data = visualService.getList(pagination);
        List<VisualListVO> list = BeanUtil.copyToList(data, VisualListVO.class);
        VisualPageVO<VisualListVO> paginationVO = BeanUtil.toBean(pagination, VisualPageVO.class);
        paginationVO.setRecords(list);
        return ServiceResult.success(paginationVO);
    }

    /**
     * 详情
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "详情")
    @GetMapping("/detail")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult<VisualInfoVO> info(@RequestParam("id")String id) {
        VisualEntity visual = visualService.getInfo(id);
        VisualConfigEntity config = configService.getInfo(id);
        VisualInfoVO vo = new VisualInfoVO();
        vo.setVisual(BeanUtil.toBean(visual, VisualInfoModel.class));
        vo.setConfig(BeanUtil.toBean(config, VisualConfigInfoModel.class));
        return ServiceResult.success(vo);
    }

    /**
     * 新增
     *
     * @param visualCrform 大屏模型
     * @return
     */
    @Operation(summary = "新增")
    @PostMapping("/save")
    @Parameters({
            @Parameter(name = "visualCrform", description = "大屏模型",required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult create(@RequestBody @Valid VisualCrform visualCrform) {
        VisualEntity visual = BeanUtil.toBean(visualCrform.getVisual(), VisualEntity.class);
        visual.setBackgroundUrl(VisusalImgUrl.url + configValueUtil.getBiVisualPath() + "bg/bg1.png");
        VisualConfigEntity config = BeanUtil.toBean(visualCrform.getConfig(), VisualConfigEntity.class);
        visualService.create(visual, config);
        Map<String, String> data = new HashMap<>(16);
        data.put("id", visual.getId());
        return ServiceResult.success(data);
    }

    /**
     * 修改
     *
     * @param categoryUpForm 大屏模型
     * @return
     */
    @Operation(summary = "修改")
    @PostMapping("/update")
    @Parameters({
            @Parameter(name = "categoryUpForm", description = "大屏模型",required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult update(@RequestBody VisualUpform categoryUpForm) {
        VisualEntity visual = BeanUtil.toBean(categoryUpForm.getVisual(), VisualEntity.class);
        VisualConfigEntity config = BeanUtil.toBean(categoryUpForm.getConfig(), VisualConfigEntity.class);
        visualService.update(visual.getId(), visual, config);
        return ServiceResult.success("更新成功");
    }

    /**
     * 删除
     *
     * @param ids 主键
     * @return
     */
    @Operation(summary = "删除")
    @PostMapping("/remove")
    @Parameters({
            @Parameter(name = "ids", description = "主键", required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult delete(@RequestParam("ids")String ids) {
        VisualEntity entity = visualService.getInfo(ids);
        if (entity != null) {
            visualService.delete(entity);
            return ServiceResult.success("删除成功");
        }
        return ServiceResult.error("删除失败，数据不存在");
    }

    /**
     * 复制
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "复制")
    @PostMapping("/copy")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult copy(@RequestParam("id")String id) {
        VisualEntity entity = visualService.getInfo(id);
        VisualConfigEntity config = configService.getInfo(id);
        if (entity != null) {
            entity.setTitle(entity.getTitle() + "_复制");
            visualService.create(entity, config);
            return ServiceResult.success("操作成功", entity.getId());
        }
        return ServiceResult.error("复制失败");
    }

    /**
     * 获取类型
     *
     * @return
     */
    @Operation(summary = "获取类型")
    @GetMapping("/category")
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult<List<VisualCategoryListVO>> list() {
        List<VisualCategoryEntity> data = categoryService.getList();
        List<VisualCategoryListVO> list = JsonUtil.createJsonToList(data, VisualCategoryListVO.class);
        return ServiceResult.success(list);
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @param type 类型
     * @return
     */
    @NoDataSourceBind()
    @Operation(summary = "上传文件")
    @Parameters({
            @Parameter(name = "type", description = "类型",required = true),
    })
    @PostMapping(value = "/put-file/{type}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResult<ImageVO> file(MultipartFile file, @PathVariable("type") String type) {
        ImageVO vo = new ImageVO();
        VisualImgEnum imgEnum = VisualImgEnum.getByMessage(type);
        if (imgEnum != null) {
            String path = imgEnum.getMessage();
            String filePath = configValueUtil.getBiVisualPath() + path + "/";
            String name = RandomUtil.uuId() + "." + UpUtil.getFileType(file);
            //上传文件
            FileInfo fileInfo = FileUploadUtils.uploadFile(file, filePath, name);
            vo.setOriginalName(fileInfo.getOriginalFilename());
            vo.setLink(VisusalImgUrl.url + fileInfo.getPath() + fileInfo.getFilename());
            vo.setName(VisusalImgUrl.url + fileInfo.getPath() + fileInfo.getFilename());
        }
        return ServiceResult.success(vo);
    }

    /**
     * 获取图片列表
     *
     * @param type 文件夹
     * @return
     */
    @NoDataSourceBind()
    @Operation(summary = "获取图片列表")
    @GetMapping("/{type}")
    @Parameters({
            @Parameter(name = "type", description = "文件夹", required = true),
    })
    public ServiceResult<List<ImageVO>> getFile(@PathVariable("type") String type) {
        List<ImageVO> vo = new ArrayList<>();
        VisualImgEnum imgEnum = VisualImgEnum.getByMessage(type);
        if (imgEnum != null) {
            String path = configValueUtil.getBiVisualPath() + imgEnum.getMessage() + "/";
            List<FileListVO> fileList = FileUploadUtils.getFileList(path);
            fileList.forEach(fileListVO -> {
                ImageVO imageVO = new ImageVO();
                imageVO.setName(fileListVO.getFileName());
                imageVO.setLink(VisusalImgUrl.url + fileListVO.getFileName());
                imageVO.setOriginalName(fileListVO.getFileName());
                vo.add(imageVO);
            });
        }
        return ServiceResult.success(vo);
    }

    /**
     * 大屏下拉框
     */
    @Operation(summary = "大屏下拉框")
    @GetMapping("/Selector")
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult<ListVO<VisualSelectorVO>> selector() {
        List<VisualEntity> visualList = visualService.getList();
        List<VisualCategoryEntity> categoryList = categoryService.getList();
        List<VisualSelectorVO> listVos = new ArrayList<>();
        for (VisualCategoryEntity category : categoryList) {
            VisualSelectorVO categoryModel = new VisualSelectorVO();
            categoryModel.setId(category.getCategoryValue());
            categoryModel.setFullName(category.getCategoryKey());
            List<VisualEntity> visualAll = visualList.stream().filter(t -> t.getCategory().equals(Integer.parseInt(category.getCategoryValue()))).collect(Collectors.toList());
            if (visualAll.size() > 0) {
                List<VisualSelectorVO> childList = new ArrayList<>();
                for (VisualEntity visual : visualAll) {
                    VisualSelectorVO visualModel = new VisualSelectorVO();
                    visualModel.setId(visual.getId());
                    visualModel.setFullName(visual.getTitle());
                    visualModel.setChildren(null);
                    visualModel.setHasChildren(false);
                    childList.add(visualModel);
                }
                categoryModel.setHasChildren(true);
                categoryModel.setChildren(childList);
                listVos.add(categoryModel);
            }
        }
        ListVO vo = new ListVO();
        vo.setList(listVos);
        return ServiceResult.success(vo);
    }

    /**
     * 大屏导出
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "大屏导出")
    @PostMapping("/{id}/Actions/ExportData")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ServiceResult<DownloadVO> exportData(@PathVariable("id") String id) {
        VisualEntity entity = visualService.getInfo(id);
        VisualConfigEntity configEntity = configService.getInfo(id);
        VisualModel model = new VisualModel();
        model.setEntity(entity);
        model.setConfigEntity(configEntity);
        DownloadVO downloadVO = fileExport.exportFile(model, configValueUtil.getTemporaryFilePath(), entity.getTitle(), ModuleTypeEnum.VISUAL_DATA.getTableName());
        return ServiceResult.success(downloadVO);
    }

    /**
     * 大屏导入
     *
     * @param multipartFile 文件
     * @return
     */
    @Operation(summary = "大屏导入")
    @SaCheckPermission("onlineDev.dataScreen")
    @PostMapping(value = "/Model/Actions/ImportData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResult ImportData(MultipartFile multipartFile) throws DataBaseException {
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.VISUAL_DATA.getTableName())) {
            return ServiceResult.error(MsgCode.IMP002.get());
        }
        //获取文件内容
        String fileContent = FileUtil.getFileContent(multipartFile);
        VisualModel vo = JsonUtil.createJsonToBean(fileContent, VisualModel.class);
        visualService.createImport(vo.getEntity(), vo.getConfigEntity());
        return ServiceResult.success(MsgCode.SU000.get());
    }

    /**
     * 获取API动态数据
     *
     * @param apiRequest 大屏模型
     * @return
     */
    @Operation(summary = "获取API动态数据")
    @PostMapping(value = "/GetApiData")
    @Parameters({
            @Parameter(name = "apiRequest", description = "大屏模型",required = true),
    })
    public String getApiData(@RequestBody @Valid VisualApiRequest apiRequest) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder().callTimeout(Duration.ofSeconds(apiRequest.getTimeout())).build();
        Headers headers;
        Request request;
        if (!apiRequest.getHeaders().isEmpty()) {
            Headers.Builder builder = new Headers.Builder();
            apiRequest.getHeaders().forEach((k, v) -> {
                builder.add(k, v);
            });
            headers = builder.build();
        } else {
            headers = new Headers.Builder().build();
        }
        if (apiRequest.getMethod().equalsIgnoreCase("post")) {
            request = new Request.Builder().url(apiRequest.getUrl())
                    .post(okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json;charset=utf-8"), apiRequest.getParams().isEmpty() ? "" : JsonUtil.createObjectToString(apiRequest.getParams())))
                    .headers(headers)
                    .build();
        } else {
            String params = Joiner.on("&")
                    .useForNull("")
                    .withKeyValueSeparator("=")
                    .join(apiRequest.getParams());
            request = new Request.Builder().url(apiRequest.getUrl() + (apiRequest.getUrl().contains("?") ? "&" : "?") + params)
                    .get()
                    .headers(headers)
                    .build();
        }
        return client.newCall(request).execute().body().string();
    }


    /**
     * 获取API动态数据
     *
     * @param proxyModel 代理模型
     * @return
     */
    @Operation(summary = "获取API动态数据")
    @PostMapping(value = "/proxy")
    @Parameters({
            @Parameter(name = "proxyModel", description = "代理模型",required = true),
    })
    public String getApiData(@RequestBody @Valid VisualProxyModel proxyModel) throws IOException {
        Map<String, String> headers;
        boolean isForm = false;
        if (!proxyModel.getHeaders().isEmpty()) {
            headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            headers.putAll(proxyModel.getHeaders());
            if(headers.containsKey("form")){
                //头部指定当前为form表单
                isForm = true;
            }
        } else {
            headers = new HashMap<>(1, 1);
        }
        //Header无自定义TOKEN 取当前TOKEN
        if(!headers.containsKey("Authorization")){
            String token = UserProvider.getToken();
            if(StringUtil.isNotEmpty(token)){
                headers.put("Authorization", token);
            }
        }
        HttpRequest httpRequest = HttpRequest.of(proxyModel.getUrl()).method(Method.valueOf(proxyModel.getMethod().toUpperCase())).addHeaders(headers);
        if(isForm){
            httpRequest.form(proxyModel.getData());
        }else if(proxyModel.getData() != null && !proxyModel.getData().isEmpty()){
            httpRequest.body(JsonUtil.createObjectToString(proxyModel.getData()));
        }else {
            httpRequest.form(proxyModel.getParams());
        }
        try {
            return httpRequest.timeout(10000).execute().body();
        } catch (Exception e){
            log.info("接口请求失败 {} {}", proxyModel.getUrl(), e.getMessage());
            throw new DataBaseException("接口请求失败");
        }
    }
}
