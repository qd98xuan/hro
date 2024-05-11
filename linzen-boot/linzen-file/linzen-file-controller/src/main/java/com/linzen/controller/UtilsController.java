package com.linzen.controller;

import cn.xuyanwu.spring.file.storage.FileInfo;
import com.alibaba.fastjson2.JSONObject;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.util.OptimizeUtil;
import com.linzen.base.vo.DownloadVO;
import com.linzen.base.vo.ListVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.FileTypeConstant;
import com.linzen.constant.MsgCode;
import com.linzen.entity.FileDetail;
import com.linzen.enums.DeviceType;
import com.linzen.exception.DataBaseException;
import com.linzen.model.*;
import com.linzen.util.*;
import com.linzen.utils.YozoUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Cleanup;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 通用控制器
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "公共", description = "file")
@RestController
@RequestMapping("/api/file")
public class UtilsController {

    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private YozoUtils yozoUtils;

    /**
     * 语言列表
     *
     * @return
     */
    @Operation(summary = "语言列表")
    @GetMapping("/Language")
    public ServiceResult<ListVO<LanguageVO>> getList() {
        String dictionaryTypeId = "dc6b2542d94b407cac61ec1d59592901";
        List<DictionaryDataEntity> list = dictionaryDataService.getList(dictionaryTypeId);
        List<LanguageVO> language = JsonUtil.createJsonToList(list, LanguageVO.class);
        ListVO vo = new ListVO();
        vo.setList(language);
        return ServiceResult.success(vo);
    }

    /**
     * 图形验证码
     *
     * @return
     */
    @NoDataSourceBind()
    @Operation(summary = "图形验证码")
    @GetMapping("/ImageCode/{timestamp}")
    @Parameters({
            @Parameter(name = "timestamp", description = "时间戳",required = true),
    })
    public void imageCode(@PathVariable("timestamp") String timestamp) {
        DownUtil.downCode(null);
        redisUtil.insert(timestamp, ServletUtil.getSession().getAttribute(CodeUtil.RANDOMCODEKEY), 120);
    }

    /**
     * 获取全部下载文件链接（打包下载）
     *
     * @return
     */
    @NoDataSourceBind
    @Operation(summary = "获取全部下载文件链接（打包下载）")
    @PostMapping("/PackDownload/{type}")
    public ServiceResult packDownloadUrl(@PathVariable("type") String type, @RequestBody List<Map<String, String>> fileInfoList) throws Exception {
        type = XSSEscape.escape(type);
        if(fileInfoList == null || fileInfoList.isEmpty()) {
            return ServiceResult.error("未发现文件");
        }
        List<String> filePathList = new ArrayList<String>();

        String zipTempFilePath = null;
        String zipFileId =  RandomUtil.uuId() + ".zip";
        for(Map fileInfoMap : fileInfoList) {
            String fileId = XSSEscape.escape((String)fileInfoMap.get("fileId")).trim();
            String fileName = XSSEscape.escape((String)fileInfoMap.get("fileName")).trim();
            if(StringUtil.isEmpty(fileId) || StringUtil.isEmpty(fileName)) {
                continue;
            }
            if(FileUploadUtils.exists(type, fileId)) {
                String typePath = FilePathUtil.getFilePath(type);
                if(fileId.indexOf(",") >= 0) {
                    typePath += fileId.substring(0, fileId.lastIndexOf(",")+1).replaceAll(",", "/");
                    fileId = fileId.substring(fileId.lastIndexOf(",")+1);
                }
                byte[] bytes = FileUploadUtils.downloadFileByte(typePath, fileId, false);
                if(zipTempFilePath == null) {
                    zipTempFilePath = FileUploadUtils.getLocalBasePath() + FilePathUtil.getFilePath(FileTypeConstant.FILEZIPDOWNTEMPPATH);
                    if(!new File(zipTempFilePath).exists()) {
                        new File(zipTempFilePath).mkdirs();
                    }
                    zipTempFilePath += zipFileId;
                }
                ZipUtil.fileAddToZip(zipTempFilePath, new ByteArrayInputStream(bytes), fileName);
            }
        }
        if(zipTempFilePath == null) {
            return ServiceResult.error("文件不存在");
        }
        //将文件上传到默认文件服务器
        String newFileId = zipFileId;
        if(!"local".equals(FileUploadUtils.getDefaultPlatform())) { //不是本地，说明是其他文件服务器，将zip文件上传到其他服务器里，方便下载
            FileInfo fileInfo = FileUploadUtils.uploadFile(new File(zipTempFilePath), FilePathUtil.getFilePath(FileTypeConstant.FILEZIPDOWNTEMPPATH), zipFileId);
            new File(zipTempFilePath).delete();
            newFileId = fileInfo.getFilename();
        }
        DownloadVO vo = DownloadVO.builder().name(zipFileId).url(UploaderUtil.uploaderFile(newFileId + "#" + FileTypeConstant.FILEZIPDOWNTEMPPATH)).build();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("downloadVo", vo);
        map.put("downloadName", "文件" + zipFileId);
        return ServiceResult.success(map);
    }

