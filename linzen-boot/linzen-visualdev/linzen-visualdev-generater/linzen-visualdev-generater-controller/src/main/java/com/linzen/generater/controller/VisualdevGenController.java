package com.linzen.generater.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.xuyanwu.spring.file.storage.FileInfo;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.entity.VisualdevEntity;
import com.linzen.base.model.DownloadCodeForm;
import com.linzen.base.model.read.ReadListVO;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.service.VisualdevService;
import com.linzen.base.util.ReadFile;
import com.linzen.base.util.VisualUtil;
import com.linzen.base.vo.DownloadVO;
import com.linzen.base.vo.ListVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.FileTypeConstant;
import com.linzen.constant.MsgCode;
import com.linzen.exception.DataBaseException;
import com.linzen.generater.service.VisualdevGenService;
import com.linzen.util.*;
import com.linzen.util.context.RequestContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 可视化开发功能表
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "代码生成器", description = "Generater")
@RestController
@RequestMapping("/api/visualdev/Generater")
public class VisualdevGenController {

    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private VisualdevService visualdevService;
    @Autowired
    private VisualdevGenService visualdevGenService;
    @Autowired
    private DictionaryDataService dictionaryDataService;


    /**
     * 下载文件
     *
     * @return
     */
    @NoDataSourceBind()
    @Operation(summary = "下载文件")
    @GetMapping("/DownloadVisCode")
    public void downloadCode() throws DataBaseException {
        HttpServletRequest request = ServletUtil.getRequest();
        String reqJson = request.getParameter("encryption");
        String name = request.getParameter("name");
        String fileNameAll = DesUtil.aesDecode(reqJson);
        if (!StringUtil.isEmpty(fileNameAll)) {
            String token = fileNameAll.split("#")[0];
            if (TicketUtil.parseTicket(token) != null) {
                TicketUtil.deleteTicket(token);
                String fileName = fileNameAll.split("#")[1];
                String path = FilePathUtil.getFilePath(FileTypeConstant.CODETEMP);
                //下载到本地
                byte[] bytes = FileUploadUtils.downloadFileByte(path, fileName, false);
                FileDownloadUtil.downloadFile(bytes, fileName, name);
            } else {
                throw new DataBaseException("下载链接已失效");
            }
        } else {
            throw new DataBaseException("下载链接已失效");
        }
    }

    @Operation(summary = "获取命名空间")
    @GetMapping("/AreasName")
    @SaCheckPermission("generator.webForm")
    public ServiceResult getAreasName() {
        String areasName = configValueUtil.getCodeAreasName();
        List<String> areasNameList = new ArrayList(Arrays.asList(areasName.split(",")));
        return ServiceResult.success(areasNameList);
    }

    @Operation(summary = "下载代码")
    @Parameters({
            @Parameter(name = "id", description = "主键"),
    })
    @PostMapping("/{id}/Actions/DownloadCode")
    @SaCheckPermission("generator.webForm")
    @Transactional
    public ServiceResult downloadCode(@PathVariable("id") String id, @RequestBody DownloadCodeForm downloadCodeForm) throws Exception {
        if (downloadCodeForm.getModule() != null) {
            DictionaryDataEntity info = dictionaryDataService.getInfo(downloadCodeForm.getModule());
            if (info != null) {
                downloadCodeForm.setModule(info.getEnCode());
            }
        }
        VisualdevEntity visualdevEntity = visualdevService.getInfo(id);
        String s = VisualUtil.checkPublishVisualModel(visualdevEntity, "下载");
        if (s != null) {
            return ServiceResult.error(s);
        }
        DownloadVO vo;
        String fileName;
        if (RequestContext.isVue3()) {
            downloadCodeForm.setVue3(true);
            fileName = visualdevGenService.codeGengerateV3(visualdevEntity, downloadCodeForm);
        } else {
            fileName = visualdevGenService.codeGengerate(id, downloadCodeForm);
        }
        //上传到minio
        String filePath = FileUploadUtils.getLocalBasePath() + configValueUtil.getServiceDirectoryPath() + fileName + ".zip";
        FileUtil.toZip(filePath, true, FileUploadUtils.getLocalBasePath() + configValueUtil.getServiceDirectoryPath() + fileName);
        // 删除源文件
        FileUtil.deleteFileAll(new File(FileUploadUtils.getLocalBasePath() + configValueUtil.getServiceDirectoryPath() + fileName));
        MultipartFile multipartFile = FileUtil.createFileItem(new File(XSSEscape.escapePath(filePath)));
        FileInfo fileInfo = FileUploadUtils.uploadFile(multipartFile, configValueUtil.getServiceDirectoryPath(), fileName + ".zip");
        vo = DownloadVO.builder().name(fileInfo.getFilename()).url(UploaderUtil.uploaderVisualFile(fileInfo.getFilename()) + "&name=" + fileName + ".zip").build();
        if (vo == null) {
            return ServiceResult.error(MsgCode.FA006.get());
        }
        return ServiceResult.success(vo);
    }


    /**
     *
     * 输出移动开发模板
     *
     * @param id               String
     * @param downloadCodeForm DownloadCodeForm
     * @return ServiceResult
     * @throws Exception
     */
    @Operation(summary = "预览代码")
    @Parameters({
            @Parameter(name = "id", description = "主键"),
    })
    @PostMapping("/{id}/Actions/CodePreview")
    @SaCheckPermission("generator.webForm")
    public ServiceResult<ListVO<ReadListVO>> codePreview(@PathVariable("id") String id, @RequestBody DownloadCodeForm downloadCodeForm) throws Exception {
        VisualdevEntity visualdevEntity = visualdevService.getInfo(id);
        String s = VisualUtil.checkPublishVisualModel(visualdevEntity, "预览");
        if (s != null) {
            return ServiceResult.error(s);
        }
        String fileName;
        if (RequestContext.isVue3()) {
            downloadCodeForm.setVue3(true);
            fileName = visualdevGenService.codeGengerateV3(visualdevEntity, downloadCodeForm);
        } else {
            fileName = visualdevGenService.codeGengerate(id, downloadCodeForm);
        }
        List<ReadListVO> dataList = ReadFile.priviewCode(FileUploadUtils.getLocalBasePath() + configValueUtil.getServiceDirectoryPath() + fileName);
        if (dataList.isEmpty()) {
            return ServiceResult.error(MsgCode.FA015.get());
        }
        ListVO<ReadListVO> datas = new ListVO<>();
        datas.setList(dataList);
        return ServiceResult.success(datas);
    }

    /**
     * App预览(后台APP表单设计)
     *
     * @return
     */
    @Operation(summary = "App预览(后台APP表单设计)")
    @Parameters({
            @Parameter(name = "data", description = "数据"),
    })
    @PostMapping("/App/Preview")
    @SaCheckPermission("generator.webForm")
    public ServiceResult appPreview(String data) {
        String id = RandomUtil.uuId();
        redisUtil.insert(id, data, 300);
        return ServiceResult.success((Object) id);
    }

    /**
     * App预览(后台APP表单设计)
     *
     * @return
     */
    @Operation(summary = "App预览查看")
    @Parameters({
            @Parameter(name = "id", description = "主键"),
    })
    @GetMapping("/App/{id}/Preview")
    @SaCheckPermission("generator.webForm")
    public ServiceResult preview(@PathVariable("id") String id) {
        if (redisUtil.exists(id)) {
            Object object = redisUtil.getString(id);
            return ServiceResult.success(object);
        } else {
            return ServiceResult.error(MsgCode.FA019.get());
        }
    }

}
