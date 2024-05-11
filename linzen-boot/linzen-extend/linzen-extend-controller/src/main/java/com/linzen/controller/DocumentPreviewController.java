package com.linzen.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.linzen.base.ServiceResult;
import com.linzen.base.Page;
import com.linzen.config.ConfigValueUtil;
import com.linzen.enums.FilePreviewTypeEnum;
import com.linzen.exception.DataBaseException;
import com.linzen.model.FileListVO;
import com.linzen.model.YozoFileParams;
import com.linzen.model.YozoParams;
import com.linzen.util.*;
import com.linzen.utils.SplicingUrlUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 文档在线预览
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@NoDataSourceBind()
@Tag(name = "文档在线预览", description = "DocumentPreview")
@RestController
@RequestMapping("/api/extend/DocumentPreview")
public class DocumentPreviewController {

    @Autowired
    private ConfigValueUtil configValueUtil;

    /**
     * 永中文件预览
     *
     * @param fileId 文件主键
     * @param params 永中模型
     * @param previewType 类型
     * @return
     */
    @Operation(summary = "文件预览")
    @GetMapping("/{fileId}/Preview")
    @Parameters({
            @Parameter(name = "fileId", description = "文件主键",required = true),
            @Parameter(name = "previewType", description = "类型"),
    })
    @SaCheckPermission("extend.documentPreview")
    public ServiceResult filePreview(@PathVariable("fileId") String fileId, YozoFileParams params, @RequestParam("previewType") String previewType) {
        FileListVO fileListVO = FileUploadUtils.getFileDetail(configValueUtil.getDocumentPreviewPath(), fileId);
        if (fileListVO == null) {
            return ServiceResult.error("文件找不到!");
        }
        if (fileListVO.getFileName() != null) {
            String[] split = fileListVO.getFileName().split("/");
            if (split.length > 0) {
                fileListVO.setFileName(split[split.length - 1]);
            }
        }
        String url = YozoParams.LINZEN_DOMAINS + "/api/extend/DocumentPreview/down/" + fileListVO.getFileName();
        String urlPath;
        if (previewType.equals(FilePreviewTypeEnum.YOZO_ONLINE_PREVIEW.getType())){
            params.setUrl(url);
            urlPath = SplicingUrlUtil.getPreviewUrl(params);
            return ServiceResult.success("success", XSSEscape.escape(urlPath));
        }
        return ServiceResult.success("success",url);
    }

    /**
     * 列表
     *
     * @param page 分页模型
     * @return
     */
    @Operation(summary = "获取文档列表")
    @GetMapping
    @SaCheckPermission("extend.documentPreview")
    public ServiceResult<List<FileListVO>> list(Page page) {
        List<FileListVO> fileList = FileUploadUtils.getFileList(configValueUtil.getDocumentPreviewPath());
        fileList.stream().forEach(t -> {
            if (t.getFileName() != null) {
                String[] split = t.getFileName().split("/");
                if (split.length > 0) {
                    t.setFileName(split[split.length - 1]);
                }
            }
        });
        if (StringUtil.isNotEmpty(page.getKeyword())) {
            fileList = fileList.stream().filter(t -> t.getFileName().contains(page.getKeyword())).collect(Collectors.toList());
        }
        return ServiceResult.success(fileList);
    }

    /**
     * 文件下载url
     *
     * @param fileName 名称
     */
    @NoDataSourceBind()
    @GetMapping("/down/{fileName}")
    @Parameters({
            @Parameter(name = "fileName", description = "名称",required = true),
    })
    public void pointDown(@PathVariable("fileName") String fileName) throws DataBaseException {
        boolean exists = FileUploadUtils.exists(configValueUtil.getDocumentPreviewPath(), fileName);
        if (!exists) {
            throw new DataBaseException("下载失败");
        }
        byte[] bytes = FileUploadUtils.downloadFileByte(configValueUtil.getDocumentPreviewPath(), fileName, false);
        FileDownloadUtil.downloadFile(bytes, fileName, null);
    }

}