    /**
     * 上传文件/图片
     *
     * @return
     */
    @NoDataSourceBind()
    @Operation(summary = "上传文件/图片")
    @PostMapping(value = "/Uploader/{type}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Parameters({
            @Parameter(name = "type", description = "类型",required = true)
    })
    public ServiceResult<UploaderVO> uploader(@PathVariable("type") String type, MultipartFile file, HttpServletRequest httpServletRequest) throws  IOException {
        String fileType = UpUtil.getFileType(file);
        //验证类型
        if (!OptimizeUtil.fileType(configValueUtil.getAllowUploadFileType(), fileType)) {
            return ServiceResult.error(MsgCode.FA017.get());
        }
        PathTypeModel pathTypeModel = new PathTypeModel();
        pathTypeModel.setPathType(httpServletRequest.getParameter("pathType"));
        pathTypeModel.setIsAccount(httpServletRequest.getParameter("isAccount"));
        pathTypeModel.setFolder(httpServletRequest.getParameter("folder"));
        if("selfPath".equals(pathTypeModel.getPathType())) {
            if (StringUtil.isNotEmpty(pathTypeModel.getFolder())) {
                String folder = pathTypeModel.getFolder();
                folder = folder.replaceAll("\\\\", "/");
                String regex = "^[a-z0-9A-Z\\u4e00-\\u9fa5\\\\\\/]+$";
                if(!folder.matches(regex)){
                    return ServiceResult.error("文件存储路径错误");
                }
            }
        }
        UploaderVO vo = uploaderVO(file, type,pathTypeModel);
        return ServiceResult.success(vo);
    }

    /**
     * 获取下载文件链接
     *
     * @return
     */
    @NoDataSourceBind()
    @Operation(summary = "获取下载文件链接")
    @GetMapping("/Download/{type}/{fileName}")
    @Parameters({
            @Parameter(name = "type", description = "类型",required = true),
            @Parameter(name = "fileName", description = "文件名称",required = true),
    })
    public ServiceResult downloadUrl(@PathVariable("type") String type, @PathVariable("fileName") String fileName) {
        type = XSSEscape.escape(type);
        fileName = XSSEscape.escape(fileName);
        boolean exists = FileUploadUtils.exists(type, fileName);
        if (exists) {
            DownloadVO vo = DownloadVO.builder().name(fileName).url(UploaderUtil.uploaderFile(fileName + "#" + type)).build();
            return ServiceResult.success(vo);
        }
        return ServiceResult.error(MsgCode.FA018.get());
    }

    /**
     * 下载文件链接
     *
     * @return
     */
    @NoDataSourceBind()
    @Operation(summary = "下载文件链接")
    @GetMapping("/Download")
    public void downloadFile() throws DataBaseException {
        HttpServletRequest request = ServletUtil.getRequest();
        String reqJson = request.getParameter("encryption");
        String name = request.getParameter("name");
        String fileNameAll = DesUtil.aesDecode(reqJson);
        if (!StringUtil.isEmpty(fileNameAll)) {
            fileNameAll = fileNameAll.replaceAll("\n", "");
            String[] data = fileNameAll.split("#");
            String cacheKEY = data.length > 0 ? data[0] : "";
            String fileName = data.length > 1 ? data[1] : "";
            String type = data.length > 2 ? data[2] : "";
            Object ticketObj = TicketUtil.parseTicket(cacheKEY);
            //验证缓存
            if (ticketObj != null) {
                //某些手机浏览器下载后会有提示窗口, 会访问两次下载地址
                if(UserProvider.getDeviceForAgent().equals(DeviceType.APP) && "".equals(ticketObj)){
                    TicketUtil.updateTicket(cacheKEY, "1", 30L);
                }else{
                    TicketUtil.deleteTicket(cacheKEY);
                }
                //下载文件
                String typePath =FilePathUtil.getFilePath(type.toLowerCase());
                if(fileName.indexOf(",") >= 0) {
                    typePath += fileName.substring(0, fileName.lastIndexOf(",")+1).replaceAll(",", "/");
                    fileName = fileName.substring(fileName.lastIndexOf(",")+1);
                }
//                String filePath = FilePathUtil.getFilePath(type.toLowerCase());
                byte[] bytes = FileUploadUtils.downloadFileByte(typePath, fileName, false);
                FileDownloadUtil.downloadFile(bytes, fileName, name);
                if(FileTypeConstant.FILEZIPDOWNTEMPPATH.equals(type)) { //删除打包的临时文件，释放存储
                    FileUploadUtils.deleteFileByPathAndFileName(typePath, fileName);
                }
            } else {
                if(FileTypeConstant.FILEZIPDOWNTEMPPATH.equals(type)) { //删除打包的临时文件，释放存储
                    String typePath = FilePathUtil.getFilePath(type);
                    FileUploadUtils.deleteFileByPathAndFileName(typePath, fileName);
                }
                throw new DataBaseException("链接已失效");
            }
        } else {
            throw new DataBaseException("链接已失效");
        }
    }

    /**
     * 下载文件链接
     *
     * @return
     */
    @NoDataSourceBind()
    @Operation(summary = "下载模板文件链接")
    @GetMapping("/DownloadModel")
    public void downloadModel() throws DataBaseException {
        HttpServletRequest request = ServletUtil.getRequest();
        String reqJson = request.getParameter("encryption");
        String fileNameAll = DesUtil.aesDecode(reqJson);
        if (!StringUtil.isEmpty(fileNameAll)) {
            String token = fileNameAll.split("#")[0];
            if (TicketUtil.parseTicket(token) != null) {
                TicketUtil.deleteTicket(token);
                String fileName = fileNameAll.split("#")[1];
                String filePath = configValueUtil.getTemplateFilePath();
                // 下载文件
                byte[] bytes = FileUploadUtils.downloadFileByte(filePath, fileName, false);
                FileDownloadUtil.downloadFile(bytes, fileName, null);
            } else {
                throw new DataBaseException("链接已失效");
            }
        } else {
            throw new DataBaseException("链接已失效");
        }
    }


    /**
     * 获取图片
     *
     * @param fileName
     * @param type
     * @return
     */
    @NoDataSourceBind()
    @Operation(summary = "获取图片")
    @GetMapping("/Image/{type}/{fileName}")
    @Parameters({
            @Parameter(name = "type", description = "类型",required = true),
            @Parameter(name = "fileName", description = "名称",required = true),
    })
    public void downLoadImg(@PathVariable("type") String type, @PathVariable("fileName") String fileName) {
        String filePath = FilePathUtil.getFilePath(type.toLowerCase());
        if(fileName.indexOf(",") >= 0) {
            filePath += fileName.substring(0, fileName.lastIndexOf(",")+1).replaceAll(",", "/");
            fileName = fileName.substring(fileName.lastIndexOf(",")+1);
        }
//        if ("im".equalsIgnoreCase(type)) {
//            type = "imfile";
//        }
//        else if (FileTypeEnum.ANNEXPIC.equalsIgnoreCase(type)) {
//            type = FileTypeEnum.ANNEX;
//        }
        // 下载文件
        byte[] bytes = FileUploadUtils.downloadFileByte(filePath, fileName, false);
        FileDownloadUtil.flushImage(bytes, fileName);
    }

    /**
     * 获取IM聊天图片
     * 注意 后缀名前端故意把 .替换@
     *
     * @param fileName
     * @return
     */
    @NoDataSourceBind()
    @Operation(summary = "获取IM聊天图片")
    @GetMapping("/IMImage/{fileName}")
    @Parameters({
            @Parameter(name = "fileName", description = "名称",required = true),
    })
    public void imImage(@PathVariable("fileName") String fileName) {
        byte[] bytes = FileUploadUtils.downloadFileByte(configValueUtil.getImContentFilePath(), fileName, false);
        FileDownloadUtil.flushImage(bytes, fileName);
    }

    /**
     * 查看图片
     *
     * @param type     哪个文件夹
     * @param fileName 文件名称
     * @return
     */
    @NoDataSourceBind()
    @Operation(summary = "查看图片")
    @GetMapping("/{type}/{fileName}")
    @Parameters({
            @Parameter(name = "fileName", description = "名称",required = true),
            @Parameter(name = "type", description = "类型",required = true),
    })
    public void img(@PathVariable("type") String type, @PathVariable("fileName") String fileName) {
//        String filePath = configValueUtil.getBiVisualPath() + type + File.separator;
//        if (StorageType.MINIO.equals(configValueUtil.getFileType())) {
//            fileName = "/" + type + "/" + fileName;
//            filePath = configValueUtil.getBiVisualPath().substring(0, configValueUtil.getBiVisualPath().length() - 1);
//        }
        String filePath = FilePathUtil.getFilePath(type.toLowerCase());
        if(fileName.indexOf(",") >= 0) {
            filePath += fileName.substring(0, fileName.lastIndexOf(",")+1).replaceAll(",", "/");
            fileName = fileName.substring(fileName.lastIndexOf(",")+1);
        }
        //下载文件
        byte[] bytes = FileUploadUtils.downloadFileByte(filePath, fileName, false);
        FileDownloadUtil.flushImage(bytes, fileName);
    }

    /**
     * 获取IM聊天语音
     * 注意 后缀名前端故意把 .替换@
     *
     * @param fileName
     * @return
     */
    @NoDataSourceBind()
    @Operation(summary = "获取IM聊天语音")
    @GetMapping("/IMVoice/{fileName}")
    @Parameters({
            @Parameter(name = "fileName", description = "名称",required = true),
    })
    public void imVoice(@PathVariable("fileName") String fileName) {
        fileName = fileName.replaceAll("@", ".");
        byte[] bytes = FileUploadUtils.downloadFileByte(configValueUtil.getImContentFilePath(), fileName, false);
        FileDownloadUtil.flushImage(bytes, fileName);
    }

    /**
     * app启动获取信息
     *
     * @param appName
     * @return
     */
    @NoDataSourceBind()
    @Operation(summary = "app启动获取信息")
    @GetMapping("/AppStartInfo/{appName}")
    @Parameters({
            @Parameter(name = "appName", description = "名称",required = true),
    })
    public ServiceResult getAppStartInfo(@PathVariable("appName") String appName) {
        appName = XSSEscape.escape(appName);
        JSONObject object = new JSONObject();
        object.put("AppVersion", configValueUtil.getAppVersion());
        object.put("AppUpdateContent", configValueUtil.getAppUpdateContent());
        return ServiceResult.success(object);
    }

    //----------大屏图片下载---------
    @NoDataSourceBind()
    @Operation(summary = "获取图片")
    @GetMapping("/VisusalImg/{bivisualpath}/{type}/{fileName}")
    @Parameters({
            @Parameter(name = "type", description = "类型",required = true),
            @Parameter(name = "bivisualpath", description = "路径",required = true),
            @Parameter(name = "fileName", description = "名称",required = true),
    })
    public void downVisusalImg(@PathVariable("type") String type,@PathVariable("bivisualpath") String bivisualpath, @PathVariable("fileName") String fileName) {
        fileName = XSSEscape.escape(fileName);
        String filePath = configValueUtil.getBiVisualPath();
        byte[] bytes = FileUploadUtils.downloadFileByte(filePath + type + "/", fileName, false);
        FileDownloadUtil.flushImage(bytes, fileName);
    }

    //----------------------

    @NoDataSourceBind()
    @Operation(summary = "预览文件")
    @GetMapping("/Uploader/Preview")
    public ServiceResult Preview(PreviewParams previewParams) {
        //读取允许文件预览类型
        String allowPreviewType = configValueUtil.getAllowPreviewFileType();
        String[] fileType = allowPreviewType.split(",");

        String fileName = XSSEscape.escape(previewParams.getFileName());

        //文件预览类型检验
        String docType = fileName.substring(fileName.lastIndexOf(".") + 1);
        String s = Arrays.asList(fileType).stream().filter(type -> type.equals(docType)).findFirst().orElse(null);

        if (StringUtil.isEmpty(s)) {
            return ServiceResult.error("预览失败,请检查文件类型是否规范");
        }

        //解析文件url 获取类型
        String type = configValueUtil.getWebAnnexFilePath();

        String fileNameAll = previewParams.getFileDownloadUrl();
        if (!StringUtil.isEmpty(fileNameAll)) {
            String[] data = fileNameAll.split("/");
             type  = data.length > 4 ? data[4] : "";
        }

        String url;
        //文件预览策略
        if ("yozo".equals(configValueUtil.getPreviewType())) {
            if (StringUtil.isEmpty(previewParams.getFileVersionId())) {
                return ServiceResult.error("预览失败,请重新上传文件");
            }

            String fileVersionId = XSSEscape.escape(previewParams.getFileVersionId());

            //获取签名
            Map<String, String[]> parameter = new HashMap<String, String[]>();
            parameter.put("appId", new String[]{YozoParams.APP_ID});
            parameter.put("fileVersionId", new String[]{fileVersionId});
            String sign = yozoUtils.generateSign(YozoParams.APP_ID, YozoParams.APP_KEY, parameter).getData();
            url = "http://eic.yozocloud.cn/api/view/file?fileVersionId="
                    + fileVersionId
                    + "&appId="
                    + YozoParams.APP_ID
                    + "&sign="
                    + sign;
        } else {
            if (FileUploadUtils.getDefaultPlatform().startsWith("local-plus")) {
                url = YozoParams.LINZEN_DOMAINS + "/api/file/filedownload/" + type + "/" + previewParams.getFileName();
            } else {
                //图像格式
                if ("pdf,jpg,gif,png,bmp,jpeg".contains(docType)){
                    url = YozoParams.LINZEN_DOMAINS + "/api/file/filedownload/" + type;
                } else {
                    String[] split = fileNameAll.split("/");
                    if (split.length > 5) {
                        type = FilePathUtil.getFilePath(type) + split[5].replaceAll(",", "/");
                    }
                    FileDetail fileDetail = FileUploadUtils.getFileDetail(type, fileName, false);
                    url = FileUploadUtils.getDomain() + FileUploadUtils.getBucketName() + fileDetail.getBasePath() + fileDetail.getPath();
                }
            }
            //encode编码
            String fileUrl = Base64.encodeBase64String(url.getBytes());
            url = configValueUtil.getKkFileUrl() + "onlinePreview?url=" + fileUrl;
        }
        return ServiceResult.success(MsgCode.SU000.get(), url);
    }

    @NoDataSourceBind()
    @Operation(summary = "kk本地文件预览")
    @GetMapping("/filedownload/{type}/{fileName}")
    public void filedownload(@PathVariable("type") String type, @PathVariable("fileName") String fileName, HttpServletResponse response) {
        String typePath =FilePathUtil.getFilePath(type);
        if(fileName.indexOf(",") >= 0) {
            typePath += fileName.substring(0, fileName.lastIndexOf(",")+1).replaceAll(",", "/");
            fileName = fileName.substring(fileName.lastIndexOf(",")+1);
        }
        String tmpPath = typePath + fileName;
        boolean b = FileUtil.fileIsFile(tmpPath);
        if (!b){
            FileUploadUtils.downLocal(FilePathUtil.getFilePath(type), FileUploadUtils.getLocalBasePath() + typePath,fileName);
        }
        String filePath = XSSEscape.escapePath(FileUploadUtils.getLocalBasePath() + typePath + fileName);
        OutputStream os = null;
            //本地取对应文件
            File file = new File(filePath);
            try {
                os = response.getOutputStream();
                String contentType = Files.probeContentType(Paths.get(file.getAbsolutePath()));
                response.setHeader("Content-Type", contentType);
                response.setHeader("Content-Dispostion", "attachment;filename=" + new String(file.getName().getBytes("utf-8"), "ISO8859-1"));
                @Cleanup FileInputStream fileInputStream = new FileInputStream(file);

                @Cleanup WritableByteChannel writableByteChannel = Channels.newChannel(os);

                @Cleanup FileChannel channel = fileInputStream.getChannel();
                channel.transferTo(0, channel.size(), writableByteChannel);
                channel.close();
                os.flush();
                writableByteChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    @Operation(summary = "分片上传获取")
    @GetMapping("/chunk")
    public ServiceResult checkChunk(Chunk chunk) {
        String type = chunk.getExtension();
        if (!OptimizeUtil.fileType(configValueUtil.getAllowUploadFileType(), type)) {
            return ServiceResult.error("上传失败，文件格式不允许上传");
        }
        String identifier = chunk.getIdentifier();
        String path = configValueUtil.getTemporaryFilePath();
        String filePath = XSSEscape.escapePath(path + identifier);
        List<File> chunkFiles = FileUtil.getFile(new File(FileUploadUtils.getLocalBasePath() + filePath));
        List<Integer> existsChunk = chunkFiles.stream().filter(f->{
            if(f.getName().endsWith(".tmp")){
                FileUtils.deleteQuietly(f);
            }else
                return f.getName().startsWith(identifier);
            return false;
        }).map(f->Integer.parseInt(f.getName().replace(identifier.concat("-"), ""))).collect(Collectors.toList());
        ChunkRes chunkRes = ChunkRes.builder().merge(chunk.getTotalChunks().equals(existsChunk.size())).chunkNumbers(existsChunk).build();
        return ServiceResult.success(chunkRes);
    }


    @Operation(summary = "分片上传附件")
    @PostMapping("/chunk")
    public ServiceResult upload(Chunk chunk, @RequestParam("file") MultipartFile file) {
        String type = chunk.getExtension();
        if (!OptimizeUtil.fileType(configValueUtil.getAllowUploadFileType(), type)) {
            return ServiceResult.error("上传失败，文件格式不允许上传");
        }
        ChunkRes chunkRes = ChunkRes.builder().build();
        chunkRes.setMerge(false);
        File chunkFile = null;
        File chunkTmpFile = null;
        try {
            String filePath = FileUploadUtils.getLocalBasePath() + configValueUtil.getTemporaryFilePath();
            Integer chunkNumber = chunk.getChunkNumber();
            String identifier = XSSEscape.escapePath(chunk.getIdentifier());
            String chunkTempPath = XSSEscape.escapePath(filePath + identifier);
            File path = new File(chunkTempPath);
            if (!path.exists()) {
                path.mkdirs();
            }
            String chunkName = XSSEscape.escapePath(identifier.concat("-") + chunkNumber);
            String chunkTmpName = XSSEscape.escapePath(chunkName.concat(".tmp"));
            chunkFile = new File(chunkTempPath, chunkName);
            chunkTmpFile = new File(chunkTempPath, chunkTmpName);
            if (chunkFile.exists() && chunkFile.length() == chunk.getCurrentChunkSize()) {
                System.out.println("该分块已经上传：" + chunkFile.getName());
            } else {
                @Cleanup InputStream inputStream = file.getInputStream();
                FileUtils.copyInputStreamToFile(inputStream, chunkTmpFile);
                chunkTmpFile.renameTo(chunkFile);
            }
            int existsSize = (int) FileUtil.getFile(new File(chunkTempPath)).stream().filter(f->
                    f.getName().startsWith(identifier) && !f.getName().endsWith(".tmp")
            ).count();
            chunkRes.setMerge(Objects.equals(existsSize, chunk.getTotalChunks()));
        } catch (Exception e) {
            try{
                FileUtils.deleteQuietly(chunkTmpFile);
                FileUtils.deleteQuietly(chunkFile);
            }catch (Exception ee){
                e.printStackTrace();
            }
            System.out.println("上传异常：" + e);
            return ServiceResult.error("上传异常");
        }
        return ServiceResult.success(chunkRes);
    }

    @Operation(summary = "分片组装")
    @PostMapping("/merge")
    public ServiceResult merge(MergeChunkDto mergeChunkDto) {
        String identifier = XSSEscape.escapePath(mergeChunkDto.getIdentifier());
        String path = FileUploadUtils.getLocalBasePath() + configValueUtil.getTemporaryFilePath();
        String filePath = XSSEscape.escapePath(path + identifier);
        String uuid = RandomUtil.uuId();
        String partFile = XSSEscape.escapePath(path + uuid + "." + mergeChunkDto.getExtension());
        UploaderVO vo = UploaderVO.builder().build();
        try {
            List<File> mergeFileList = FileUtil.getFile(new File(filePath));
            @Cleanup FileOutputStream destTempfos = new FileOutputStream(partFile, true);
            for (int i = 0; i < mergeFileList.size(); i++) {
                String chunkName = identifier.concat("-") + (i + 1);
                File files = new File(filePath, chunkName);
                if (files.exists()) {
                    FileUtils.copyFile(files, destTempfos);
                }
            }
            File partFiles = new File(partFile);
            if (partFiles.exists()) {
                MultipartFile multipartFile = FileUtil.createFileItem(partFiles);
                String type = mergeChunkDto.getType();
                PathTypeModel pathTypeModel = new PathTypeModel();
                pathTypeModel.setPathType(mergeChunkDto.getPathType());
                pathTypeModel.setIsAccount(mergeChunkDto.getIsAccount());
                pathTypeModel.setFolder(mergeChunkDto.getFolder());
                if("selfPath".equals(pathTypeModel.getPathType())) {
                    if (StringUtil.isNotEmpty(pathTypeModel.getFolder())) {
                        String folder = pathTypeModel.getFolder();
                        folder = folder.replaceAll("\\\\", "/");
                        String regex = "^[a-z0-9A-Z\\u4e00-\\u9fa5\\\\\\/]+$";
                        if(!folder.matches(regex)){
                            return ServiceResult.error("文件存储路径错误");
                        }
                    }
                }
                vo = uploaderVO(multipartFile, type,pathTypeModel);
                FileUtil.deleteTmp(multipartFile);
            }
        } catch (Exception e) {
            System.out.println("合并分片失败:" + e);
        }finally {
            FileUtils.deleteQuietly(new File(filePath));
            FileUtils.deleteQuietly(new File(partFile));
        }
        return ServiceResult.success(vo);
    }

    /**
     * 封装上传附件
     *
     * @param file
     * @param type
     * @return
     * @throws IOException
     */
    private UploaderVO uploaderVO(MultipartFile file, String type, PathTypeModel pathTypeModel) throws IOException {
        String orgFileName = file.getOriginalFilename();
        String fileType = UpUtil.getFileType(file);
//        if (OptimizeUtil.fileSize(file.getSize(), 1024000)) {
//            return ServiceResult.error("上传失败，文件大小超过1M");
//        }
//        if ("mail".equals(type)) {
//            type = "temporary";
//        }
        //实际文件名
        String fileName = DateUtil.dateNow("yyyyMMdd") + "_" + RandomUtil.uuId() + "." + fileType;
        //文件上传路径
        String filePath = FilePathUtil.getFilePath(type.toLowerCase());

        //文件自定义路径相对路径
        String relativeFilePath = "";
        if("selfPath".equals(pathTypeModel.getPathType())){
            if ("1".equals(pathTypeModel.getIsAccount())) {
                UserInfo userInfo = UserProvider.getUser();
                relativeFilePath += userInfo.getUserAccount() +"/";
            }
            if(StringUtil.isNotEmpty(pathTypeModel.getFolder())) {
                String folder = pathTypeModel.getFolder();
                folder = folder.replaceAll("\\\\", "/");
                relativeFilePath += folder;
                if(!folder.endsWith("/")){
                    relativeFilePath += "/";
                }
            }
            if(StringUtil.isNotEmpty(relativeFilePath)){
                relativeFilePath = StringUtil.replaceMoreStrToOneStr(relativeFilePath,"/");
                if(relativeFilePath.startsWith("/")){
                    relativeFilePath =  relativeFilePath.substring(1);
                }
                filePath += relativeFilePath;
                fileName = relativeFilePath.replaceAll("/",",") + fileName;
            }
        }

        UploaderVO vo = UploaderVO.builder().fileSize(file.getSize()).fileExtension(fileType).build();
//        //上传文件
//        if ("im".equalsIgnoreCase(type)){
//            type = "imfile";
//        }
        FileInfo fileInfo = FileUploadUtils.uploadFile(file, filePath, fileName);
        fileName = fileInfo.getFilename();
        String thFilename = fileInfo.getThFilename();
        //自定义文件实际文件名
        if (StringUtil.isNotEmpty(relativeFilePath)) {
            fileName = relativeFilePath.replaceAll("/", ",") + fileName;
            thFilename = relativeFilePath.replaceAll("/", ",") + thFilename;
        }
        vo.setName(fileName);
//        UploadUtil.uploadFile(configValueUtil.getFileType(), type, fileName, file, filePath);
        if ("useravatar".equalsIgnoreCase(type)) {
            vo.setUrl(UploaderUtil.uploaderImg(fileName));
            vo.setUrl(UploaderUtil.uploaderImg(thFilename));
        } else if ("annex".equalsIgnoreCase(type)) {
//            UserInfo userInfo = userProvider.get();
//            vo.setUrl(UploaderUtil.uploaderFile(userInfo.getId() + "#" + fileName + "#" + type));
            vo.setUrl(UploaderUtil.uploaderImg("/api/file/Image/annex/", fileName));
            vo.setThumbUrl(UploaderUtil.uploaderImg("/api/file/Image/annex/", thFilename));
        } else if ("annexpic".equalsIgnoreCase(type)) {
            vo.setUrl(UploaderUtil.uploaderImg("/api/file/Image/annexpic/", fileName));
            vo.setThumbUrl(UploaderUtil.uploaderImg("/api/file/Image/annexpic/", thFilename));
        }else  {
            vo.setUrl(UploaderUtil.uploaderImg("/api/file/Image/"+type.toLowerCase()+"/", fileName));
            vo.setThumbUrl(UploaderUtil.uploaderImg("/api/file/Image/"+type.toLowerCase()+"/", thFilename));
        }

        //上传到永中
        if ("yozo".equals(configValueUtil.getPreviewType())) {
            try {
                @Cleanup InputStream inputStream = file.getInputStream();
                String s = yozoUtils.uploadFileInPreview(inputStream, orgFileName);
                Map<String, Object> map = JsonUtil.stringToMap(s);
                if ("操作成功".equals(map.get("message"))) {
                    Map<String, Object> dataMap = JsonUtil.stringToMap(String.valueOf(map.get("data")));
                    String verId = String.valueOf(dataMap.get("fileVersionId"));
                    vo.setFileVersionId(verId);
                }
            } catch (Exception e) {
                System.out.println("上传到永中失败");
                e.printStackTrace();
            }
        }
        return vo;
    }
}
