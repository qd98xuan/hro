package com.linzen.controller;


import com.alibaba.fastjson2.JSONObject;
import com.linzen.base.ServiceResult;
import com.linzen.base.vo.PaginationVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.entity.FileEntity;
import com.linzen.model.FileForm;
import com.linzen.model.UploaderVO;
import com.linzen.model.YozoFileParams;
import com.linzen.model.YozoParams;
import com.linzen.service.YozoService;
import com.linzen.util.FileUtil;
import com.linzen.util.JsonUtil;
import com.linzen.util.XSSEscape;
import com.linzen.util.wxutil.HttpUtil;
import com.linzen.utils.YozoUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author FHNP
 */
@RestController
@RequestMapping
@Tag(name = "在线文档预览", description = "文件在线预览")
public class YozoFileController {

    @Autowired
    private YozoService yozoService;

    @Autowired
    private YozoUtils yozoUtil;

    @Autowired
    private ConfigValueUtil configValueUtil;

    @PostMapping("/api/file/getViewUrlWebPath")
    @Operation(summary = "文档预览")
    public ServiceResult getUrl(YozoFileParams params) {
        String previewUrl = XSSEscape.escape(yozoService.getPreviewUrl(params));
        return ServiceResult.success("success", previewUrl);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传本地文件")
    public ServiceResult upload(@RequestPart("multipartFile") MultipartFile file) throws IOException {
        String result =  yozoUtil.uploadFileInPreview(file.getInputStream(),file.getOriginalFilename());
        String fileName = file.getOriginalFilename();
        UploaderVO vo = UploaderVO.builder().name(fileName).build();
        Map<String, Object> map = JsonUtil.stringToMap(result);
        if ("操作成功".equals(map.get("message"))){
            Map<String, Object> dataMap = JsonUtil.stringToMap(String.valueOf(map.get("data")));
            String verId = String.valueOf(dataMap.get("fileVersionId"));
            vo.setFileVersionId(verId);
            return ServiceResult.success("Success",vo);
        }

        return ServiceResult.error("上传失败!");
    }

    /**
     *
     * @param fileName 新建文件名
     * @param templateType (模板类型；1新建doc文档，2新建docx文档，3新建ppt文档，4新建pptx文档，5新建xls文档，6新建xlsx文档)
     * @return
     */
    @GetMapping("/newCreate")
    @Operation(summary = "新建文件")
    @Parameters({
            @Parameter(name = "fileName", description = "名称"),
            @Parameter(name = "templateType", description = "类型"),
    })
    public ServiceResult newCreate(@RequestParam("fileName") String fileName, @RequestParam("templateType") String templateType) {
        String fileNa = yozoUtil.getFileName(fileName, templateType);
        if (fileNa == null) {
            return ServiceResult.error("请输入正确的文件格式");
        }
        //判断文件是否创建过
        FileEntity fileEntity = yozoService.selectByName(fileNa);
        if (fileEntity != null) {
            return ServiceResult.error("存在同名文件！");
        }
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("templateType", new String[]{templateType});
        params.put("fileName", new String[]{fileName});
        String sign = yozoUtil.generateSign(YozoParams.APP_ID, YozoParams.APP_KEY, params).getData();
        String url = YozoParams.CLOUD_DOMAIN + "/api/file/template?templateType=" + templateType +
                "&fileName=" + fileName +
                "&appId=" + YozoParams.APP_ID +
                "&sign=" + sign;
        String s = HttpUtil.sendHttpPost(url);
        Map<String, Object> maps = JSONObject.parseObject(s, Map.class);
        Map<String, String> fileMap = (Map<String, String>) maps.get("data");
        String fileVersionId = fileMap.get("fileVersionId");
        String fileId = fileMap.get("fileId");
        ServiceResult back = yozoService.saveFileId(fileVersionId, fileId, fileNa);
        //在本地新建文件
        FileUtil.createFile(configValueUtil.getDocumentPreviewPath(), fileNa);
        return back;
    }

    @GetMapping("/uploadByHttp")
    @Operation(summary = "http上传文件")
    @Parameters({
            @Parameter(name = "fileUrl", description = "路径"),
    })
    public ServiceResult uploadByHttp(@RequestParam("fileUrl") String fileUrl) {
        //获取签名
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("fileUrl", new String[]{fileUrl});
        String sign = yozoUtil.generateSign(YozoParams.APP_ID, YozoParams.APP_KEY, params).getData();
        String url = YozoParams.CLOUD_DOMAIN + "/api/file/http?fileUrl=" + fileUrl +
                "&appId=" + YozoParams.APP_ID +
                "&sign=" + sign;
        String s = HttpUtil.sendHttpPost(url);
        Map<String, Object> maps = JSONObject.parseObject(s, Map.class);
        Map<String, String> fileMap = (Map<String, String>) maps.get("data");
        String fileVersionId = fileMap.get("fileVersionId");
        String fileId = fileMap.get("fileId");
        ServiceResult back = yozoService.saveFileIdByHttp(fileVersionId, fileId, fileUrl);
        return back;
    }

    @GetMapping("/downloadFile")
    @Operation(summary = "永中下载文件")
    @Parameters({
            @Parameter(name = "fileVersionId", description = "主键"),
    })
    public String downloadFile(@RequestParam("fileVersionId") String fileVersionId) {
        String newFileVersionId = XSSEscape.escape(fileVersionId);
        FileEntity fileEntity = yozoService.selectByVersionId(newFileVersionId);
        if (fileEntity == null) {
            return "不存在该文件";
        }
        //获取签名
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("fileVersionId", new String[]{newFileVersionId});
        String sign = yozoUtil.generateSign(YozoParams.APP_ID, YozoParams.APP_KEY, params).getData();
        String url = YozoParams.CLOUD_DOMAIN + "/api/file/download?fileVersionId=" + newFileVersionId +
                "&appId=" + YozoParams.APP_ID +
                "&sign=" + sign;
        return url;
    }


    @GetMapping("/deleteVersionFile")
    @Operation(summary = "删除文件版本")
    @Parameters({
            @Parameter(name = "fileVersionId", description = "主键"),
    })
    public ServiceResult deleteVersion(@RequestParam("fileVersionId") String fileVersionId) {
        //获取签名
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("fileVersionId", new String[]{fileVersionId});
        String sign = yozoUtil.generateSign(YozoParams.APP_ID, YozoParams.APP_KEY, params).getData();
        String url = YozoParams.CLOUD_DOMAIN + "/api/file/delete/version?fileVersionId=" + fileVersionId +
                "&appId=" + YozoParams.APP_ID +
                "&sign=" + sign;
        String s = HttpUtil.sendHttpGet(url);
        Map<String, Object> maps = JSONObject.parseObject(s, Map.class);
        String fileName = yozoService.selectByVersionId(fileVersionId).getFileName();
        String path = configValueUtil.getDocumentPreviewPath() + fileName;
        if (FileUtil.fileIsFile(path)) {
            File file = new File(XSSEscape.escapePath(path));
            file.delete();
        }
        String versionId = (String) maps.get("data");
        ServiceResult back = yozoService.deleteFileByVersionId(versionId);
        return back;
    }

    @GetMapping("/batchDelete")
    @Operation(summary = "批量删除文件版本")
    @Parameters({
            @Parameter(name = "fileVersionIds", description = "主键"),
    })
    public ServiceResult batchDelete(@RequestParam("fileVersionIds") String[] fileVersionIds) {
        List<String> asList = new ArrayList<>(16);
        //获取签名
        for (String fileVersionId : fileVersionIds) {
            String escape = XSSEscape.escape(fileVersionId);
            asList.add(escape);
        }
        String[] newFileVersionIds = asList.toArray(fileVersionIds);
        Map<String, String[]> params = new HashMap<>();
        params.put("fileVersionIds", newFileVersionIds);
        String sign = yozoUtil.generateSign(YozoParams.APP_ID, YozoParams.APP_KEY, params).getData();

        StringBuilder fileVersionIdList = new StringBuilder();
        for (String s : newFileVersionIds) {
            String fileName = yozoService.selectByVersionId(s).getFileName();
            String path = configValueUtil.getDocumentPreviewPath() + fileName;
            File file = new File(XSSEscape.escapePath(path));
            file.delete();
            fileVersionIdList.append("fileVersionIds=" + s + "&");
        }
        String list = fileVersionIdList.toString();
        String url = YozoParams.CLOUD_DOMAIN + "/api/file/delete/versions?" + list +
                "appId=" + YozoParams.APP_ID +
                "&sign=" + sign;
        String s = HttpUtil.sendHttpGet(url);
        ServiceResult back = yozoService.deleteBatch(newFileVersionIds);
        return back;
    }

    @GetMapping("/editFile")
    @Operation(summary = "在线编辑")
    @Parameters({
            @Parameter(name = "fileVersionId", description = "主键"),
    })
    public ServiceResult editFile(@RequestParam("fileVersionId") String fileVersionId) {
        String newFileVersionId = XSSEscape.escape(fileVersionId);
        //获取签名
        Map<String, String[]> params = new HashMap<>();
        params.put("fileVersionId", new String[]{newFileVersionId});
        String sign = yozoUtil.generateSign(YozoParams.APP_ID, YozoParams.APP_KEY, params).getData();
        String url = YozoParams.EDIT_DOMAIN + "/api/edit/file?fileVersionId=" + newFileVersionId +
                "&appId=" + YozoParams.APP_ID +
                "&sign=" + sign;
        return ServiceResult.success("success", url);
    }

    /**
     * 永中回调
     *
     * @param oldFileId
     * @param newFileId
     * @param message
     * @param errorCode
     * @return
     */
    @PostMapping("/3rd/edit/callBack")
    @Parameters({
            @Parameter(name = "oldFileId", description = "主键"),
            @Parameter(name = "newFileId", description = "主键"),
            @Parameter(name = "message", description = "消息"),
            @Parameter(name = "errorCode", description = "编码"),
    })
    public Map<String, Object> editCallBack(@RequestParam("oldFileId") String oldFileId, @RequestParam("newFileId") String newFileId, @RequestParam("message") String message, @RequestParam("errorCode") Integer errorCode) {

        String escapeOldFileId = XSSEscape.escape(oldFileId);
        String escapeNewFileId = XSSEscape.escape(newFileId);
        String escapeMessage = XSSEscape.escape(message);
        yozoService.editFileVersion(escapeOldFileId, escapeNewFileId);

        Map<String, Object> result = new HashMap<>();
        result.put("oldFileId", escapeOldFileId);
        result.put("newFileId", escapeNewFileId);
        result.put("message", escapeMessage);
        result.put("errorCode", errorCode);
        return result;
    }

    @PostMapping("/documentList")
    @Operation(summary = "文档列表")
    @Parameters({
            @Parameter(name = "pageModel", description = "分页模型", required = true),
    })
    public ServiceResult documentList(@RequestBody PaginationVO pageModel) {
        PaginationVO pv = new PaginationVO();
        pv.setCurrentPage(pageModel.getCurrentPage());
        pv.setPageSize(pageModel.getPageSize());
        pv.setTotal(pageModel.getTotal());
        List<FileEntity> list = yozoService.getAllList(pv);
        List<FileForm> listVo = JsonUtil.createJsonToList(list, FileForm.class);
        return ServiceResult.pageList(listVo, pv);
    }

    /**
     * 传入新的fileVersionId同步
     *
     * @param fileVersionId
     * @return
     * @throws Exception
     */
    @GetMapping("/updateFile")
    @Operation(summary = "/同步文件版本")
    @Parameters({
            @Parameter(name = "fileVersionId", description = "主键"),
    })
    public ServiceResult updateFile(@RequestParam("fileVersionId") String fileVersionId) throws Exception {
        FileEntity fileEntity = yozoService.selectByVersionId(fileVersionId);
        String fileName = fileEntity.getFileName();
        String path = configValueUtil.getDocumentPreviewPath() + fileName;
        if (FileUtil.fileIsFile(path)) {
            File file = new File(XSSEscape.escapePath(path));
            file.delete();
        }
        String fileUrl = this.downloadFile(fileVersionId);
        yozoUtil.downloadFile(fileUrl, path);
        return ServiceResult.success("更新完毕");
    }
}
